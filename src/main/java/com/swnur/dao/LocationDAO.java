package com.swnur.dao;

import com.swnur.model.Location;
import com.swnur.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Location save(Location location) {
        return entityManager.merge(location);
    }

    @Transactional(readOnly = true)
    public List<Location> findByUser(User user) {
        return entityManager.createQuery("SELECT l FROM Location l WHERE l.user = :user ORDER BY l.name ASC",
                        Location.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByIdAndUser(Integer id, User user) {
        try {
            return Optional.ofNullable(entityManager.createQuery("SELECT l FROM Location l WHERE l.id = :id AND l.user = :user", Location.class)
                    .setParameter("id", id)
                    .setParameter("user", user)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Location> findByUserAndDetails(User user, Location location) {
        try {
            String sqlQuery = "SELECT l FROM Location l " +
                    "WHERE l.user = :user AND l.name = :name AND l.latitude = :latitude and l.longitude = :longitude";

            TypedQuery<Location> query = entityManager.createQuery(sqlQuery, Location.class)
                    .setParameter("user", user)
                    .setParameter("name", location.getName())
                    .setParameter("latitude", location.getLatitude())
                    .setParameter("longitude", location.getLongitude());

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public int deleteByIdAndUser(Integer id, User user) {
        return entityManager.createQuery("DELETE FROM Location l WHERE l.id = :id AND l.user = :user")
                .setParameter("id", id)
                .setParameter("user", user)
                .executeUpdate();
    }
}
