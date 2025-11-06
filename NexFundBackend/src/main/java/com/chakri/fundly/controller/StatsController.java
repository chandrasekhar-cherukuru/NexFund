package com.chakri.fundly.controller;

import com.chakri.fundly.model.Stats;
import com.chakri.fundly.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            return ResponseEntity.ok(Map.of(
                    "events", stats.getEventCount(),
                    "donations", stats.getDonationCount(),
                    "giftPools", stats.getGiftPoolCount()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
