package com.swnur.intercepter;

import com.swnur.model.User;
import com.swnur.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    private final SessionService sessionService;

    public static final String SESSION_COOKIE_NAME = "APP_SESSION_ID";
    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";

    private static final String[] EXCLUDED_PATHS = {
            "/sign-in", "/sign-up", "/static", "/error/general-error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPathExcluded(request.getRequestURI(), request.getContextPath())) {
            return true;
        }

        Optional<UUID> sessionIdOpt = extractSessionIdFromCookie(request.getCookies());

        if (sessionIdOpt.isEmpty()) {
            logger.warn("Session cookies not found or invalid. Redirecting to sign-in page.");
            sendRedirect(response, request.getContextPath(), "/sign-in?auth_required=true");
            return false;
        }

        UUID sessionId = sessionIdOpt.get();
        Optional<User> authenticatedUserOpt = sessionService.validateSession(sessionId);

        if (authenticatedUserOpt.isEmpty()) {
            logger.warn("Session ID {} is expired or invalid. Expiring cookies and redirecting.", sessionId);
            expireSessionCookie(response);
            sendRedirect(response, request.getContextPath(), "/sign-in?session_expired=true");
            return false;
        }

        User authenticatedUser = authenticatedUserOpt.get();
        logger.debug("Session valid for user: {}", authenticatedUser.getLogin());
        request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);

        return true;
    }

    private boolean isPathExcluded(String requestURI, String contextPath) {
        String path = requestURI.substring(contextPath.length());
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    private Optional<UUID> extractSessionIdFromCookie(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(this::parseUuidFromString);
    }

    private Optional<UUID> parseUuidFromString(String uuidString) {
        try {
            return Optional.of(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format in session cookie: '{}'", uuidString, e);
            return Optional.empty();
        }
    }

    private void expireSessionCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie(SESSION_COOKIE_NAME, "");
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        expiredCookie.setHttpOnly(true);

        response.addCookie(expiredCookie);
    }

    private void sendRedirect(HttpServletResponse response, String contextPath, String targetUrl) throws IOException {
        response.sendRedirect(contextPath + targetUrl);
    }
}
