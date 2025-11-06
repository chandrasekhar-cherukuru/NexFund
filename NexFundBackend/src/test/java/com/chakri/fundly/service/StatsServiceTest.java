package com.chakri.fundly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.Stats;
import com.chakri.fundly.repo.StatsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private StatsRepo statsRepo;

    @InjectMocks
    private StatsService statsService;

    private Stats testStats;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testStats = new Stats();
        testStats.setId(1L);
        testStats.setEventCount(0L);
        testStats.setDonationCount(0L);
        testStats.setGiftPoolCount(0L);
    }

    @Test
    public void testInit_WhenStatsDoesNotExist_ShouldCreateNewStats() {
        when(statsRepo.existsById(1L)).thenReturn(false);
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.init();
        verify(statsRepo, times(1)).existsById(1L);
        verify(statsRepo, times(1)).save(any(Stats.class));
        assertNotNull(statsService.getStats());
        assertEquals(1L, statsService.getStats().getId());
        assertEquals(0L, statsService.getStats().getEventCount());
        assertEquals(0L, statsService.getStats().getDonationCount());
        assertEquals(0L, statsService.getStats().getGiftPoolCount());
    }

    @Test
    public void testInit_WhenStatsExists_ShouldLoadExistingStats() {
        testStats.setEventCount(5L);
        testStats.setDonationCount(3L);
        testStats.setGiftPoolCount(2L);
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        statsService.init();
        verify(statsRepo, times(1)).existsById(1L);
        verify(statsRepo, times(1)).findById(1L);
        verify(statsRepo, never()).save(any(Stats.class));
        assertNotNull(statsService.getStats());
        assertEquals(5L, statsService.getStats().getEventCount());
        assertEquals(3L, statsService.getStats().getDonationCount());
        assertEquals(2L, statsService.getStats().getGiftPoolCount());
    }

    @Test
    public void testInit_WhenStatsExists_ButFindByIdReturnsEmpty_ShouldHandleGracefully() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        statsService.init();
        verify(statsRepo, times(1)).existsById(1L);
        verify(statsRepo, times(1)).findById(1L);
        assertNotNull(statsService.getStats());
        assertEquals(1L, statsService.getStats().getId());
    }

    @Test
    public void testInit_WhenExceptionOccursDuringCheck_ShouldCreateDefaultStatsInMemory() {
        when(statsRepo.existsById(1L)).thenThrow(new RuntimeException("Database error"));
        statsService.init();
        verify(statsRepo, times(1)).existsById(1L);
        assertNotNull(statsService.getStats());
        assertEquals(1L, statsService.getStats().getId());
        assertEquals(0L, statsService.getStats().getEventCount());
        assertEquals(0L, statsService.getStats().getDonationCount());
        assertEquals(0L, statsService.getStats().getGiftPoolCount());
    }

    @Test
    public void testInit_WhenExceptionOccursDuringSave_ShouldCreateDefaultStatsInMemory() {
        when(statsRepo.existsById(1L)).thenReturn(false);
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));
        statsService.init();
        verify(statsRepo, times(1)).existsById(1L);
        verify(statsRepo, times(1)).save(any(Stats.class));
        assertNotNull(statsService.getStats());
        assertEquals(1L, statsService.getStats().getId());
        assertEquals(0L, statsService.getStats().getEventCount());
    }

    @Test
    public void testGetStats_WhenStatsExistsInDatabase_ShouldReturnStats() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        Stats result = statsService.getStats();
        verify(statsRepo, times(1)).findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0L, result.getEventCount());
    }

    @Test
    public void testGetStats_WhenStatsDoesNotExistInDatabase_ShouldReturnCachedStats() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        Stats result = statsService.getStats();
        verify(statsRepo, times(1)).findById(1L);
        assertNotNull(result);
    }

    @Test
    public void testGetStats_WhenExceptionThrown_ShouldReturnCachedStats() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database connection error"));
        Stats result = statsService.getStats();
        verify(statsRepo, times(1)).findById(1L);
        assertNotNull(result);
    }

    @Test
    public void testGetStats_WhenCacheIsNull_ShouldReturnNewStatsWithId() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        Stats result = statsService.getStats();
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetStats_MultipleCallsInSequence_ShouldReturnConsistentResults() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        Stats result1 = statsService.getStats();
        Stats result2 = statsService.getStats();
        assertEquals(result1.getId(), result2.getId());
        verify(statsRepo, times(2)).findById(1L);
    }

    @Test
    public void testIncrementEventCount_WhenStatsExists_ShouldIncrementByOne() {
        testStats.setEventCount(5L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        verify(statsRepo, times(1)).findById(1L);
        verify(statsRepo, times(1)).save(any(Stats.class));
        assertEquals(6L, testStats.getEventCount());
    }

    @Test
    public void testIncrementEventCount_FromZero_ShouldIncrementToOne() {
        testStats.setEventCount(0L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        assertEquals(1L, testStats.getEventCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementEventCount_WithLargeNumber_ShouldHandleCorrectly() {
        testStats.setEventCount(Long.MAX_VALUE - 1);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        assertEquals(Long.MAX_VALUE, testStats.getEventCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementEventCount_WhenGetStatsReturnsNull_ShouldNotThrowException() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        when(statsRepo.save(any(Stats.class))).thenReturn(null);
        assertDoesNotThrow(() -> statsService.incrementEventCount());
    }

    @Test
    public void testIncrementEventCount_WhenExceptionThrown_ShouldHandleGracefully() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database error"));
        assertDoesNotThrow(() -> statsService.incrementEventCount());
        verify(statsRepo, times(1)).findById(1L);
    }

    @Test
    public void testIncrementEventCount_WhenSaveThrowsException_ShouldHandleGracefully() {
        testStats.setEventCount(5L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));
        assertDoesNotThrow(() -> statsService.incrementEventCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementDonationCount_WhenStatsExists_ShouldIncrementByOne() {
        testStats.setDonationCount(3L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementDonationCount();
        verify(statsRepo, times(1)).findById(1L);
        verify(statsRepo, times(1)).save(any(Stats.class));
        assertEquals(4L, testStats.getDonationCount());
    }

    @Test
    public void testIncrementDonationCount_FromZero_ShouldIncrementToOne() {
        testStats.setDonationCount(0L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementDonationCount();
        assertEquals(1L, testStats.getDonationCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementDonationCount_WithLargeNumber_ShouldHandleCorrectly() {
        testStats.setDonationCount(Long.MAX_VALUE - 1);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementDonationCount();
        assertEquals(Long.MAX_VALUE, testStats.getDonationCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementDonationCount_WhenGetStatsReturnsNull_ShouldNotThrowException() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> statsService.incrementDonationCount());
    }

    @Test
    public void testIncrementDonationCount_WhenExceptionThrown_ShouldHandleGracefully() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database error"));
        assertDoesNotThrow(() -> statsService.incrementDonationCount());
    }

    @Test
    public void testIncrementDonationCount_WhenSaveThrowsException_ShouldHandleGracefully() {
        testStats.setDonationCount(5L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));
        assertDoesNotThrow(() -> statsService.incrementDonationCount());
    }

    @Test
    public void testIncrementGiftPoolCount_WhenStatsExists_ShouldIncrementByOne() {
        testStats.setGiftPoolCount(2L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementGiftPoolCount();
        verify(statsRepo, times(1)).findById(1L);
        verify(statsRepo, times(1)).save(any(Stats.class));
        assertEquals(3L, testStats.getGiftPoolCount());
    }

    @Test
    public void testIncrementGiftPoolCount_FromZero_ShouldIncrementToOne() {
        testStats.setGiftPoolCount(0L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementGiftPoolCount();
        assertEquals(1L, testStats.getGiftPoolCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementGiftPoolCount_WithLargeNumber_ShouldHandleCorrectly() {
        testStats.setGiftPoolCount(Long.MAX_VALUE - 1);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementGiftPoolCount();
        assertEquals(Long.MAX_VALUE, testStats.getGiftPoolCount());
        verify(statsRepo, times(1)).save(any(Stats.class));
    }

    @Test
    public void testIncrementGiftPoolCount_WhenGetStatsReturnsNull_ShouldNotThrowException() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> statsService.incrementGiftPoolCount());
    }

    @Test
    public void testIncrementGiftPoolCount_WhenExceptionThrown_ShouldHandleGracefully() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database error"));
        assertDoesNotThrow(() -> statsService.incrementGiftPoolCount());
    }

    @Test
    public void testIncrementGiftPoolCount_WhenSaveThrowsException_ShouldHandleGracefully() {
        testStats.setGiftPoolCount(5L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));
        assertDoesNotThrow(() -> statsService.incrementGiftPoolCount());
    }

    @Test
    public void testMultipleIncrements_AllCounters_ShouldIncrementIndependently() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        statsService.incrementDonationCount();
        statsService.incrementGiftPoolCount();
        assertEquals(1L, testStats.getEventCount());
        assertEquals(1L, testStats.getDonationCount());
        assertEquals(1L, testStats.getGiftPoolCount());
        verify(statsRepo, times(3)).save(any(Stats.class));
    }

    @Test
    public void testMultipleIncrements_SameCounter_ShouldStackCorrectly() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        for (int i = 0; i < 5; i++) {
            statsService.incrementEventCount();
        }
        assertEquals(5L, testStats.getEventCount());
        verify(statsRepo, times(5)).save(any(Stats.class));
    }

    @Test
    public void testGetStats_AfterInit_ShouldReturnInitializedStats() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        statsService.init();
        Stats result = statsService.getStats();
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testAllMethodsCalled_ShouldVerifyRepositoryInteractions() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.init();
        statsService.incrementEventCount();
        statsService.incrementDonationCount();
        statsService.incrementGiftPoolCount();
        Stats result = statsService.getStats();
        assertNotNull(result);
        verify(statsRepo, atLeast(1)).existsById(1L);
        verify(statsRepo, atLeast(4)).findById(1L);
        verify(statsRepo, times(3)).save(any(Stats.class));
    }

    @Test
    public void testIncrementEventCount_ShouldUpdateCacheAndDatabase() {
        testStats.setEventCount(10L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        assertEquals(11L, testStats.getEventCount());
        verify(statsRepo).save(any(Stats.class));
    }

    @Test
    public void testIncrementDonationCount_ShouldUpdateCacheAndDatabase() {
        testStats.setDonationCount(7L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementDonationCount();
        assertEquals(8L, testStats.getDonationCount());
        verify(statsRepo).save(any(Stats.class));
    }

    @Test
    public void testIncrementGiftPoolCount_ShouldUpdateCacheAndDatabase() {
        testStats.setGiftPoolCount(4L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementGiftPoolCount();
        assertEquals(5L, testStats.getGiftPoolCount());
        verify(statsRepo).save(any(Stats.class));
    }

    @Test
    public void testGetStats_CacheUpdateAfterIncrement_ShouldReflectChanges() {
        testStats.setEventCount(2L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        Stats cachedStats = statsService.getStats();
        assertEquals(3L, cachedStats.getEventCount());
    }

    @Test
    public void testInit_ShouldNotCallSaveWhenStatsExists() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        statsService.init();
        verify(statsRepo, never()).save(any(Stats.class));
    }

    @Test
    public void testMultipleConcurrentIncrements_ShouldMaintainOrder() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        statsService.incrementDonationCount();
        statsService.incrementGiftPoolCount();
        statsService.incrementEventCount();
        assertEquals(2L, testStats.getEventCount());
        assertEquals(1L, testStats.getDonationCount());
        assertEquals(1L, testStats.getGiftPoolCount());
        verify(statsRepo, times(4)).save(any(Stats.class));
    }

    @Test
    public void testInit_WhenFindByIdThrowsException_ShouldCreateDefaultStats() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database error"));
        statsService.init();
        assertNotNull(statsService.getStats());
    }

    @Test
    public void testGetStats_WhenCacheIsNotNull_ShouldReturnCachedValue() {
        Stats cachedStats = new Stats();
        cachedStats.setId(1L);
        cachedStats.setEventCount(10L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(cachedStats));
        Stats result1 = statsService.getStats();
        Stats result2 = statsService.getStats();
        assertEquals(10L, result1.getEventCount());
        assertEquals(10L, result2.getEventCount());
    }

    @Test
    public void testIncrementEventCount_MultipleTimes_ShouldIncrementSequentially() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementEventCount();
        assertEquals(1L, testStats.getEventCount());
        statsService.incrementEventCount();
        assertEquals(2L, testStats.getEventCount());
        statsService.incrementEventCount();
        assertEquals(3L, testStats.getEventCount());
        verify(statsRepo, times(3)).save(any(Stats.class));
    }

    @Test
    public void testIncrementDonationCount_MultipleTimes_ShouldIncrementSequentially() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementDonationCount();
        assertEquals(1L, testStats.getDonationCount());
        statsService.incrementDonationCount();
        assertEquals(2L, testStats.getDonationCount());
        statsService.incrementDonationCount();
        assertEquals(3L, testStats.getDonationCount());
        verify(statsRepo, times(3)).save(any(Stats.class));
    }

    @Test
    public void testIncrementGiftPoolCount_MultipleTimes_ShouldIncrementSequentially() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);
        statsService.incrementGiftPoolCount();
        assertEquals(1L, testStats.getGiftPoolCount());
        statsService.incrementGiftPoolCount();
        assertEquals(2L, testStats.getGiftPoolCount());
        statsService.incrementGiftPoolCount();
        assertEquals(3L, testStats.getGiftPoolCount());
        verify(statsRepo, times(3)).save(any(Stats.class));
    }
}
