package com.example.bag.service;

import com.example.bag.model.Item;
import com.example.bag.model.Trip;
import com.example.bag.model.WeatherForecast;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PackingRulesEngine {

    public List<Item> generatePackingList(Trip trip, WeatherForecast forecast) {
        int duration = calculateDurationDays(trip);
        trip.setDurationDays(duration);

        List<Item> packingList = new ArrayList<>();

        // Duration Calculation: durationDays * basic essentials
        packingList.add(createItem("Shirts", duration, "Clothing"));
        packingList.add(createItem("Socks", duration, "Clothing"));
        packingList.add(createItem("Underwear", duration, "Clothing"));
        packingList.add(createItem("Pants", Math.max(1, (duration / 2) + 1), "Clothing"));

        // Essentials
        packingList.add(createItem("Toothbrush & Toothpaste", 1, "Essentials"));
        packingList.add(createItem("Phone Charger & Adapter", 1, "Essentials"));
        packingList.add(createItem("Passport / ID & Travel Docs", 1, "Essentials"));
        packingList.add(createItem("Reusable Water Bottle", 1, "Essentials"));

        // Conditional Appending:
        // If forecast lists Rain, add Umbrella and Rain Jacket
        if (forecast != null && (forecast.isRain() || (forecast.getCondition() != null && forecast.getCondition().toLowerCase().contains("rain")))) {
            packingList.add(createItem("Umbrella", 1, "Weather Gear"));
            packingList.add(createItem("Rain Jacket", 1, "Weather Gear"));
        }

        // If temperature is <15C, add Hoodie and Jacket
        if (forecast != null && forecast.getTemperature() < 15.0) {
            packingList.add(createItem("Hoodie", 1, "Weather Gear"));
            packingList.add(createItem("Jacket", 1, "Weather Gear"));
        }

        return packingList;
    }

    private int calculateDurationDays(Trip trip) {
        if (trip.getDurationDays() != null && trip.getDurationDays() > 0) {
            return trip.getDurationDays();
        }

        if (trip.getStartDate() != null && trip.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate());
            return Math.max(1, (int) days + 1);
        }

        return 3;
    }

    private Item createItem(String name, int quantity, String category) {
        return Item.builder()
                .itemId(UUID.randomUUID().toString())
                .itemName(name)
                .quantity(quantity)
                .isPacked(false)
                .category(category)
                .build();
    }
}