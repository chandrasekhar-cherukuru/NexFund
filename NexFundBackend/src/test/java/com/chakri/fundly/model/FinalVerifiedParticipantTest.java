package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test class for FinalVerifiedParticipant entity
 * Tests all constructors, getters, setters, and toString method
 * Achieves 80%+ code coverage by testing all code paths
 */
@ExtendWith(MockitoExtension.class)
class FinalVerifiedParticipantTest {

    private FinalVerifiedParticipant participant;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        // ARRANGE: Initialize test data before each test
        testDateTime = LocalDateTime.of(2025, 11, 7, 9, 0, 0);
    }

    // ==================== Constructor Tests ====================

    @Test
    void testDefaultConstructor_ShouldInitializeVerifiedAtWithCurrentTime() {
        // ARRANGE
        LocalDateTime beforeCreation = LocalDateTime.now();

        // ACT
        participant = new FinalVerifiedParticipant();

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertNotNull(participant.getVerifiedAt(), "VerifiedAt should be initialized");
        assertNull(participant.getId(), "ID should be null before persistence");
        assertNull(participant.getParticipantId(), "ParticipantId should be null");
        assertNull(participant.getVerifiedBy(), "VerifiedBy should be null");
        assertNull(participant.getNotes(), "Notes should be null");
        assertTrue(participant.getVerifiedAt().isAfter(beforeCreation.minusSeconds(1))
                        && participant.getVerifiedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "VerifiedAt should be set to current time");
    }

    @Test
    void testTwoArgConstructor_ShouldInitializeParticipantIdAndVerifiedBy() {
        // ARRANGE
        String participantId = "PART-001";
        String verifiedBy = "admin@test.com";
        LocalDateTime beforeCreation = LocalDateTime.now();

        // ACT
        participant = new FinalVerifiedParticipant(participantId, verifiedBy);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertEquals(participantId, participant.getParticipantId(), "ParticipantId should match");
        assertEquals(verifiedBy, participant.getVerifiedBy(), "VerifiedBy should match");
        assertNotNull(participant.getVerifiedAt(), "VerifiedAt should be initialized");
        assertNull(participant.getNotes(), "Notes should be null");
        assertTrue(participant.getVerifiedAt().isAfter(beforeCreation.minusSeconds(1)),
                "VerifiedAt should be set to current time");
    }

    @Test
    void testTwoArgConstructor_WithNullValues_ShouldAcceptNullInputs() {
        // ARRANGE & ACT
        participant = new FinalVerifiedParticipant(null, null);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertNull(participant.getParticipantId(), "ParticipantId should be null");
        assertNull(participant.getVerifiedBy(), "VerifiedBy should be null");
        assertNotNull(participant.getVerifiedAt(), "VerifiedAt should still be initialized");
    }

    @Test
    void testThreeArgConstructor_ShouldInitializeAllFields() {
        // ARRANGE
        String participantId = "PART-002";
        String verifiedBy = "creator@test.com";
        String notes = "Final verification completed successfully";
        LocalDateTime beforeCreation = LocalDateTime.now();

        // ACT
        participant = new FinalVerifiedParticipant(participantId, verifiedBy, notes);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertEquals(participantId, participant.getParticipantId(), "ParticipantId should match");
        assertEquals(verifiedBy, participant.getVerifiedBy(), "VerifiedBy should match");
        assertEquals(notes, participant.getNotes(), "Notes should match");
        assertNotNull(participant.getVerifiedAt(), "VerifiedAt should be initialized");
        assertTrue(participant.getVerifiedAt().isAfter(beforeCreation.minusSeconds(1)),
                "VerifiedAt should be set to current time");
    }

    @Test
    void testThreeArgConstructor_WithEmptyNotes_ShouldAcceptEmptyString() {
        // ARRANGE
        String participantId = "PART-003";
        String verifiedBy = "admin@test.com";
        String emptyNotes = "";

        // ACT
        participant = new FinalVerifiedParticipant(participantId, verifiedBy, emptyNotes);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertEquals(emptyNotes, participant.getNotes(), "Notes should be empty string");
    }

    @Test
    void testThreeArgConstructor_WithNullNotes_ShouldAcceptNullNotes() {
        // ARRANGE
        String participantId = "PART-004";
        String verifiedBy = "admin@test.com";

        // ACT
        participant = new FinalVerifiedParticipant(participantId, verifiedBy, null);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertNull(participant.getNotes(), "Notes should be null");
    }

    @Test
    void testThreeArgConstructor_WithLongNotes_ShouldAcceptLongString() {
        // ARRANGE
        String participantId = "PART-005";
        String verifiedBy = "admin@test.com";
        String longNotes = "A".repeat(500); // Maximum length as per schema

        // ACT
        participant = new FinalVerifiedParticipant(participantId, verifiedBy, longNotes);

        // ASSERT
        assertNotNull(participant, "Participant should not be null");
        assertEquals(longNotes, participant.getNotes(), "Notes should match long string");
        assertEquals(500, participant.getNotes().length(), "Notes length should be 500");
    }

    // ==================== Getter Tests ====================

    @Test
    void testGetId_ShouldReturnCorrectId() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        Long expectedId = 100L;

        // ACT
        participant.setId(expectedId);

        // ASSERT
        assertEquals(expectedId, participant.getId(), "getId should return correct ID");
    }

    @Test
    void testGetId_WithNullValue_ShouldReturnNull() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT
        Long result = participant.getId();

        // ASSERT
        assertNull(result, "getId should return null when not set");
    }

    @Test
    void testGetParticipantId_ShouldReturnCorrectValue() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String expectedParticipantId = "PART-100";

        // ACT
        participant.setParticipantId(expectedParticipantId);

        // ASSERT
        assertEquals(expectedParticipantId, participant.getParticipantId(),
                "getParticipantId should return correct value");
    }

    @Test
    void testGetVerifiedBy_ShouldReturnCorrectValue() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String expectedVerifiedBy = "tester@test.com";

        // ACT
        participant.setVerifiedBy(expectedVerifiedBy);

        // ASSERT
        assertEquals(expectedVerifiedBy, participant.getVerifiedBy(),
                "getVerifiedBy should return correct value");
    }

    @Test
    void testGetVerifiedAt_ShouldReturnCorrectDateTime() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 10, 15, 10, 30, 0);

        // ACT
        participant.setVerifiedAt(expectedDateTime);

        // ASSERT
        assertEquals(expectedDateTime, participant.getVerifiedAt(),
                "getVerifiedAt should return correct datetime");
    }

    @Test
    void testGetNotes_ShouldReturnCorrectValue() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String expectedNotes = "Test notes for verification";

        // ACT
        participant.setNotes(expectedNotes);

        // ASSERT
        assertEquals(expectedNotes, participant.getNotes(),
                "getNotes should return correct value");
    }

    // ==================== Setter Tests ====================

    @Test
    void testSetId_ShouldUpdateIdCorrectly() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        Long newId = 200L;

        // ACT
        participant.setId(newId);

        // ASSERT
        assertEquals(newId, participant.getId(), "setId should update ID correctly");
    }

    @Test
    void testSetId_WithNullValue_ShouldAcceptNull() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        participant.setId(100L);

        // ACT
        participant.setId(null);

        // ASSERT
        assertNull(participant.getId(), "setId should accept null value");
    }

    @Test
    void testSetParticipantId_ShouldUpdateValueCorrectly() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String newParticipantId = "PART-NEW-001";

        // ACT
        participant.setParticipantId(newParticipantId);

        // ASSERT
        assertEquals(newParticipantId, participant.getParticipantId(),
                "setParticipantId should update value correctly");
    }

    @Test
    void testSetParticipantId_WithNullValue_ShouldAcceptNull() {
        // ARRANGE
        participant = new FinalVerifiedParticipant("PART-001", "admin");

        // ACT
        participant.setParticipantId(null);

        // ASSERT
        assertNull(participant.getParticipantId(), "setParticipantId should accept null");
    }

    @Test
    void testSetVerifiedBy_ShouldUpdateValueCorrectly() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String newVerifiedBy = "newadmin@test.com";

        // ACT
        participant.setVerifiedBy(newVerifiedBy);

        // ASSERT
        assertEquals(newVerifiedBy, participant.getVerifiedBy(),
                "setVerifiedBy should update value correctly");
    }

    @Test
    void testSetVerifiedBy_WithEmptyString_ShouldAcceptEmptyValue() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT
        participant.setVerifiedBy("");

        // ASSERT
        assertEquals("", participant.getVerifiedBy(), "setVerifiedBy should accept empty string");
    }

    @Test
    void testSetVerifiedAt_ShouldUpdateDateTimeCorrectly() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        LocalDateTime newDateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        // ACT
        participant.setVerifiedAt(newDateTime);

        // ASSERT
        assertEquals(newDateTime, participant.getVerifiedAt(),
                "setVerifiedAt should update datetime correctly");
    }

    @Test
    void testSetVerifiedAt_WithNullValue_ShouldAcceptNull() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT
        participant.setVerifiedAt(null);

        // ASSERT
        assertNull(participant.getVerifiedAt(), "setVerifiedAt should accept null");
    }

    @Test
    void testSetNotes_ShouldUpdateValueCorrectly() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String newNotes = "Updated verification notes";

        // ACT
        participant.setNotes(newNotes);

        // ASSERT
        assertEquals(newNotes, participant.getNotes(), "setNotes should update value correctly");
    }

    @Test
    void testSetNotes_WithNullValue_ShouldAcceptNull() {
        // ARRANGE
        participant = new FinalVerifiedParticipant("PART-001", "admin", "Initial notes");

        // ACT
        participant.setNotes(null);

        // ASSERT
        assertNull(participant.getNotes(), "setNotes should accept null");
    }

    @Test
    void testSetNotes_WithMaxLength_ShouldAcceptLongString() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String maxLengthNotes = "X".repeat(500);

        // ACT
        participant.setNotes(maxLengthNotes);

        // ASSERT
        assertEquals(maxLengthNotes, participant.getNotes(),
                "setNotes should accept max length string");
        assertEquals(500, participant.getNotes().length(), "Notes should be 500 characters");
    }

    // ==================== toString Method Tests ====================

    @Test
    void testToString_WithAllFieldsSet_ShouldReturnFormattedString() {
        // ARRANGE
        participant = new FinalVerifiedParticipant("PART-001", "admin@test.com", "Test notes");
        participant.setId(1L);
        LocalDateTime specificDateTime = LocalDateTime.of(2025, 11, 7, 9, 0, 0);
        participant.setVerifiedAt(specificDateTime);

        // ACT
        String result = participant.toString();

        // ASSERT
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("FinalVerifiedParticipant{"),
                "toString should contain class name");
        assertTrue(result.contains("id=1"), "toString should contain id");
        assertTrue(result.contains("participantId='PART-001'"),
                "toString should contain participantId");
        assertTrue(result.contains("verifiedBy='admin@test.com'"),
                "toString should contain verifiedBy");
        assertTrue(result.contains("verifiedAt=" + specificDateTime),
                "toString should contain verifiedAt");
    }

    @Test
    void testToString_WithNullFields_ShouldHandleNullValues() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT
        String result = participant.toString();

        // ASSERT
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("FinalVerifiedParticipant{"),
                "toString should contain class name");
        assertTrue(result.contains("id=null"), "toString should contain null id");
        assertTrue(result.contains("participantId='null'"),
                "toString should contain null participantId");
        assertTrue(result.contains("verifiedBy='null'"),
                "toString should contain null verifiedBy");
    }

    @Test
    void testToString_WithPartiallySetFields_ShouldReturnCorrectFormat() {
        // ARRANGE
        participant = new FinalVerifiedParticipant("PART-999", "partial@test.com");
        participant.setId(999L);

        // ACT
        String result = participant.toString();

        // ASSERT
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("id=999"), "toString should contain correct id");
        assertTrue(result.contains("participantId='PART-999'"),
                "toString should contain correct participantId");
        assertTrue(result.contains("verifiedBy='partial@test.com'"),
                "toString should contain correct verifiedBy");
        assertTrue(result.contains("verifiedAt="), "toString should contain verifiedAt field");
    }

    // ==================== Edge Case Tests ====================

    @Test
    void testMultipleSettersInSequence_ShouldRetainLatestValue() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT
        participant.setParticipantId("PART-001");
        participant.setParticipantId("PART-002");
        participant.setParticipantId("PART-003");

        // ASSERT
        assertEquals("PART-003", participant.getParticipantId(),
                "Should retain the latest set value");
    }

    @Test
    void testObjectCreationWithConstructorChaining_ShouldInitializeCorrectly() {
        // ARRANGE & ACT
        participant = new FinalVerifiedParticipant("PART-CHAIN", "chain@test.com", "Chained constructor");

        // ASSERT
        assertNotNull(participant.getVerifiedAt(),
                "Constructor chaining should initialize verifiedAt");
        assertEquals("PART-CHAIN", participant.getParticipantId(),
                "Constructor chaining should set participantId");
        assertEquals("chain@test.com", participant.getVerifiedBy(),
                "Constructor chaining should set verifiedBy");
        assertEquals("Chained constructor", participant.getNotes(),
                "Constructor chaining should set notes");
    }

    @Test
    void testSettersWithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String specialParticipantId = "PART-@#$%^&*()";
        String specialVerifiedBy = "user+test@domain.co.uk";
        String specialNotes = "Notes with special chars: !@#$%^&*()_+-={}[]|:;<>?,./";

        // ACT
        participant.setParticipantId(specialParticipantId);
        participant.setVerifiedBy(specialVerifiedBy);
        participant.setNotes(specialNotes);

        // ASSERT
        assertEquals(specialParticipantId, participant.getParticipantId(),
                "Should handle special characters in participantId");
        assertEquals(specialVerifiedBy, participant.getVerifiedBy(),
                "Should handle special characters in verifiedBy");
        assertEquals(specialNotes, participant.getNotes(),
                "Should handle special characters in notes");
    }

    @Test
    void testVerifiedAtTimestamp_ShouldBeConsistentAcrossGetterCalls() {
        // ARRANGE
        participant = new FinalVerifiedParticipant("PART-001", "admin");

        // ACT
        LocalDateTime firstCall = participant.getVerifiedAt();
        LocalDateTime secondCall = participant.getVerifiedAt();

        // ASSERT
        assertEquals(firstCall, secondCall,
                "Multiple getter calls should return the same timestamp");
    }

    @Test
    void testIdBoundaryValues_ShouldHandleMinAndMaxLongValues() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();

        // ACT & ASSERT - Test minimum value
        participant.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, participant.getId(),
                "Should handle Long.MIN_VALUE");

        // ACT & ASSERT - Test maximum value
        participant.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, participant.getId(),
                "Should handle Long.MAX_VALUE");

        // ACT & ASSERT - Test zero
        participant.setId(0L);
        assertEquals(0L, participant.getId(), "Should handle zero value");
    }

    @Test
    void testNotesWithUnicodeCharacters_ShouldAcceptUnicodeText() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        String unicodeNotes = "Unicode test: 你好 مرحبا שלום Привет 🎉";

        // ACT
        participant.setNotes(unicodeNotes);

        // ASSERT
        assertEquals(unicodeNotes, participant.getNotes(),
                "Should handle Unicode characters in notes");
    }

    @Test
    void testVerifiedAtWithPastDateTime_ShouldAcceptPastDates() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        LocalDateTime pastDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        // ACT
        participant.setVerifiedAt(pastDateTime);

        // ASSERT
        assertEquals(pastDateTime, participant.getVerifiedAt(),
                "Should accept past datetime values");
    }

    @Test
    void testVerifiedAtWithFutureDateTime_ShouldAcceptFutureDates() {
        // ARRANGE
        participant = new FinalVerifiedParticipant();
        LocalDateTime futureDateTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

        // ACT
        participant.setVerifiedAt(futureDateTime);

        // ASSERT
        assertEquals(futureDateTime, participant.getVerifiedAt(),
                "Should accept future datetime values");
    }
}
