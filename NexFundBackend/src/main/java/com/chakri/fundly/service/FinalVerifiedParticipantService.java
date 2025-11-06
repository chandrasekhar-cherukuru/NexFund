package com.chakri.fundly.service;

import com.chakri.fundly.model.*;
import com.chakri.fundly.repo.FinalVerifiedParticipantRepo;
import com.chakri.fundly.repo.VerifiedParticipantRepo;
import com.chakri.fundly.repo.EventRepo;
import com.chakri.fundly.repo.GiftRepo;
import com.chakri.fundly.repo.DonationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinalVerifiedParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(FinalVerifiedParticipantService.class);

    @Autowired
    private FinalVerifiedParticipantRepo finalVerifiedRepository;

    @Autowired
    private VerifiedParticipantRepo verifiedParticipantRepository;

    @Autowired
    private DonationRepo donationRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private GiftRepo giftRepo;

    @Autowired
    private EmailService emailService;

    /**
     * Perform final verification of a participant
     */
    public FinalVerifiedParticipant finallyVerifyParticipant(String participantId, String verifiedBy, String notes) {

        logger.info("🔄 Attempting final verification for participant: {} by: {}", participantId, verifiedBy);

        // Check if participant exists
        VerifiedParticipant participant = verifiedParticipantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        logger.info("📋 Participant details - FundraiserId: {}, Type: {}, CreatedBy: {}",
                participant.getFundraiserId(),
                participant.getFundraiserType(),
                participant.getCreatedBy());

        // Get the actual fundraiser creator based on fundraiser type
        String fundraiserCreator = getFundraiserCreator(
                participant.getFundraiserId(),
                participant.getFundraiserType()
        );

        if (fundraiserCreator == null) {
            logger.error("❌ Fundraiser not found - ID: {}, Type: {}",
                    participant.getFundraiserId(),
                    participant.getFundraiserType());
            throw new IllegalArgumentException("Fundraiser not found or creator information missing");
        }

        logger.info("🔍 Comparing - Fundraiser creator: '{}', Verified by: '{}'",
                fundraiserCreator, verifiedBy);

        // Business Logic: Only creator can finally verify (case-insensitive)
        if (!fundraiserCreator.equalsIgnoreCase(verifiedBy)) {
            logger.warn("⚠️ Verification attempt failed. Creator: '{}', Attempted by: '{}'",
                    fundraiserCreator, verifiedBy);
            throw new IllegalArgumentException("You can only verify participants from your own fundraisers");
        }

        // Check if already finally verified
        if (finalVerifiedRepository.existsByParticipantId(participantId)) {
            logger.warn("⚠️ Participant {} is already finally verified", participantId);
            throw new IllegalArgumentException("Participant is already finally verified");
        }

        try {
            // Create final verification record
            FinalVerifiedParticipant finalVerification = new FinalVerifiedParticipant(
                    participantId,
                    verifiedBy,
                    notes
            );

            FinalVerifiedParticipant saved = finalVerifiedRepository.save(finalVerification);

            logger.info("✅ Successfully finally verified participant: {} by: {}", participantId, verifiedBy);

            // 📧 Send verification email
            if (participant.getEmail() != null && !participant.getEmail().trim().isEmpty()) {
                try {
                    emailService.sendVerificationEmail(
                            participant.getEmail(),
                            participant.getParticipantName(),
                            participant.getFundraiserTitle(),
                            verifiedBy,
                            participant.getAmountPaid().toString()
                    );
                    logger.info("📧 Verification email sent to: {}", participant.getEmail());
                } catch (Exception emailError) {
                    logger.error("❌ Email failed but verification successful: {}", emailError.getMessage());
                }
            } else {
                logger.info("ℹ️ No email provided, skipping notification");
            }

            return saved;

        } catch (Exception e) {
            logger.error("❌ Error in final verification for participant {}: {}", participantId, e.getMessage(), e);
            throw new RuntimeException("Failed to perform final verification", e);
        }
    }

    /**
     * Get fundraiser creator based on type and ID
     */
    private String getFundraiserCreator(String fundraiserId, String fundraiserType) {
        if (fundraiserType == null || fundraiserId == null) {
            logger.warn("⚠️ Null fundraiser type or ID");
            return null;
        }

        logger.info("🔍 Looking up fundraiser - ID: {}, Type: {}", fundraiserId, fundraiserType);

        String creator = null;

        switch (fundraiserType.toLowerCase()) {
            case "donation":
                creator = donationRepo.findById(fundraiserId)
                        .map(Donation::getCreatorUsername)
                        .orElse(null);
                logger.info("📌 Donation creator: {}", creator);
                break;

            case "event":
                creator = eventRepo.findById(fundraiserId)
                        .map(Events::getCreatedByUsername)
                        .orElse(null);
                logger.info("📌 Event creator: {}", creator);
                break;

            case "gift":
                creator = giftRepo.findById(fundraiserId)
                        .map(Gift::getCreatorUsername)
                        .orElse(null);
                logger.info("📌 Gift creator: {}", creator);
                break;

            default:
                logger.warn("⚠️ Unknown fundraiser type: {}", fundraiserType);
                return null;
        }

        if (creator == null) {
            logger.error("❌ Fundraiser not found in database - ID: {}, Type: {}",
                    fundraiserId, fundraiserType);
        }

        return creator;
    }

    @Transactional(readOnly = true)
    public boolean isParticipantFinallyVerified(String participantId) {
        return finalVerifiedRepository.existsByParticipantId(participantId);
    }

    @Transactional(readOnly = true)
    public Optional<FinalVerifiedParticipant> getFinalVerification(String participantId) {
        return finalVerifiedRepository.findByParticipantId(participantId);
    }

    @Transactional(readOnly = true)
    public List<FinalVerifiedParticipant> getFinalVerificationsByCreator(String verifiedBy) {
        return finalVerifiedRepository.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);
    }

    @Transactional(readOnly = true)
    public List<FinalVerifiedParticipant> getFinalVerificationsByFundraiser(String fundraiserId) {
        return finalVerifiedRepository.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);
    }

    @Transactional(readOnly = true)
    public Long getFinalVerificationCount(String fundraiserId) {
        return finalVerifiedRepository.countByFundraiserId(fundraiserId);
    }

    public void removeFinalVerification(String participantId, String verifiedBy) {
        FinalVerifiedParticipant finalVerification = finalVerifiedRepository.findByParticipantId(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Final verification not found"));

        if (!finalVerification.getVerifiedBy().equalsIgnoreCase(verifiedBy)) {
            throw new IllegalArgumentException("You can only remove verifications you performed");
        }

        finalVerifiedRepository.delete(finalVerification);
        logger.info("🗑️ Removed final verification for participant: {} by: {}", participantId, verifiedBy);
    }
}
