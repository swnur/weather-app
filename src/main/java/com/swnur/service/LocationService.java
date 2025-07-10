package com.swnur.service;

import com.swnur.dao.LocationDAO;
import com.swnur.dao.UserDAO;
import com.swnur.dto.GeocodingResponse;
import com.swnur.exception.UserNotFoundException;
import com.swnur.model.Location;
import com.swnur.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;

    @Transactional(readOnly = true)
    public List<Location> findLocationsForUser(User user) {
        return locationDAO.findByUser(user);
    }

    @Transactional
    public void addLocationForUser(GeocodingResponse geocodingResponse, Integer userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cannot add location for non-existent user."));

        Location location = new Location(
                geocodingResponse.getName(),
                geocodingResponse.getLat(),
                geocodingResponse.getLon(),
                user);

        Optional<Location> existingLocation = locationDAO.findByUserAndDetails(user, location);
        if (existingLocation.isPresent()) {
            log.warn("User '{}' attempted to add a duplicate location: '{}'", user, location);

            return;
        }

        log.info("Adding new location '{}' for user '{}'", location.getName(), user.getLogin());

        user.addLocation(location);
    }

    @Transactional
    public void deleteLocationForUser(Integer locationId, User user) {
        locationDAO.deleteByIdAndUser(locationId, user);
    }
}
