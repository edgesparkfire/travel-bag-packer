package com.example.bag.controller;

import com.example.bag.service.GeminiAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(originPatterns = {"https://*.onrender.com*", "https://*.vercel.app*", "http://localhost:*", "http://127.0.0.1:*"}, allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class AiAdvisorController {

    private final GeminiAiService geminiAiService;

    @Autowired
    public AiAdvisorController(GeminiAiService geminiAiService) {
        this.geminiAiService = geminiAiService;
    }

    @PostMapping("/tips")
    public ResponseEntity<Map<String, Object>> getTips(@RequestBody Map<String, Object> request) {
        String originCity = (String) request.getOrDefault("origin", "New York");
        String destination = (String) request.getOrDefault("destination", "Hyderabad");
        int durationDays = 4;
        try {
            if (request.containsKey("durationDays")) {
                durationDays = Integer.parseInt(String.valueOf(request.get("durationDays")));
            }
        } catch (Exception ignored) {}
        String transportMode = (String) request.getOrDefault("transportMode", "TRAIN");

        Map<String, Object> tips = geminiAiService.getAiTravelRecommendations(originCity, destination, durationDays, transportMode);
        return ResponseEntity.ok(tips);
    }
}
