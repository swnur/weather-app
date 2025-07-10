package com.swnur.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainWeatherDetails {

    private double temp;

    @JsonProperty("feels_like")
    private double feelsLike;

    private int humidity;

}
