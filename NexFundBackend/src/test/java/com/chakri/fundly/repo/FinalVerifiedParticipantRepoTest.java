package com.chakri.fundly.repo;

import com.chakri.fundly.model.FinalVerifiedParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit test class for FinalVerifiedParticipantRepo interface.
 * This test class mocks all repository methods and tests various scenarios
 * including happy paths, edge cases, error scenarios, and boundary conditions.
 *
 * Target Code Coverage: 80%+
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinalVerifiedParticipantRepo Tests")
class FinalVerifiedParticipantRepoTest {

    @Mock
    private FinalVerifiedParticipantRepo finalVerifiedParticipantRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============== Test Data Setup Methods ==============

    private FinalVerifiedParticipant createTestParticipant(
            String participantId, String verifiedBy, String notes) {
        FinalVerifiedParticipant participant = new FinalVerifiedParticipant(
                participantId, verifiedBy, notes);
        participant.setId(1L);
        participant.setVerifiedAt(LocalDateTime.now());
        return participant;
    }

    private FinalVerifiedParticipant createTestParticipant(
            String participantId, String verifiedBy) {
        FinalVerifiedParticipant participant = new FinalVerifiedParticipant(
                participantId, verifiedBy);
        participant.setId(1L);
        participant.setVerifiedAt(LocalDateTime.now());
        return participant;
    }

    // ============== existsByParticipantId Tests ==============

    @Test
    @DisplayName("existsByParticipantId: Should return true when participant exists")
    void testExistsByParticipantId_ParticipantExists_ReturnsTrue() {
        // ARRANGE
        String participantId = "PART001";
        when(finalVerifiedParticipantRepo.existsByParticipantId(participantId))
                .thenReturn(true);

        // ACT
        boolean result = finalVerifiedParticipantRepo.existsByParticipantId(participantId);

        // ASSERT
        assertTrue(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(participantId);
    }

    @Test
    @DisplayName("existsByParticipantId: Should return false when participant does not exist")
    void testExistsByParticipantId_ParticipantNotExists_ReturnsFalse() {
        // ARRANGE
        String participantId = "NONEXISTENT001";
        when(finalVerifiedParticipantRepo.existsByParticipantId(participantId))
                .thenReturn(false);

        // ACT
        boolean result = finalVerifiedParticipantRepo.existsByParticipantId(participantId);

        // ASSERT
        assertFalse(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(participantId);
    }

    @Test
    @DisplayName("existsByParticipantId: Should handle null participantId")
    void testExistsByParticipantId_NullParticipantId() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.existsByParticipantId(null))
                .thenReturn(false);

        // ACT
        boolean result = finalVerifiedParticipantRepo.existsByParticipantId(null);

        // ASSERT
        assertFalse(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(null);
    }

    @Test
    @DisplayName("existsByParticipantId: Should handle empty participantId string")
    void testExistsByParticipantId_EmptyParticipantId_ReturnsFalse() {
        // ARRANGE
        String participantId = "";
        when(finalVerifiedParticipantRepo.existsByParticipantId(participantId))
                .thenReturn(false);

        // ACT
        boolean result = finalVerifiedParticipantRepo.existsByParticipantId(participantId);

        // ASSERT
        assertFalse(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(participantId);
    }

    // ============== findByParticipantId Tests ==============

    @Test
    @DisplayName("findByParticipantId: Should return participant when exists")
    void testFindByParticipantId_ParticipantExists_ReturnsOptional() {
        // ARRANGE
        String participantId = "PART001";
        FinalVerifiedParticipant expectedParticipant =
                createTestParticipant(participantId, "admin", "Test notes");
        when(finalVerifiedParticipantRepo.findByParticipantId(participantId))
                .thenReturn(Optional.of(expectedParticipant));

        // ACT
        Optional<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantId(participantId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(participantId, result.get().getParticipantId());
        assertEquals("admin", result.get().getVerifiedBy());
        assertEquals("Test notes", result.get().getNotes());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantId(participantId);
    }

    @Test
    @DisplayName("findByParticipantId: Should return empty Optional when participant not found")
    void testFindByParticipantId_ParticipantNotFound_ReturnsEmpty() {
        // ARRANGE
        String participantId = "NONEXISTENT001";
        when(finalVerifiedParticipantRepo.findByParticipantId(participantId))
                .thenReturn(Optional.empty());

        // ACT
        Optional<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantId(participantId);

        // ASSERT
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantId(participantId);
    }

    @Test
    @DisplayName("findByParticipantId: Should handle null participantId")
    void testFindByParticipantId_NullParticipantId_ReturnsEmpty() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.findByParticipantId(null))
                .thenReturn(Optional.empty());

        // ACT
        Optional<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantId(null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantId(null);
    }

    @Test
    @DisplayName("findByParticipantId: Should handle empty participantId string")
    void testFindByParticipantId_EmptyParticipantId_ReturnsEmpty() {
        // ARRANGE
        String participantId = "";
        when(finalVerifiedParticipantRepo.findByParticipantId(participantId))
                .thenReturn(Optional.empty());

        // ACT
        Optional<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantId(participantId);

        // ASSERT
        assertFalse(result.isPresent());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantId(participantId);
    }

    // ============== findByVerifiedByOrderByVerifiedAtDesc Tests ==============

    @Test
    @DisplayName("findByVerifiedByOrderByVerifiedAtDesc: Should return list of participants ordered by verified date")
    void testFindByVerifiedByOrderByVerifiedAtDesc_ParticipantsExist_ReturnsOrderedList() {
        // ARRANGE
        String verifiedBy = "admin";
        LocalDateTime now = LocalDateTime.now();
        FinalVerifiedParticipant participant1 = createTestParticipant("PART001", verifiedBy);
        participant1.setVerifiedAt(now.minusDays(1));
        FinalVerifiedParticipant participant2 = createTestParticipant("PART002", verifiedBy);
        participant2.setVerifiedAt(now);

        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant2, participant1);
        when(finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("PART002", result.get(0).getParticipantId());
        assertEquals("PART001", result.get(1).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);
    }

    @Test
    @DisplayName("findByVerifiedByOrderByVerifiedAtDesc: Should return empty list when no participants found")
    void testFindByVerifiedByOrderByVerifiedAtDesc_NoParticipants_ReturnsEmptyList() {
        // ARRANGE
        String verifiedBy = "unknownAdmin";
        when(finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);
    }

    @Test
    @DisplayName("findByVerifiedByOrderByVerifiedAtDesc: Should handle null verifiedBy")
    void testFindByVerifiedByOrderByVerifiedAtDesc_NullVerifiedBy_ReturnsEmptyList() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(null))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByVerifiedByOrderByVerifiedAtDesc(null);
    }

    @Test
    @DisplayName("findByVerifiedByOrderByVerifiedAtDesc: Should handle single participant")
    void testFindByVerifiedByOrderByVerifiedAtDesc_SingleParticipant_ReturnsList() {
        // ARRANGE
        String verifiedBy = "admin";
        FinalVerifiedParticipant participant = createTestParticipant("PART001", verifiedBy);
        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant);
        when(finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("PART001", result.get(0).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);
    }

    // ============== findByParticipantIdInOrderByVerifiedAtDesc Tests ==============

    @Test
    @DisplayName("findByParticipantIdInOrderByVerifiedAtDesc: Should return list of participants for given IDs")
    void testFindByParticipantIdInOrderByVerifiedAtDesc_ValidIds_ReturnsList() {
        // ARRANGE
        List<String> participantIds = Arrays.asList("PART001", "PART002", "PART003");
        LocalDateTime now = LocalDateTime.now();

        FinalVerifiedParticipant participant1 = createTestParticipant("PART001", "admin");
        participant1.setVerifiedAt(now.minusDays(2));

        FinalVerifiedParticipant participant2 = createTestParticipant("PART002", "admin");
        participant2.setVerifiedAt(now);

        FinalVerifiedParticipant participant3 = createTestParticipant("PART003", "admin");
        participant3.setVerifiedAt(now.minusDays(1));

        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant2, participant3, participant1);
        when(finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds);

        // ASSERT
        assertEquals(3, result.size());
        assertEquals("PART002", result.get(0).getParticipantId());
        assertEquals("PART003", result.get(1).getParticipantId());
        assertEquals("PART001", result.get(2).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantIdInOrderByVerifiedAtDesc(participantIds);
    }

    @Test
    @DisplayName("findByParticipantIdInOrderByVerifiedAtDesc: Should return empty list for empty input list")
    void testFindByParticipantIdInOrderByVerifiedAtDesc_EmptyList_ReturnsEmptyList() {
        // ARRANGE
        List<String> participantIds = Collections.emptyList();
        when(finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantIdInOrderByVerifiedAtDesc(participantIds);
    }

    @Test
    @DisplayName("findByParticipantIdInOrderByVerifiedAtDesc: Should handle null list")
    void testFindByParticipantIdInOrderByVerifiedAtDesc_NullList_ReturnsEmptyList() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(null))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantIdInOrderByVerifiedAtDesc(null);
    }

    @Test
    @DisplayName("findByParticipantIdInOrderByVerifiedAtDesc: Should handle single ID in list")
    void testFindByParticipantIdInOrderByVerifiedAtDesc_SingleId_ReturnsList() {
        // ARRANGE
        List<String> participantIds = Arrays.asList("PART001");
        FinalVerifiedParticipant participant = createTestParticipant("PART001", "admin");
        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant);
        when(finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByParticipantIdInOrderByVerifiedAtDesc(participantIds);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("PART001", result.get(0).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantIdInOrderByVerifiedAtDesc(participantIds);
    }

    // ============== countByVerifiedBy Tests ==============

    @Test
    @DisplayName("countByVerifiedBy: Should return correct count of verified participants")
    void testCountByVerifiedBy_ParticipantsExist_ReturnsCorrectCount() {
        // ARRANGE
        String verifiedBy = "admin";
        Long expectedCount = 5L;
        when(finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy))
                .thenReturn(expectedCount);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy);

        // ASSERT
        assertEquals(expectedCount, result);
        assertEquals(5L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByVerifiedBy(verifiedBy);
    }

    @Test
    @DisplayName("countByVerifiedBy: Should return zero when no participants verified by given verifier")
    void testCountByVerifiedBy_NoParticipants_ReturnsZero() {
        // ARRANGE
        String verifiedBy = "unknownAdmin";
        when(finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy))
                .thenReturn(0L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy);

        // ASSERT
        assertEquals(0L, result);
        assertNotNull(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByVerifiedBy(verifiedBy);
    }

    @Test
    @DisplayName("countByVerifiedBy: Should handle null verifiedBy")
    void testCountByVerifiedBy_NullVerifiedBy_ReturnsZero() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.countByVerifiedBy(null))
                .thenReturn(0L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByVerifiedBy(null);

        // ASSERT
        assertEquals(0L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByVerifiedBy(null);
    }

    @Test
    @DisplayName("countByVerifiedBy: Should return one when single participant verified")
    void testCountByVerifiedBy_SingleParticipant_ReturnsOne() {
        // ARRANGE
        String verifiedBy = "admin";
        when(finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy))
                .thenReturn(1L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy);

        // ASSERT
        assertEquals(1L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByVerifiedBy(verifiedBy);
    }

    // ============== deleteByParticipantId Tests ==============

    @Test
    @DisplayName("deleteByParticipantId: Should successfully delete participant")
    void testDeleteByParticipantId_ValidParticipantId_DeletesSuccessfully() {
        // ARRANGE
        String participantId = "PART001";
        doNothing().when(finalVerifiedParticipantRepo)
                .deleteByParticipantId(participantId);

        // ACT
        finalVerifiedParticipantRepo.deleteByParticipantId(participantId);

        // ASSERT
        verify(finalVerifiedParticipantRepo, times(1))
                .deleteByParticipantId(participantId);
    }

    @Test
    @DisplayName("deleteByParticipantId: Should handle null participantId")
    void testDeleteByParticipantId_NullParticipantId() {
        // ARRANGE
        doNothing().when(finalVerifiedParticipantRepo)
                .deleteByParticipantId(null);

        // ACT
        finalVerifiedParticipantRepo.deleteByParticipantId(null);

        // ASSERT
        verify(finalVerifiedParticipantRepo, times(1))
                .deleteByParticipantId(null);
    }

    @Test
    @DisplayName("deleteByParticipantId: Should handle empty participantId string")
    void testDeleteByParticipantId_EmptyParticipantId() {
        // ARRANGE
        String participantId = "";
        doNothing().when(finalVerifiedParticipantRepo)
                .deleteByParticipantId(participantId);

        // ACT
        finalVerifiedParticipantRepo.deleteByParticipantId(participantId);

        // ASSERT
        verify(finalVerifiedParticipantRepo, times(1))
                .deleteByParticipantId(participantId);
    }

    @Test
    @DisplayName("deleteByParticipantId: Should verify delete is called only once")
    void testDeleteByParticipantId_VerifyCallCount() {
        // ARRANGE
        String participantId = "PART001";
        doNothing().when(finalVerifiedParticipantRepo)
                .deleteByParticipantId(participantId);

        // ACT
        finalVerifiedParticipantRepo.deleteByParticipantId(participantId);
        finalVerifiedParticipantRepo.deleteByParticipantId(participantId);

        // ASSERT
        verify(finalVerifiedParticipantRepo, times(2))
                .deleteByParticipantId(participantId);
    }

    // ============== findByFundraiserIdOrderByVerifiedAtDesc Tests ==============

    @Test
    @DisplayName("findByFundraiserIdOrderByVerifiedAtDesc: Should return list of verified participants by fundraiser")
    void testFindByFundraiserIdOrderByVerifiedAtDesc_ValidFundraiserId_ReturnsList() {
        // ARRANGE
        String fundraiserId = "FUND001";
        LocalDateTime now = LocalDateTime.now();

        FinalVerifiedParticipant participant1 = createTestParticipant("PART001", "admin");
        participant1.setVerifiedAt(now);

        FinalVerifiedParticipant participant2 = createTestParticipant("PART002", "admin");
        participant2.setVerifiedAt(now.minusDays(1));

        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant1, participant2);
        when(finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("PART001", result.get(0).getParticipantId());
        assertEquals("PART002", result.get(1).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);
    }

    @Test
    @DisplayName("findByFundraiserIdOrderByVerifiedAtDesc: Should return empty list when no participants found")
    void testFindByFundraiserIdOrderByVerifiedAtDesc_NoParticipants_ReturnsEmptyList() {
        // ARRANGE
        String fundraiserId = "NONEXISTENT_FUND";
        when(finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);
    }

    @Test
    @DisplayName("findByFundraiserIdOrderByVerifiedAtDesc: Should handle null fundraiserId")
    void testFindByFundraiserIdOrderByVerifiedAtDesc_NullFundraiserId_ReturnsEmptyList() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(null))
                .thenReturn(Collections.emptyList());

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByFundraiserIdOrderByVerifiedAtDesc(null);
    }

    @Test
    @DisplayName("findByFundraiserIdOrderByVerifiedAtDesc: Should handle single participant")
    void testFindByFundraiserIdOrderByVerifiedAtDesc_SingleParticipant_ReturnsList() {
        // ARRANGE
        String fundraiserId = "FUND001";
        FinalVerifiedParticipant participant = createTestParticipant("PART001", "admin");
        List<FinalVerifiedParticipant> expectedList = Arrays.asList(participant);
        when(finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId))
                .thenReturn(expectedList);

        // ACT
        List<FinalVerifiedParticipant> result =
                finalVerifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("PART001", result.get(0).getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);
    }

    // ============== countByFundraiserId Tests ==============

    @Test
    @DisplayName("countByFundraiserId: Should return correct count of participants for fundraiser")
    void testCountByFundraiserId_ValidFundraiserId_ReturnsCorrectCount() {
        // ARRANGE
        String fundraiserId = "FUND001";
        Long expectedCount = 10L;
        when(finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(expectedCount);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertEquals(expectedCount, result);
        assertEquals(10L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("countByFundraiserId: Should return zero when no participants for fundraiser")
    void testCountByFundraiserId_NoParticipants_ReturnsZero() {
        // ARRANGE
        String fundraiserId = "NONEXISTENT_FUND";
        when(finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(0L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertEquals(0L, result);
        assertNotNull(result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("countByFundraiserId: Should handle null fundraiserId")
    void testCountByFundraiserId_NullFundraiserId_ReturnsZero() {
        // ARRANGE
        when(finalVerifiedParticipantRepo.countByFundraiserId(null))
                .thenReturn(0L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByFundraiserId(null);

        // ASSERT
        assertEquals(0L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByFundraiserId(null);
    }

    @Test
    @DisplayName("countByFundraiserId: Should return one when single participant for fundraiser")
    void testCountByFundraiserId_SingleParticipant_ReturnsOne() {
        // ARRANGE
        String fundraiserId = "FUND001";
        when(finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(1L);

        // ACT
        Long result = finalVerifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertEquals(1L, result);
        verify(finalVerifiedParticipantRepo, times(1))
                .countByFundraiserId(fundraiserId);
    }

    // ============== Integration Tests for Multiple Methods ==============

    @Test
    @DisplayName("Integration: Find and verify participant existence flow")
    void testIntegration_FindAndVerifyParticipant() {
        // ARRANGE
        String participantId = "PART001";
        FinalVerifiedParticipant expectedParticipant =
                createTestParticipant(participantId, "admin", "Verified notes");

        when(finalVerifiedParticipantRepo.existsByParticipantId(participantId))
                .thenReturn(true);
        when(finalVerifiedParticipantRepo.findByParticipantId(participantId))
                .thenReturn(Optional.of(expectedParticipant));

        // ACT
        boolean exists = finalVerifiedParticipantRepo.existsByParticipantId(participantId);
        Optional<FinalVerifiedParticipant> participant =
                finalVerifiedParticipantRepo.findByParticipantId(participantId);

        // ASSERT
        assertTrue(exists);
        assertTrue(participant.isPresent());
        assertEquals(expectedParticipant.getParticipantId(), participant.get().getParticipantId());
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(participantId);
        verify(finalVerifiedParticipantRepo, times(1))
                .findByParticipantId(participantId);
    }

    @Test
    @DisplayName("Integration: Delete and verify non-existence")
    void testIntegration_DeleteAndVerifyNonExistence() {
        // ARRANGE
        String participantId = "PART001";
        doNothing().when(finalVerifiedParticipantRepo)
                .deleteByParticipantId(participantId);
        when(finalVerifiedParticipantRepo.existsByParticipantId(participantId))
                .thenReturn(false);

        // ACT
        finalVerifiedParticipantRepo.deleteByParticipantId(participantId);
        boolean exists = finalVerifiedParticipantRepo.existsByParticipantId(participantId);

        // ASSERT
        assertFalse(exists);
        verify(finalVerifiedParticipantRepo, times(1))
                .deleteByParticipantId(participantId);
        verify(finalVerifiedParticipantRepo, times(1))
                .existsByParticipantId(participantId);
    }

    @Test
    @DisplayName("Integration: Count and find by verifier")
    void testIntegration_CountAndFindByVerifier() {
        // ARRANGE
        String verifiedBy = "admin";
        FinalVerifiedParticipant participant = createTestParticipant("PART001", verifiedBy);

        when(finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy))
                .thenReturn(1L);
        when(finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy))
                .thenReturn(Arrays.asList(participant));

        // ACT
        Long count = finalVerifiedParticipantRepo.countByVerifiedBy(verifiedBy);
        List<FinalVerifiedParticipant> participants =
                finalVerifiedParticipantRepo.findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);

        // ASSERT
        assertEquals(1L, count);
        assertEquals(1, participants.size());
        verify(finalVerifiedParticipantRepo, times(1))
                .countByVerifiedBy(verifiedBy);
        verify(finalVerifiedParticipantRepo, times(1))
                .findByVerifiedByOrderByVerifiedAtDesc(verifiedBy);
    }
}
