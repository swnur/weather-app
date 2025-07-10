package com.swnur.dao;

import com.swnur.model.Session;
import com.swnur.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Session save(Session session) {
        return entityManager.merge(session);
    }

    @Transactional(readOnly = true)
    public Optional<Session> findById(UUID id) {
        TypedQuery<Session> query = entityManager.createNamedQuery("Session.findByIdAndFetchUser", Session.class)
                .setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteByExpired() {
        entityManager.createNamedQuery("Session.deleteByExpired")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }

    @Transactional
    public void deleteByExpiredForUser(User user) {
        entityManager.createNamedQuery("Session.deleteByExpiredForUser")
                .setParameter("user", user)
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }

    @Transactional
    public int deleteById(UUID id) {
        return entityManager.createNamedQuery("Session.deleteById")
                .setParameter("id", id)
                .executeUpdate();
    }
}
