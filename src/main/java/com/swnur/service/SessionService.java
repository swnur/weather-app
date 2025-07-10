package com.swnur.service;

import com.swnur.dao.SessionDAO;
import com.swnur.model.Session;
import com.swnur.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionDAO sessionDAO;

    @Value("${session.expiration.minutes}")
    private int sessionExpirationMinutes;

    @Transactional
    public Session createSession(User user) {
        sessionDAO.deleteByExpiredForUser(user);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(sessionExpirationMinutes);

        Session newSession = new Session(user, expiresAt);

        return sessionDAO.save(newSession);
    }

    @Transactional
    public Optional<User> validateSession(UUID sessionID) {
        Optional<Session> sessionOptional = sessionDAO.findById(sessionID);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        Session session = sessionOptional.get();

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessionDAO.deleteById(sessionID);
            return Optional.empty();
        }

        return Optional.of(session.getUser());
    }

    @Transactional
    public boolean invalidateSession(UUID sessionID) {
        return sessionDAO.deleteById(sessionID) > 0;
    }

    @Transactional
    public void cleanupExpiredSessions() {
        sessionDAO.deleteByExpired();
    }
}
