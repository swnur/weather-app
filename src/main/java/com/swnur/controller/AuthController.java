package com.swnur.controller;

import com.swnur.model.User;
import com.swnur.service.UserService;
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

        try {
            userService.registerUser(user.getLogin(), user.getPassword());

            return "redirect:/sign-in?success=registration";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);

            return "auth/sign-up";
        }
    }

    @GetMapping("/sign-in")
    public String showLoginForm(Model model,
                                @RequestParam(value = "success", required = false) String success) {
        model.addAttribute("error", null);
        if (success != null && success.equals("registration"))
            model.addAttribute("successMessage", "Registration successful! Please log in.");
        return "auth/sign-in";
    }

    @PostMapping("/sign-in")
    public String loginUser(@RequestParam("login") String login,
                            @RequestParam("password") String password,
                            Model model) {
        try {
            User authenticatedUser = userService.authenticateUser(login, password);

            return "redirect:/?loggedInUser=" + authenticatedUser.getLogin();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());

            return "auth/sign-in";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:auth/sign-in";
    }

    @GetMapping("/")
    public String showMainPage() {
        return "index";
    }
}
