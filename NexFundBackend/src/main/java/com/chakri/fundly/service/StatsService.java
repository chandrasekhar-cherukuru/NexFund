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
            if (statsCache == null) {
                if (!statsRepo.existsById(STATS_ID)) {
                    Stats stats = new Stats();
                    stats.setId(STATS_ID);
                    stats.setEventCount(0L);
                    stats.setDonationCount(0L);
                    stats.setGiftPoolCount(0L);
                    statsCache = statsRepo.save(stats);
                    System.out.println("✅ Stats initialized");
                } else {
                    // Throws if stats does not exist despite existsById true, to catch inconsistent DB state
                    statsCache = statsRepo.findById(STATS_ID).orElseThrow(() ->
                            new IllegalStateException("Stats entity missing after existsById check"));
                    System.out.println("✅ Stats loaded from database");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing stats: " + e.getMessage());
            // Fallback to always having in-memory default stats
            statsCache = new Stats();
            statsCache.setId(STATS_ID);
            statsCache.setEventCount(0L);
            statsCache.setDonationCount(0L);
            statsCache.setGiftPoolCount(0L);
        }
    }

    public synchronized Stats getStats() {
        try {
            if (statsCache == null) {
                Stats stats = statsRepo.findById(STATS_ID).orElse(null);
                if (stats != null) {
                    statsCache = stats;
                    return statsCache;
                } else {
                    System.out.println("⚠️ Stats missing in DB, initializing new stats");
                    statsCache = new Stats();
                    statsCache.setId(STATS_ID);
                    statsCache.setEventCount(0L);
                    statsCache.setDonationCount(0L);
                    statsCache.setGiftPoolCount(0L);
                    return statsCache;
                }
            }
            return statsCache;
        } catch (Exception e) {
            System.err.println("❌ Error fetching stats: " + e.getMessage());
            if (statsCache == null) {
                statsCache = new Stats();
                statsCache.setId(STATS_ID);
                statsCache.setEventCount(0L);
                statsCache.setDonationCount(0L);
                statsCache.setGiftPoolCount(0L);
            }
            return statsCache;
        }
    }

    public synchronized void incrementEventCount() {
        try {
            Stats stats = getStats();
            if (stats != null) {
                // Safe increment with overflow handling optional
                stats.setEventCount(stats.getEventCount() + 1);
                statsCache = statsRepo.save(stats);
                System.out.println("✅ Event count incremented: " + stats.getEventCount());
            }
        } catch (Exception e) {
            System.err.println("❌ Error incrementing event count: " + e.getMessage());
        }
    }

    public synchronized void incrementDonationCount() {
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

    public synchronized void incrementGiftPoolCount() {
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
