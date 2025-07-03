package com.swnur.controller;

import com.swnur.intercepter.AuthInterceptor;
import com.swnur.model.User;
import com.swnur.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final SessionService sessionService;

    @GetMapping("/")
    public String showMainPage(HttpServletRequest request, HttpServletResponse response,
                               Model model) {
        User authenticatedUser = (User) request.getAttribute(AuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE);

        if (authenticatedUser == null) {
            return "redirect:/sign-in?auth_required=true";
        }

        model.addAttribute("loggedInUser", authenticatedUser.getLogin());
        model.addAttribute("message", "Welcome to the Main Page!");

        return "main/index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(Optional.of(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> AuthInterceptor.SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .ifPresent(sessionIdString -> {
                    try {
                        UUID sessionId = UUID.fromString(sessionIdString);
                        sessionService.invalidateSession(sessionId);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Logout: Invalid session UD format in cookie: " + sessionIdString);
                    }
                });

        Cookie expiredCookie = new Cookie(AuthInterceptor.SESSION_COOKIE_NAME, "");
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);

        return "redirect:/sign-in?logout=true";
    }
}
