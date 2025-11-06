package com.chakri.fundly.service;

import com.chakri.fundly.model.Donation;
import com.chakri.fundly.repo.DonationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepo donationRepo;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private DonationService donationService;

    // ==================== CREATE DONATION TESTS ====================

    @Test
    void testCreateDonation_withValidDonation_returnsCreatedDonation() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Help Build School",
                "Fundraising for school construction",
                "user@upi",
                "Donation for school",
                "john_doe",
                "data:image/png;base64,iVBORw0KGgo=",
                5000
        );

        Donation savedDonation = new Donation(
                "uuid-123-456",
                "Help Build School",
                "Fundraising for school construction",
                "user@upi",
                "Donation for school",
                "john_doe",
                "data:image/png;base64,iVBORw0KGgo=",
                5000
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals("uuid-123-456", result.getId());
        assertEquals("Help Build School", result.getTitle());
        assertEquals("Fundraising for school construction", result.getDescription());
        assertEquals("user@upi", result.getUpiId());
        assertEquals("Donation for school", result.getUpiMsg());
        assertEquals("john_doe", result.getCreatorUsername());
        assertEquals("data:image/png;base64,iVBORw0KGgo=", result.getImageDataUrl());
        assertEquals(5000, result.getAmount());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withNullFields_savesSuccessfully() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0
        );

        Donation savedDonation = new Donation(
                "uuid-null-fields",
                null,
                null,
                null,
                null,
                null,
                null,
                0
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals("uuid-null-fields", result.getId());
        assertNull(result.getTitle());
        assertNull(result.getDescription());
        assertNull(result.getUpiId());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withEmptyStrings_savesSuccessfully() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "",
                "",
                "",
                "",
                "",
                "",
                0
        );

        Donation savedDonation = new Donation(
                "uuid-empty-strings",
                "",
                "",
                "",
                "",
                "",
                "",
                0
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals("uuid-empty-strings", result.getId());
        assertEquals("", result.getTitle());
        assertEquals("", result.getDescription());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withZeroAmount_savesSuccessfully() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Zero Donation",
                "Testing zero amount",
                "test@upi",
                "Test message",
                "test_user",
                "imagedata",
                0
        );

        Donation savedDonation = new Donation(
                "uuid-zero-amount",
                "Zero Donation",
                "Testing zero amount",
                "test@upi",
                "Test message",
                "test_user",
                "imagedata",
                0
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getAmount());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withNegativeAmount_savesSuccessfully() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Negative Test",
                "Testing negative amount",
                "test@upi",
                "Test",
                "user",
                "data",
                -100
        );

        Donation savedDonation = new Donation(
                "uuid-negative",
                "Negative Test",
                "Testing negative amount",
                "test@upi",
                "Test",
                "user",
                "data",
                -100
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals(-100, result.getAmount());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withLargeAmount_savesSuccessfully() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Large Donation",
                "Testing large amount",
                "test@upi",
                "Test",
                "user",
                "data",
                Integer.MAX_VALUE
        );

        Donation savedDonation = new Donation(
                "uuid-large",
                "Large Donation",
                "Testing large amount",
                "test@upi",
                "Test",
                "user",
                "data",
                Integer.MAX_VALUE
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals(Integer.MAX_VALUE, result.getAmount());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_withVeryLongImageData_savesSuccessfully() {
        // ARRANGE
        String longImageData = "data:image/png;base64," + "A".repeat(10000);
        Donation inputDonation = new Donation(
                null,
                "Long Image Test",
                "Testing long image data",
                "test@upi",
                "Test",
                "user",
                longImageData,
                1000
        );

        Donation savedDonation = new Donation(
                "uuid-long-image",
                "Long Image Test",
                "Testing long image data",
                "test@upi",
                "Test",
                "user",
                longImageData,
                1000
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);

        // ACT
        Donation result = donationService.createDonation(inputDonation);

        // ASSERT
        assertNotNull(result);
        assertEquals(longImageData, result.getImageDataUrl());

        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_whenStatsServiceThrowsException_exceptionPropagates() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Exception Test",
                "Testing exception handling",
                "test@upi",
                "Test",
                "user",
                "data",
                1000
        );

        Donation savedDonation = new Donation(
                "uuid-exception",
                "Exception Test",
                "Testing exception handling",
                "test@upi",
                "Test",
                "user",
                "data",
                1000
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);
        doThrow(new RuntimeException("Stats service unavailable"))
                .when(statsService).incrementDonationCount();

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            donationService.createDonation(inputDonation);
        });

        assertEquals("Stats service unavailable", thrown.getMessage());
        verify(donationRepo, times(1)).save(inputDonation);
        verify(statsService, times(1)).incrementDonationCount();
    }

    @Test
    void testCreateDonation_capturesCorrectDonationArgument() {
        // ARRANGE
        Donation inputDonation = new Donation(
                null,
                "Capture Test",
                "Testing argument capture",
                "test@upi",
                "Test",
                "user",
                "data",
                500
        );

        Donation savedDonation = new Donation(
                "uuid-captured",
                "Capture Test",
                "Testing argument capture",
                "test@upi",
                "Test",
                "user",
                "data",
                500
        );

        when(donationRepo.save(any(Donation.class))).thenReturn(savedDonation);
        ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);

        // ACT
        donationService.createDonation(inputDonation);

        // ASSERT
        verify(donationRepo).save(donationCaptor.capture());
        Donation capturedDonation = donationCaptor.getValue();
        assertEquals("Capture Test", capturedDonation.getTitle());
        assertEquals("user", capturedDonation.getCreatorUsername());
    }

    // ==================== GET ALL DONATIONS TESTS ====================

    @Test
    void testGetAllDonations_whenDonationsExist_returnsAllDonations() {
        // ARRANGE
        List<Donation> donations = Arrays.asList(
                new Donation("id1", "Title1", "Desc1", "upi1", "msg1", "user1", "img1", 100),
                new Donation("id2", "Title2", "Desc2", "upi2", "msg2", "user2", "img2", 200),
                new Donation("id3", "Title3", "Desc3", "upi3", "msg3", "user3", "img3", 300)
        );

        when(donationRepo.findAll()).thenReturn(donations);

        // ACT
        List<Donation> result = donationService.getAllDonations();

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("id1", result.get(0).getId());
        assertEquals("id2", result.get(1).getId());
        assertEquals("id3", result.get(2).getId());
        assertEquals("Title1", result.get(0).getTitle());
        assertEquals(100, result.get(0).getAmount());
        assertEquals(300, result.get(2).getAmount());

        verify(donationRepo, times(1)).findAll();
        verifyNoInteractions(statsService);
    }

    @Test
    void testGetAllDonations_whenNoDonationsExist_returnsEmptyList() {
        // ARRANGE
        List<Donation> emptyList = new ArrayList<>();
        when(donationRepo.findAll()).thenReturn(emptyList);

        // ACT
        List<Donation> result = donationService.getAllDonations();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(donationRepo, times(1)).findAll();
    }

    @Test
    void testGetAllDonations_whenSingleDonationExists_returnsListWithOne() {
        // ARRANGE
        List<Donation> singleDonation = Arrays.asList(
                new Donation("id1", "Title1", "Desc1", "upi1", "msg1", "user1", "img1", 500)
        );

        when(donationRepo.findAll()).thenReturn(singleDonation);

        // ACT
        List<Donation> result = donationService.getAllDonations();

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("id1", result.get(0).getId());
        assertEquals("Title1", result.get(0).getTitle());

        verify(donationRepo, times(1)).findAll();
    }

    @Test
    void testGetAllDonations_whenRepoThrowsException_propagatesException() {
        // ARRANGE
        when(donationRepo.findAll()).thenThrow(new RuntimeException("Database connection error"));

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            donationService.getAllDonations();
        });

        assertEquals("Database connection error", thrown.getMessage());
        verify(donationRepo, times(1)).findAll();
    }

    @Test
    void testGetAllDonations_multipleCallsReturnDifferentResults() {
        // ARRANGE
        List<Donation> firstCall = Arrays.asList(
                new Donation("id1", "Title1", "Desc1", "upi1", "msg1", "user1", "img1", 100)
        );
        List<Donation> secondCall = Arrays.asList(
                new Donation("id1", "Title1", "Desc1", "upi1", "msg1", "user1", "img1", 100),
                new Donation("id2", "Title2", "Desc2", "upi2", "msg2", "user2", "img2", 200)
        );

        when(donationRepo.findAll()).thenReturn(firstCall).thenReturn(secondCall);

        // ACT
        List<Donation> result1 = donationService.getAllDonations();
        List<Donation> result2 = donationService.getAllDonations();

        // ASSERT
        assertEquals(1, result1.size());
        assertEquals(2, result2.size());
        verify(donationRepo, times(2)).findAll();
    }

    // ==================== GET DONATION BY ID TESTS ====================

    @Test
    void testGetDonationById_withValidId_returnsOptionalWithDonation() {
        // ARRANGE
        String donationId = "uuid-123";
        Donation donation = new Donation(
                donationId,
                "Test Title",
                "Test Description",
                "test@upi",
                "Test message",
                "testuser",
                "imagedata",
                1000
        );

        when(donationRepo.findById(donationId)).thenReturn(Optional.of(donation));

        // ACT
        Optional<Donation> result = donationService.getDonationById(donationId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(donationId, result.get().getId());
        assertEquals("Test Title", result.get().getTitle());
        assertEquals(1000, result.get().getAmount());

        verify(donationRepo, times(1)).findById(donationId);
    }

    @Test
    void testGetDonationById_withNonExistentId_returnsEmptyOptional() {
        // ARRANGE
        String nonExistentId = "non-existent-id";
        when(donationRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        Optional<Donation> result = donationService.getDonationById(nonExistentId);

        // ASSERT
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());

        verify(donationRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetDonationById_withNullId_callsRepoWithNull() {
        // ARRANGE
        when(donationRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        Optional<Donation> result = donationService.getDonationById(null);

        // ASSERT
        assertFalse(result.isPresent());

        verify(donationRepo, times(1)).findById(null);
    }

    @Test
    void testGetDonationById_withEmptyStringId_returnsEmptyOptional() {
        // ARRANGE
        String emptyId = "";
        when(donationRepo.findById(emptyId)).thenReturn(Optional.empty());

        // ACT
        Optional<Donation> result = donationService.getDonationById(emptyId);

        // ASSERT
        assertFalse(result.isPresent());

        verify(donationRepo, times(1)).findById(emptyId);
    }

    @Test
    void testGetDonationById_whenRepoThrowsException_propagatesException() {
        // ARRANGE
        String donationId = "error-id";
        when(donationRepo.findById(donationId))
                .thenThrow(new RuntimeException("Database error"));

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            donationService.getDonationById(donationId);
        });

        assertEquals("Database error", thrown.getMessage());
        verify(donationRepo, times(1)).findById(donationId);
    }

    @Test
    void testGetDonationById_capturesCorrectIdArgument() {
        // ARRANGE
        String searchId = "search-uuid";
        Donation donation = new Donation(
                searchId,
                "Found Title",
                "Found Desc",
                "upi",
                "msg",
                "user",
                "img",
                100
        );

        when(donationRepo.findById(searchId)).thenReturn(Optional.of(donation));
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        // ACT
        donationService.getDonationById(searchId);

        // ASSERT
        verify(donationRepo).findById(idCaptor.capture());
        assertEquals(searchId, idCaptor.getValue());
    }

    // ==================== DELETE DONATION TESTS ====================

    @Test
    void testDeleteDonation_withValidId_callsRepoDelete() {
        // ARRANGE
        String donationId = "uuid-to-delete";

        // ACT
        donationService.deleteDonation(donationId);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(donationId);
        verifyNoInteractions(statsService);
    }

    @Test
    void testDeleteDonation_withNullId_callsRepoDeleteWithNull() {
        // ARRANGE & ACT
        donationService.deleteDonation(null);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(null);
    }

    @Test
    void testDeleteDonation_withEmptyStringId_callsRepoDelete() {
        // ARRANGE
        String emptyId = "";

        // ACT
        donationService.deleteDonation(emptyId);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(emptyId);
    }

    @Test
    void testDeleteDonation_withNonExistentId_completesWithoutError() {
        // ARRANGE
        String nonExistentId = "non-existent-id";

        // ACT
        donationService.deleteDonation(nonExistentId);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(nonExistentId);
    }

    @Test
    void testDeleteDonation_whenRepoThrowsException_propagatesException() {
        // ARRANGE
        String donationId = "error-id";
        doThrow(new RuntimeException("Delete failed"))
                .when(donationRepo).deleteById(donationId);

        // ACT & ASSERT
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            donationService.deleteDonation(donationId);
        });

        assertEquals("Delete failed", thrown.getMessage());
        verify(donationRepo, times(1)).deleteById(donationId);
    }

    @Test
    void testDeleteDonation_multipleCallsWithSameId_eachCallInvokesRepo() {
        // ARRANGE
        String donationId = "repeated-delete-id";

        // ACT
        donationService.deleteDonation(donationId);
        donationService.deleteDonation(donationId);
        donationService.deleteDonation(donationId);

        // ASSERT
        verify(donationRepo, times(3)).deleteById(donationId);
    }

    @Test
    void testDeleteDonation_multipleCallsWithDifferentIds() {
        // ARRANGE
        String id1 = "delete-id-1";
        String id2 = "delete-id-2";
        String id3 = "delete-id-3";

        // ACT
        donationService.deleteDonation(id1);
        donationService.deleteDonation(id2);
        donationService.deleteDonation(id3);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(id1);
        verify(donationRepo, times(1)).deleteById(id2);
        verify(donationRepo, times(1)).deleteById(id3);
        verify(donationRepo, times(3)).deleteById(anyString());
    }

    @Test
    void testDeleteDonation_capturesCorrectIdArgument() {
        // ARRANGE
        String deleteId = "capture-delete-id";
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        // ACT
        donationService.deleteDonation(deleteId);

        // ASSERT
        verify(donationRepo).deleteById(idCaptor.capture());
        assertEquals(deleteId, idCaptor.getValue());
    }
}
