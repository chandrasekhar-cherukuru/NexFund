package com.chakri.fundly.service;

import com.chakri.fundly.model.Donation;
import com.chakri.fundly.repo.DonationRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private final DonationRepo donationRepo;
    private final StatsService statsService;

    public DonationService(DonationRepo donationRepo, StatsService statsService) {
        this.donationRepo = donationRepo;
        this.statsService = statsService;
    }

    public Donation createDonation(Donation donation) {
        Donation savedDonation = donationRepo.save(donation);
        statsService.incrementDonationCount();
        System.out.println("✅ Donation created and stats incremented");
        return savedDonation;
    }

    public List<Donation> getAllDonations() {
        return donationRepo.findAll();
    }

    public Optional<Donation> getDonationById(String id) {
        return donationRepo.findById(id);
    }

    public void deleteDonation(String id) {
        donationRepo.deleteById(id);
    }
}
