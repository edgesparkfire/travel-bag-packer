package com.example.bag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    private String id;
    private String originCity;
    private String destinationCountry;
    private String destinationCity;
    private String destination; // Combined e.g. "Hyderabad, India"
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private String transportMode; // "TRAIN", "FLIGHT", "CAR", "BUS"
    private String travelStyle;   // "Sightseeing", "Casual", "Business", "Adventure"
    private Double maxWeightLimit;
    private String weatherSummary;
    private String createdAt;
    
    @Builder.Default
    private String budgetTier = "BALANCED"; // "BUDGET", "BALANCED", "LUXURY"

    @Builder.Default
    private Boolean isCompleted = false;

    @Builder.Default
    private List<Item> packingList = new ArrayList<>();

    @Builder.Default
    private List<ChatMessage> aiChatHistory = new ArrayList<>();
}
