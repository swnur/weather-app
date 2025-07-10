package com.swnur.controller;

import com.swnur.dto.GeocodingResponse;
import com.swnur.dto.WeatherResponse;
import com.swnur.intercepter.AuthInterceptor;
import com.swnur.model.User;
import com.swnur.service.LocationService;
import com.swnur.service.OpenWeatherService;
import com.swnur.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class AppController {

    private static final Logger log = LoggerFactory.getLogger(AppController.class);
    private final SessionService sessionService;
    private final OpenWeatherService openWeatherService;
    private final LocationService locationService;

    @GetMapping("/")
    public String showMainPage(@RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE) User authenticatedUser,
                               Model model) {
        Map<Integer, WeatherResponse> weatherForUsersLocations = openWeatherService.getWeatherResponseForUser(authenticatedUser);
        log.debug("Weather data for user {}: {}", authenticatedUser.getLogin(), weatherForUsersLocations);

        model.addAttribute("weatherMap", weatherForUsersLocations);
        model.addAttribute("loggedInUser", authenticatedUser.getLogin());

        return "main/index";
    }

    @GetMapping("/search")
    public String searchResults(@RequestParam("locationName") String locationName,
                                Model model) {
        List<GeocodingResponse> geocodingResponseList = openWeatherService.searchLocations(locationName, OpenWeatherService.LIMIT);
        model.addAttribute("locationName", locationName);
        model.addAttribute("locations", geocodingResponseList);

        return "main/search-results";
    }

    @PostMapping("/locations/add")
    public String addLocation(HttpServletRequest request,
            @RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE) User user,
            @ModelAttribute("location") GeocodingResponse geocodingResponse) {
        locationService.addLocationForUser(geocodingResponse, user.getId());
        log.info("User {} added location: {}", user.getLogin(), geocodingResponse.getName());

        return "redirect:/";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(@RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ATTRIBUTE) User user,
                                 @ModelAttribute("locationId") Integer locationId) {
        locationService.deleteLocationForUser(locationId, user);
        log.info("User {} deleted location with ID: {}", user.getLogin(), locationId);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = AuthInterceptor.SESSION_COOKIE_NAME, required = false) String sessionId,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        if (sessionId != null) {
            sessionService.invalidateSessionByStringId(sessionId);
        }

        Cookie expiredCookie = new Cookie(AuthInterceptor.SESSION_COOKIE_NAME, "");
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);

        redirectAttributes.addFlashAttribute("successMessage", "You have been successfully logged out.");
        return "redirect:/sign-in?logout=true";
    }
}
