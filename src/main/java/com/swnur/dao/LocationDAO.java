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
        return entityManager.createNamedQuery("Location.findByUser",
                        Location.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByIdAndUser(Integer id, User user) {
        TypedQuery<Location> query = entityManager.createNamedQuery("Location.findByIdAndUser", Location.class)
                .setParameter("id", id)
                .setParameter("user", user);

        return getSingleResultOptional(query);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByUserAndDetails(User user, Location location) {
        TypedQuery<Location> query = entityManager.createNamedQuery("Location.findByUserAndDetails", Location.class)
                .setParameter("user", user)
                .setParameter("name", location.getName())
                .setParameter("latitude", location.getLatitude())
                .setParameter("longitude", location.getLongitude());

        return getSingleResultOptional(query);
    }

    @Transactional
    public int deleteByIdAndUser(Integer id, User user) {
        return entityManager.createQuery("DELETE FROM Location l WHERE l.id = :id AND l.user = :user")
                .setParameter("id", id)
                .setParameter("user", user)
                .executeUpdate();
    }

    private <T> Optional<T> getSingleResultOptional(TypedQuery<T> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
