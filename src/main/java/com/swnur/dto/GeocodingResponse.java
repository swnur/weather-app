package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResponse {

    private String name;
    private BigDecimal lat;
    private BigDecimal lon;
    private String country;
    private String state;

}
