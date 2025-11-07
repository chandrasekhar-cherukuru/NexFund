package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stats Entity Test Suite")
class StatsTest {

    @InjectMocks
    private Stats stats;

    @BeforeEach
    void setUp() {
        // Mockito will automatically instantiate stats using MockitoExtension
        // This method ensures clean state before each test
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should initialize Stats with default values when no-arg constructor is called")
    void testConstructorInitializesDefaultValues() {
        // Arrange
        Stats newStats = new Stats();

        // Act & Assert
        assertNotNull(newStats, "Stats object should not be null after construction");
        assertEquals(0L, newStats.getEventCount(), "Event count should initialize to 0L");
        assertEquals(0L, newStats.getDonationCount(), "Donation count should initialize to 0L");
        assertEquals(0L, newStats.getGiftPoolCount(), "Gift pool count should initialize to 0L");
    }

    @Test
    @DisplayName("Should have null id when initialized through constructor")
    void testConstructorIdIsNull() {
        // Arrange
        Stats newStats = new Stats();

        // Act & Assert
        assertNull(newStats.getId(), "Id should be null after construction as it's auto-generated");
    }

    // ==================== ID Getter/Setter Tests ====================

    @Test
    @DisplayName("Should set and get ID correctly when ID is positive long value")
    void testSetAndGetIdWithPositiveValue() {
        // Arrange
        Long testId = 1L;

        // Act
        stats.setId(testId);
        Long retrievedId = stats.getId();

        // Assert
        assertEquals(testId, retrievedId, "Retrieved ID should match set ID");
    }

    @Test
    @DisplayName("Should set and get ID correctly when ID is large long value")
    void testSetAndGetIdWithLargeValue() {
        // Arrange
        Long testId = 9223372036854775807L; // Max Long value

        // Act
        stats.setId(testId);
        Long retrievedId = stats.getId();

        // Assert
        assertEquals(testId, retrievedId, "Retrieved ID should match large set ID");
    }

    @Test
    @DisplayName("Should set and get ID correctly when ID is zero")
    void testSetAndGetIdWithZero() {
        // Arrange
        Long testId = 0L;

        // Act
        stats.setId(testId);
        Long retrievedId = stats.getId();

        // Assert
        assertEquals(0L, retrievedId, "Retrieved ID should be zero when set to zero");
    }

    @Test
    @DisplayName("Should set and get ID correctly when ID is null")
    void testSetAndGetIdWithNull() {
        // Arrange
        stats.setId(100L); // First set a value

        // Act
        stats.setId(null);
        Long retrievedId = stats.getId();

        // Assert
        assertNull(retrievedId, "Retrieved ID should be null when set to null");
    }

    @Test
    @DisplayName("Should handle multiple ID assignments correctly")
    void testMultipleIdAssignments() {
        // Arrange
        Long firstId = 10L;
        Long secondId = 20L;
        Long thirdId = 30L;

        // Act
        stats.setId(firstId);
        assertEquals(firstId, stats.getId());

        stats.setId(secondId);
        assertEquals(secondId, stats.getId());

        stats.setId(thirdId);
        Long finalId = stats.getId();

        // Assert
        assertEquals(thirdId, finalId, "Final ID should be the last assigned value");
    }

    // ==================== Event Count Getter/Setter Tests ====================

    @Test
    @DisplayName("Should set and get event count correctly when value is positive")
    void testSetAndGetEventCountWithPositiveValue() {
        // Arrange
        Long eventCount = 100L;

        // Act
        stats.setEventCount(eventCount);
        Long retrievedCount = stats.getEventCount();

        // Assert
        assertEquals(eventCount, retrievedCount, "Retrieved event count should match set value");
    }

    @Test
    @DisplayName("Should initialize event count to zero by default")
    void testEventCountDefaultValue() {
        // Arrange & Act
        Long eventCount = stats.getEventCount();

        // Assert
        assertEquals(0L, eventCount, "Event count should default to 0L");
    }

    @Test
    @DisplayName("Should set and get event count when value is zero")
    void testSetAndGetEventCountWithZero() {
        // Arrange
        stats.setEventCount(50L); // Set initial value

        // Act
        stats.setEventCount(0L);
        Long retrievedCount = stats.getEventCount();

        // Assert
        assertEquals(0L, retrievedCount, "Retrieved event count should be zero");
    }

    @Test
    @DisplayName("Should set and get event count when value is large")
    void testSetAndGetEventCountWithLargeValue() {
        // Arrange
        Long largeCount = 9223372036854775807L; // Max Long value

        // Act
        stats.setEventCount(largeCount);
        Long retrievedCount = stats.getEventCount();

        // Assert
        assertEquals(largeCount, retrievedCount, "Retrieved event count should match large value");
    }

    @Test
    @DisplayName("Should set and get event count when value is null")
    void testSetAndGetEventCountWithNull() {
        // Arrange
        stats.setEventCount(100L);

        // Act
        stats.setEventCount(null);
        Long retrievedCount = stats.getEventCount();

        // Assert
        assertNull(retrievedCount, "Retrieved event count should be null when set to null");
    }

    @Test
    @DisplayName("Should handle multiple event count assignments correctly")
    void testMultipleEventCountAssignments() {
        // Arrange
        Long firstCount = 10L;
        Long secondCount = 20L;
        Long thirdCount = 30L;

        // Act
        stats.setEventCount(firstCount);
        assertEquals(firstCount, stats.getEventCount());

        stats.setEventCount(secondCount);
        assertEquals(secondCount, stats.getEventCount());

        stats.setEventCount(thirdCount);
        Long finalCount = stats.getEventCount();

        // Assert
        assertEquals(thirdCount, finalCount, "Final event count should be the last assigned value");
    }

    // ==================== Donation Count Getter/Setter Tests ====================

    @Test
    @DisplayName("Should set and get donation count correctly when value is positive")
    void testSetAndGetDonationCountWithPositiveValue() {
        // Arrange
        Long donationCount = 50L;

        // Act
        stats.setDonationCount(donationCount);
        Long retrievedCount = stats.getDonationCount();

        // Assert
        assertEquals(donationCount, retrievedCount, "Retrieved donation count should match set value");
    }

    @Test
    @DisplayName("Should initialize donation count to zero by default")
    void testDonationCountDefaultValue() {
        // Arrange & Act
        Long donationCount = stats.getDonationCount();

        // Assert
        assertEquals(0L, donationCount, "Donation count should default to 0L");
    }

    @Test
    @DisplayName("Should set and get donation count when value is zero")
    void testSetAndGetDonationCountWithZero() {
        // Arrange
        stats.setDonationCount(75L); // Set initial value

        // Act
        stats.setDonationCount(0L);
        Long retrievedCount = stats.getDonationCount();

        // Assert
        assertEquals(0L, retrievedCount, "Retrieved donation count should be zero");
    }

    @Test
    @DisplayName("Should set and get donation count when value is large")
    void testSetAndGetDonationCountWithLargeValue() {
        // Arrange
        Long largeCount = 9223372036854775807L; // Max Long value

        // Act
        stats.setDonationCount(largeCount);
        Long retrievedCount = stats.getDonationCount();

        // Assert
        assertEquals(largeCount, retrievedCount, "Retrieved donation count should match large value");
    }

    @Test
    @DisplayName("Should set and get donation count when value is null")
    void testSetAndGetDonationCountWithNull() {
        // Arrange
        stats.setDonationCount(100L);

        // Act
        stats.setDonationCount(null);
        Long retrievedCount = stats.getDonationCount();

        // Assert
        assertNull(retrievedCount, "Retrieved donation count should be null when set to null");
    }

    @Test
    @DisplayName("Should handle multiple donation count assignments correctly")
    void testMultipleDonationCountAssignments() {
        // Arrange
        Long firstCount = 5L;
        Long secondCount = 15L;
        Long thirdCount = 25L;

        // Act
        stats.setDonationCount(firstCount);
        assertEquals(firstCount, stats.getDonationCount());

        stats.setDonationCount(secondCount);
        assertEquals(secondCount, stats.getDonationCount());

        stats.setDonationCount(thirdCount);
        Long finalCount = stats.getDonationCount();

        // Assert
        assertEquals(thirdCount, finalCount, "Final donation count should be the last assigned value");
    }

    // ==================== Gift Pool Count Getter/Setter Tests ====================

    @Test
    @DisplayName("Should set and get gift pool count correctly when value is positive")
    void testSetAndGetGiftPoolCountWithPositiveValue() {
        // Arrange
        Long giftPoolCount = 75L;

        // Act
        stats.setGiftPoolCount(giftPoolCount);
        Long retrievedCount = stats.getGiftPoolCount();

        // Assert
        assertEquals(giftPoolCount, retrievedCount, "Retrieved gift pool count should match set value");
    }

    @Test
    @DisplayName("Should initialize gift pool count to zero by default")
    void testGiftPoolCountDefaultValue() {
        // Arrange & Act
        Long giftPoolCount = stats.getGiftPoolCount();

        // Assert
        assertEquals(0L, giftPoolCount, "Gift pool count should default to 0L");
    }

    @Test
    @DisplayName("Should set and get gift pool count when value is zero")
    void testSetAndGetGiftPoolCountWithZero() {
        // Arrange
        stats.setGiftPoolCount(150L); // Set initial value

        // Act
        stats.setGiftPoolCount(0L);
        Long retrievedCount = stats.getGiftPoolCount();

        // Assert
        assertEquals(0L, retrievedCount, "Retrieved gift pool count should be zero");
    }

    @Test
    @DisplayName("Should set and get gift pool count when value is large")
    void testSetAndGetGiftPoolCountWithLargeValue() {
        // Arrange
        Long largeCount = 9223372036854775807L; // Max Long value

        // Act
        stats.setGiftPoolCount(largeCount);
        Long retrievedCount = stats.getGiftPoolCount();

        // Assert
        assertEquals(largeCount, retrievedCount, "Retrieved gift pool count should match large value");
    }

    @Test
    @DisplayName("Should set and get gift pool count when value is null")
    void testSetAndGetGiftPoolCountWithNull() {
        // Arrange
        stats.setGiftPoolCount(100L);

        // Act
        stats.setGiftPoolCount(null);
        Long retrievedCount = stats.getGiftPoolCount();

        // Assert
        assertNull(retrievedCount, "Retrieved gift pool count should be null when set to null");
    }

    @Test
    @DisplayName("Should handle multiple gift pool count assignments correctly")
    void testMultipleGiftPoolCountAssignments() {
        // Arrange
        Long firstCount = 2L;
        Long secondCount = 8L;
        Long thirdCount = 12L;

        // Act
        stats.setGiftPoolCount(firstCount);
        assertEquals(firstCount, stats.getGiftPoolCount());

        stats.setGiftPoolCount(secondCount);
        assertEquals(secondCount, stats.getGiftPoolCount());

        stats.setGiftPoolCount(thirdCount);
        Long finalCount = stats.getGiftPoolCount();

        // Assert
        assertEquals(thirdCount, finalCount, "Final gift pool count should be the last assigned value");
    }

    // ==================== Combined State Tests ====================

    @Test
    @DisplayName("Should maintain independent state for all count fields")
    void testIndependenceOfCountFields() {
        // Arrange
        Long eventCount = 100L;
        Long donationCount = 50L;
        Long giftPoolCount = 25L;

        // Act
        stats.setEventCount(eventCount);
        stats.setDonationCount(donationCount);
        stats.setGiftPoolCount(giftPoolCount);

        // Assert
        assertEquals(eventCount, stats.getEventCount(), "Event count should be independent");
        assertEquals(donationCount, stats.getDonationCount(), "Donation count should be independent");
        assertEquals(giftPoolCount, stats.getGiftPoolCount(), "Gift pool count should be independent");
    }

    @Test
    @DisplayName("Should maintain state after setting all fields multiple times")
    void testStateConsistencyAfterMultipleOperations() {
        // Arrange & Act
        stats.setId(1L);
        stats.setEventCount(100L);
        stats.setDonationCount(50L);
        stats.setGiftPoolCount(25L);

        stats.setId(2L);
        stats.setEventCount(200L);
        stats.setDonationCount(100L);
        stats.setGiftPoolCount(50L);

        // Assert
        assertEquals(2L, stats.getId(), "ID should be updated to latest value");
        assertEquals(200L, stats.getEventCount(), "Event count should be updated to latest value");
        assertEquals(100L, stats.getDonationCount(), "Donation count should be updated to latest value");
        assertEquals(50L, stats.getGiftPoolCount(), "Gift pool count should be updated to latest value");
    }

    @Test
    @DisplayName("Should handle mixed null and non-null assignments")
    void testMixedNullAndNonNullAssignments() {
        // Arrange
        stats.setEventCount(100L);
        stats.setDonationCount(50L);
        stats.setGiftPoolCount(25L);

        // Act
        stats.setEventCount(null);
        stats.setDonationCount(50L); // Keep same
        stats.setGiftPoolCount(null);

        // Assert
        assertNull(stats.getEventCount(), "Event count should be null");
        assertEquals(50L, stats.getDonationCount(), "Donation count should remain 50L");
        assertNull(stats.getGiftPoolCount(), "Gift pool count should be null");
    }

    @Test
    @DisplayName("Should handle all fields as null simultaneously")
    void testAllFieldsAsNull() {
        // Arrange
        stats.setId(null);
        stats.setEventCount(null);
        stats.setDonationCount(null);
        stats.setGiftPoolCount(null);

        // Act & Assert
        assertNull(stats.getId(), "ID should be null");
        assertNull(stats.getEventCount(), "Event count should be null");
        assertNull(stats.getDonationCount(), "Donation count should be null");
        assertNull(stats.getGiftPoolCount(), "Gift pool count should be null");
    }

    // ==================== Object Identity Tests ====================

    @Test
    @DisplayName("Should have different instances with separate state")
    void testObjectsAreIndependent() {
        // Arrange
        Stats stats1 = new Stats();
        Stats stats2 = new Stats();

        // Act
        stats1.setId(1L);
        stats1.setEventCount(100L);
        stats2.setId(2L);
        stats2.setEventCount(200L);

        // Assert
        assertEquals(1L, stats1.getId(), "Stats1 ID should be 1L");
        assertEquals(100L, stats1.getEventCount(), "Stats1 event count should be 100L");
        assertEquals(2L, stats2.getId(), "Stats2 ID should be 2L");
        assertEquals(200L, stats2.getEventCount(), "Stats2 event count should be 200L");
        assertNotEquals(stats1.getId(), stats2.getId(), "IDs should be different");
    }

    // ==================== Edge Cases and Boundary Tests ====================

    @Test
    @DisplayName("Should handle minimum long value correctly")
    void testHandleMinimumLongValue() {
        // Arrange
        Long minValue = Long.MIN_VALUE;

        // Act
        stats.setEventCount(minValue);
        stats.setDonationCount(minValue);
        stats.setGiftPoolCount(minValue);

        // Assert
        assertEquals(minValue, stats.getEventCount(), "Should handle minimum long value");
        assertEquals(minValue, stats.getDonationCount(), "Should handle minimum long value");
        assertEquals(minValue, stats.getGiftPoolCount(), "Should handle minimum long value");
    }

    @Test
    @DisplayName("Should handle transition from null to valid value")
    void testTransitionFromNullToValidValue() {
        // Arrange
        stats.setEventCount(null);

        // Act
        stats.setEventCount(100L);
        Long value = stats.getEventCount();

        // Assert
        assertEquals(100L, value, "Should correctly transition from null to valid value");
    }

    @Test
    @DisplayName("Should handle transition from valid value to null")
    void testTransitionFromValidValueToNull() {
        // Arrange
        stats.setEventCount(100L);

        // Act
        stats.setEventCount(null);
        Long value = stats.getEventCount();

        // Assert
        assertNull(value, "Should correctly transition from valid value to null");
    }
}
