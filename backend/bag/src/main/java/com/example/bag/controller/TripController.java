package com.example.bag.controller;

import com.example.bag.model.ChatMessage;
import com.example.bag.model.Item;
import com.example.bag.model.Trip;
import com.example.bag.model.WeatherForecast;
import com.example.bag.repository.TripRepository;
import com.example.bag.service.GeminiAiService;
import com.example.bag.service.PackingRulesEngine;
import com.example.bag.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TripController {

    private final TripRepository tripRepository;
    private final WeatherService weatherService;
    private final PackingRulesEngine packingRulesEngine;
    private final GeminiAiService geminiAiService;

    @Autowired
    public TripController(TripRepository tripRepository,
                          WeatherService weatherService,
                          PackingRulesEngine packingRulesEngine,
                          GeminiAiService geminiAiService) {
        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
        this.packingRulesEngine = packingRulesEngine;
        this.geminiAiService = geminiAiService;
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
        String cityForWeather;
        if (trip.getDestination() != null && !trip.getDestination().trim().isEmpty()) {
            cityForWeather = trip.getDestination();
        } else {
            String destCity = (trip.getDestinationCity() != null ? trip.getDestinationCity() : "");
            String destCountry = (trip.getDestinationCountry() != null ? trip.getDestinationCountry() : "");
            cityForWeather = (!destCountry.isEmpty()) ? (destCity + ", " + destCountry) : destCity;
        }

        WeatherForecast forecast = weatherService.getForecast(cityForWeather);
        if (forecast != null) {
            trip.setWeatherSummary(forecast.getCondition() + " (" + forecast.getTemperature() + "°C)");
        }

        if (trip.getBudgetTier() == null || trip.getBudgetTier().trim().isEmpty()) {
            trip.setBudgetTier("BALANCED");
        }

        List<Item> generatedPackingList = packingRulesEngine.generatePackingList(trip, forecast);
        trip.setPackingList(generatedPackingList);
        trip.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        trip.setIsCompleted(false);

        List<ChatMessage> chat = new ArrayList<>();
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String destDisplay = (trip.getDestination() != null && !trip.getDestination().trim().isEmpty())
                ? trip.getDestination()
                : (trip.getDestinationCity() != null ? trip.getDestinationCity() : "your destination");
        String originDisplay = (trip.getOriginCity() != null && !trip.getOriginCity().trim().isEmpty())
                ? trip.getOriginCity()
                : "your departure location";

        String welcomeMsg = "Welcome to your journey from " + originDisplay + " to " + destDisplay + "! " +
                "I have organized your clothing, essentials, and transport equipment. " +
                "Feel free to ask me anything about local cultural etiquette, transit guidelines, power sockets, or packing hacks!";

        chat.add(ChatMessage.builder()
                .sender("gemini")
                .text(welcomeMsg)
                .timestamp(timeNow)
                .build());

        trip.setAiChatHistory(chat);

        Trip savedTrip = tripRepository.save(trip);
        return new ResponseEntity<>(savedTrip, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/tier")
    public ResponseEntity<Trip> changeBudgetTier(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String newTier = payload.get("budgetTier");
        if (newTier == null || newTier.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return tripRepository.findById(id).map(trip -> {
            trip.setBudgetTier(newTier.toUpperCase());
            String cityForWeather = (trip.getDestination() != null && !trip.getDestination().trim().isEmpty())
                    ? trip.getDestination()
                    : trip.getDestinationCity();
            WeatherForecast forecast = weatherService.getForecast(cityForWeather);
            List<Item> regeneratedList = packingRulesEngine.generatePackingList(trip, forecast);

            // Preserve already packed items if names match
            Map<String, Boolean> previousPacked = new HashMap<>();
            if (trip.getPackingList() != null) {
                for (Item it : trip.getPackingList()) {
                    if (it.getIsPacked() != null && it.getIsPacked()) {
                        previousPacked.put(it.getItemName().toLowerCase(), true);
                    }
                }
            }

            for (Item newItem : regeneratedList) {
                if (previousPacked.containsKey(newItem.getItemName().toLowerCase())) {
                    newItem.setIsPacked(true);
                }
            }

            trip.setPackingList(regeneratedList);
            Trip updated = tripRepository.save(trip);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<Trip> toggleItemPacked(@PathVariable String id,
                                                 @PathVariable String itemId,
                                                 @RequestBody(required = false) Map<String, Boolean> payload) {
        return tripRepository.findById(id).map(trip -> {
            Boolean newStatus = null;
            if (payload != null && payload.containsKey("isPacked")) {
                newStatus = payload.get("isPacked");
            }

            if (trip.getPackingList() != null) {
                for (Item item : trip.getPackingList()) {
                    if (item.getItemId().equals(itemId)) {
                        item.setIsPacked(newStatus != null ? newStatus : !Boolean.TRUE.equals(item.getIsPacked()));
                        break;
                    }
                }
            }
            Trip updated = tripRepository.save(trip);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Trip> toggleTripCompleted(@PathVariable String id,
                                                    @RequestBody(required = false) Map<String, Boolean> payload) {
        return tripRepository.findById(id).map(trip -> {
            Boolean newStatus = null;
            if (payload != null && payload.containsKey("isCompleted")) {
                newStatus = payload.get("isCompleted");
            }
            trip.setIsCompleted(newStatus != null ? newStatus : !Boolean.TRUE.equals(trip.getIsCompleted()));
            Trip updated = tripRepository.save(trip);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/chat")
    public ResponseEntity<Trip> chatWithGemini(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return tripRepository.findById(id).map(trip -> {
            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            if (trip.getAiChatHistory() == null) {
                trip.setAiChatHistory(new ArrayList<>());
            }

            trip.getAiChatHistory().add(ChatMessage.builder()
                    .sender("user")
                    .text(userMessage)
                    .timestamp(timeNow)
                    .build());

            String aiResponse = geminiAiService.generateChatAnswer(trip, userMessage);
            trip.getAiChatHistory().add(ChatMessage.builder()
                    .sender("gemini")
                    .text(aiResponse)
                    .timestamp(timeNow)
                    .build());

            Trip updated = tripRepository.save(trip);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
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
