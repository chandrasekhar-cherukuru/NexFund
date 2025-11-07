package com.chakri.fundly.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive JUnit test class for Feedback entity with 80%+ code coverage.
 *
 * This test class covers:
 * - Default constructor behavior
 * - All setter methods with various inputs
 * - All getter methods
 * - Edge cases and boundary conditions
 * - Null value handling
 * - Field initialization
 *
 * @author Test Developer
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Feedback Entity Tests")
class FeedbackTest {

    // Service/Entity under test (no @InjectMocks needed for simple entity)
    private Feedback feedback;

    /**
     * Initialize test data before each test method
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feedback = new Feedback();
    }

    // ============== CONSTRUCTOR TESTS ==============

    @Test
    @DisplayName("Constructor - Should initialize createdAt to current time")
    void testConstructorInitializesCreatedAtToCurrentTime() {
        // ARRANGE
        LocalDateTime beforeCreation = LocalDateTime.now();

        // ACT
        Feedback newFeedback = new Feedback();

        // ASSERT
        LocalDateTime afterCreation = LocalDateTime.now();
        assertNotNull(newFeedback.getCreatedAt(), "createdAt should not be null");
        assertTrue(
                newFeedback.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)),
                "createdAt should be after or equal to current time"
        );
        assertTrue(
                newFeedback.getCreatedAt().isBefore(afterCreation.plusSeconds(1)),
                "createdAt should be before or equal to current time"
        );
    }

    @Test
    @DisplayName("Constructor - Should initialize isApproved to false")
    void testConstructorInitializesIsApprovedToFalse() {
        // ARRANGE & ACT
        Feedback newFeedback = new Feedback();

        // ASSERT
        assertNotNull(newFeedback.getIsApproved(), "isApproved should not be null");
        assertFalse(newFeedback.getIsApproved(), "isApproved should be false by default");
    }

    // ============== ID SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("ID Setter/Getter - Should set and get positive ID")
    void testIdSetterGetterWithPositiveId() {
        // ARRANGE
        Long testId = 1L;

        // ACT
        feedback.setId(testId);

        // ASSERT
        assertEquals(testId, feedback.getId(), "ID should match the set value");
    }

    @Test
    @DisplayName("ID Setter/Getter - Should set and get null ID")
    void testIdSetterGetterWithNullId() {
        // ARRANGE
        feedback.setId(999L);

        // ACT
        feedback.setId(null);

        // ASSERT
        assertNull(feedback.getId(), "ID should be null when set to null");
    }

    @Test
    @DisplayName("ID Setter/Getter - Should set and get large ID value")
    void testIdSetterGetterWithLargeId() {
        // ARRANGE
        Long largeId = Long.MAX_VALUE;

        // ACT
        feedback.setId(largeId);

        // ASSERT
        assertEquals(largeId, feedback.getId(), "ID should handle large values");
    }

    @Test
    @DisplayName("ID Setter/Getter - Should set and get negative ID")
    void testIdSetterGetterWithNegativeId() {
        // ARRANGE
        Long negativeId = -1L;

        // ACT
        feedback.setId(negativeId);

        // ASSERT
        assertEquals(negativeId, feedback.getId(), "ID should handle negative values");
    }

    // ============== NAME SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("Name Setter/Getter - Should set and get valid name")
    void testNameSetterGetterWithValidName() {
        // ARRANGE
        String testName = "John Doe";

        // ACT
        feedback.setName(testName);

        // ASSERT
        assertEquals(testName, feedback.getName(), "Name should match the set value");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get null name")
    void testNameSetterGetterWithNullName() {
        // ARRANGE
        feedback.setName("Test");

        // ACT
        feedback.setName(null);

        // ASSERT
        assertNull(feedback.getName(), "Name should be null when set to null");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get empty name")
    void testNameSetterGetterWithEmptyName() {
        // ARRANGE
        String emptyName = "";

        // ACT
        feedback.setName(emptyName);

        // ASSERT
        assertEquals(emptyName, feedback.getName(), "Name should accept empty strings");
        assertTrue(feedback.getName().isEmpty(), "Name should be empty");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get whitespace-only name")
    void testNameSetterGetterWithWhitespaceName() {
        // ARRANGE
        String whitespaceName = "   ";

        // ACT
        feedback.setName(whitespaceName);

        // ASSERT
        assertEquals(whitespaceName, feedback.getName(), "Name should preserve whitespace");
        assertTrue(feedback.getName().trim().isEmpty(), "Trimmed name should be empty");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get name with special characters")
    void testNameSetterGetterWithSpecialCharactersName() {
        // ARRANGE
        String specialName = "John@#$%^&*()_+-=[]{}|;':\",./<>?Doe";

        // ACT
        feedback.setName(specialName);

        // ASSERT
        assertEquals(specialName, feedback.getName(), "Name should accept special characters");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get very long name")
    void testNameSetterGetterWithVeryLongName() {
        // ARRANGE
        String longName = "A".repeat(500);

        // ACT
        feedback.setName(longName);

        // ASSERT
        assertEquals(longName, feedback.getName(), "Name should handle long strings");
        assertEquals(500, feedback.getName().length(), "Name length should be 500 characters");
    }

    @Test
    @DisplayName("Name Setter/Getter - Should set and get name with unicode characters")
    void testNameSetterGetterWithUnicodeCharsName() {
        // ARRANGE
        String unicodeName = "José García 中文 العربية";

        // ACT
        feedback.setName(unicodeName);

        // ASSERT
        assertEquals(unicodeName, feedback.getName(), "Name should support unicode characters");
    }

    // ============== EMAIL SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("Email Setter/Getter - Should set and get valid email")
    void testEmailSetterGetterWithValidEmail() {
        // ARRANGE
        String validEmail = "test@example.com";

        // ACT
        feedback.setEmail(validEmail);

        // ASSERT
        assertEquals(validEmail, feedback.getEmail(), "Email should match the set value");
    }

    @Test
    @DisplayName("Email Setter/Getter - Should set and get null email")
    void testEmailSetterGetterWithNullEmail() {
        // ARRANGE
        feedback.setEmail("test@test.com");

        // ACT
        feedback.setEmail(null);

        // ASSERT
        assertNull(feedback.getEmail(), "Email should be null when set to null");
    }

    @Test
    @DisplayName("Email Setter/Getter - Should set and get empty email")
    void testEmailSetterGetterWithEmptyEmail() {
        // ARRANGE
        String emptyEmail = "";

        // ACT
        feedback.setEmail(emptyEmail);

        // ASSERT
        assertEquals(emptyEmail, feedback.getEmail(), "Email should accept empty strings");
    }

    @Test
    @DisplayName("Email Setter/Getter - Should set and get email with special format")
    void testEmailSetterGetterWithSpecialFormatEmail() {
        // ARRANGE
        String specialEmail = "user+tag@sub.example.co.uk";

        // ACT
        feedback.setEmail(specialEmail);

        // ASSERT
        assertEquals(specialEmail, feedback.getEmail(), "Email should preserve special characters");
    }

    @Test
    @DisplayName("Email Setter/Getter - Should set and get very long email")
    void testEmailSetterGetterWithVeryLongEmail() {
        // ARRANGE
        String longEmail = "a".repeat(200) + "@example.com";

        // ACT
        feedback.setEmail(longEmail);

        // ASSERT
        assertEquals(longEmail, feedback.getEmail(), "Email should handle long strings");
    }

    // ============== MESSAGE SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("Message Setter/Getter - Should set and get valid message")
    void testMessageSetterGetterWithValidMessage() {
        // ARRANGE
        String testMessage = "This is a test feedback message";

        // ACT
        feedback.setMessage(testMessage);

        // ASSERT
        assertEquals(testMessage, feedback.getMessage(), "Message should match the set value");
    }

    @Test
    @DisplayName("Message Setter/Getter - Should set and get null message")
    void testMessageSetterGetterWithNullMessage() {
        // ARRANGE
        feedback.setMessage("Test");

        // ACT
        feedback.setMessage(null);

        // ASSERT
        assertNull(feedback.getMessage(), "Message should be null when set to null");
    }

    @Test
    @DisplayName("Message Setter/Getter - Should set and get empty message")
    void testMessageSetterGetterWithEmptyMessage() {
        // ARRANGE
        String emptyMessage = "";

        // ACT
        feedback.setMessage(emptyMessage);

        // ASSERT
        assertEquals(emptyMessage, feedback.getMessage(), "Message should accept empty strings");
    }

    @Test
    @DisplayName("Message Setter/Getter - Should set and get multiline message")
    void testMessageSetterGetterWithMultilineMessage() {
        // ARRANGE
        String multilineMessage = "Line 1\nLine 2\nLine 3";

        // ACT
        feedback.setMessage(multilineMessage);

        // ASSERT
        assertEquals(multilineMessage, feedback.getMessage(), "Message should preserve line breaks");
        assertTrue(feedback.getMessage().contains("\n"), "Message should contain newlines");
    }

    @Test
    @DisplayName("Message Setter/Getter - Should set and get very long message")
    void testMessageSetterGetterWithVeryLongMessage() {
        // ARRANGE
        String longMessage = "This is a message. ".repeat(500);

        // ACT
        feedback.setMessage(longMessage);

        // ASSERT
        assertEquals(longMessage, feedback.getMessage(), "Message should handle long strings");
    }

    @Test
    @DisplayName("Message Setter/Getter - Should set and get message with special characters")
    void testMessageSetterGetterWithSpecialCharsMessage() {
        // ARRANGE
        String specialMessage = "Message with @#$%^&*()_+-=[]{}|;':\",./<>? characters";

        // ACT
        feedback.setMessage(specialMessage);

        // ASSERT
        assertEquals(specialMessage, feedback.getMessage(), "Message should accept special characters");
    }

    // ============== TYPE SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("Type Setter/Getter - Should set and get 'event' type")
    void testTypeSetterGetterWithEventType() {
        // ARRANGE
        String eventType = "event";

        // ACT
        feedback.setType(eventType);

        // ASSERT
        assertEquals(eventType, feedback.getType(), "Type should be 'event'");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get 'donation' type")
    void testTypeSetterGetterWithDonationType() {
        // ARRANGE
        String donationType = "donation";

        // ACT
        feedback.setType(donationType);

        // ASSERT
        assertEquals(donationType, feedback.getType(), "Type should be 'donation'");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get 'giftpool' type")
    void testTypeSetterGetterWithGiftpoolType() {
        // ARRANGE
        String giftpoolType = "giftpool";

        // ACT
        feedback.setType(giftpoolType);

        // ASSERT
        assertEquals(giftpoolType, feedback.getType(), "Type should be 'giftpool'");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get null type")
    void testTypeSetterGetterWithNullType() {
        // ARRANGE
        feedback.setType("event");

        // ACT
        feedback.setType(null);

        // ASSERT
        assertNull(feedback.getType(), "Type should be null when set to null");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get empty type")
    void testTypeSetterGetterWithEmptyType() {
        // ARRANGE
        String emptyType = "";

        // ACT
        feedback.setType(emptyType);

        // ASSERT
        assertEquals(emptyType, feedback.getType(), "Type should accept empty strings");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get invalid/unexpected type")
    void testTypeSetterGetterWithInvalidType() {
        // ARRANGE
        String invalidType = "invalid_type";

        // ACT
        feedback.setType(invalidType);

        // ASSERT
        assertEquals(invalidType, feedback.getType(), "Type should accept any string value");
    }

    @Test
    @DisplayName("Type Setter/Getter - Should set and get uppercase type")
    void testTypeSetterGetterWithUppercaseType() {
        // ARRANGE
        String uppercaseType = "EVENT";

        // ACT
        feedback.setType(uppercaseType);

        // ASSERT
        assertEquals(uppercaseType, feedback.getType(), "Type should be case-sensitive");
    }

    // ============== RATING SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get minimum valid rating (1)")
    void testRatingSetterGetterWithMinimumValidRating() {
        // ARRANGE
        int minRating = 1;

        // ACT
        feedback.setRating(minRating);

        // ASSERT
        assertEquals(minRating, feedback.getRating(), "Rating should be 1");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get maximum valid rating (5)")
    void testRatingSetterGetterWithMaximumValidRating() {
        // ARRANGE
        int maxRating = 5;

        // ACT
        feedback.setRating(maxRating);

        // ASSERT
        assertEquals(maxRating, feedback.getRating(), "Rating should be 5");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get middle range rating (3)")
    void testRatingSetterGetterWithMiddleRating() {
        // ARRANGE
        int middleRating = 3;

        // ACT
        feedback.setRating(middleRating);

        // ASSERT
        assertEquals(middleRating, feedback.getRating(), "Rating should be 3");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get rating below minimum (0)")
    void testRatingSetterGetterWithBelowMinimumRating() {
        // ARRANGE
        int belowMinRating = 0;

        // ACT
        feedback.setRating(belowMinRating);

        // ASSERT
        assertEquals(belowMinRating, feedback.getRating(), "Rating should accept 0 (below minimum)");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get rating above maximum (6)")
    void testRatingSetterGetterWithAboveMaximumRating() {
        // ARRANGE
        int aboveMaxRating = 6;

        // ACT
        feedback.setRating(aboveMaxRating);

        // ASSERT
        assertEquals(aboveMaxRating, feedback.getRating(), "Rating should accept 6 (above maximum)");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get negative rating")
    void testRatingSetterGetterWithNegativeRating() {
        // ARRANGE
        int negativeRating = -5;

        // ACT
        feedback.setRating(negativeRating);

        // ASSERT
        assertEquals(negativeRating, feedback.getRating(), "Rating should accept negative values");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get very large rating")
    void testRatingSetterGetterWithVeryLargeRating() {
        // ARRANGE
        int largeRating = Integer.MAX_VALUE;

        // ACT
        feedback.setRating(largeRating);

        // ASSERT
        assertEquals(largeRating, feedback.getRating(), "Rating should accept large values");
    }

    @Test
    @DisplayName("Rating Setter/Getter - Should set and get default rating (0)")
    void testRatingSetterGetterWithDefaultRating() {
        // ARRANGE & ACT
        // No explicit setter call - testing default value

        // ASSERT
        assertEquals(0, feedback.getRating(), "Default rating should be 0 (primitive int default)");
    }

    // ============== CREATEDAT SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should set and get valid LocalDateTime")
    void testCreatedAtSetterGetterWithValidDateTime() {
        // ARRANGE
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30, 45);

        // ACT
        feedback.setCreatedAt(testDateTime);

        // ASSERT
        assertEquals(testDateTime, feedback.getCreatedAt(), "CreatedAt should match the set value");
    }

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should set and get null LocalDateTime")
    void testCreatedAtSetterGetterWithNullDateTime() {
        // ARRANGE
        LocalDateTime initialDateTime = feedback.getCreatedAt();

        // ACT
        feedback.setCreatedAt(null);

        // ASSERT
        assertNull(feedback.getCreatedAt(), "CreatedAt should be null when set to null");
    }

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should set and get past date")
    void testCreatedAtSetterGetterWithPastDate() {
        // ARRANGE
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        // ACT
        feedback.setCreatedAt(pastDate);

        // ASSERT
        assertEquals(pastDate, feedback.getCreatedAt(), "CreatedAt should accept past dates");
        assertTrue(feedback.getCreatedAt().isBefore(LocalDateTime.now()), "Past date should be before now");
    }

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should set and get future date")
    void testCreatedAtSetterGetterWithFutureDate() {
        // ARRANGE
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

        // ACT
        feedback.setCreatedAt(futureDate);

        // ASSERT
        assertEquals(futureDate, feedback.getCreatedAt(), "CreatedAt should accept future dates");
        assertTrue(feedback.getCreatedAt().isAfter(LocalDateTime.now()), "Future date should be after now");
    }

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should handle minimum LocalDateTime")
    void testCreatedAtSetterGetterWithMinDateTime() {
        // ARRANGE
        LocalDateTime minDateTime = LocalDateTime.MIN;

        // ACT
        feedback.setCreatedAt(minDateTime);

        // ASSERT
        assertEquals(minDateTime, feedback.getCreatedAt(), "CreatedAt should accept MIN value");
    }

    @Test
    @DisplayName("CreatedAt Setter/Getter - Should handle maximum LocalDateTime")
    void testCreatedAtSetterGetterWithMaxDateTime() {
        // ARRANGE
        LocalDateTime maxDateTime = LocalDateTime.MAX;

        // ACT
        feedback.setCreatedAt(maxDateTime);

        // ASSERT
        assertEquals(maxDateTime, feedback.getCreatedAt(), "CreatedAt should accept MAX value");
    }

    // ============== ISAPPROVED SETTER/GETTER TESTS ==============

    @Test
    @DisplayName("IsApproved Setter/Getter - Should set and get true value")
    void testIsApprovedSetterGetterWithTrueValue() {
        // ARRANGE
        Boolean trueValue = true;

        // ACT
        feedback.setIsApproved(trueValue);

        // ASSERT
        assertTrue(feedback.getIsApproved(), "IsApproved should be true");
    }

    @Test
    @DisplayName("IsApproved Setter/Getter - Should set and get false value")
    void testIsApprovedSetterGetterWithFalseValue() {
        // ARRANGE
        feedback.setIsApproved(true);

        // ACT
        feedback.setIsApproved(false);

        // ASSERT
        assertFalse(feedback.getIsApproved(), "IsApproved should be false");
    }

    @Test
    @DisplayName("IsApproved Setter/Getter - Should set and get null value")
    void testIsApprovedSetterGetterWithNullValue() {
        // ARRANGE
        feedback.setIsApproved(true);

        // ACT
        feedback.setIsApproved(null);

        // ASSERT
        assertNull(feedback.getIsApproved(), "IsApproved should be null when set to null");
    }

    // ============== INTEGRATION TESTS ==============

    @Test
    @DisplayName("Integration - Should maintain all fields after multiple sets")
    void testAllFieldsAfterMultipleSets() {
        // ARRANGE
        Long testId = 100L;
        String testName = "Jane Smith";
        String testEmail = "jane@example.com";
        String testMessage = "Great service!";
        String testType = "donation";
        int testRating = 5;
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 0);
        Boolean testApproved = true;

        // ACT
        feedback.setId(testId);
        feedback.setName(testName);
        feedback.setEmail(testEmail);
        feedback.setMessage(testMessage);
        feedback.setType(testType);
        feedback.setRating(testRating);
        feedback.setCreatedAt(testDateTime);
        feedback.setIsApproved(testApproved);

        // ASSERT
        assertEquals(testId, feedback.getId(), "ID should match");
        assertEquals(testName, feedback.getName(), "Name should match");
        assertEquals(testEmail, feedback.getEmail(), "Email should match");
        assertEquals(testMessage, feedback.getMessage(), "Message should match");
        assertEquals(testType, feedback.getType(), "Type should match");
        assertEquals(testRating, feedback.getRating(), "Rating should match");
        assertEquals(testDateTime, feedback.getCreatedAt(), "CreatedAt should match");
        assertEquals(testApproved, feedback.getIsApproved(), "IsApproved should match");
    }

    @Test
    @DisplayName("Integration - Should reinitialize default values with new instance")
    void testReinitializeDefaultValuesWithNewInstance() {
        // ARRANGE
        feedback.setId(999L);
        feedback.setName("Old Name");
        feedback.setIsApproved(true);

        // ACT
        Feedback newFeedback = new Feedback();

        // ASSERT
        assertNull(newFeedback.getId(), "New instance ID should be null");
        assertNull(newFeedback.getName(), "New instance Name should be null");
        assertNotNull(newFeedback.getCreatedAt(), "New instance CreatedAt should not be null");
        assertFalse(newFeedback.getIsApproved(), "New instance IsApproved should be false");
    }

    @Test
    @DisplayName("Integration - Should handle complete feedback flow")
    void testCompleteFeedbackFlow() {
        // ARRANGE
        Long feedbackId = 50L;
        String name = "Alice Johnson";
        String email = "alice@example.com";
        String message = "Excellent event organization";
        String type = "event";
        int rating = 5;

        // ACT
        feedback.setId(feedbackId);
        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setMessage(message);
        feedback.setType(type);
        feedback.setRating(rating);
        feedback.setIsApproved(true);

        // Retrieve all values
        Long retrievedId = feedback.getId();
        String retrievedName = feedback.getName();
        String retrievedEmail = feedback.getEmail();
        String retrievedMessage = feedback.getMessage();
        String retrievedType = feedback.getType();
        int retrievedRating = feedback.getRating();
        Boolean retrievedApproved = feedback.getIsApproved();
        LocalDateTime retrievedCreatedAt = feedback.getCreatedAt();

        // ASSERT
        assertAll("Feedback complete flow",
                () -> assertEquals(feedbackId, retrievedId),
                () -> assertEquals(name, retrievedName),
                () -> assertEquals(email, retrievedEmail),
                () -> assertEquals(message, retrievedMessage),
                () -> assertEquals(type, retrievedType),
                () -> assertEquals(rating, retrievedRating),
                () -> assertTrue(retrievedApproved),
                () -> assertNotNull(retrievedCreatedAt)
        );
    }

    // ============== PARAMETERIZED TESTS ==============

    @ParameterizedTest
    @DisplayName("Parameterized - Should handle multiple valid types")
    @ValueSource(strings = {"event", "donation", "giftpool"})
    void testValidFeedbackTypes(String type) {
        // ARRANGE & ACT
        feedback.setType(type);

        // ASSERT
        assertEquals(type, feedback.getType(), "Should accept valid type: " + type);
        assertNotNull(feedback.getType(), "Type should not be null");
    }

    @ParameterizedTest
    @DisplayName("Parameterized - Should handle multiple valid ratings")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testValidRatingRange(int rating) {
        // ARRANGE & ACT
        feedback.setRating(rating);

        // ASSERT
        assertEquals(rating, feedback.getRating(), "Should accept valid rating: " + rating);
        assertTrue(feedback.getRating() >= 1 && feedback.getRating() <= 5,
                "Rating should be within 1-5 range");
    }

    @ParameterizedTest
    @DisplayName("Parameterized - Should handle edge case ratings")
    @ValueSource(ints = {0, -1, 6, 10, -100, 100})
    void testEdgeCaseRatings(int rating) {
        // ARRANGE & ACT
        feedback.setRating(rating);

        // ASSERT
        assertEquals(rating, feedback.getRating(), "Should accept edge case rating: " + rating);
    }
}
