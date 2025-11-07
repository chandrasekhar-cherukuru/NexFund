package com.chakri.fundly.repo;

import com.chakri.fundly.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatsRepo Repository Unit Tests")
class StatsRepoTest {

    @Mock
    private StatsRepo statsRepo;

    private Stats testStats;

    @BeforeEach
    void setUp() {
        // Arrange: Initialize test data
        testStats = new Stats();
        testStats.setId(1L);
        testStats.setEventCount(10L);
        testStats.setDonationCount(5L);
        testStats.setGiftPoolCount(3L);
    }

    // ==================== Save Method Tests ====================

    @DisplayName("Should save stats successfully")
    @Test
    void testSaveStatsSuccess() {
        // Arrange: Setup mock to return saved stats
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        // Act: Call save method
        Stats savedStats = statsRepo.save(testStats);

        // Assert: Verify save was called and returned correct stats
        assertNotNull(savedStats, "Saved stats should not be null");
        assertEquals(testStats.getId(), savedStats.getId(), "Saved stats ID should match");
        assertEquals(testStats.getEventCount(), savedStats.getEventCount(), "Saved stats event count should match");
        verify(statsRepo, times(1)).save(testStats);
    }

    @DisplayName("Should save stats with null values")
    @Test
    void testSaveStatsWithNullValues() {
        // Arrange: Create stats with null values
        Stats nullStats = new Stats();
        nullStats.setId(null);
        nullStats.setEventCount(null);
        nullStats.setDonationCount(null);
        nullStats.setGiftPoolCount(null);

        when(statsRepo.save(any(Stats.class))).thenReturn(nullStats);

        // Act: Save stats with null values
        Stats savedStats = statsRepo.save(nullStats);

        // Assert: Verify null values are preserved
        assertNull(savedStats.getId(), "ID should be null");
        assertNull(savedStats.getEventCount(), "Event count should be null");
        assertNull(savedStats.getDonationCount(), "Donation count should be null");
        assertNull(savedStats.getGiftPoolCount(), "Gift pool count should be null");
        verify(statsRepo, times(1)).save(nullStats);
    }

    @DisplayName("Should save multiple stats objects")
    @Test
    void testSaveMultipleStatsObjects() {
        // Arrange: Create multiple stats objects
        Stats stats1 = new Stats();
        stats1.setId(1L);
        stats1.setEventCount(10L);

        Stats stats2 = new Stats();
        stats2.setId(2L);
        stats2.setEventCount(20L);

        Stats stats3 = new Stats();
        stats3.setId(3L);
        stats3.setEventCount(30L);

        List<Stats> statsList = List.of(stats1, stats2, stats3);

        when(statsRepo.saveAll(statsList)).thenReturn(statsList);

        // Act: Save all stats
        List<Stats> savedStatsList = statsRepo.saveAll(statsList);

        // Assert: Verify all stats were saved
        assertEquals(3, savedStatsList.size(), "Should save 3 stats objects");
        verify(statsRepo, times(1)).saveAll(statsList);
    }

    @DisplayName("Should save stats with maximum Long values")
    @Test
    void testSaveStatsWithMaximumValues() {
        // Arrange: Create stats with maximum values
        Stats maxStats = new Stats();
        maxStats.setId(Long.MAX_VALUE);
        maxStats.setEventCount(Long.MAX_VALUE);
        maxStats.setDonationCount(Long.MAX_VALUE);
        maxStats.setGiftPoolCount(Long.MAX_VALUE);

        when(statsRepo.save(any(Stats.class))).thenReturn(maxStats);

        // Act: Save stats with maximum values
        Stats savedStats = statsRepo.save(maxStats);

        // Assert: Verify maximum values are saved
        assertEquals(Long.MAX_VALUE, savedStats.getId(), "Should save maximum ID value");
        assertEquals(Long.MAX_VALUE, savedStats.getEventCount(), "Should save maximum event count");
        verify(statsRepo, times(1)).save(maxStats);
    }

    @DisplayName("Should save stats with zero values")
    @Test
    void testSaveStatsWithZeroValues() {
        // Arrange: Create stats with zero values
        Stats zeroStats = new Stats();
        zeroStats.setId(0L);
        zeroStats.setEventCount(0L);
        zeroStats.setDonationCount(0L);
        zeroStats.setGiftPoolCount(0L);

        when(statsRepo.save(any(Stats.class))).thenReturn(zeroStats);

        // Act: Save stats with zero values
        Stats savedStats = statsRepo.save(zeroStats);

        // Assert: Verify zero values are saved
        assertEquals(0L, savedStats.getId(), "Should save zero ID value");
        assertEquals(0L, savedStats.getEventCount(), "Should save zero event count");
        verify(statsRepo, times(1)).save(zeroStats);
    }

    // ==================== FindById Method Tests ====================

    @DisplayName("Should find stats by ID successfully")
    @Test
    void testFindByIdSuccess() {
        // Arrange: Setup mock to return stats
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));

        // Act: Find stats by ID
        Optional<Stats> foundStats = statsRepo.findById(1L);

        // Assert: Verify stats was found
        assertTrue(foundStats.isPresent(), "Stats should be present");
        assertEquals(testStats.getId(), foundStats.get().getId(), "Found stats ID should match");
        verify(statsRepo, times(1)).findById(1L);
    }

    @DisplayName("Should return empty Optional when stats not found")
    @Test
    void testFindByIdNotFound() {
        // Arrange: Setup mock to return empty Optional
        when(statsRepo.findById(999L)).thenReturn(Optional.empty());

        // Act: Find stats by non-existent ID
        Optional<Stats> foundStats = statsRepo.findById(999L);

        // Assert: Verify Optional is empty
        assertFalse(foundStats.isPresent(), "Stats should not be present");
        assertTrue(foundStats.isEmpty(), "Optional should be empty");
        verify(statsRepo, times(1)).findById(999L);
    }

    @DisplayName("Should find stats by zero ID")
    @Test
    void testFindByZeroId() {
        // Arrange: Setup stats with ID 0
        Stats zeroIdStats = new Stats();
        zeroIdStats.setId(0L);

        when(statsRepo.findById(0L)).thenReturn(Optional.of(zeroIdStats));

        // Act: Find stats by zero ID
        Optional<Stats> foundStats = statsRepo.findById(0L);

        // Assert: Verify stats was found
        assertTrue(foundStats.isPresent(), "Stats with zero ID should be found");
        verify(statsRepo, times(1)).findById(0L);
    }

    @DisplayName("Should find stats by null ID")
    @Test
    void testFindByNullId() {
        // Arrange: Setup mock to return empty when null ID
        when(statsRepo.findById(null)).thenReturn(Optional.empty());

        // Act: Find stats by null ID
        Optional<Stats> foundStats = statsRepo.findById(null);

        // Assert: Verify Optional is empty
        assertFalse(foundStats.isPresent(), "Stats with null ID should not be found");
        verify(statsRepo, times(1)).findById(null);
    }

    @DisplayName("Should find stats by maximum Long ID")
    @Test
    void testFindByMaximumId() {
        // Arrange: Create stats with maximum ID
        Stats maxStats = new Stats();
        maxStats.setId(Long.MAX_VALUE);

        when(statsRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.of(maxStats));

        // Act: Find stats by maximum ID
        Optional<Stats> foundStats = statsRepo.findById(Long.MAX_VALUE);

        // Assert: Verify stats was found
        assertTrue(foundStats.isPresent(), "Stats with maximum ID should be found");
        assertEquals(Long.MAX_VALUE, foundStats.get().getId(), "Found stats ID should be maximum");
        verify(statsRepo, times(1)).findById(Long.MAX_VALUE);
    }

    // ==================== FindAll Method Tests ====================

    @DisplayName("Should find all stats successfully")
    @Test
    void testFindAllSuccess() {
        // Arrange: Create multiple stats objects
        Stats stats1 = new Stats();
        stats1.setId(1L);
        stats1.setEventCount(10L);

        Stats stats2 = new Stats();
        stats2.setId(2L);
        stats2.setEventCount(20L);

        List<Stats> statsList = List.of(stats1, stats2);

        when(statsRepo.findAll()).thenReturn(statsList);

        // Act: Find all stats
        List<Stats> foundStats = statsRepo.findAll();

        // Assert: Verify all stats were found
        assertNotNull(foundStats, "Stats list should not be null");
        assertEquals(2, foundStats.size(), "Should find 2 stats objects");
        assertEquals(stats1.getId(), foundStats.get(0).getId(), "First stats should match");
        verify(statsRepo, times(1)).findAll();
    }

    @DisplayName("Should return empty list when no stats found")
    @Test
    void testFindAllEmpty() {
        // Arrange: Setup mock to return empty list
        when(statsRepo.findAll()).thenReturn(List.of());

        // Act: Find all stats
        List<Stats> foundStats = statsRepo.findAll();

        // Assert: Verify list is empty
        assertNotNull(foundStats, "Stats list should not be null");
        assertTrue(foundStats.isEmpty(), "Stats list should be empty");
        assertEquals(0, foundStats.size(), "Stats list size should be 0");
        verify(statsRepo, times(1)).findAll();
    }

    @DisplayName("Should find all stats with various values")
    @Test
    void testFindAllWithVariousValues() {
        // Arrange: Create stats with different values
        Stats stats1 = new Stats();
        stats1.setId(1L);
        stats1.setEventCount(100L);
        stats1.setDonationCount(50L);

        Stats stats2 = new Stats();
        stats2.setId(2L);
        stats2.setEventCount(0L);
        stats2.setDonationCount(null);

        Stats stats3 = new Stats();
        stats3.setId(Long.MAX_VALUE);
        stats3.setEventCount(Long.MAX_VALUE);

        List<Stats> statsList = List.of(stats1, stats2, stats3);

        when(statsRepo.findAll()).thenReturn(statsList);

        // Act: Find all stats
        List<Stats> foundStats = statsRepo.findAll();

        // Assert: Verify all stats were found with correct values
        assertEquals(3, foundStats.size(), "Should find 3 stats");
        assertEquals(100L, foundStats.get(0).getEventCount(), "First stats event count should be 100");
        assertNull(foundStats.get(1).getDonationCount(), "Second stats donation count should be null");
        assertEquals(Long.MAX_VALUE, foundStats.get(2).getId(), "Third stats ID should be maximum");
        verify(statsRepo, times(1)).findAll();
    }

    // ==================== Delete Method Tests ====================

    @DisplayName("Should delete stats by ID successfully")
    @Test
    void testDeleteByIdSuccess() {
        // Arrange: Setup mock for delete
        doNothing().when(statsRepo).deleteById(1L);

        // Act: Delete stats by ID
        statsRepo.deleteById(1L);

        // Assert: Verify delete was called
        verify(statsRepo, times(1)).deleteById(1L);
    }

    @DisplayName("Should delete stats object successfully")
    @Test
    void testDeleteStatsObjectSuccess() {
        // Arrange: Setup mock for delete
        doNothing().when(statsRepo).delete(testStats);

        // Act: Delete stats object
        statsRepo.delete(testStats);

        // Assert: Verify delete was called with correct object
        verify(statsRepo, times(1)).delete(testStats);
    }

    @DisplayName("Should delete stats with null ID")
    @Test
    void testDeleteStatsWithNullId() {
        // Arrange: Create stats with null ID
        Stats nullIdStats = new Stats();
        nullIdStats.setId(null);

        doNothing().when(statsRepo).delete(nullIdStats);

        // Act: Delete stats with null ID
        statsRepo.delete(nullIdStats);

        // Assert: Verify delete was called
        verify(statsRepo, times(1)).delete(nullIdStats);
    }

    @DisplayName("Should delete all stats")
    @Test
    void testDeleteAllStats() {
        // Arrange: Setup mock for deleteAll
        doNothing().when(statsRepo).deleteAll();

        // Act: Delete all stats
        statsRepo.deleteAll();

        // Assert: Verify deleteAll was called
        verify(statsRepo, times(1)).deleteAll();
    }

    @DisplayName("Should delete multiple stats objects")
    @Test
    void testDeleteMultipleStatsObjects() {
        // Arrange: Create multiple stats objects
        Stats stats1 = new Stats();
        stats1.setId(1L);

        Stats stats2 = new Stats();
        stats2.setId(2L);

        List<Stats> statsList = List.of(stats1, stats2);

        doNothing().when(statsRepo).deleteAll(statsList);

        // Act: Delete all stats in the list
        statsRepo.deleteAll(statsList);

        // Assert: Verify deleteAll was called with list
        verify(statsRepo, times(1)).deleteAll(statsList);
    }

    // ==================== Count Method Tests ====================

    @DisplayName("Should count all stats successfully")
    @Test
    void testCountAllStatsSuccess() {
        // Arrange: Setup mock to return count
        when(statsRepo.count()).thenReturn(5L);

        // Act: Count all stats
        long count = statsRepo.count();

        // Assert: Verify count is correct
        assertEquals(5L, count, "Count should be 5");
        verify(statsRepo, times(1)).count();
    }

    @DisplayName("Should return zero count when no stats exist")
    @Test
    void testCountAllStatsZero() {
        // Arrange: Setup mock to return zero
        when(statsRepo.count()).thenReturn(0L);

        // Act: Count all stats
        long count = statsRepo.count();

        // Assert: Verify count is zero
        assertEquals(0L, count, "Count should be 0");
        verify(statsRepo, times(1)).count();
    }

    @DisplayName("Should count stats as maximum Long value")
    @Test
    void testCountStatsMaximum() {
        // Arrange: Setup mock to return maximum count
        when(statsRepo.count()).thenReturn(Long.MAX_VALUE);

        // Act: Count all stats
        long count = statsRepo.count();

        // Assert: Verify maximum count is returned
        assertEquals(Long.MAX_VALUE, count, "Count should be maximum Long value");
        verify(statsRepo, times(1)).count();
    }

    // ==================== Exists Method Tests ====================

    @DisplayName("Should check if stats exists by ID successfully")
    @Test
    void testExistsByIdSuccess() {
        // Arrange: Setup mock to return true
        when(statsRepo.existsById(1L)).thenReturn(true);

        // Act: Check if stats exists
        boolean exists = statsRepo.existsById(1L);

        // Assert: Verify stats exists
        assertTrue(exists, "Stats should exist");
        verify(statsRepo, times(1)).existsById(1L);
    }

    @DisplayName("Should return false when stats does not exist by ID")
    @Test
    void testExistsByIdNotFound() {
        // Arrange: Setup mock to return false
        when(statsRepo.existsById(999L)).thenReturn(false);

        // Act: Check if stats exists
        boolean exists = statsRepo.existsById(999L);

        // Assert: Verify stats does not exist
        assertFalse(exists, "Stats should not exist");
        verify(statsRepo, times(1)).existsById(999L);
    }

    @DisplayName("Should check if stats exists with null ID")
    @Test
    void testExistsByNullId() {
        // Arrange: Setup mock to return false
        when(statsRepo.existsById(null)).thenReturn(false);

        // Act: Check if stats exists
        boolean exists = statsRepo.existsById(null);

        // Assert: Verify stats does not exist with null ID
        assertFalse(exists, "Stats should not exist with null ID");
        verify(statsRepo, times(1)).existsById(null);
    }

    // ==================== Flush Method Tests ====================

    @DisplayName("Should flush stats to database successfully")
    @Test
    void testFlushSuccess() {
        // Arrange: Setup mock for flush
        doNothing().when(statsRepo).flush();

        // Act: Flush stats
        statsRepo.flush();

        // Assert: Verify flush was called
        verify(statsRepo, times(1)).flush();
    }

    // ==================== SaveAndFlush Method Tests ====================

    @DisplayName("Should save and flush stats successfully")
    @Test
    void testSaveAndFlushSuccess() {
        // Arrange: Setup mock to return saved stats
        when(statsRepo.saveAndFlush(any(Stats.class))).thenReturn(testStats);

        // Act: Save and flush stats
        Stats savedStats = statsRepo.saveAndFlush(testStats);

        // Assert: Verify save and flush was called
        assertNotNull(savedStats, "Saved stats should not be null");
        assertEquals(testStats.getId(), savedStats.getId(), "Saved stats ID should match");
        verify(statsRepo, times(1)).saveAndFlush(testStats);
    }

    // ==================== GetOne/FindById Optional Tests ====================

    @DisplayName("Should handle multiple calls to same repository method")
    @Test
    void testMultipleFindByIdCalls() {
        // Arrange: Setup mock to return same stats
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));

        // Act: Call findById multiple times
        Optional<Stats> firstCall = statsRepo.findById(1L);
        Optional<Stats> secondCall = statsRepo.findById(1L);
        Optional<Stats> thirdCall = statsRepo.findById(1L);

        // Assert: Verify all calls return correct stats
        assertTrue(firstCall.isPresent(), "First call should return stats");
        assertTrue(secondCall.isPresent(), "Second call should return stats");
        assertTrue(thirdCall.isPresent(), "Third call should return stats");
        verify(statsRepo, times(3)).findById(1L);
    }

    @DisplayName("Should handle different repository method calls in sequence")
    @Test
    void testSequentialRepositoryMethodCalls() {
        // Arrange: Setup mocks for multiple operations
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.count()).thenReturn(1L);
        doNothing().when(statsRepo).deleteById(1L);

        // Act: Call methods in sequence
        Stats savedStats = statsRepo.save(testStats);
        Optional<Stats> foundStats = statsRepo.findById(1L);
        long count = statsRepo.count();
        statsRepo.deleteById(1L);

        // Assert: Verify all operations succeeded
        assertNotNull(savedStats, "Save should succeed");
        assertTrue(foundStats.isPresent(), "Find should succeed");
        assertEquals(1L, count, "Count should be 1");
        verify(statsRepo, times(1)).save(any(Stats.class));
        verify(statsRepo, times(1)).findById(1L);
        verify(statsRepo, times(1)).count();
        verify(statsRepo, times(1)).deleteById(1L);
    }

    @DisplayName("Should verify mock interactions for repository operations")
    @Test
    void testMockInteractionVerification() {
        // Arrange: Setup mocks
        Stats stats1 = new Stats();
        stats1.setId(1L);

        when(statsRepo.save(stats1)).thenReturn(stats1);

        // Act: Perform operations
        statsRepo.save(stats1);
        statsRepo.save(stats1);

        // Assert: Verify exact number of invocations
        verify(statsRepo, times(2)).save(stats1);
        verify(statsRepo, never()).delete(any());
        verify(statsRepo, never()).findById(anyLong());
    }

    @DisplayName("Should handle ArgumentMatchers for flexible mock matching")
    @Test
    void testArgumentMatchersFlexibleMatching() {
        // Arrange: Setup mock with ArgumentMatchers
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        // Act: Call with any Stats object
        Stats result1 = statsRepo.save(new Stats());
        Stats result2 = statsRepo.save(testStats);

        // Assert: Both calls should return the mocked result
        assertNotNull(result1, "Result should not be null");
        assertNotNull(result2, "Result should not be null");
        verify(statsRepo, times(2)).save(any(Stats.class));
    }
}
