package com.swnur.dao;

import com.swnur.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        try {
            return Optional.ofNullable(entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }
}
