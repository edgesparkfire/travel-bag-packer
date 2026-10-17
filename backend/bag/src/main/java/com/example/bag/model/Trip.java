package com.example.bag.model;

import com.example.bag.converter.ChatMessageListConverter;
import com.example.bag.converter.ItemListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "origin_city")
    private String originCity;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "destination_city")
    private String destinationCity;

    @Column(name = "destination")
    private String destination; // Combined e.g. "Hyderabad, India" or "Hyderabad, Pakistan"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "transport_mode")
    private String transportMode; // "TRAIN", "FLIGHT", "CAR", "BUS"

    @Column(name = "travel_style")
    private String travelStyle;   // "Sightseeing", "Casual", "Business", "Adventure"

    @Column(name = "max_weight_limit")
    private Double maxWeightLimit;

    @Column(name = "weather_summary", columnDefinition = "TEXT")
    private String weatherSummary;

    @Column(name = "created_at")
    private String createdAt;
    
    @Builder.Default
    @Column(name = "budget_tier")
    private String budgetTier = "BALANCED"; // "BUDGET", "BALANCED", "LUXURY"

    @Builder.Default
    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Builder.Default
    @Column(name = "packing_list", columnDefinition = "TEXT")
    @Convert(converter = ItemListConverter.class)
    private List<Item> packingList = new ArrayList<>();

    @Builder.Default
    @Column(name = "ai_chat_history", columnDefinition = "TEXT")
    @Convert(converter = ChatMessageListConverter.class)
    private List<ChatMessage> aiChatHistory = new ArrayList<>();

    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}

