package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {

    private MainWeatherDetails main;
    private List<WeatherCondition> weather;
    private String name;
    private SysDetails sys;

}
