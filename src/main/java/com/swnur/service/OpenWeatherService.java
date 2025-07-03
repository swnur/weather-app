package com.swnur.service;

import com.swnur.dto.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    private final RestTemplate restTemplate;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final String GEOCODING_BASE_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static final int LIMIT = 10;

    public List<GeocodingResponse> searchLocations(String cityName, int limit) {
        String url = UriComponentsBuilder.fromUriString(GEOCODING_BASE_URL)
                .queryParam("q", cityName)
                .queryParam("limit", limit)
                .queryParam("appid", apiKey)
                .toUriString();

        System.out.println("OpenWeatherService: Calling Geocoding API: " + url);

        try {
            ResponseEntity<List<GeocodingResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GeocodingResponse>>() {}
            );

            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error calling OpenWeatherMap GeoCoding API for city: " + cityName);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
