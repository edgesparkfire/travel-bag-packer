package com.example.bag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityGuide {
    private String city;
    private String country;
    private String generalSummary;
    private String knowledgeSource;
    private String bestTimeToVisit;
    private String voltageAndSocket;
    private Map<String, String> emergencyContacts;
    private List<String> transportAdvice;
    private List<String> rideApps;
    private List<String> culturalEtiquette;
    private String tapWaterStatus;
    private String tippingCulture;
    private Map<String, String> commonPhrases;
    private List<String> mustTryDishes;
}