package com.swnur.controller;

import com.swnur.intercepter.AuthInterceptor;
import com.swnur.model.Session;
import com.swnur.model.User;
import com.swnur.service.SessionService;
import com.swnur.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    @Value("${session.expiration.minutes}")
    private int sessionExpirationMinutes;

    @GetMapping("/sign-up")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        return "auth/sign-up";
    }

    @PostMapping("sign-up")
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/sign-up";
        }

        userService.registerUser(user.getLogin(), user.getPassword());

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/sign-in";
    }

    @GetMapping("/sign-in")
    public String showLoginForm(Model model,
                                @RequestParam(value = "auth_required", required = false) String authRequired,
                                @RequestParam(value = "session_expired", required = false) String sessionExpired,
                                @RequestParam(value = "invalid_session", required = false) String invalidSession) {

        if (authRequired != null)
            model.addAttribute("error", "Please log in to access this page.");
        if (sessionExpired != null)
            model.addAttribute("error", "Your sessions has expired. Please log in again.");
        if (invalidSession != null)
            model.addAttribute("error", "Invalid session. Please log in again.");

        return "auth/sign-in";
    }

    @PostMapping("/sign-in")
    public String loginUser(@RequestParam("login") String login,
                            @RequestParam("password") String password,
                            HttpServletResponse response) {
        User authenticatedUser = userService.authenticateUser(login, password);
        Session session = sessionService.createSession(authenticatedUser);

        Cookie sessionCookie = new Cookie(AuthInterceptor.SESSION_COOKIE_NAME, session.getId().toString());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(sessionExpirationMinutes * 60);

        response.addCookie(sessionCookie);
        return "redirect:/";
    }

}
