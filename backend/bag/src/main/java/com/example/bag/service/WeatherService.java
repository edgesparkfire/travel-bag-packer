package com.example.bag.service;

import com.example.bag.model.WeatherForecast;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WeatherForecast getForecast(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            return defaultForecast("Clear", 20.0, false);
        }

        try {
            String encodedDestination = URLEncoder.encode(destination.trim(), StandardCharsets.UTF_8);
            String url = "https://wttr.in/" + encodedDestination + "?format=j1";

            logger.info("Fetching weather forecast for destination: {} via RestTemplate from {}", destination, url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode currentCondition = root.path("current_condition");
                if (currentCondition.isArray() && currentCondition.size() > 0) {
                    JsonNode current = currentCondition.get(0);
                    double temp = current.path("temp_C").asDouble(20.0);

                    String condition = "Clear";
                    JsonNode weatherDesc = current.path("weatherDesc");
                    if (weatherDesc.isArray() && weatherDesc.size() > 0) {
                        condition = weatherDesc.get(0).path("value").asText("Clear").trim();
                    }

                    boolean isRain = condition.toLowerCase().contains("rain")
                            || condition.toLowerCase().contains("drizzle")
                            || condition.toLowerCase().contains("shower");

                    JsonNode weatherDays = root.path("weather");
                    if (!isRain && weatherDays.isArray()) {
                        for (JsonNode day : weatherDays) {
                            JsonNode hourly = day.path("hourly");
                            if (hourly.isArray()) {
                                for (JsonNode hour : hourly) {
                                    JsonNode hourDesc = hour.path("weatherDesc");
                                    if (hourDesc.isArray() && hourDesc.size() > 0) {
                                        String val = hourDesc.get(0).path("value").asText("");
                                        if (val.toLowerCase().contains("rain") || val.toLowerCase().contains("drizzle")) {
                                            isRain = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (isRain) break;
                        }
                    }

                    logger.info("Weather parsed successfully for {}: temp={}C, condition={}, isRain={}",
                            destination, temp, condition, isRain);

                    return WeatherForecast.builder()
                            .temperature(temp)
                            .condition(condition)
                            .rain(isRain)
                            .description(condition + " (" + temp + "C)")
                            .build();
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to retrieve live weather for destination '{}' via RestTemplate: {}. Using fallback forecast.", destination, e.getMessage());
        }

        return defaultForecast("Clear", 20.0, false);
    }

    private WeatherForecast defaultForecast(String condition, double temp, boolean rain) {
        return WeatherForecast.builder()
                .temperature(temp)
                .condition(condition)
                .rain(rain)
                .description(condition + " (" + temp + "C, default)")
                .build();
    }
}