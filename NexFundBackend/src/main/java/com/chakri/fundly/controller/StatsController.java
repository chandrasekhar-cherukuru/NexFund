package com.chakri.fundly.controller;

import com.chakri.fundly.model.Stats;
import com.chakri.fundly.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Stats stats = statsService.getStats();

            Map<String, Object> response = new HashMap<>();
            response.put("events", stats.getEventCount());
            response.put("donations", stats.getDonationCount());
            response.put("giftPools", stats.getGiftPoolCount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            String errorMessage = e.getMessage() != null ? e.getMessage() : "An error occurred";
            errorResponse.put("error", errorMessage);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
