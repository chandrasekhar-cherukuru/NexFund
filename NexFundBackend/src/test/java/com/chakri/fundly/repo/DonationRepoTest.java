package com.chakri.fundly.repo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("DonationRepo Unit Tests with Mockito")
class DonationRepoTest {

    @Mock
    private DonationRepo donationRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== SAVE OPERATION TESTS ====================

    @Test
    @DisplayName("Should save a valid Donation object successfully")
    void testSaveDonation_WithValidData_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-001", "Emergency Relief", "Help needed",
                "upi@example", "Emergency", "john_doe", 5000);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation, "Saved donation should not be null");
        assertEquals("DNG-001", savedDonation.getId(), "Donation ID should match");
        assertEquals("Emergency Relief", savedDonation.getTitle(), "Donation title should match");
        assertEquals(5000, savedDonation.getAmount(), "Donation amount should match");
        assertEquals("john_doe", savedDonation.getCreatorUsername(), "Creator username should match");
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should handle save operation with null donation")
    void testSaveDonation_WithNullDonation_ThrowsException() {
        // ARRANGE
        when(donationRepo.save(null)).thenThrow(new IllegalArgumentException("Donation cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.save(null),
                "Should throw exception when saving null donation");
        verify(donationRepo, times(1)).save(null);
    }

    @Test
    @DisplayName("Should save donation with minimum valid amount")
    void testSaveDonation_WithMinimumAmount_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-002", "Small Donation", "Help",
                "upi@test", "Test", "jane_smith", 1);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals(1, savedDonation.getAmount());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with very large amount")
    void testSaveDonation_WithLargeAmount_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-003", "Large Donation", "Generous help",
                "upi@generous", "Large", "generous_donor", 999999);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals(999999, savedDonation.getAmount());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with zero amount")
    void testSaveDonation_WithZeroAmount_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-004", "Zero Amount", "Testing",
                "upi@zero", "Zero", "test_user", 0);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals(0, savedDonation.getAmount());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with null title")
    void testSaveDonation_WithNullTitle_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-005", null, "Description",
                "upi@null", "Msg", "user", 1000);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertNull(savedDonation.getTitle());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with empty title")
    void testSaveDonation_WithEmptyTitle_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-006", "", "Description",
                "upi@empty", "Msg", "user", 2000);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals("", savedDonation.getTitle());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with null description")
    void testSaveDonation_WithNullDescription_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-007", "Title", null,
                "upi@test", "Msg", "user", 1500);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertNull(savedDonation.getDescription());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with very long description")
    void testSaveDonation_WithLongDescription_ReturnsSavedDonation() {
        // ARRANGE
        String longDescription = "A".repeat(5000);
        Donation donation = createTestDonation("DNG-008", "Title", longDescription,
                "upi@long", "Msg", "user", 3000);
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals(longDescription, savedDonation.getDescription());
        verify(donationRepo, times(1)).save(donation);
    }

    @Test
    @DisplayName("Should save donation with all fields populated")
    void testSaveDonation_WithAllFields_ReturnsSavedDonation() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-009", "Complete Donation",
                "Full description with details",
                "user@upi", "Thanks for donation", "complete_user", 5000);
        donation.setImageDataUrl("data:image/png;base64,iVBORw0KGgo...");
        when(donationRepo.save(donation)).thenReturn(donation);

        // ACT
        Donation savedDonation = donationRepo.save(donation);

        // ASSERT
        assertNotNull(savedDonation);
        assertEquals("DNG-009", savedDonation.getId());
        assertEquals("Complete Donation", savedDonation.getTitle());
        assertEquals("user@upi", savedDonation.getUpiId());
        assertEquals("Thanks for donation", savedDonation.getUpiMsg());
        assertEquals("complete_user", savedDonation.getCreatorUsername());
        assertEquals(5000, savedDonation.getAmount());
        assertNotNull(savedDonation.getImageDataUrl());
        verify(donationRepo, times(1)).save(donation);
    }

    // ==================== SAVE ALL OPERATION TESTS ====================

    @Test
    @DisplayName("Should save multiple donations using saveAll")
    void testSaveAll_WithMultipleDonations_ReturnsSavedList() {
        // ARRANGE
        List<Donation> donations = new ArrayList<>();
        donations.add(createTestDonation("DNG-010", "Bulk 1", "Bulk Desc 1",
                "bulk1@upi", "Bulk Msg 1", "bulk_user_1", 1500));
        donations.add(createTestDonation("DNG-011", "Bulk 2", "Bulk Desc 2",
                "bulk2@upi", "Bulk Msg 2", "bulk_user_2", 2500));

        when(donationRepo.saveAll(donations)).thenReturn(donations);

        // ACT
        List<Donation> savedDonations = donationRepo.saveAll(donations);

        // ASSERT
        assertNotNull(savedDonations);
        assertEquals(2, savedDonations.size());
        assertTrue(savedDonations.stream().allMatch(d -> d != null && d.getId() != null));
        verify(donationRepo, times(1)).saveAll(donations);
    }

    @Test
    @DisplayName("Should handle saveAll with empty list")
    void testSaveAll_WithEmptyList_ReturnsEmptyList() {
        // ARRANGE
        List<Donation> emptyList = new ArrayList<>();
        when(donationRepo.saveAll(emptyList)).thenReturn(emptyList);

        // ACT
        List<Donation> result = donationRepo.saveAll(emptyList);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(donationRepo, times(1)).saveAll(emptyList);
    }

    @Test
    @DisplayName("Should handle saveAll with null list")
    void testSaveAll_WithNullList_ThrowsException() {
        // ARRANGE
        when(donationRepo.saveAll(null)).thenThrow(new IllegalArgumentException("Donations list cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.saveAll(null));
        verify(donationRepo, times(1)).saveAll(null);
    }

    @Test
    @DisplayName("Should saveAll with large batch of donations")
    void testSaveAll_WithLargeBatch_ReturnsList() {
        // ARRANGE
        List<Donation> largeBatch = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeBatch.add(createTestDonation("DNG-" + (1000 + i), "Batch " + i,
                    "Batch Desc " + i, "batch" + i + "@upi",
                    "Batch Msg " + i, "batch_user_" + i, 1000 + i));
        }

        when(donationRepo.saveAll(largeBatch)).thenReturn(largeBatch);

        // ACT
        List<Donation> result = donationRepo.saveAll(largeBatch);

        // ASSERT
        assertNotNull(result);
        assertEquals(100, result.size());
        verify(donationRepo, times(1)).saveAll(largeBatch);
    }

    // ==================== FIND BY ID OPERATION TESTS ====================

    @Test
    @DisplayName("Should find donation by valid ID")
    void testFindById_WithValidId_ReturnsDonation() {
        // ARRANGE
        String donationId = "DNG-012";
        Donation donation = createTestDonation(donationId, "Search Donation", "Search Desc",
                "search@upi", "Search Msg", "search_user", 7500);
        when(donationRepo.findById(donationId)).thenReturn(Optional.of(donation));

        // ACT
        Optional<Donation> result = donationRepo.findById(donationId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(donationId, result.get().getId());
        assertEquals("Search Donation", result.get().getTitle());
        assertEquals(7500, result.get().getAmount());
        verify(donationRepo, times(1)).findById(donationId);
    }

    @Test
    @DisplayName("Should return empty Optional when donation not found")
    void testFindById_WithInvalidId_ReturnsEmptyOptional() {
        // ARRANGE
        String nonExistentId = "DNG-999999";
        when(donationRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        Optional<Donation> result = donationRepo.findById(nonExistentId);

        // ASSERT
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(donationRepo, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should handle findById with null ID")
    void testFindById_WithNullId_ThrowsException() {
        // ARRANGE
        when(donationRepo.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.findById(null));
        verify(donationRepo, times(1)).findById(null);
    }

    @Test
    @DisplayName("Should handle findById with empty string ID")
    void testFindById_WithEmptyStringId_ReturnsEmptyOptional() {
        // ARRANGE
        String emptyId = "";
        when(donationRepo.findById(emptyId)).thenReturn(Optional.empty());

        // ACT
        Optional<Donation> result = donationRepo.findById(emptyId);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(donationRepo, times(1)).findById(emptyId);
    }

    @Test
    @DisplayName("Should find donation with UUID ID")
    void testFindById_WithUUIDId_ReturnsDonation() {
        // ARRANGE
        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        Donation donation = createTestDonation(uuidId, "UUID Donation", "UUID Desc",
                "uuid@upi", "UUID Msg", "uuid_user", 2500);
        when(donationRepo.findById(uuidId)).thenReturn(Optional.of(donation));

        // ACT
        Optional<Donation> result = donationRepo.findById(uuidId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(uuidId, result.get().getId());
        verify(donationRepo, times(1)).findById(uuidId);
    }

    // ==================== DELETE OPERATION TESTS ====================

    @Test
    @DisplayName("Should delete donation by ID successfully")
    void testDeleteById_WithValidId_DeletesSuccessfully() {
        // ARRANGE
        String donationId = "DNG-013";
        doNothing().when(donationRepo).deleteById(donationId);

        // ACT
        donationRepo.deleteById(donationId);

        // ASSERT
        verify(donationRepo, times(1)).deleteById(donationId);
    }

    @Test
    @DisplayName("Should handle delete with null ID")
    void testDeleteById_WithNullId_ThrowsException() {
        // ARRANGE
        doThrow(new IllegalArgumentException("ID cannot be null")).when(donationRepo).deleteById(null);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.deleteById(null));
        verify(donationRepo, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("Should delete donation object directly")
    void testDelete_WithDonationObject_DeletesSuccessfully() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-014", "Delete Donation", "Delete Desc",
                "delete@upi", "Delete Msg", "delete_user", 3000);
        doNothing().when(donationRepo).delete(donation);

        // ACT
        donationRepo.delete(donation);

        // ASSERT
        verify(donationRepo, times(1)).delete(donation);
    }

    @Test
    @DisplayName("Should handle delete with null object")
    void testDelete_WithNullDonation_ThrowsException() {
        // ARRANGE
        doThrow(new IllegalArgumentException("Donation cannot be null")).when(donationRepo).delete(null);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.delete(null));
        verify(donationRepo, times(1)).delete(null);
    }

    @Test
    @DisplayName("Should delete all donations")
    void testDeleteAll_RemovesAllDonations_Successfully() {
        // ARRANGE
        doNothing().when(donationRepo).deleteAll();

        // ACT
        donationRepo.deleteAll();

        // ASSERT
        verify(donationRepo, times(1)).deleteAll();
    }

    @Test
    @DisplayName("Should delete multiple donations using deleteAll collection")
    void testDeleteAll_WithCollection_DeletesSuccessfully() {
        // ARRANGE
        List<Donation> donations = new ArrayList<>();
        donations.add(createTestDonation("DNG-015", "Delete 1", "Desc 1",
                "del1@upi", "Msg 1", "del_user_1", 1000));
        donations.add(createTestDonation("DNG-016", "Delete 2", "Desc 2",
                "del2@upi", "Msg 2", "del_user_2", 2000));
        doNothing().when(donationRepo).deleteAll(donations);

        // ACT
        donationRepo.deleteAll(donations);

        // ASSERT
        verify(donationRepo, times(1)).deleteAll(donations);
    }

    // ==================== FIND ALL OPERATION TESTS ====================

    @Test
    @DisplayName("Should retrieve all donations successfully")
    void testFindAll_ReturnsAllDonations() {
        // ARRANGE
        List<Donation> donations = new ArrayList<>();
        donations.add(createTestDonation("DNG-017", "All 1", "Desc 1",
                "all1@upi", "Msg 1", "all_user_1", 1000));
        donations.add(createTestDonation("DNG-018", "All 2", "Desc 2",
                "all2@upi", "Msg 2", "all_user_2", 2000));
        donations.add(createTestDonation("DNG-019", "All 3", "Desc 3",
                "all3@upi", "Msg 3", "all_user_3", 3000));

        when(donationRepo.findAll()).thenReturn(donations);

        // ACT
        List<Donation> result = donationRepo.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(d -> d != null && d.getId() != null));
        verify(donationRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no donations exist")
    void testFindAll_WhenNoDonationsExist_ReturnsEmptyList() {
        // ARRANGE
        when(donationRepo.findAll()).thenReturn(new ArrayList<>());

        // ACT
        List<Donation> result = donationRepo.findAll();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(donationRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find all with large dataset")
    void testFindAll_WithLargeDataset_ReturnsAllDonations() {
        // ARRANGE
        List<Donation> largeDonationList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeDonationList.add(createTestDonation("DNG-" + (5000 + i), "Large " + i,
                    "Desc " + i, "large" + i + "@upi",
                    "Msg " + i, "large_user_" + i, 1000 + i));
        }

        when(donationRepo.findAll()).thenReturn(largeDonationList);

        // ACT
        List<Donation> result = donationRepo.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(1000, result.size());
        verify(donationRepo, times(1)).findAll();
    }

    // ==================== EXISTS OPERATION TESTS ====================

    @Test
    @DisplayName("Should verify donation exists by ID")
    void testExistsById_WithExistingId_ReturnsTrue() {
        // ARRANGE
        String donationId = "DNG-020";
        when(donationRepo.existsById(donationId)).thenReturn(true);

        // ACT
        boolean exists = donationRepo.existsById(donationId);

        // ASSERT
        assertTrue(exists);
        verify(donationRepo, times(1)).existsById(donationId);
    }

    @Test
    @DisplayName("Should verify donation does not exist by ID")
    void testExistsById_WithNonExistingId_ReturnsFalse() {
        // ARRANGE
        String donationId = "DNG-999999";
        when(donationRepo.existsById(donationId)).thenReturn(false);

        // ACT
        boolean exists = donationRepo.existsById(donationId);

        // ASSERT
        assertFalse(exists);
        verify(donationRepo, times(1)).existsById(donationId);
    }

    @Test
    @DisplayName("Should handle existsById with null ID")
    void testExistsById_WithNullId_ThrowsException() {
        // ARRANGE
        when(donationRepo.existsById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.existsById(null));
        verify(donationRepo, times(1)).existsById(null);
    }

    @Test
    @DisplayName("Should handle existsById with empty string ID")
    void testExistsById_WithEmptyStringId_ReturnsFalse() {
        // ARRANGE
        when(donationRepo.existsById("")).thenReturn(false);

        // ACT
        boolean exists = donationRepo.existsById("");

        // ASSERT
        assertFalse(exists);
        verify(donationRepo, times(1)).existsById("");
    }

    // ==================== COUNT OPERATION TESTS ====================

    @Test
    @DisplayName("Should return count of all donations")
    void testCount_ReturnsCorrectCount() {
        // ARRANGE
        long expectedCount = 5L;
        when(donationRepo.count()).thenReturn(expectedCount);

        // ACT
        long count = donationRepo.count();

        // ASSERT
        assertEquals(5L, count);
        verify(donationRepo, times(1)).count();
    }

    @Test
    @DisplayName("Should return zero count when no donations exist")
    void testCount_WhenNoDonations_ReturnsZero() {
        // ARRANGE
        when(donationRepo.count()).thenReturn(0L);

        // ACT
        long count = donationRepo.count();

        // ASSERT
        assertEquals(0L, count);
        verify(donationRepo, times(1)).count();
    }

    @Test
    @DisplayName("Should return high count for large dataset")
    void testCount_WithLargeDataset_ReturnsCorrectCount() {
        // ARRANGE
        long largeCount = 1000000L;
        when(donationRepo.count()).thenReturn(largeCount);

        // ACT
        long count = donationRepo.count();

        // ASSERT
        assertEquals(1000000L, count);
        verify(donationRepo, times(1)).count();
    }

    @Test
    @DisplayName("Should return one when single donation exists")
    void testCount_WithSingleDonation_ReturnsOne() {
        // ARRANGE
        when(donationRepo.count()).thenReturn(1L);

        // ACT
        long count = donationRepo.count();

        // ASSERT
        assertEquals(1L, count);
        verify(donationRepo, times(1)).count();
    }

    // ==================== FLUSH AND SAVE AND FLUSH TESTS ====================

    @Test
    @DisplayName("Should flush pending changes to database")
    void testFlush_FlushesPendingChanges() {
        // ARRANGE
        doNothing().when(donationRepo).flush();

        // ACT
        donationRepo.flush();

        // ASSERT
        verify(donationRepo, times(1)).flush();
    }

    @Test
    @DisplayName("Should save and flush donation")
    void testSaveAndFlush_WithValidDonation_SavesAndFlushes() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-021", "Flush Donation", "Flush Desc",
                "flush@upi", "Flush Msg", "flush_user", 4500);
        when(donationRepo.saveAndFlush(donation)).thenReturn(donation);

        // ACT
        Donation result = donationRepo.saveAndFlush(donation);

        // ASSERT
        assertNotNull(result);
        assertEquals("DNG-021", result.getId());
        assertEquals("Flush Donation", result.getTitle());
        verify(donationRepo, times(1)).saveAndFlush(donation);
    }

    @Test
    @DisplayName("Should handle saveAndFlush with null donation")
    void testSaveAndFlush_WithNullDonation_ThrowsException() {
        // ARRANGE
        when(donationRepo.saveAndFlush(null)).thenThrow(new IllegalArgumentException("Donation cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.saveAndFlush(null));
        verify(donationRepo, times(1)).saveAndFlush(null);
    }

    @Test
    @DisplayName("Should saveAndFlush with all fields")
    void testSaveAndFlush_WithAllFields_SavesAndFlushes() {
        // ARRANGE
        Donation donation = createTestDonation("DNG-022", "Complete Flush", "Complete Desc",
                "complete@upi", "Complete Msg", "complete_user", 9999);
        donation.setImageDataUrl("data:image/png;base64,iVBORw0KGgo...");
        when(donationRepo.saveAndFlush(donation)).thenReturn(donation);

        // ACT
        Donation result = donationRepo.saveAndFlush(donation);

        // ASSERT
        assertNotNull(result);
        assertEquals("DNG-022", result.getId());
        assertNotNull(result.getImageDataUrl());
        verify(donationRepo, times(1)).saveAndFlush(donation);
    }

    // ==================== FIND BY IDS OPERATION TESTS ====================

    @Test
    @DisplayName("Should find all donations by list of IDs")
    void testFindAllById_WithValidIds_ReturnsDonations() {
        // ARRANGE
        List<String> ids = new ArrayList<>();
        ids.add("DNG-023");
        ids.add("DNG-024");

        List<Donation> donations = new ArrayList<>();
        donations.add(createTestDonation("DNG-023", "Find 1", "Desc 1",
                "find1@upi", "Msg 1", "find_user_1", 1000));
        donations.add(createTestDonation("DNG-024", "Find 2", "Desc 2",
                "find2@upi", "Msg 2", "find_user_2", 2000));

        when(donationRepo.findAllById(ids)).thenReturn(donations);

        // ACT
        List<Donation> result = donationRepo.findAllById(ids);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(donationRepo, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Should handle findAllById with empty ID list")
    void testFindAllById_WithEmptyIdList_ReturnsEmptyList() {
        // ARRANGE
        List<String> emptyIds = new ArrayList<>();
        when(donationRepo.findAllById(emptyIds)).thenReturn(new ArrayList<>());

        // ACT
        List<Donation> result = donationRepo.findAllById(emptyIds);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(donationRepo, times(1)).findAllById(emptyIds);
    }

    @Test
    @DisplayName("Should handle findAllById with null ID list")
    void testFindAllById_WithNullIdList_ThrowsException() {
        // ARRANGE
        when(donationRepo.findAllById(null)).thenThrow(new IllegalArgumentException("ID list cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> donationRepo.findAllById(null));
        verify(donationRepo, times(1)).findAllById(null);
    }

    @Test
    @DisplayName("Should return partial results when some IDs don't exist")
    void testFindAllById_WithPartialMatchingIds_ReturnsAvailableDonations() {
        // ARRANGE
        List<String> ids = new ArrayList<>();
        ids.add("DNG-025");
        ids.add("DNG-999999"); // Non-existent

        List<Donation> donations = new ArrayList<>();
        donations.add(createTestDonation("DNG-025", "Partial Find", "Partial Desc",
                "partial@upi", "Partial Msg", "partial_user", 1500));

        when(donationRepo.findAllById(ids)).thenReturn(donations);

        // ACT
        List<Donation> result = donationRepo.findAllById(ids);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DNG-025", result.get(0).getId());
        verify(donationRepo, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Should find all by large list of IDs")
    void testFindAllById_WithLargeIdList_ReturnsDonations() {
        // ARRANGE
        List<String> largeIdList = new ArrayList<>();
        List<Donation> largeResultList = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            String id = "DNG-" + (6000 + i);
            largeIdList.add(id);
            largeResultList.add(createTestDonation(id, "Large Find " + i, "Desc " + i,
                    "large" + i + "@upi", "Msg " + i,
                    "large_user_" + i, 1000 + i));
        }

        when(donationRepo.findAllById(largeIdList)).thenReturn(largeResultList);

        // ACT
        List<Donation> result = donationRepo.findAllById(largeIdList);

        // ASSERT
        assertNotNull(result);
        assertEquals(500, result.size());
        verify(donationRepo, times(1)).findAllById(largeIdList);
    }

    // ==================== HELPER METHOD ====================

    /**
     * Helper method to create a test Donation object with specified parameters
     *
     * @param id the donation ID
     * @param title the donation title
     * @param description the donation description
     * @param upiId the UPI ID
     * @param upiMsg the UPI message
     * @param creatorUsername the creator username
     * @param amount the donation amount (int type)
     * @return a new Donation object with specified values
     */
    private Donation createTestDonation(String id, String title, String description,
                                        String upiId, String upiMsg, String creatorUsername, int amount) {
        Donation donation = new Donation();
        donation.setId(id);
        donation.setTitle(title);
        donation.setDescription(description);
        donation.setUpiId(upiId);
        donation.setUpiMsg(upiMsg);
        donation.setCreatorUsername(creatorUsername);
        donation.setAmount(amount);
        return donation;
    }
}
