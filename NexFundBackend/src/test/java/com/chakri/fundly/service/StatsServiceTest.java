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

import java.lang.reflect.Field;
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
        clearServiceCache();
    }

    private void clearServiceCache() {
        try {
            Field cacheField = StatsService.class.getDeclaredField("statsCache");
            cacheField.setAccessible(true);
            cacheField.set(statsService, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Ignore
        }
    }

    @Test
    public void testInit_WhenStatsDoesNotExist_ShouldCreateNewStats() {
        when(statsRepo.existsById(1L)).thenReturn(false);
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        statsService.init();

        verify(statsRepo).existsById(1L);
        verify(statsRepo).save(any(Stats.class));
        Stats stats = statsService.getStats();
        assertNotNull(stats);
        assertEquals(1L, stats.getId());
        assertEquals(0L, stats.getEventCount());
        assertEquals(0L, stats.getDonationCount());
        assertEquals(0L, stats.getGiftPoolCount());
    }

    @Test
    public void testInit_WhenStatsExists_ShouldLoadExistingStats() {
        testStats.setEventCount(5L);
        testStats.setDonationCount(3L);
        testStats.setGiftPoolCount(2L);
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));

        statsService.init();

        verify(statsRepo).existsById(1L);
        verify(statsRepo).findById(1L);
        verify(statsRepo, never()).save(any(Stats.class));
        Stats stats = statsService.getStats();
        assertNotNull(stats);
        assertEquals(5L, stats.getEventCount());
        assertEquals(3L, stats.getDonationCount());
        assertEquals(2L, stats.getGiftPoolCount());
    }

    @Test
    public void testInit_WhenStatsExists_ButFindByIdReturnsEmpty_ShouldInitializeDefaultStats() {
        when(statsRepo.existsById(1L)).thenReturn(true);
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());

        statsService.init();

        verify(statsRepo).existsById(1L);
        verify(statsRepo).findById(1L);

        Stats stats = statsService.getStats();
        assertNotNull(stats);
        assertEquals(1L, stats.getId());
        assertEquals(0L, stats.getEventCount());
        assertEquals(0L, stats.getDonationCount());
        assertEquals(0L, stats.getGiftPoolCount());
    }

    @Test
    public void testInit_WhenExceptionOccursDuringCheck_ShouldCreateDefaultStatsInMemory() {
        when(statsRepo.existsById(1L)).thenThrow(new RuntimeException("Database error"));

        statsService.init();

        verify(statsRepo).existsById(1L);

        Stats stats = statsService.getStats();
        assertNotNull(stats);
        assertEquals(1L, stats.getId());
        assertEquals(0L, stats.getEventCount());
        assertEquals(0L, stats.getDonationCount());
        assertEquals(0L, stats.getGiftPoolCount());
    }

    @Test
    public void testInit_WhenExceptionOccursDuringSave_ShouldCreateDefaultStatsInMemory() {
        when(statsRepo.existsById(1L)).thenReturn(false);
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));

        statsService.init();

        verify(statsRepo).existsById(1L);
        verify(statsRepo).save(any(Stats.class));

        Stats stats = statsService.getStats();
        assertNotNull(stats);
        assertEquals(1L, stats.getId());
        assertEquals(0L, stats.getEventCount());
    }

    @Test
    public void testGetStats_WhenStatsExistsInDatabase_ShouldReturnStats() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));

        Stats result = statsService.getStats();

        verify(statsRepo).findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0L, result.getEventCount());
    }

    @Test
    public void testGetStats_WhenStatsDoesNotExistInDatabase_ShouldReturnDefaultStats() {
        when(statsRepo.findById(1L)).thenReturn(Optional.empty());

        Stats result = statsService.getStats();

        verify(statsRepo).findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0L, result.getEventCount());
    }

    @Test
    public void testGetStats_WhenExceptionThrown_ShouldReturnDefaultStats() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("Database error"));

        Stats result = statsService.getStats();

        verify(statsRepo).findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetStats_CacheIsUsedOnMultipleCalls() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));

        Stats result1 = statsService.getStats();
        Stats result2 = statsService.getStats();

        assertEquals(result1.getId(), result2.getId());
        verify(statsRepo, times(1)).findById(1L);
    }

    @Test
    public void testIncrementEventCount_IncrementsCorrectly() {
        testStats.setEventCount(5L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        statsService.incrementEventCount();

        verify(statsRepo).save(any(Stats.class));
        assertEquals(6L, testStats.getEventCount());
    }

    @Test
    public void testIncrementDonationCount_IncrementsCorrectly() {
        testStats.setDonationCount(3L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        statsService.incrementDonationCount();

        verify(statsRepo).save(any(Stats.class));
        assertEquals(4L, testStats.getDonationCount());
    }

    @Test
    public void testIncrementGiftPoolCount_IncrementsCorrectly() {
        testStats.setGiftPoolCount(2L);
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        statsService.incrementGiftPoolCount();

        verify(statsRepo).save(any(Stats.class));
        assertEquals(3L, testStats.getGiftPoolCount());
    }

    @Test
    public void testIncrementMethods_HandleExceptionsGracefully() {
        when(statsRepo.findById(1L)).thenThrow(new RuntimeException("DB error"));
        when(statsRepo.save(any(Stats.class))).thenThrow(new RuntimeException("Save failed"));

        assertDoesNotThrow(() -> statsService.incrementEventCount());
        assertDoesNotThrow(() -> statsService.incrementDonationCount());
        assertDoesNotThrow(() -> statsService.incrementGiftPoolCount());
    }

    @Test
    public void testMultipleIncrements_WorkProperly() {
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
    public void testMultipleSequentialIncrements_OnSameCounter() {
        when(statsRepo.findById(1L)).thenReturn(Optional.of(testStats));
        when(statsRepo.save(any(Stats.class))).thenReturn(testStats);

        for (int i = 1; i <= 5; i++) {
            statsService.incrementEventCount();
            assertEquals(i, testStats.getEventCount());
        }
        verify(statsRepo, times(5)).save(any(Stats.class));
    }
}
