package com.swnur.intercepter;

import com.swnur.model.User;
import com.swnur.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public static final String SESSION_COOKIE_NAME = "APP_SESSION_ID";
    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";
    public static final int SESSION_EXPIRATION_SECONDS = 300;
    public static final int SESSION_EXPIRATION_MINUTES = 5;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = requestURI.substring(contextPath.length());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        System.out.println("AuthInterceptor: Full URI: " + requestURI);
        System.out.println("AuthInterceptor: Context Path: " + contextPath);
        System.out.println("AuthInterceptor: Normalized Path: " + path);

        if (path.startsWith("/sign-in") ||
                path.startsWith("/sign-up") ||
                path.startsWith("/static") ||
                path.equals("/error/general-error")) {
            System.out.println("AuthInterceptor: Allowing public URI: " + path);
            return true;
        }

        Optional<String> sessionIdString = Arrays.stream(
                Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();

        if (sessionIdString.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-in?auth_required=true");
            return false;
        }

        UUID sessionId;

        try {
            sessionId = UUID.fromString(sessionIdString.get());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid session ID format in cookie: " + sessionIdString.get());
            response.sendRedirect(request.getContextPath() + "/sign-in?invalidSession=true");
            return false;
        }

        Optional<User> authenticatedUser = sessionService.validateSession(sessionId);

        if (authenticatedUser.isEmpty()) {
            Cookie expiredCookie = new Cookie(SESSION_COOKIE_NAME, "");
            expiredCookie.setMaxAge(0);
            expiredCookie.setPath("/");
            response.addCookie(expiredCookie);

            response.sendRedirect(request.getContextPath() + "/sign-in?session_expired=true");
            return false;
        }

        request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser.get());
        return true;
    }
}
