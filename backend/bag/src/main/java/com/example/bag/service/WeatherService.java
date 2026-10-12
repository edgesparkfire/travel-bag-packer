package com.example.bag.service;

import com.example.bag.model.WeatherForecast;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2500);
        factory.setReadTimeout(2500);
        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    public WeatherForecast getForecast(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            return getDefaultForecast("Your Destination");
        }

        String cleanDest = destination.trim().split(",")[0].trim().replace(" ", "+");
        String url = "https://wttr.in/" + cleanDest + "?format=j1";

        try {
            logger.info("Fetching weather forecast for destination: {} via RestTemplate from {}", destination, url);
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode currentCondition = root.path("current_condition").get(0);

                if (currentCondition != null && !currentCondition.isMissingNode()) {
                    double temp = currentCondition.path("temp_C").asDouble(22.0);
                    String conditionDesc = "Clear";
                    JsonNode descNode = currentCondition.path("weatherDesc").get(0);
                    if (descNode != null) {
                        conditionDesc = descNode.path("value").asText("Clear");
                    }

                    boolean isRain = conditionDesc.toLowerCase().contains("rain") ||
                            conditionDesc.toLowerCase().contains("drizzle") ||
                            conditionDesc.toLowerCase().contains("shower");

                    return WeatherForecast.builder()
                            .destination(destination)
                            .temperature(temp)
                            .condition(conditionDesc)
                            .isRain(isRain)
                            .build();
                }
            }
        } catch (Exception e) {
            logger.warn("Weather fetch fallback for '{}': {}. Using realistic regional fallback.", destination, e.getMessage());
        }

        return getDefaultForecast(destination);
    }

    private WeatherForecast getDefaultForecast(String destination) {
        String lower = destination.toLowerCase();
        double temp = 24.0;
        String condition = "Pleasant & Clear";
        boolean isRain = false;

        if (lower.contains("london") || lower.contains("paris") || lower.contains("uk")) {
            temp = 14.0;
            condition = "Overcast & Cool";
            isRain = true;
        } else if (lower.contains("hyderabad") || lower.contains("mumbai") || lower.contains("delhi") || lower.contains("india")) {
            temp = 28.0;
            condition = "Warm & Sunny";
            isRain = false;
        } else if (lower.contains("tokyo") || lower.contains("japan")) {
            temp = 18.0;
            condition = "Mild & Clear";
            isRain = false;
        }

        return WeatherForecast.builder()
                .destination(destination)
                .temperature(temp)
                .condition(condition)
                .isRain(isRain)
                .build();
    }
}
