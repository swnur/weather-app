package com.swnur.service;

import com.swnur.dao.SessionDAO;
import com.swnur.model.Session;
import com.swnur.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

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
        return sessionDAO.findById(sessionID)
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(Session::getUser);
    }

    @Transactional
    public void invalidateSessionByStringId(String sessionIdString) {
        try {
            UUID sessionId = UUID.fromString(sessionIdString);
            sessionDAO.deleteById(sessionId);
        } catch (IllegalArgumentException e) {
            log.warn("Attempted to invalidate session with UUID: {}", sessionIdString);
        }
    }

    @Transactional
    public void cleanupExpiredSessions() {
        sessionDAO.deleteByExpired();
    }
}
