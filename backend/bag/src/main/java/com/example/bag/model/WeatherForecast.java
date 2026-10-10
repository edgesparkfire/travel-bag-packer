package com.example.bag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecast {
    private double temperature;
    private String condition;
    private boolean rain;
    private String description;
}