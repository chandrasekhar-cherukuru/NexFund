package com.chakri.fundly.repo;

import com.chakri.fundly.model.VerifiedParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifiedParticipantRepo Test Suite")
class VerifiedParticipantRepoTest {

    @Mock
    private VerifiedParticipantRepo verifiedParticipantRepo;

    private VerifiedParticipant verifiedParticipant;
    private VerifiedParticipant verifiedParticipant2;
    private String fundraiserId;
    private String createdBy;
    private String utrNumber;

    @BeforeEach
    void setUp() {
        // ARRANGE: Initialize test data
        fundraiserId = "fundraiser-123";
        createdBy = "creator-456";
        utrNumber = "UTR123456789";

        verifiedParticipant = new VerifiedParticipant(
                fundraiserId,
                "John Doe",
                utrNumber,
                new BigDecimal("5000.00"),
                "john@example.com",
                "MEDICAL",
                "Medical Fund Drive",
                createdBy
        );

        verifiedParticipant2 = new VerifiedParticipant(
                fundraiserId,
                "Jane Smith",
                "UTR987654321",
                new BigDecimal("10000.00"),
                "jane@example.com",
                "EDUCATION",
                "Education Fund Drive",
                createdBy
        );
    }

    // ==================== findByFundraiserId Tests ====================

    @Test
    @DisplayName("Should return list of participants when fundraiserId exists")
    void testFindByFundraiserIdWithValidFundraiserId() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Arrays.asList(verifiedParticipant, verifiedParticipant2);
        when(verifiedParticipantRepo.findByFundraiserId(fundraiserId))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(verifiedParticipant.getId(), result.get(0).getId());
        assertEquals(verifiedParticipant2.getId(), result.get(1).getId());
        verify(verifiedParticipantRepo, times(1)).findByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("Should return empty list when fundraiserId has no participants")
    void testFindByFundraiserIdWithNoResults() {
        // ARRANGE
        when(verifiedParticipantRepo.findByFundraiserId("non-existent-id"))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserId("non-existent-id");

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(verifiedParticipantRepo, times(1)).findByFundraiserId("non-existent-id");
    }

    @Test
    @DisplayName("Should return single participant when fundraiserId matches one record")
    void testFindByFundraiserIdWithSingleResult() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Collections.singletonList(verifiedParticipant);
        when(verifiedParticipantRepo.findByFundraiserId(fundraiserId))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(verifiedParticipant.getParticipantName(), result.get(0).getParticipantName());
    }

    @Test
    @DisplayName("Should handle null fundraiserId gracefully")
    void testFindByFundraiserIdWithNullId() {
        // ARRANGE
        when(verifiedParticipantRepo.findByFundraiserId(null))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserId(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty string fundraiserId")
    void testFindByFundraiserIdWithEmptyString() {
        // ARRANGE
        when(verifiedParticipantRepo.findByFundraiserId(""))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserId("");

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== findByFundraiserIdOrderByVerifiedAtDesc Tests ====================

    @Test
    @DisplayName("Should return participants ordered by verification date descending")
    void testFindByFundraiserIdOrderByVerifiedAtDescWithValidData() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();
        verifiedParticipant.setVerifiedAt(now);
        verifiedParticipant2.setVerifiedAt(now.minusDays(1));

        List<VerifiedParticipant> expectedParticipants = Arrays.asList(verifiedParticipant, verifiedParticipant2);
        when(verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getVerifiedAt().isAfter(result.get(1).getVerifiedAt()));
        verify(verifiedParticipantRepo, times(1)).findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);
    }

    @Test
    @DisplayName("Should return empty list when no participants found for fundraiser order by verified date")
    void testFindByFundraiserIdOrderByVerifiedAtDescWithNoResults() {
        // ARRANGE
        when(verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc("non-existent-id"))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc("non-existent-id");

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null fundraiserId in order by verified date query")
    void testFindByFundraiserIdOrderByVerifiedAtDescWithNullId() {
        // ARRANGE
        when(verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(null))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return single participant ordered by verified date")
    void testFindByFundraiserIdOrderByVerifiedAtDescWithSingleResult() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Collections.singletonList(verifiedParticipant);
        when(verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByFundraiserIdOrderByVerifiedAtDesc(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==================== findByCreatedByOrderByVerifiedAtDesc Tests ====================

    @Test
    @DisplayName("Should return participants created by specific user ordered by verified date")
    void testFindByCreatedByOrderByVerifiedAtDescWithValidData() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Arrays.asList(verifiedParticipant, verifiedParticipant2);
        when(verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(createdBy))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(createdBy);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(createdBy, result.get(0).getCreatedBy());
        assertEquals(createdBy, result.get(1).getCreatedBy());
        verify(verifiedParticipantRepo, times(1)).findByCreatedByOrderByVerifiedAtDesc(createdBy);
    }

    @Test
    @DisplayName("Should return empty list when no participants found for createdBy")
    void testFindByCreatedByOrderByVerifiedAtDescWithNoResults() {
        // ARRANGE
        when(verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc("unknown-creator"))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc("unknown-creator");

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null createdBy parameter")
    void testFindByCreatedByOrderByVerifiedAtDescWithNullCreatedBy() {
        // ARRANGE
        when(verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(null))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(null);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty createdBy string")
    void testFindByCreatedByOrderByVerifiedAtDescWithEmptyString() {
        // ARRANGE
        when(verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(""))
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc("");

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return single participant for createdBy")
    void testFindByCreatedByOrderByVerifiedAtDescWithSingleResult() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Collections.singletonList(verifiedParticipant);
        when(verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(createdBy))
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findByCreatedByOrderByVerifiedAtDesc(createdBy);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==================== existsByFundraiserIdAndUtrNumber Tests ====================

    @Test
    @DisplayName("Should return true when UTR exists for fundraiser")
    void testExistsByFundraiserIdAndUtrNumberWhenExists() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, utrNumber))
                .thenReturn(true);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, utrNumber);

        // ASSERT
        assertTrue(result);
        verify(verifiedParticipantRepo, times(1)).existsByFundraiserIdAndUtrNumber(fundraiserId, utrNumber);
    }

    @Test
    @DisplayName("Should return false when UTR does not exist for fundraiser")
    void testExistsByFundraiserIdAndUtrNumberWhenDoesNotExist() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, "NON-EXISTENT-UTR"))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, "NON-EXISTENT-UTR");

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when fundraiserId does not exist")
    void testExistsByFundraiserIdAndUtrNumberWithInvalidFundraiserId() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber("invalid-id", utrNumber))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber("invalid-id", utrNumber);

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle null fundraiserId in exists query")
    void testExistsByFundraiserIdAndUtrNumberWithNullFundraiserId() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(null, utrNumber))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(null, utrNumber);

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle null UTR number in exists query")
    void testExistsByFundraiserIdAndUtrNumberWithNullUtrNumber() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, null))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, null);

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle both null parameters in exists query")
    void testExistsByFundraiserIdAndUtrNumberWithBothNullParameters() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(null, null))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(null, null);

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle empty UTR number string")
    void testExistsByFundraiserIdAndUtrNumberWithEmptyUtrNumber() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, ""))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber(fundraiserId, "");

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle empty fundraiserId string in exists query")
    void testExistsByFundraiserIdAndUtrNumberWithEmptyFundraiserId() {
        // ARRANGE
        when(verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber("", utrNumber))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsByFundraiserIdAndUtrNumber("", utrNumber);

        // ASSERT
        assertFalse(result);
    }

    // ==================== countByFundraiserId Tests ====================

    @Test
    @DisplayName("Should return correct count when participants exist")
    void testCountByFundraiserIdWithMultipleParticipants() {
        // ARRANGE
        when(verifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(5L);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(5L, result);
        verify(verifiedParticipantRepo, times(1)).countByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("Should return zero count when no participants exist")
    void testCountByFundraiserIdWithNoParticipants() {
        // ARRANGE
        when(verifiedParticipantRepo.countByFundraiserId("non-existent-id"))
                .thenReturn(0L);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId("non-existent-id");

        // ASSERT
        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Should return count of one when single participant exists")
    void testCountByFundraiserIdWithSingleParticipant() {
        // ARRANGE
        when(verifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(1L);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("Should handle null fundraiserId in count query")
    void testCountByFundraiserIdWithNullId() {
        // ARRANGE
        when(verifiedParticipantRepo.countByFundraiserId(null))
                .thenReturn(0L);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId(null);

        // ASSERT
        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Should handle empty fundraiserId string in count query")
    void testCountByFundraiserIdWithEmptyString() {
        // ARRANGE
        when(verifiedParticipantRepo.countByFundraiserId(""))
                .thenReturn(0L);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId("");

        // ASSERT
        assertNotNull(result);
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Should return large count value")
    void testCountByFundraiserIdWithLargeCount() {
        // ARRANGE
        Long largeCount = 1000L;
        when(verifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(largeCount);

        // ACT
        Long result = verifiedParticipantRepo.countByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(largeCount, result);
    }

    // ==================== getTotalAmountByFundraiserId Tests ====================

    @Test
    @DisplayName("Should return total amount when participants have contributed")
    void testGetTotalAmountByFundraiserIdWithValidAmount() {
        // ARRANGE
        Double totalAmount = 15000.00;
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(totalAmount);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(15000.00, result);
        verify(verifiedParticipantRepo, times(1)).getTotalAmountByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("Should return null when no participants exist for fundraiser")
    void testGetTotalAmountByFundraiserIdWithNoParticipants() {
        // ARRANGE
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId("non-existent-id"))
                .thenReturn(null);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId("non-existent-id");

        // ASSERT
        assertNull(result);
    }

    @Test
    @DisplayName("Should return zero amount when sum aggregation returns zero")
    void testGetTotalAmountByFundraiserIdWithZeroAmount() {
        // ARRANGE
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(0.0);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("Should return total amount with decimal precision")
    void testGetTotalAmountByFundraiserIdWithDecimalPrecision() {
        // ARRANGE
        Double preciseAmount = 10599.99;
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(preciseAmount);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(10599.99, result, 0.01);
    }

    @Test
    @DisplayName("Should handle null fundraiserId in total amount query")
    void testGetTotalAmountByFundraiserIdWithNullId() {
        // ARRANGE
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(null))
                .thenReturn(null);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(null);

        // ASSERT
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle empty fundraiserId string in total amount query")
    void testGetTotalAmountByFundraiserIdWithEmptyString() {
        // ARRANGE
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(""))
                .thenReturn(null);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId("");

        // ASSERT
        assertNull(result);
    }

    @Test
    @DisplayName("Should return large total amount value")
    void testGetTotalAmountByFundraiserIdWithLargeAmount() {
        // ARRANGE
        Double largeAmount = 999999.99;
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(largeAmount);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(999999.99, result, 0.01);
    }

    @Test
    @DisplayName("Should return negative amount if applicable")
    void testGetTotalAmountByFundraiserIdWithNegativeAmount() {
        // ARRANGE
        Double negativeAmount = -5000.00;
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(negativeAmount);

        // ACT
        Double result = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertNotNull(result);
        assertEquals(-5000.00, result);
    }

    // ==================== JpaRepository Default Methods Tests ====================

    @Test
    @DisplayName("Should save verified participant successfully")
    void testSaveVerifiedParticipant() {
        // ARRANGE
        when(verifiedParticipantRepo.save(any(VerifiedParticipant.class)))
                .thenReturn(verifiedParticipant);

        // ACT
        VerifiedParticipant result = verifiedParticipantRepo.save(verifiedParticipant);

        // ASSERT
        assertNotNull(result);
        assertEquals(verifiedParticipant.getId(), result.getId());
        assertEquals(verifiedParticipant.getFundraiserId(), result.getFundraiserId());
        verify(verifiedParticipantRepo, times(1)).save(any(VerifiedParticipant.class));
    }

    @Test
    @DisplayName("Should find participant by ID")
    void testFindById() {
        // ARRANGE
        String participantId = verifiedParticipant.getId();
        when(verifiedParticipantRepo.findById(participantId))
                .thenReturn(Optional.of(verifiedParticipant));

        // ACT
        Optional<VerifiedParticipant> result = verifiedParticipantRepo.findById(participantId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(verifiedParticipant.getId(), result.get().getId());
        verify(verifiedParticipantRepo, times(1)).findById(participantId);
    }

    @Test
    @DisplayName("Should return empty Optional when participant not found by ID")
    void testFindByIdNotFound() {
        // ARRANGE
        when(verifiedParticipantRepo.findById("non-existent-id"))
                .thenReturn(Optional.empty());

        // ACT
        Optional<VerifiedParticipant> result = verifiedParticipantRepo.findById("non-existent-id");

        // ASSERT
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return all participants")
    void testFindAll() {
        // ARRANGE
        List<VerifiedParticipant> expectedParticipants = Arrays.asList(verifiedParticipant, verifiedParticipant2);
        when(verifiedParticipantRepo.findAll())
                .thenReturn(expectedParticipants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(verifiedParticipantRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no participants exist in database")
    void testFindAllEmpty() {
        // ARRANGE
        when(verifiedParticipantRepo.findAll())
                .thenReturn(new ArrayList<>());

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.findAll();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete participant by ID")
    void testDeleteById() {
        // ARRANGE
        String participantId = verifiedParticipant.getId();
        doNothing().when(verifiedParticipantRepo).deleteById(participantId);

        // ACT
        verifiedParticipantRepo.deleteById(participantId);

        // ASSERT
        verify(verifiedParticipantRepo, times(1)).deleteById(participantId);
    }

    @Test
    @DisplayName("Should delete participant entity")
    void testDelete() {
        // ARRANGE
        doNothing().when(verifiedParticipantRepo).delete(any(VerifiedParticipant.class));

        // ACT
        verifiedParticipantRepo.delete(verifiedParticipant);

        // ASSERT
        verify(verifiedParticipantRepo, times(1)).delete(any(VerifiedParticipant.class));
    }

    @Test
    @DisplayName("Should check existence of participant by ID")
    void testExistsById() {
        // ARRANGE
        String participantId = verifiedParticipant.getId();
        when(verifiedParticipantRepo.existsById(participantId))
                .thenReturn(true);

        // ACT
        boolean result = verifiedParticipantRepo.existsById(participantId);

        // ASSERT
        assertTrue(result);
        verify(verifiedParticipantRepo, times(1)).existsById(participantId);
    }

    @Test
    @DisplayName("Should return false when participant does not exist by ID")
    void testExistsByIdNotFound() {
        // ARRANGE
        when(verifiedParticipantRepo.existsById("non-existent-id"))
                .thenReturn(false);

        // ACT
        boolean result = verifiedParticipantRepo.existsById("non-existent-id");

        // ASSERT
        assertFalse(result);
    }

    @Test
    @DisplayName("Should save all participants")
    void testSaveAll() {
        // ARRANGE
        List<VerifiedParticipant> participants = Arrays.asList(verifiedParticipant, verifiedParticipant2);
        when(verifiedParticipantRepo.saveAll(any(List.class)))
                .thenReturn(participants);

        // ACT
        List<VerifiedParticipant> result = verifiedParticipantRepo.saveAll(participants);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(verifiedParticipantRepo, times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("Should return count of all participants")
    void testCount() {
        // ARRANGE
        when(verifiedParticipantRepo.count())
                .thenReturn(10L);

        // ACT
        Long result = verifiedParticipantRepo.count();

        // ASSERT
        assertNotNull(result);
        assertEquals(10L, result);
        verify(verifiedParticipantRepo, times(1)).count();
    }

    @Test
    @DisplayName("Should return zero count when no participants exist")
    void testCountZero() {
        // ARRANGE
        when(verifiedParticipantRepo.count())
                .thenReturn(0L);

        // ACT
        Long result = verifiedParticipantRepo.count();

        // ASSERT
        assertNotNull(result);
        assertEquals(0L, result);
    }

    // ==================== Integration Scenario Tests ====================

    @Test
    @DisplayName("Should verify participant data consistency across multiple methods")
    void testDataConsistencyAcrossMultipleMethods() {
        // ARRANGE
        List<VerifiedParticipant> participants = Collections.singletonList(verifiedParticipant);
        when(verifiedParticipantRepo.findByFundraiserId(fundraiserId))
                .thenReturn(participants);
        when(verifiedParticipantRepo.countByFundraiserId(fundraiserId))
                .thenReturn(1L);
        when(verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId))
                .thenReturn(5000.00);

        // ACT
        List<VerifiedParticipant> foundParticipants = verifiedParticipantRepo.findByFundraiserId(fundraiserId);
        Long count = verifiedParticipantRepo.countByFundraiserId(fundraiserId);
        Double totalAmount = verifiedParticipantRepo.getTotalAmountByFundraiserId(fundraiserId);

        // ASSERT
        assertEquals(count.intValue(), foundParticipants.size());
        assertEquals(5000.00, totalAmount);
        verify(verifiedParticipantRepo, times(1)).findByFundraiserId(fundraiserId);
        verify(verifiedParticipantRepo, times(1)).countByFundraiserId(fundraiserId);
        verify(verifiedParticipantRepo, times(1)).getTotalAmountByFundraiserId(fundraiserId);
    }

    @Test
    @DisplayName("Should handle multiple fundraisers independently")
    void testMultipleFundraisersIndependence() {
        // ARRANGE
        String fundraiserId2 = "fundraiser-456";
        List<VerifiedParticipant> fundraiser1Participants = Collections.singletonList(verifiedParticipant);
        List<VerifiedParticipant> fundraiser2Participants = Collections.singletonList(verifiedParticipant2);

        when(verifiedParticipantRepo.findByFundraiserId(fundraiserId))
                .thenReturn(fundraiser1Participants);
        when(verifiedParticipantRepo.findByFundraiserId(fundraiserId2))
                .thenReturn(fundraiser2Participants);

        // ACT
        List<VerifiedParticipant> result1 = verifiedParticipantRepo.findByFundraiserId(fundraiserId);
        List<VerifiedParticipant> result2 = verifiedParticipantRepo.findByFundraiserId(fundraiserId2);

        // ASSERT
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertNotEquals(result1.get(0).getId(), result2.get(0).getId());
    }
}
