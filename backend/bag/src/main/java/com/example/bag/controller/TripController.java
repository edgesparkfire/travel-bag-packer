package com.example.bag.controller;

import com.example.bag.model.Item;
import com.example.bag.model.Trip;
import com.example.bag.model.WeatherForecast;
import com.example.bag.repository.TripRepository;
import com.example.bag.service.PackingRulesEngine;
import com.example.bag.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    private final TripRepository tripRepository;
    private final WeatherService weatherService;
    private final PackingRulesEngine packingRulesEngine;

    @Autowired
    public TripController(TripRepository tripRepository,
                          WeatherService weatherService,
                          PackingRulesEngine packingRulesEngine) {
        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
        this.packingRulesEngine = packingRulesEngine;
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable String id) {
        return tripRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        WeatherForecast forecast = weatherService.getForecast(trip.getDestination());
        List<Item> generatedPackingList = packingRulesEngine.generatePackingList(trip, forecast);
        trip.setPackingList(generatedPackingList);
        Trip savedTrip = tripRepository.save(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<Trip> togglePackedStatus(
            @PathVariable String id,
            @PathVariable String itemId,
            @RequestBody(required = false) Map<String, Object> body) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        if (optionalTrip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip trip = optionalTrip.get();
        boolean itemFound = false;

        if (trip.getPackingList() != null) {
            for (Item item : trip.getPackingList()) {
                if (itemId.equals(item.getItemId())) {
                    itemFound = true;
                    if (body != null && body.containsKey("isPacked")) {
                        item.setIsPacked(Boolean.valueOf(String.valueOf(body.get("isPacked"))));
                    } else {
                        item.setIsPacked(!Boolean.TRUE.equals(item.getIsPacked()));
                    }
                    break;
                }
            }
        }

        if (!itemFound) {
            return ResponseEntity.notFound().build();
        }

        Trip updatedTrip = tripRepository.save(trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable String id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}