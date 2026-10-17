package com.example.bag.controller;

import com.example.bag.model.CityGuide;
import com.example.bag.service.CityGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/city-guide")
@CrossOrigin(originPatterns = {"https://*.onrender.com*", "https://*.vercel.app*", "http://localhost:*", "http://127.0.0.1:*"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
public class CityGuideController {

    private final CityGuideService cityGuideService;

    @Autowired
    public CityGuideController(CityGuideService cityGuideService) {
        this.cityGuideService = cityGuideService;
    }

    @GetMapping
    public ResponseEntity<CityGuide> getGuide(@RequestParam(value = "city", required = false, defaultValue = "") String city) {
        CityGuide guide = cityGuideService.getGuideForCity(city);
        return ResponseEntity.ok(guide);
    }
}