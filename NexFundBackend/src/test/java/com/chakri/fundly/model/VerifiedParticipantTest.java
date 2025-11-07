package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifiedParticipant Entity Tests")
class VerifiedParticipantTest {

    private VerifiedParticipant verifiedParticipant;
    private VerifiedParticipant verifiedParticipantWithParams;

    @BeforeEach
    void setUp() {
        // Initialize test instances
        verifiedParticipant = new VerifiedParticipant();
        verifiedParticipantWithParams = new VerifiedParticipant(
                "fundraiser-123",
                "John Doe",
                "UTR123456789",
                new BigDecimal("5000.00"),
                "john@example.com",
                "MEDICAL",
                "Help for Medical Emergency",
                "admin-user"
        );
    }

    // ========== DEFAULT CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Should create VerifiedParticipant with default constructor and generate UUID")
    void testDefaultConstructorGeneratesId() {
        // Arrange & Act
        VerifiedParticipant participant = new VerifiedParticipant();

        // Assert
        assertNotNull(participant.getId());
        assertTrue(participant.getId().length() > 0);
        assertNotNull(participant.getVerifiedAt());
    }

    @Test
    @DisplayName("Should create unique IDs for each instance using default constructor")
    void testDefaultConstructorGeneratesUniqueIds() {
        // Arrange & Act
        VerifiedParticipant participant1 = new VerifiedParticipant();
        VerifiedParticipant participant2 = new VerifiedParticipant();

        // Assert
        assertNotEquals(participant1.getId(), participant2.getId());
    }

    @Test
    @DisplayName("Should set verifiedAt to current LocalDateTime in default constructor")
    void testDefaultConstructorSetsVerifiedAtToNow() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // Act
        VerifiedParticipant participant = new VerifiedParticipant();

        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertNotNull(participant.getVerifiedAt());
        assertTrue(participant.getVerifiedAt().isAfter(beforeCreation));
        assertTrue(participant.getVerifiedAt().isBefore(afterCreation));
    }

    // ========== PARAMETERIZED CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Should create VerifiedParticipant with parameterized constructor and set all fields correctly")
    void testParameterizedConstructorSetsAllFields() {
        // Arrange & Act - using setUp() instance

        // Assert
        assertNotNull(verifiedParticipantWithParams.getId());
        assertEquals("fundraiser-123", verifiedParticipantWithParams.getFundraiserId());
        assertEquals("John Doe", verifiedParticipantWithParams.getParticipantName());
        assertEquals("UTR123456789", verifiedParticipantWithParams.getUtrNumber());
        assertEquals(new BigDecimal("5000.00"), verifiedParticipantWithParams.getAmountPaid());
        assertEquals("john@example.com", verifiedParticipantWithParams.getEmail());
        assertEquals("MEDICAL", verifiedParticipantWithParams.getFundraiserType());
        assertEquals("Help for Medical Emergency", verifiedParticipantWithParams.getFundraiserTitle());
        assertEquals("admin-user", verifiedParticipantWithParams.getCreatedBy());
    }

    @Test
    @DisplayName("Should set payerUsername to participantName in parameterized constructor")
    void testParameterizedConstructorSetsPayerUsernameToParticipantName() {
        // Arrange & Act - using setUp() instance

        // Assert
        assertEquals("John Doe", verifiedParticipantWithParams.getPayerUsername());
        assertEquals(verifiedParticipantWithParams.getParticipantName(),
                verifiedParticipantWithParams.getPayerUsername());
    }

    @Test
    @DisplayName("Should set verifiedBy to createdBy in parameterized constructor")
    void testParameterizedConstructorSetsVerifiedByToCreatedBy() {
        // Arrange & Act - using setUp() instance

        // Assert
        assertEquals("admin-user", verifiedParticipantWithParams.getVerifiedBy());
        assertEquals(verifiedParticipantWithParams.getCreatedBy(),
                verifiedParticipantWithParams.getVerifiedBy());
    }

    @Test
    @DisplayName("Should generate UUID and set verifiedAt in parameterized constructor")
    void testParameterizedConstructorGeneratesIdAndVerifiedAt() {
        // Arrange & Act
        VerifiedParticipant participant = new VerifiedParticipant(
                "fundraiser-456",
                "Jane Smith",
                "UTR987654321",
                new BigDecimal("10000.00"),
                "jane@example.com",
                "EDUCATION",
                "Scholarship Fund",
                "admin-user2"
        );

        // Assert
        assertNotNull(participant.getId());
        assertNotNull(participant.getVerifiedAt());
        assertTrue(participant.getId().length() > 0);
    }

    // ========== ID GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set ID correctly")
    void testIdGetterSetter() {
        // Arrange
        String testId = "test-id-12345";

        // Act
        verifiedParticipant.setId(testId);

        // Assert
        assertEquals(testId, verifiedParticipant.getId());
    }

    @Test
    @DisplayName("Should handle null ID in setter")
    void testIdSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setId(null);

        // Assert
        assertNull(verifiedParticipant.getId());
    }

    @Test
    @DisplayName("Should handle empty string ID in setter")
    void testIdSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setId("");

        // Assert
        assertEquals("", verifiedParticipant.getId());
    }

    // ========== FUNDRAISER_ID GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set fundraiserId correctly")
    void testFundraiserIdGetterSetter() {
        // Arrange
        String fundraiserId = "fundraiser-999";

        // Act
        verifiedParticipant.setFundraiserId(fundraiserId);

        // Assert
        assertEquals(fundraiserId, verifiedParticipant.getFundraiserId());
    }

    @Test
    @DisplayName("Should handle null fundraiserId in setter")
    void testFundraiserIdSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setFundraiserId(null);

        // Assert
        assertNull(verifiedParticipant.getFundraiserId());
    }

    @Test
    @DisplayName("Should handle empty fundraiserId in setter")
    void testFundraiserIdSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setFundraiserId("");

        // Assert
        assertEquals("", verifiedParticipant.getFundraiserId());
    }

    // ========== PARTICIPANT_NAME GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set participantName correctly")
    void testParticipantNameGetterSetter() {
        // Arrange
        String participantName = "Robert Johnson";

        // Act
        verifiedParticipant.setParticipantName(participantName);

        // Assert
        assertEquals(participantName, verifiedParticipant.getParticipantName());
    }

    @Test
    @DisplayName("Should handle null participantName in setter")
    void testParticipantNameSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setParticipantName(null);

        // Assert
        assertNull(verifiedParticipant.getParticipantName());
    }

    @Test
    @DisplayName("Should handle empty participantName in setter")
    void testParticipantNameSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setParticipantName("");

        // Assert
        assertEquals("", verifiedParticipant.getParticipantName());
    }

    @Test
    @DisplayName("Should handle long participantName in setter")
    void testParticipantNameSetterWithLongString() {
        // Arrange
        String longName = "A".repeat(255);

        // Act
        verifiedParticipant.setParticipantName(longName);

        // Assert
        assertEquals(longName, verifiedParticipant.getParticipantName());
    }

    // ========== UTR_NUMBER GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set utrNumber correctly")
    void testUtrNumberGetterSetter() {
        // Arrange
        String utrNumber = "UTR111222333";

        // Act
        verifiedParticipant.setUtrNumber(utrNumber);

        // Assert
        assertEquals(utrNumber, verifiedParticipant.getUtrNumber());
    }

    @Test
    @DisplayName("Should handle null utrNumber in setter")
    void testUtrNumberSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setUtrNumber(null);

        // Assert
        assertNull(verifiedParticipant.getUtrNumber());
    }

    @Test
    @DisplayName("Should handle empty utrNumber in setter")
    void testUtrNumberSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setUtrNumber("");

        // Assert
        assertEquals("", verifiedParticipant.getUtrNumber());
    }

    @Test
    @DisplayName("Should handle 12-character utrNumber in setter")
    void testUtrNumberSetterWith12Characters() {
        // Arrange
        String utrNumber = "UTR123456789A";

        // Act
        verifiedParticipant.setUtrNumber(utrNumber);

        // Assert
        assertEquals(utrNumber, verifiedParticipant.getUtrNumber());
    }

    // ========== AMOUNT_PAID GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set amountPaid correctly")
    void testAmountPaidGetterSetter() {
        // Arrange
        BigDecimal amount = new BigDecimal("9999.99");

        // Act
        verifiedParticipant.setAmountPaid(amount);

        // Assert
        assertEquals(amount, verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should handle null amountPaid in setter")
    void testAmountPaidSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setAmountPaid(null);

        // Assert
        assertNull(verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should handle zero amountPaid in setter")
    void testAmountPaidSetterWithZero() {
        // Arrange
        BigDecimal zeroAmount = new BigDecimal("0.00");

        // Act
        verifiedParticipant.setAmountPaid(zeroAmount);

        // Assert
        assertEquals(zeroAmount, verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should handle large amountPaid in setter")
    void testAmountPaidSetterWithLargeAmount() {
        // Arrange
        BigDecimal largeAmount = new BigDecimal("999999.99");

        // Act
        verifiedParticipant.setAmountPaid(largeAmount);

        // Assert
        assertEquals(largeAmount, verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should handle negative amountPaid in setter")
    void testAmountPaidSetterWithNegativeAmount() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-1000.00");

        // Act
        verifiedParticipant.setAmountPaid(negativeAmount);

        // Assert
        assertEquals(negativeAmount, verifiedParticipant.getAmountPaid());
    }

    // ========== EMAIL GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set email correctly")
    void testEmailGetterSetter() {
        // Arrange
        String email = "test@example.com";

        // Act
        verifiedParticipant.setEmail(email);

        // Assert
        assertEquals(email, verifiedParticipant.getEmail());
    }

    @Test
    @DisplayName("Should handle null email in setter")
    void testEmailSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setEmail(null);

        // Assert
        assertNull(verifiedParticipant.getEmail());
    }

    @Test
    @DisplayName("Should handle empty email in setter")
    void testEmailSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setEmail("");

        // Assert
        assertEquals("", verifiedParticipant.getEmail());
    }

    // ========== FUNDRAISER_TYPE GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set fundraiserType correctly")
    void testFundraiserTypeGetterSetter() {
        // Arrange
        String fundraiserType = "EMERGENCY";

        // Act
        verifiedParticipant.setFundraiserType(fundraiserType);

        // Assert
        assertEquals(fundraiserType, verifiedParticipant.getFundraiserType());
    }

    @Test
    @DisplayName("Should handle null fundraiserType in setter")
    void testFundraiserTypeSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setFundraiserType(null);

        // Assert
        assertNull(verifiedParticipant.getFundraiserType());
    }

    @Test
    @DisplayName("Should handle empty fundraiserType in setter")
    void testFundraiserTypeSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setFundraiserType("");

        // Assert
        assertEquals("", verifiedParticipant.getFundraiserType());
    }

    // ========== FUNDRAISER_TITLE GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set fundraiserTitle correctly")
    void testFundraiserTitleGetterSetter() {
        // Arrange
        String fundraiserTitle = "Help Save Lives";

        // Act
        verifiedParticipant.setFundraiserTitle(fundraiserTitle);

        // Assert
        assertEquals(fundraiserTitle, verifiedParticipant.getFundraiserTitle());
    }

    @Test
    @DisplayName("Should handle null fundraiserTitle in setter")
    void testFundraiserTitleSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setFundraiserTitle(null);

        // Assert
        assertNull(verifiedParticipant.getFundraiserTitle());
    }

    @Test
    @DisplayName("Should handle empty fundraiserTitle in setter")
    void testFundraiserTitleSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setFundraiserTitle("");

        // Assert
        assertEquals("", verifiedParticipant.getFundraiserTitle());
    }

    // ========== VERIFIED_AT GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set verifiedAt correctly")
    void testVerifiedAtGetterSetter() {
        // Arrange
        LocalDateTime verifiedTime = LocalDateTime.of(2025, 11, 7, 10, 30, 45);

        // Act
        verifiedParticipant.setVerifiedAt(verifiedTime);

        // Assert
        assertEquals(verifiedTime, verifiedParticipant.getVerifiedAt());
    }

    @Test
    @DisplayName("Should handle null verifiedAt in setter")
    void testVerifiedAtSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setVerifiedAt(null);

        // Assert
        assertNull(verifiedParticipant.getVerifiedAt());
    }

    @Test
    @DisplayName("Should handle past verifiedAt date in setter")
    void testVerifiedAtSetterWithPastDate() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        // Act
        verifiedParticipant.setVerifiedAt(pastDate);

        // Assert
        assertEquals(pastDate, verifiedParticipant.getVerifiedAt());
    }

    @Test
    @DisplayName("Should handle future verifiedAt date in setter")
    void testVerifiedAtSetterWithFutureDate() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

        // Act
        verifiedParticipant.setVerifiedAt(futureDate);

        // Assert
        assertEquals(futureDate, verifiedParticipant.getVerifiedAt());
    }

    // ========== CREATED_BY GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set createdBy correctly")
    void testCreatedByGetterSetter() {
        // Arrange
        String createdBy = "admin-user-123";

        // Act
        verifiedParticipant.setCreatedBy(createdBy);

        // Assert
        assertEquals(createdBy, verifiedParticipant.getCreatedBy());
    }

    @Test
    @DisplayName("Should handle null createdBy in setter")
    void testCreatedBySetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setCreatedBy(null);

        // Assert
        assertNull(verifiedParticipant.getCreatedBy());
    }

    @Test
    @DisplayName("Should handle empty createdBy in setter")
    void testCreatedBySetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setCreatedBy("");

        // Assert
        assertEquals("", verifiedParticipant.getCreatedBy());
    }

    // ========== PAYER_USERNAME GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set payerUsername correctly")
    void testPayerUsernameGetterSetter() {
        // Arrange
        String payerUsername = "john_doe_payer";

        // Act
        verifiedParticipant.setPayerUsername(payerUsername);

        // Assert
        assertEquals(payerUsername, verifiedParticipant.getPayerUsername());
    }

    @Test
    @DisplayName("Should handle null payerUsername in setter")
    void testPayerUsernameSetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setPayerUsername(null);

        // Assert
        assertNull(verifiedParticipant.getPayerUsername());
    }

    @Test
    @DisplayName("Should handle empty payerUsername in setter")
    void testPayerUsernameSetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setPayerUsername("");

        // Assert
        assertEquals("", verifiedParticipant.getPayerUsername());
    }

    // ========== VERIFIED_BY GETTER/SETTER TESTS ==========

    @Test
    @DisplayName("Should get and set verifiedBy correctly")
    void testVerifiedByGetterSetter() {
        // Arrange
        String verifiedBy = "verification-officer-456";

        // Act
        verifiedParticipant.setVerifiedBy(verifiedBy);

        // Assert
        assertEquals(verifiedBy, verifiedParticipant.getVerifiedBy());
    }

    @Test
    @DisplayName("Should handle null verifiedBy in setter")
    void testVerifiedBySetterWithNullValue() {
        // Arrange & Act
        verifiedParticipant.setVerifiedBy(null);

        // Assert
        assertNull(verifiedParticipant.getVerifiedBy());
    }

    @Test
    @DisplayName("Should handle empty verifiedBy in setter")
    void testVerifiedBySetterWithEmptyString() {
        // Arrange & Act
        verifiedParticipant.setVerifiedBy("");

        // Assert
        assertEquals("", verifiedParticipant.getVerifiedBy());
    }

    // ========== TOSTRING TESTS ==========

    @Test
    @DisplayName("Should generate toString representation with all relevant fields")
    void testToStringContainsAllRelevantFields() {
        // Arrange & Act
        String result = verifiedParticipantWithParams.toString();

        // Assert
        assertTrue(result.contains("VerifiedParticipant"));
        assertTrue(result.contains("id="));
        assertTrue(result.contains("fundraiserId="));
        assertTrue(result.contains("participantName="));
        assertTrue(result.contains("utrNumber="));
        assertTrue(result.contains("amountPaid="));
        assertTrue(result.contains("payerUsername="));
        assertTrue(result.contains("verifiedBy="));
        assertTrue(result.contains("verifiedAt="));
    }

    @Test
    @DisplayName("Should include correct field values in toString representation")
    void testToStringIncludesCorrectValues() {
        // Arrange & Act
        String result = verifiedParticipantWithParams.toString();

        // Assert
        assertTrue(result.contains("fundraiserId='fundraiser-123'"));
        assertTrue(result.contains("participantName='John Doe'"));
        assertTrue(result.contains("utrNumber='UTR123456789'"));
        assertTrue(result.contains("payerUsername='John Doe'"));
        assertTrue(result.contains("verifiedBy='admin-user'"));
    }

    @Test
    @DisplayName("Should handle toString with null fields gracefully")
    void testToStringWithNullFields() {
        // Arrange
        VerifiedParticipant participant = new VerifiedParticipant();
        participant.setId(null);
        participant.setFundraiserId(null);
        participant.setParticipantName(null);

        // Act
        String result = participant.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("VerifiedParticipant"));
    }

    @Test
    @DisplayName("Should provide useful toString representation for debugging")
    void testToStringFormat() {
        // Arrange & Act
        String result = verifiedParticipantWithParams.toString();

        // Assert
        assertTrue(result.startsWith("VerifiedParticipant{"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.contains("="));
    }

    // ========== EDGE CASE COMBINATION TESTS ==========

    @Test
    @DisplayName("Should handle multiple setters and getters in sequence")
    void testMultipleSettersAndGettersSequence() {
        // Arrange
        String fundraiserId = "fundraiser-seq-1";
        String participantName = "Sequential Tester";
        BigDecimal amount = new BigDecimal("5555.55");

        // Act
        verifiedParticipant.setFundraiserId(fundraiserId);
        verifiedParticipant.setParticipantName(participantName);
        verifiedParticipant.setAmountPaid(amount);

        // Assert
        assertEquals(fundraiserId, verifiedParticipant.getFundraiserId());
        assertEquals(participantName, verifiedParticipant.getParticipantName());
        assertEquals(amount, verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should maintain data consistency across getter and setter operations")
    void testDataConsistencyAcrossOperations() {
        // Arrange
        String testId = "consistency-test-id";
        String testEmail = "consistency@test.com";
        LocalDateTime testTime = LocalDateTime.now();

        // Act
        verifiedParticipant.setId(testId);
        verifiedParticipant.setEmail(testEmail);
        verifiedParticipant.setVerifiedAt(testTime);

        String retrievedId = verifiedParticipant.getId();
        String retrievedEmail = verifiedParticipant.getEmail();
        LocalDateTime retrievedTime = verifiedParticipant.getVerifiedAt();

        // Assert
        assertEquals(testId, retrievedId);
        assertEquals(testEmail, retrievedEmail);
        assertEquals(testTime, retrievedTime);
    }

    @Test
    @DisplayName("Should overwrite previous values when setters are called multiple times")
    void testSettersOverwritePreviousValues() {
        // Arrange
        String firstValue = "first-value";
        String secondValue = "second-value";

        // Act
        verifiedParticipant.setFundraiserId(firstValue);
        assertEquals(firstValue, verifiedParticipant.getFundraiserId());

        verifiedParticipant.setFundraiserId(secondValue);

        // Assert
        assertEquals(secondValue, verifiedParticipant.getFundraiserId());
        assertNotEquals(firstValue, verifiedParticipant.getFundraiserId());
    }

    @Test
    @DisplayName("Should preserve independence of different instances")
    void testInstanceIndependence() {
        // Arrange
        VerifiedParticipant participant1 = new VerifiedParticipant();
        VerifiedParticipant participant2 = new VerifiedParticipant();

        // Act
        participant1.setParticipantName("Participant 1");
        participant2.setParticipantName("Participant 2");

        // Assert
        assertEquals("Participant 1", participant1.getParticipantName());
        assertEquals("Participant 2", participant2.getParticipantName());
        assertNotEquals(participant1.getParticipantName(), participant2.getParticipantName());
    }

    @Test
    @DisplayName("Should maintain separate ID values for different instances")
    void testUniqueIdForDifferentInstances() {
        // Arrange & Act
        VerifiedParticipant participant1 = new VerifiedParticipant();
        VerifiedParticipant participant2 = new VerifiedParticipant();

        // Assert
        assertNotNull(participant1.getId());
        assertNotNull(participant2.getId());
        assertNotEquals(participant1.getId(), participant2.getId());
    }

    @Test
    @DisplayName("Should handle boundary values for BigDecimal amounts")
    void testBigDecimalBoundaryValues() {
        // Arrange
        BigDecimal minValue = new BigDecimal("0.01");
        BigDecimal maxValue = new BigDecimal("9999999.99");

        // Act & Assert
        verifiedParticipant.setAmountPaid(minValue);
        assertEquals(minValue, verifiedParticipant.getAmountPaid());

        verifiedParticipant.setAmountPaid(maxValue);
        assertEquals(maxValue, verifiedParticipant.getAmountPaid());
    }

    @Test
    @DisplayName("Should handle special characters in string fields")
    void testSpecialCharactersInStringFields() {
        // Arrange
        String specialChars = "Test@#$%^&*()_+-=[]{}|;:',.<>?/";

        // Act
        verifiedParticipant.setParticipantName(specialChars);
        verifiedParticipant.setEmail(specialChars);
        verifiedParticipant.setFundraiserTitle(specialChars);

        // Assert
        assertEquals(specialChars, verifiedParticipant.getParticipantName());
        assertEquals(specialChars, verifiedParticipant.getEmail());
        assertEquals(specialChars, verifiedParticipant.getFundraiserTitle());
    }

    @Test
    @DisplayName("Should handle unicode characters in string fields")
    void testUnicodeCharactersInStringFields() {
        // Arrange
        String unicodeText = "नमस्ते مرحبا 你好 🎉";

        // Act
        verifiedParticipant.setParticipantName(unicodeText);

        // Assert
        assertEquals(unicodeText, verifiedParticipant.getParticipantName());
    }

    @Test
    @DisplayName("Should verify parameterized constructor invokes default constructor initialization")
    void testParameterizedConstructorCallsDefaultConstructor() {
        // Arrange & Act
        VerifiedParticipant participant = new VerifiedParticipant(
                "fundraiser-id",
                "Test User",
                "UTR123456789",
                new BigDecimal("1000.00"),
                "test@example.com",
                "MEDICAL",
                "Test Title",
                "admin"
        );

        // Assert - Verify default constructor was called (ID and verifiedAt set)
        assertNotNull(participant.getId());
        assertNotNull(participant.getVerifiedAt());
    }
}
