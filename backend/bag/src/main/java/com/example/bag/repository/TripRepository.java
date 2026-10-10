package com.example.bag.repository;

import com.example.bag.model.Trip;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TripRepository {
    private final Map<String, Trip> database = new ConcurrentHashMap<>();

    public List<Trip> findAll() {
        return new ArrayList<>(database.values());
    }

    public Optional<Trip> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    public Trip save(Trip trip) {
        if (trip.getId() == null || trip.getId().trim().isEmpty()) {
            trip.setId(UUID.randomUUID().toString());
        }
        database.put(trip.getId(), trip);
        return trip;
    }

    public boolean existsById(String id) {
        return database.containsKey(id);
    }

    public void deleteById(String id) {
        database.remove(id);
    }
}