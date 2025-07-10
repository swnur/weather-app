package com.swnur.service;

import com.swnur.dao.UserDAO;
import com.swnur.exception.InvalidCredentialsException;
import com.swnur.exception.LoginAlreadyExistsException;
import com.swnur.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String login, String password) {
        if (userDAO.findByLogin(login).isPresent())
            throw new LoginAlreadyExistsException("Login: " + login + " is already taken.");

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));

        return userDAO.save(newUser);
    }

    @Transactional
    public User authenticateUser(String login, String password) {
        return userDAO.findByLogin(login)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Authentication failed: Invalid login or password"));
    }

}
