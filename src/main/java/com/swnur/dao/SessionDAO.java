package com.swnur.dao;

import com.swnur.model.Session;
import com.swnur.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        entityManager.persist(session);
        return session;
    }

    @Transactional(readOnly = true)
    public Optional<Session> findById(UUID id) {
        Session session = entityManager.find(Session.class, id);
        return Optional.ofNullable(session);
    }

    @Transactional
    public void deleteByExpired() {
        entityManager.createQuery("DELETE FROM Session s WHERE s.expiresAt < :now")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }

    @Transactional
    public void deleteByExpiredForUser(User user) {
        entityManager.createQuery("DELETE FROM Session s WHERE s.user = :user AND s.expiresAt <:now")
                .setParameter("user", user)
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }

    @Transactional
    public int deleteById(UUID id) {
        return entityManager.createQuery("DELETE FROM Session s WHERE s.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
