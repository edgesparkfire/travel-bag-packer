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
        String cityForWeather = (trip.getDestinationCity() != null && !trip.getDestinationCity().trim().isEmpty())
                ? trip.getDestinationCity()
                : trip.getDestination();

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
        String destDisplay = (trip.getDestinationCity() != null ? trip.getDestinationCity() : trip.getDestination());
        String originDisplay = (trip.getOriginCity() != null ? trip.getOriginCity() : "your departure city");

        String welcomeMsg = "Welcome to your journey from " + originDisplay + " to " + destDisplay + "! " +
                "I have organized your clothing, essentials, and transport equipment. " +
                "Feel free to ask me anything about local cultural etiquette, train standards, voltages, or packing hacks!";

        chat.add(ChatMessage.builder()
                .sender("gemini")
                .text(welcomeMsg)
                .timestamp(timeNow)
                .build());

        trip.setAiChatHistory(chat);

        Trip savedTrip = tripRepository.save(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
    }

    // PATCH /api/trips/{id}/tier - Recalculate packing list by budget tier
    @PatchMapping("/{id}/tier")
    public ResponseEntity<Trip> changeBudgetTier(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        if (optionalTrip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip trip = optionalTrip.get();
        String tier = request.getOrDefault("budgetTier", "BALANCED").toUpperCase();
        trip.setBudgetTier(tier);

        String cityForWeather = (trip.getDestinationCity() != null && !trip.getDestinationCity().trim().isEmpty())
                ? trip.getDestinationCity()
                : trip.getDestination();

        WeatherForecast forecast = weatherService.getForecast(cityForWeather);
        List<Item> newPackingList = packingRulesEngine.generatePackingList(trip, forecast);
        trip.setPackingList(newPackingList);

        Trip updatedTrip = tripRepository.save(trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @PostMapping("/{id}/chat")
    public ResponseEntity<Map<String, Object>> chatWithGemini(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        if (optionalTrip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip trip = optionalTrip.get();
        String userMessage = request.getOrDefault("message", "").trim();
        if (userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        if (trip.getAiChatHistory() == null) {
            trip.setAiChatHistory(new ArrayList<>());
        }

        trip.getAiChatHistory().add(ChatMessage.builder()
                .sender("user")
                .text(userMessage)
                .timestamp(timeNow)
                .build());

        String aiReply = geminiAiService.generateChatAnswer(trip, userMessage);

        trip.getAiChatHistory().add(ChatMessage.builder()
                .sender("gemini")
                .text(aiReply)
                .timestamp(timeNow)
                .build());

        Trip updatedTrip = tripRepository.save(trip);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("reply", aiReply);
        response.put("chatHistory", updatedTrip.getAiChatHistory());
        response.put("tripId", id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Trip> toggleTripCompleted(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> body) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        if (optionalTrip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip trip = optionalTrip.get();
        if (body != null && body.containsKey("isCompleted")) {
            trip.setIsCompleted(Boolean.valueOf(String.valueOf(body.get("isCompleted"))));
        } else {
            trip.setIsCompleted(!Boolean.TRUE.equals(trip.getIsCompleted()));
        }

        Trip updatedTrip = tripRepository.save(trip);
        return ResponseEntity.ok(updatedTrip);
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
