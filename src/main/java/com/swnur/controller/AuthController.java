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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/sign-up")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("error", null);

        return "auth/sign-up";
    }

    @PostMapping("sign-up")
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "auth/sign-up";
        }

        userService.registerUser(user.getLogin(), user.getPassword());
        return "redirect:/sign-in?success=registration";
    }

    @GetMapping("/sign-in")
    public String showLoginForm(Model model,
                                @RequestParam(value = "success", required = false) String success,
                                @RequestParam(value = "auth_required", required = false) String authRequired,
                                @RequestParam(value = "session_expired", required = false) String sessionExpired,
                                @RequestParam(value = "invalid_session", required = false) String invalidSession,
                                @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("error", null);
        model.addAttribute("success", null);

        if (success != null && success.equals("registration"))
            model.addAttribute("successMessage", "Registration successful! Please log in.");
        if (authRequired != null)
            model.addAttribute("error", "Please log in to access this page.");
        if (sessionExpired != null)
            model.addAttribute("error", "Your sessions has expired. Please log in again.");
        if (invalidSession != null)
            model.addAttribute("error", "Invalid session. Please log in again.");
        if (logout != null)
            model.addAttribute("successMessage", "You have been successfully logged out");

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
        sessionCookie.setMaxAge(AuthInterceptor.SESSION_EXPIRATION_SECONDS);

        response.addCookie(sessionCookie);
        return "redirect:/";
    }

}
