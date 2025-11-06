package com.chakri.fundly.service;

import com.chakri.fundly.model.Stats;
import com.chakri.fundly.repo.StatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class StatsService {

    @Autowired
    private StatsRepo statsRepo;

    private static final Long STATS_ID = 1L;
    private Stats statsCache;

    @PostConstruct
    public void init() {
        try {
            // Check if stats exists, if not create it
            if (!statsRepo.existsById(STATS_ID)) {
                Stats stats = new Stats();
                stats.setId(STATS_ID);
                stats.setEventCount(0L);
                stats.setDonationCount(0L);
                stats.setGiftPoolCount(0L);
                statsCache = statsRepo.save(stats);
                System.out.println("✅ Stats initialized");
            } else {
                // Load existing stats
                statsCache = statsRepo.findById(STATS_ID).orElse(null);
                System.out.println("✅ Stats loaded from database");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing stats: " + e.getMessage());
            // Create default stats in memory if database fails
            statsCache = new Stats();
            statsCache.setId(STATS_ID);
            statsCache.setEventCount(0L);
            statsCache.setDonationCount(0L);
            statsCache.setGiftPoolCount(0L);
        }
    }

    public Stats getStats() {
        try {
            Stats stats = statsRepo.findById(STATS_ID).orElse(null);
            if (stats != null) {
                statsCache = stats;
                return stats;
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching stats: " + e.getMessage());
        }

        // Return cached or create new
        if (statsCache == null) {
            statsCache = new Stats();
            statsCache.setId(STATS_ID);
        }
        return statsCache;
    }

    public void incrementEventCount() {
        try {
            Stats stats = getStats();
            if (stats != null) {
                stats.setEventCount(stats.getEventCount() + 1);
                statsCache = statsRepo.save(stats);
                System.out.println("✅ Event count incremented: " + stats.getEventCount());
            }
        } catch (Exception e) {
            System.err.println("❌ Error incrementing event count: " + e.getMessage());
        }
    }

    public void incrementDonationCount() {
        try {
            Stats stats = getStats();
            if (stats != null) {
                stats.setDonationCount(stats.getDonationCount() + 1);
                statsCache = statsRepo.save(stats);
                System.out.println("✅ Donation count incremented: " + stats.getDonationCount());
            }
        } catch (Exception e) {
            System.err.println("❌ Error incrementing donation count: " + e.getMessage());
        }
    }

    public void incrementGiftPoolCount() {
        try {
            Stats stats = getStats();
            if (stats != null) {
                stats.setGiftPoolCount(stats.getGiftPoolCount() + 1);
                statsCache = statsRepo.save(stats);
                System.out.println("✅ Gift Pool count incremented: " + stats.getGiftPoolCount());
            }
        } catch (Exception e) {
            System.err.println("❌ Error incrementing gift pool count: " + e.getMessage());
        }
    }
}
