package com.chakri.fundly.service;

import com.chakri.fundly.model.*;
import com.chakri.fundly.repo.FinalVerifiedParticipantRepo;
import com.chakri.fundly.repo.VerifiedParticipantRepo;
import com.chakri.fundly.repo.EventRepo;
import com.chakri.fundly.repo.GiftRepo;
import com.chakri.fundly.repo.DonationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinalVerifiedParticipantService Test Suite")
class FinalVerifiedParticipantServiceTest {

    @Mock
    private FinalVerifiedParticipantRepo finalVerifiedRepository;

    @Mock
    private VerifiedParticipantRepo verifiedParticipantRepository;

    @Mock
    private DonationRepo donationRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private GiftRepo giftRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private FinalVerifiedParticipantService finalVerifiedParticipantService;

    private VerifiedParticipant testParticipant;
    private FinalVerifiedParticipant testFinalVerification;
    private Donation testDonation;
    private Events testEvent;
    private Gift testGift;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        testParticipant = new VerifiedParticipant();
        testParticipant.setId("participant-123");
        testParticipant.setFundraiserId("fundraiser-456");
        testParticipant.setFundraiserType("donation");
        testParticipant.setCreatedBy("john_doe");
        testParticipant.setParticipantName("Jane Participant");
        testParticipant.setEmail("jane@example.com");
        testParticipant.setFundraiserTitle("Help For Education");
        testParticipant.setAmountPaid(BigDecimal.valueOf(5000));

        testFinalVerification = new FinalVerifiedParticipant(
                "participant-123",
                "john_doe",
                "Verified successfully"
        );
        testFinalVerification.setId(1L);

        testDonation = new Donation();
        testDonation.setId("fundraiser-456");
        testDonation.setCreatorUsername("john_doe");

        testEvent = new Events();
        testEvent.setId("event-789");
        testEvent.setCreatedByUsername("alice_smith");

        testGift = new Gift();
        testGift.setId("gift-012");
        testGift.setCreatorUsername("bob_wilson");
    }

    @Nested
    @DisplayName("finallyVerifyParticipant - Main Verification Method")
    class FinallyVerifyParticipantTests {

        @Test
        @DisplayName("Should successfully verify participant with donation fundraiser")
        void testFinallyVerifyParticipant_SuccessWithDonation() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "Verified successfully");

            // ASSERT
            assertNotNull(result);
            assertEquals("participant-123", result.getParticipantId());
            assertEquals("john_doe", result.getVerifiedBy());
            assertEquals("Verified successfully", result.getNotes());
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
            verify(emailService).sendVerificationEmail(
                    "jane@example.com",
                    "Jane Participant",
                    "Help For Education",
                    "john_doe",
                    "5000"
            );
        }

        @Test
        @DisplayName("Should successfully verify participant with event fundraiser")
        void testFinallyVerifyParticipant_SuccessWithEvent() {
            // ARRANGE
            testParticipant.setFundraiserType("event");
            testParticipant.setFundraiserId("event-789");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(eventRepo.findById("event-789"))
                    .thenReturn(Optional.of(testEvent));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "alice_smith", "Event verified");

            // ASSERT
            assertNotNull(result);
            assertEquals("participant-123", result.getParticipantId());
            verify(eventRepo).findById("event-789");
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
        }

        @Test
        @DisplayName("Should successfully verify participant with gift fundraiser")
        void testFinallyVerifyParticipant_SuccessWithGift() {
            // ARRANGE
            testParticipant.setFundraiserType("gift");
            testParticipant.setFundraiserId("gift-012");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(giftRepo.findById("gift-012"))
                    .thenReturn(Optional.of(testGift));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "bob_wilson", "Gift verified");

            // ASSERT
            assertNotNull(result);
            assertEquals("participant-123", result.getParticipantId());
            verify(giftRepo).findById("gift-012");
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
        }

        @Test
        @DisplayName("Should throw exception when participant not found")
        void testFinallyVerifyParticipant_ParticipantNotFound() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-999"))
                    .thenReturn(Optional.empty());

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-999", "john_doe", "notes"
                    )
            );

            assertEquals("Participant not found", exception.getMessage());
            verify(verifiedParticipantRepository).findById("participant-999");
            verify(finalVerifiedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when fundraiser not found")
        void testFinallyVerifyParticipant_FundraiserNotFound() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.empty());

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Fundraiser not found or creator information missing", exception.getMessage());
            verify(finalVerifiedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when verifier is not fundraiser creator")
        void testFinallyVerifyParticipant_UnauthorizedVerifier() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "unauthorized_user", "notes"
                    )
            );

            assertEquals("You can only verify participants from your own fundraisers",
                    exception.getMessage());
            verify(finalVerifiedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when participant already verified")
        void testFinallyVerifyParticipant_AlreadyVerified() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(true);

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Participant is already finally verified", exception.getMessage());
            verify(finalVerifiedRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle case-insensitive verifier comparison")
        void testFinallyVerifyParticipant_CaseInsensitiveVerifier() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "JOHN_DOE", "notes");

            // ASSERT
            assertNotNull(result);
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
        }

        @Test
        @DisplayName("Should skip email when email is null")
        void testFinallyVerifyParticipant_NullEmail() {
            // ARRANGE
            testParticipant.setEmail(null);

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should skip email when email is empty/blank")
        void testFinallyVerifyParticipant_EmptyEmail() {
            // ARRANGE
            testParticipant.setEmail("   ");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should continue verification even if email service fails")
        void testFinallyVerifyParticipant_EmailServiceFailure() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doThrow(new RuntimeException("Email service error"))
                    .when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            assertEquals("participant-123", result.getParticipantId());
            verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw exception when database save fails")
        void testFinallyVerifyParticipant_SaveFailure() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // ACT & ASSERT
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Failed to perform final verification", exception.getMessage());
        }

        @Test
        @DisplayName("Should verify with null notes")
        void testFinallyVerifyParticipant_NullNotes() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", null);

            // ASSERT
            assertNotNull(result);
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
        }
    }

    @Nested
    @DisplayName("getFundraiserCreator - Helper Method")
    class GetFundraiserCreatorTests {

        @Test
        @DisplayName("Should return creator for donation type")
        void testGetFundraiserCreator_DonationType() {
            // ARRANGE
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            verify(donationRepo).findById("fundraiser-456");
        }

        @Test
        @DisplayName("Should return creator for event type")
        void testGetFundraiserCreator_EventType() {
            // ARRANGE
            testParticipant.setFundraiserType("event");
            testParticipant.setFundraiserId("event-789");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(eventRepo.findById("event-789"))
                    .thenReturn(Optional.of(testEvent));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "alice_smith", "notes");

            // ASSERT
            assertNotNull(result);
            verify(eventRepo).findById("event-789");
        }

        @Test
        @DisplayName("Should return creator for gift type")
        void testGetFundraiserCreator_GiftType() {
            // ARRANGE
            testParticipant.setFundraiserType("gift");
            testParticipant.setFundraiserId("gift-012");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(giftRepo.findById("gift-012"))
                    .thenReturn(Optional.of(testGift));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "bob_wilson", "notes");

            // ASSERT
            assertNotNull(result);
            verify(giftRepo).findById("gift-012");
        }

        @Test
        @DisplayName("Should return null for unknown fundraiser type")
        void testGetFundraiserCreator_UnknownType() {
            // ARRANGE
            testParticipant.setFundraiserType("unknown");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Fundraiser not found or creator information missing", exception.getMessage());
        }

        @Test
        @DisplayName("Should return null when fundraiser ID is null")
        void testGetFundraiserCreator_NullFundraiserId() {
            // ARRANGE
            testParticipant.setFundraiserId(null);

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Fundraiser not found or creator information missing", exception.getMessage());
        }

        @Test
        @DisplayName("Should return null when fundraiser type is null")
        void testGetFundraiserCreator_NullFundraiserType() {
            // ARRANGE
            testParticipant.setFundraiserType(null);

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.finallyVerifyParticipant(
                            "participant-123", "john_doe", "notes"
                    )
            );

            assertEquals("Fundraiser not found or creator information missing", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("isParticipantFinallyVerified - Query Method")
    class IsParticipantFinallyVerifiedTests {

        @Test
        @DisplayName("Should return true when participant is verified")
        void testIsParticipantFinallyVerified_True() {
            // ARRANGE
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(true);

            // ACT
            boolean result = finalVerifiedParticipantService.isParticipantFinallyVerified("participant-123");

            // ASSERT
            assertTrue(result);
            verify(finalVerifiedRepository).existsByParticipantId("participant-123");
        }

        @Test
        @DisplayName("Should return false when participant is not verified")
        void testIsParticipantFinallyVerified_False() {
            // ARRANGE
            when(finalVerifiedRepository.existsByParticipantId("participant-999"))
                    .thenReturn(false);

            // ACT
            boolean result = finalVerifiedParticipantService.isParticipantFinallyVerified("participant-999");

            // ASSERT
            assertFalse(result);
            verify(finalVerifiedRepository).existsByParticipantId("participant-999");
        }
    }

    @Nested
    @DisplayName("getFinalVerification - Single Record Retrieval")
    class GetFinalVerificationTests {

        @Test
        @DisplayName("Should return final verification when found")
        void testGetFinalVerification_Found() {
            // ARRANGE
            when(finalVerifiedRepository.findByParticipantId("participant-123"))
                    .thenReturn(Optional.of(testFinalVerification));

            // ACT
            Optional<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerification("participant-123");

            // ASSERT
            assertTrue(result.isPresent());
            assertEquals("participant-123", result.get().getParticipantId());
            assertEquals("john_doe", result.get().getVerifiedBy());
            verify(finalVerifiedRepository).findByParticipantId("participant-123");
        }

        @Test
        @DisplayName("Should return empty Optional when not found")
        void testGetFinalVerification_NotFound() {
            // ARRANGE
            when(finalVerifiedRepository.findByParticipantId("participant-999"))
                    .thenReturn(Optional.empty());

            // ACT
            Optional<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerification("participant-999");

            // ASSERT
            assertFalse(result.isPresent());
            verify(finalVerifiedRepository).findByParticipantId("participant-999");
        }
    }

    @Nested
    @DisplayName("getFinalVerificationsByCreator - List Retrieval by Creator")
    class GetFinalVerificationsByCreatorTests {

        @Test
        @DisplayName("Should return list of verifications by creator")
        void testGetFinalVerificationsByCreator_MultipleResults() {
            // ARRANGE
            FinalVerifiedParticipant verification1 = new FinalVerifiedParticipant(
                    "participant-1", "john_doe", "notes1"
            );
            FinalVerifiedParticipant verification2 = new FinalVerifiedParticipant(
                    "participant-2", "john_doe", "notes2"
            );
            List<FinalVerifiedParticipant> verificationList = Arrays.asList(verification1, verification2);

            when(finalVerifiedRepository.findByVerifiedByOrderByVerifiedAtDesc("john_doe"))
                    .thenReturn(verificationList);

            // ACT
            List<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerificationsByCreator("john_doe");

            // ASSERT
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("participant-1", result.get(0).getParticipantId());
            assertEquals("participant-2", result.get(1).getParticipantId());
            verify(finalVerifiedRepository).findByVerifiedByOrderByVerifiedAtDesc("john_doe");
        }

        @Test
        @DisplayName("Should return empty list when no verifications found")
        void testGetFinalVerificationsByCreator_EmptyList() {
            // ARRANGE
            when(finalVerifiedRepository.findByVerifiedByOrderByVerifiedAtDesc("unknown_user"))
                    .thenReturn(Arrays.asList());

            // ACT
            List<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerificationsByCreator("unknown_user");

            // ASSERT
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(finalVerifiedRepository).findByVerifiedByOrderByVerifiedAtDesc("unknown_user");
        }
    }

    @Nested
    @DisplayName("getFinalVerificationsByFundraiser - List Retrieval by Fundraiser")
    class GetFinalVerificationsByFundraiserTests {

        @Test
        @DisplayName("Should return list of verifications by fundraiser ID")
        void testGetFinalVerificationsByFundraiser_MultipleResults() {
            // ARRANGE
            FinalVerifiedParticipant verification1 = new FinalVerifiedParticipant(
                    "participant-1", "john_doe", "notes1"
            );
            FinalVerifiedParticipant verification2 = new FinalVerifiedParticipant(
                    "participant-2", "john_doe", "notes2"
            );
            List<FinalVerifiedParticipant> verificationList = Arrays.asList(verification1, verification2);

            when(finalVerifiedRepository.findByFundraiserIdOrderByVerifiedAtDesc("fundraiser-456"))
                    .thenReturn(verificationList);

            // ACT
            List<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerificationsByFundraiser("fundraiser-456");

            // ASSERT
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(finalVerifiedRepository).findByFundraiserIdOrderByVerifiedAtDesc("fundraiser-456");
        }

        @Test
        @DisplayName("Should return empty list when no verifications found for fundraiser")
        void testGetFinalVerificationsByFundraiser_EmptyList() {
            // ARRANGE
            when(finalVerifiedRepository.findByFundraiserIdOrderByVerifiedAtDesc("fundraiser-999"))
                    .thenReturn(Arrays.asList());

            // ACT
            List<FinalVerifiedParticipant> result = finalVerifiedParticipantService
                    .getFinalVerificationsByFundraiser("fundraiser-999");

            // ASSERT
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(finalVerifiedRepository).findByFundraiserIdOrderByVerifiedAtDesc("fundraiser-999");
        }
    }

    @Nested
    @DisplayName("getFinalVerificationCount - Count Method")
    class GetFinalVerificationCountTests {

        @Test
        @DisplayName("Should return count of verifications for fundraiser")
        void testGetFinalVerificationCount_NonZeroCount() {
            // ARRANGE
            when(finalVerifiedRepository.countByFundraiserId("fundraiser-456"))
                    .thenReturn(5L);

            // ACT
            Long result = finalVerifiedParticipantService.getFinalVerificationCount("fundraiser-456");

            // ASSERT
            assertNotNull(result);
            assertEquals(5L, result);
            verify(finalVerifiedRepository).countByFundraiserId("fundraiser-456");
        }

        @Test
        @DisplayName("Should return zero count when no verifications exist")
        void testGetFinalVerificationCount_ZeroCount() {
            // ARRANGE
            when(finalVerifiedRepository.countByFundraiserId("fundraiser-999"))
                    .thenReturn(0L);

            // ACT
            Long result = finalVerifiedParticipantService.getFinalVerificationCount("fundraiser-999");

            // ASSERT
            assertNotNull(result);
            assertEquals(0L, result);
            verify(finalVerifiedRepository).countByFundraiserId("fundraiser-999");
        }
    }

    @Nested
    @DisplayName("removeFinalVerification - Deletion Method")
    class RemoveFinalVerificationTests {

        @Test
        @DisplayName("Should successfully remove verification by correct verifier")
        void testRemoveFinalVerification_Success() {
            // ARRANGE
            FinalVerifiedParticipant verification = new FinalVerifiedParticipant(
                    "participant-123", "john_doe", "notes"
            );
            verification.setId(1L);

            when(finalVerifiedRepository.findByParticipantId("participant-123"))
                    .thenReturn(Optional.of(verification));
            doNothing().when(finalVerifiedRepository).delete(verification);

            // ACT
            finalVerifiedParticipantService.removeFinalVerification("participant-123", "john_doe");

            // ASSERT
            verify(finalVerifiedRepository).findByParticipantId("participant-123");
            verify(finalVerifiedRepository).delete(verification);
        }

        @Test
        @DisplayName("Should throw exception when verification not found")
        void testRemoveFinalVerification_NotFound() {
            // ARRANGE
            when(finalVerifiedRepository.findByParticipantId("participant-999"))
                    .thenReturn(Optional.empty());

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.removeFinalVerification(
                            "participant-999", "john_doe"
                    )
            );

            assertEquals("Final verification not found", exception.getMessage());
            verify(finalVerifiedRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw exception when remover is not the original verifier")
        void testRemoveFinalVerification_UnauthorizedRemoval() {
            // ARRANGE
            FinalVerifiedParticipant verification = new FinalVerifiedParticipant(
                    "participant-123", "john_doe", "notes"
            );

            when(finalVerifiedRepository.findByParticipantId("participant-123"))
                    .thenReturn(Optional.of(verification));

            // ACT & ASSERT
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> finalVerifiedParticipantService.removeFinalVerification(
                            "participant-123", "unauthorized_user"
                    )
            );

            assertEquals("You can only remove verifications you performed", exception.getMessage());
            verify(finalVerifiedRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should handle case-insensitive removal verification")
        void testRemoveFinalVerification_CaseInsensitive() {
            // ARRANGE
            FinalVerifiedParticipant verification = new FinalVerifiedParticipant(
                    "participant-123", "john_doe", "notes"
            );
            verification.setId(1L);

            when(finalVerifiedRepository.findByParticipantId("participant-123"))
                    .thenReturn(Optional.of(verification));
            doNothing().when(finalVerifiedRepository).delete(verification);

            // ACT
            finalVerifiedParticipantService.removeFinalVerification("participant-123", "JOHN_DOE");

            // ASSERT
            verify(finalVerifiedRepository).delete(verification);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesAndIntegrationTests {

        @Test
        @DisplayName("Should handle verification with special characters in notes")
        void testFinallyVerifyParticipant_SpecialCharactersInNotes() {
            // ARRANGE
            String specialNotes = "Verified with @#$%^&*() special chars!";

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", specialNotes);

            // ASSERT
            assertNotNull(result);
            verify(finalVerifiedRepository).save(any(FinalVerifiedParticipant.class));
        }

        @Test
        @DisplayName("Should handle verification with large amount values")
        void testFinallyVerifyParticipant_LargeAmountValue() {
            // ARRANGE
            testParticipant.setAmountPaid(BigDecimal.valueOf(999999999.99));

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            verify(emailService).sendVerificationEmail(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    eq("999999999.99")
            );
        }

        @Test
        @DisplayName("Should handle mixed case fundraiser type")
        void testFinallyVerifyParticipant_MixedCaseFundraiserType() {
            // ARRANGE
            testParticipant.setFundraiserType("DoNaTiOn");

            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            FinalVerifiedParticipant result = finalVerifiedParticipantService
                    .finallyVerifyParticipant("participant-123", "john_doe", "notes");

            // ASSERT
            assertNotNull(result);
            verify(donationRepo).findById("fundraiser-456");
        }

        @Test
        @DisplayName("Should verify save method is called with correct parameters")
        void testFinallyVerifyParticipant_SaveMethodParameterVerification() {
            // ARRANGE
            when(verifiedParticipantRepository.findById("participant-123"))
                    .thenReturn(Optional.of(testParticipant));
            when(donationRepo.findById("fundraiser-456"))
                    .thenReturn(Optional.of(testDonation));
            when(finalVerifiedRepository.existsByParticipantId("participant-123"))
                    .thenReturn(false);
            when(finalVerifiedRepository.save(any(FinalVerifiedParticipant.class)))
                    .thenReturn(testFinalVerification);
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString(), anyString(), anyString());

            // ACT
            finalVerifiedParticipantService.finallyVerifyParticipant("participant-123", "john_doe", "Verified");

            // ASSERT
            verify(finalVerifiedRepository).save(argThat(verification ->
                    "participant-123".equals(verification.getParticipantId()) &&
                            "john_doe".equals(verification.getVerifiedBy()) &&
                            "Verified".equals(verification.getNotes())
            ));
        }
    }
}
