package com.chakri.fundly.controller;

import com.chakri.fundly.model.VerifiedParticipant;
import com.chakri.fundly.repo.VerifiedParticipantRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifiedParticipantControllerTest {

    @Mock
    private VerifiedParticipantRepo participantRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VerifiedParticipantController controller;

    private Map<String, Object> validRequestData;
    private VerifiedParticipant testParticipant;
    private String testFundraiserId;
    private String testUsername;

    @BeforeEach
    void setUp() {
        testFundraiserId = "fundraiser-123";
        testUsername = "user@example.com";

        // Valid request data
        validRequestData = new HashMap<>();
        validRequestData.put("fundraiserId", testFundraiserId);
        validRequestData.put("participantName", "John Doe");
        validRequestData.put("utrNumber", "123456789012");
        validRequestData.put("amount", "1000.50");
        validRequestData.put("email", "john@example.com");
        validRequestData.put("fundraiserType", "Medical");
        validRequestData.put("fundraiserTitle", "Help John's Treatment");

        // Test participant
        testParticipant = new VerifiedParticipant(
                testFundraiserId,
                "John Doe",
                "123456789012",
                new BigDecimal("1000.50"),
                "john@example.com",
                "Medical",
                "Help John's Treatment",
                testUsername
        );
        testParticipant.setId("participant-123");
    }

    // ============= VERIFY PARTICIPANT TESTS =============

    @Test
    void testVerifyParticipant_SuccessfulVerification() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenReturn(false);
        when(participantRepository.save(any(VerifiedParticipant.class)))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));
        assertEquals("Participant verified successfully", responseBody.get("message"));
        assertEquals("participant-123", responseBody.get("participantId"));

        verify(participantRepository, times(1)).save(any(VerifiedParticipant.class));
    }

    @Test
    void testVerifyParticipant_MissingFundraiserId() {
        // ARRANGE
        validRequestData.put("fundraiserId", null);
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_MissingParticipantName() {
        // ARRANGE
        validRequestData.put("participantName", null);
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_MissingUtrNumber() {
        // ARRANGE
        validRequestData.put("utrNumber", null);
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_MissingAmount() {
        // ARRANGE
        validRequestData.put("amount", null);
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_NegativeAmount() {
        // ARRANGE
        validRequestData.put("amount", "-100");
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_ZeroAmount() {
        // ARRANGE
        validRequestData.put("amount", "0");
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_DuplicateUtr() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenReturn(true);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("UTR number already exists for this fundraiser", response.getBody());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void testVerifyParticipant_NumberFormatException() {
        // ARRANGE
        validRequestData.put("amount", "invalid-number");
        when(authentication.getName()).thenReturn(testUsername);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
        assertTrue(((String) responseBody.get("message")).contains("Failed to verify participant"));
    }

    @Test
    void testVerifyParticipant_RepositoryException() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
        assertTrue(((String) responseBody.get("message")).contains("Database error"));
    }

    @Test
    void testVerifyParticipant_SaveThrowsException() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenReturn(false);
        when(participantRepository.save(any(VerifiedParticipant.class)))
                .thenThrow(new RuntimeException("Save failed"));

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
    }

    @Test
    void testVerifyParticipant_WithNullOptionalFields() {
        // ARRANGE
        Map<String, Object> requestWithNulls = new HashMap<>(validRequestData);
        requestWithNulls.put("email", null);
        requestWithNulls.put("fundraiserType", null);
        requestWithNulls.put("fundraiserTitle", null);

        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenReturn(false);
        when(participantRepository.save(any(VerifiedParticipant.class)))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(requestWithNulls, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(participantRepository, times(1)).save(any(VerifiedParticipant.class));
    }

    @Test
    void testVerifyParticipant_VerifiesSavedParticipantDetails() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.existsByFundraiserIdAndUtrNumber(testFundraiserId, "123456789012"))
                .thenReturn(false);
        when(participantRepository.save(any(VerifiedParticipant.class)))
                .thenReturn(testParticipant);

        ArgumentCaptor<VerifiedParticipant> captor = ArgumentCaptor.forClass(VerifiedParticipant.class);

        // ACT
        ResponseEntity<?> response = controller.verifyParticipant(validRequestData, authentication);

        // ASSERT
        verify(participantRepository, times(1)).save(captor.capture());
        VerifiedParticipant savedParticipant = captor.getValue();

        assertEquals("John Doe", savedParticipant.getParticipantName());
        assertEquals("123456789012", savedParticipant.getUtrNumber());
        assertThat(savedParticipant.getAmountPaid()).isEqualByComparingTo(new BigDecimal("1000.50"));
        assertEquals("john@example.com", savedParticipant.getEmail());
        assertEquals(testFundraiserId, savedParticipant.getFundraiserId());
    }

    // ============= GET PARTICIPANTS BY FUNDRAISER TESTS =============

    @Test
    void testGetParticipantsByFundraiser_Success() {
        // ARRANGE
        List<VerifiedParticipant> participants = Arrays.asList(testParticipant);
        when(participantRepository.findByFundraiserIdOrderByVerifiedAtDesc(testFundraiserId))
                .thenReturn(participants);

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getParticipantsByFundraiser(
                testFundraiserId, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getParticipantName());

        verify(participantRepository, times(1))
                .findByFundraiserIdOrderByVerifiedAtDesc(testFundraiserId);
    }

    @Test
    void testGetParticipantsByFundraiser_EmptyList() {
        // ARRANGE
        when(participantRepository.findByFundraiserIdOrderByVerifiedAtDesc(testFundraiserId))
                .thenReturn(Collections.emptyList());

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getParticipantsByFundraiser(
                testFundraiserId, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetParticipantsByFundraiser_MultipleParticipants() {
        // ARRANGE
        VerifiedParticipant participant2 = new VerifiedParticipant(
                testFundraiserId,
                "Jane Smith",
                "987654321098",
                new BigDecimal("2000.00"),
                "jane@example.com",
                "Education",
                "Help Jane's Studies",
                testUsername
        );
        participant2.setId("participant-456");

        List<VerifiedParticipant> participants = Arrays.asList(testParticipant, participant2);
        when(participantRepository.findByFundraiserIdOrderByVerifiedAtDesc(testFundraiserId))
                .thenReturn(participants);

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getParticipantsByFundraiser(
                testFundraiserId, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetParticipantsByFundraiser_RepositoryException() {
        // ARRANGE
        when(participantRepository.findByFundraiserIdOrderByVerifiedAtDesc(testFundraiserId))
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getParticipantsByFundraiser(
                testFundraiserId, authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ============= GET MY FUNDRAISER PARTICIPANTS TESTS =============

    @Test
    void testGetMyFundraiserParticipants_Success() {
        // ARRANGE
        List<VerifiedParticipant> participants = Arrays.asList(testParticipant);
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.findByCreatedByOrderByVerifiedAtDesc(testUsername))
                .thenReturn(participants);

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getMyFundraiserParticipants(
                authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(participantRepository, times(1))
                .findByCreatedByOrderByVerifiedAtDesc(testUsername);
    }

    @Test
    void testGetMyFundraiserParticipants_EmptyList() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.findByCreatedByOrderByVerifiedAtDesc(testUsername))
                .thenReturn(Collections.emptyList());

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getMyFundraiserParticipants(
                authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetMyFundraiserParticipants_MultipleParticipants() {
        // ARRANGE
        VerifiedParticipant participant2 = new VerifiedParticipant(
                "fundraiser-456",
                "Jane Smith",
                "987654321098",
                new BigDecimal("2000.00"),
                "jane@example.com",
                "Education",
                "Help Jane's Studies",
                testUsername
        );
        participant2.setId("participant-456");

        List<VerifiedParticipant> participants = Arrays.asList(testParticipant, participant2);
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.findByCreatedByOrderByVerifiedAtDesc(testUsername))
                .thenReturn(participants);

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getMyFundraiserParticipants(
                authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetMyFundraiserParticipants_RepositoryException() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUsername);
        when(participantRepository.findByCreatedByOrderByVerifiedAtDesc(testUsername))
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getMyFundraiserParticipants(
                authentication);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetMyFundraiserParticipants_DifferentUsernames() {
        // ARRANGE
        String differentUser = "different@example.com";
        VerifiedParticipant differentUserParticipant = new VerifiedParticipant(
                testFundraiserId,
                "Different User",
                "111111111111",
                new BigDecimal("500.00"),
                "different@example.com",
                "Medical",
                "Different Fundraiser",
                differentUser
        );
        differentUserParticipant.setId("participant-789");

        List<VerifiedParticipant> participants = Arrays.asList(differentUserParticipant);
        when(authentication.getName()).thenReturn(differentUser);
        when(participantRepository.findByCreatedByOrderByVerifiedAtDesc(differentUser))
                .thenReturn(participants);

        // ACT
        ResponseEntity<List<VerifiedParticipant>> response = controller.getMyFundraiserParticipants(
                authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(differentUser, response.getBody().get(0).getCreatedBy());

        verify(participantRepository, times(1))
                .findByCreatedByOrderByVerifiedAtDesc(differentUser);
    }

    // ============= GET PARTICIPANT STATISTICS TESTS =============

    @Test
    void testGetParticipantStats_Success() {
        // ARRANGE
        Long participantCount = 5L;
        Double totalAmount = 5000.0;

        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(participantCount);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(totalAmount);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().get("participantCount"));
        assertEquals(5000.0, response.getBody().get("totalAmount"));

        verify(participantRepository, times(1)).countByFundraiserId(testFundraiserId);
        verify(participantRepository, times(1)).getTotalAmountByFundraiserId(testFundraiserId);
    }

    @Test
    void testGetParticipantStats_NoParticipants() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(0L);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(null);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody().get("participantCount"));
        assertEquals(0.0, response.getBody().get("totalAmount"));
    }

    @Test
    void testGetParticipantStats_NullCountReturnsZero() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(null);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(5000.0);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody().get("participantCount"));
        assertEquals(5000.0, response.getBody().get("totalAmount"));
    }

    @Test
    void testGetParticipantStats_NullAmountReturnsZero() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(3L);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(null);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody().get("participantCount"));
        assertEquals(0.0, response.getBody().get("totalAmount"));
    }

    @Test
    void testGetParticipantStats_LargeNumbers() {
        // ARRANGE
        Long largeCount = 1000L;
        Double largeAmount = 999999.99;

        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(largeCount);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(largeAmount);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000L, response.getBody().get("participantCount"));
        assertEquals(999999.99, response.getBody().get("totalAmount"));
    }

    @Test
    void testGetParticipantStats_RepositoryException() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetParticipantStats_GetTotalAmountThrowsException() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(5L);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenThrow(new RuntimeException("Aggregation error"));

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetParticipantStats_VerifyBothMethodsCalled() {
        // ARRANGE
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(5L);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(5000.0);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        verify(participantRepository, times(1)).countByFundraiserId(testFundraiserId);
        verify(participantRepository, times(1)).getTotalAmountByFundraiserId(testFundraiserId);
    }

    @Test
    void testGetParticipantStats_DecimalPrecision() {
        // ARRANGE
        Double preciseAmount = 1234.56;
        when(participantRepository.countByFundraiserId(testFundraiserId))
                .thenReturn(2L);
        when(participantRepository.getTotalAmountByFundraiserId(testFundraiserId))
                .thenReturn(preciseAmount);

        // ACT
        ResponseEntity<Map<String, Object>> response = controller.getParticipantStats(testFundraiserId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Double actualAmount = (Double) response.getBody().get("totalAmount");
        assertEquals(1234.56, actualAmount, 0.001);
    }
}
