package com.swnur.service;

import com.swnur.dto.GeocodingResponse;
import com.swnur.dto.WeatherResponse;
import com.swnur.model.Location;
import com.swnur.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherService.class);
    private final RestTemplate restTemplate;
    private final LocationService locationService;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final String GEOCODING_BASE_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static final int LIMIT = 10;

    public List<GeocodingResponse> searchLocations(String cityName, int limit) {
        String url = UriComponentsBuilder.fromUriString(GEOCODING_BASE_URL)
                .queryParam("q", cityName)
                .queryParam("limit", limit)
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Calling Geocoding API for city: {}", cityName);

        try {
            ResponseEntity<List<GeocodingResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Error calling OpenWeatherMap GeoCoding API for city: {}. Reason: {}", cityName, e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<Integer, WeatherResponse> getWeatherResponseForUser(User user) {
        List<Location> locationList = locationService.findLocationsForUser(user);
        Map<Integer, WeatherResponse> weatherResponseMap = new HashMap<>();

        for (Location location : locationList) {
            getCurrentWeatherForLocation(location.getLatitude(), location.getLongitude())
                    .ifPresent(weatherResponse -> weatherResponseMap.put(location.getId(), weatherResponse));
        }

        return weatherResponseMap;
    }

    private Optional<WeatherResponse> getCurrentWeatherForLocation(BigDecimal latitude, BigDecimal longitude) {
        String url = UriComponentsBuilder.fromUriString(WEATHER_BASE_URL)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);

            return Optional.ofNullable(response.getBody());
        } catch (RestClientException e) {
            log.error("Error calling OpenWeatherMap weather API for location: {}, {}. Reason: {}", latitude, longitude, e.getMessage());
            return Optional.empty();
        }
    }

}
