package com.chakri.fundly.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventCount = 0L;
    private Long donationCount = 0L;
    private Long giftPoolCount = 0L;

    // Constructor
    public Stats() {
        this.eventCount = 0L;
        this.donationCount = 0L;
        this.giftPoolCount = 0L;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventCount() { return eventCount; }
    public void setEventCount(Long eventCount) { this.eventCount = eventCount; }

    public Long getDonationCount() { return donationCount; }
    public void setDonationCount(Long donationCount) { this.donationCount = donationCount; }

    public Long getGiftPoolCount() { return giftPoolCount; }
    public void setGiftPoolCount(Long giftPoolCount) { this.giftPoolCount = giftPoolCount; }
}
