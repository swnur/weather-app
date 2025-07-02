package com.swnur.service;

import com.swnur.dao.UserDAO;
import com.swnur.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String login, String password) {
        Optional<User> existingUser = userDAO.findByLogin(login);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Login already exists: " + login);
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));

        return userDAO.save(newUser);
    }

    @Transactional
    public User authenticateUser(String login, String password) {
        Optional<User> existingUser = userDAO.findByLogin(login);

        if (existingUser.isEmpty()) {
            throw new RuntimeException("Authentication failed: User not found");
        }

        User user = existingUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Authentication failed: Password is incorrect");
        }

        return user;
    }

}
