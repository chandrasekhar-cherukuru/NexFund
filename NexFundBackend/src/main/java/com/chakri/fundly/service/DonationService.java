package com.chakri.fundly.service;

import com.chakri.fundly.model.Donation;
import com.chakri.fundly.repo.DonationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private final DonationRepo donationRepo;

    @Autowired
    private StatsService statsService;

    public DonationService(DonationRepo donationRepo) {
        this.donationRepo = donationRepo;
    }

    public Donation createDonation(Donation donation) {
        Donation savedDonation = donationRepo.save(donation);
        statsService.incrementDonationCount(); // ✅ Increment on create
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
