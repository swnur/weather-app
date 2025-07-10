package com.swnur.dao;

import com.swnur.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
       TypedQuery<User> query = entityManager.createNamedQuery("User.findById", User.class)
               .setParameter("id", id);

       return getSingleResultOptional(query);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = entityManager.createNamedQuery("User.findByLogin", User.class)
                .setParameter("login", login);

        return getSingleResultOptional(query);
    }

    private <T> Optional<T> getSingleResultOptional(TypedQuery<T> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
