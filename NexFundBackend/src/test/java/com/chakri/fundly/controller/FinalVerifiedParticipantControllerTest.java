package com.chakri.fundly.controller;

import com.chakri.fundly.model.FinalVerifiedParticipant;
import com.chakri.fundly.service.FinalVerifiedParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit test suite for FinalVerifiedParticipantController
 * Covers all methods, edge cases, error scenarios, and conditional branches
 * Targets 80%+ code coverage with Arrange-Act-Assert pattern
 *
 * FIXED ISSUES:
 * - Changed verify(authentication, times(1)) to times(2) - controller calls getName() twice
 * - Fixed null count comparison to use 0L instead of 0
 * - Removed unnecessary verify calls for authentication in non-auth methods
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinalVerifiedParticipantController Tests")
class FinalVerifiedParticipantControllerTest {

    @Mock
    private FinalVerifiedParticipantService finalVerificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FinalVerifiedParticipantController controller;

    private FinalVerifiedParticipant testParticipant;
    private String testParticipantId;
    private String testUserId;
    private String testFundraiserId;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testParticipantId = "PART-12345";
        testUserId = "user@example.com";
        testFundraiserId = "FUND-67890";
        testDateTime = LocalDateTime.now();

        testParticipant = new FinalVerifiedParticipant();
        testParticipant.setId(1L);
        testParticipant.setParticipantId(testParticipantId);
        testParticipant.setVerifiedBy(testUserId);
        testParticipant.setVerifiedAt(testDateTime);
        testParticipant.setNotes("Test verification notes");
    }

    // ============================================================================
    // POST /participants/final-verify/{participantId} - FINALIZE VERIFICATION
    // ============================================================================

    @Test
    @DisplayName("✅ POST: Should successfully verify participant with notes")
    void testFinallyVerifyParticipant_Success_WithNotes() {
        // ARRANGE
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("notes", "All documents verified");

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, "All documents verified"))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Participant finally verified successfully", responseBody.get("message"));
        assertEquals(testParticipantId, responseBody.get("participantId"));
        assertEquals(testUserId, responseBody.get("verifiedBy"));
        assertEquals(1L, responseBody.get("id"));

        verify(finalVerificationService, times(1))
                .finallyVerifyParticipant(testParticipantId, testUserId, "All documents verified");
        // Controller calls getName() twice: once at line 34 and once at line 37
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("✅ POST: Should successfully verify participant without notes")
    void testFinallyVerifyParticipant_Success_WithoutNotes() {
        // ARRANGE
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("notes", "");

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, ""))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));

        verify(finalVerificationService, times(1))
                .finallyVerifyParticipant(testParticipantId, testUserId, "");
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("✅ POST: Should verify participant with null request body")
    void testFinallyVerifyParticipant_Success_NullRequestBody() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, null))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, null, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));

        verify(finalVerificationService, times(1))
                .finallyVerifyParticipant(testParticipantId, testUserId, null);
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("❌ POST: Should handle IllegalArgumentException during verification")
    void testFinallyVerifyParticipant_IllegalArgumentException() {
        // ARRANGE
        String errorMessage = "Participant not found";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("notes", "Test");

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, "Test"))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals(errorMessage, responseBody.get("message"));
        assertEquals(testParticipantId, responseBody.get("participantId"));

        verify(finalVerificationService, times(1))
                .finallyVerifyParticipant(testParticipantId, testUserId, "Test");
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("❌ POST: Should handle generic Exception during verification")
    void testFinallyVerifyParticipant_GenericException() {
        // ARRANGE
        String errorMessage = "Database connection failed";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("notes", "Test");

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, "Test"))
                .thenThrow(new RuntimeException(errorMessage));

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(((String) responseBody.get("message")).contains("Failed to finally verify participant"));
        assertEquals(testParticipantId, responseBody.get("participantId"));

        verify(authentication, times(2)).getName();
    }

    // ============================================================================
    // GET /participants/final-verify/check/{participantId} - CHECK VERIFICATION
    // ============================================================================

    @Test
    @DisplayName("✅ GET: Should check participant is verified with details")
    void testCheckFinalVerification_IsVerified() {
        // ARRANGE
        when(finalVerificationService.isParticipantFinallyVerified(testParticipantId)).thenReturn(true);
        when(finalVerificationService.getFinalVerification(testParticipantId))
                .thenReturn(Optional.of(testParticipant));

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(testParticipantId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testParticipantId, responseBody.get("participantId"));
        assertEquals(true, responseBody.get("isFinallyVerified"));
        assertEquals(testUserId, responseBody.get("verifiedBy"));
        assertEquals(testDateTime, responseBody.get("verifiedAt"));
        assertEquals("Test verification notes", responseBody.get("notes"));
        assertEquals(1L, responseBody.get("id"));

        verify(finalVerificationService, times(1))
                .isParticipantFinallyVerified(testParticipantId);
        verify(finalVerificationService, times(1))
                .getFinalVerification(testParticipantId);
    }

    @Test
    @DisplayName("✅ GET: Should check participant is not verified")
    void testCheckFinalVerification_NotVerified() {
        // ARRANGE
        when(finalVerificationService.isParticipantFinallyVerified(testParticipantId)).thenReturn(false);

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(testParticipantId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testParticipantId, responseBody.get("participantId"));
        assertEquals(false, responseBody.get("isFinallyVerified"));
        assertFalse(responseBody.containsKey("verifiedBy"));

        verify(finalVerificationService, times(1))
                .isParticipantFinallyVerified(testParticipantId);
        verify(finalVerificationService, never())
                .getFinalVerification(testParticipantId);
    }

    @Test
    @DisplayName("✅ GET: Should handle empty Optional when verified")
    void testCheckFinalVerification_EmptyOptional() {
        // ARRANGE
        when(finalVerificationService.isParticipantFinallyVerified(testParticipantId)).thenReturn(true);
        when(finalVerificationService.getFinalVerification(testParticipantId))
                .thenReturn(Optional.empty());

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(testParticipantId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testParticipantId, responseBody.get("participantId"));
        assertEquals(true, responseBody.get("isFinallyVerified"));
        assertFalse(responseBody.containsKey("verifiedBy"));
    }

    @Test
    @DisplayName("❌ GET: Should handle exception during verification check")
    void testCheckFinalVerification_Exception() {
        // ARRANGE
        when(finalVerificationService.isParticipantFinallyVerified(testParticipantId))
                .thenThrow(new RuntimeException("Service error"));

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(testParticipantId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertEquals(testParticipantId, responseBody.get("participantId"));
    }

    // ============================================================================
    // GET /participants/final-verify/my-verifications - GET USER'S VERIFICATIONS
    // ============================================================================

    @Test
    @DisplayName("✅ GET: Should retrieve user's final verifications")
    void testGetMyFinalVerifications_WithVerifications() {
        // ARRANGE
        FinalVerifiedParticipant participant2 = new FinalVerifiedParticipant();
        participant2.setId(2L);
        participant2.setParticipantId("PART-54321");
        participant2.setVerifiedBy(testUserId);

        List<FinalVerifiedParticipant> verifications = List.of(testParticipant, participant2);

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.getFinalVerificationsByCreator(testUserId))
                .thenReturn(verifications);

        // ACT
        ResponseEntity<?> response = controller.getMyFinalVerifications(authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(2, responseBody.get("count"));
        assertEquals(verifications, responseBody.get("verifications"));

        verify(finalVerificationService, times(1))
                .getFinalVerificationsByCreator(testUserId);
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("✅ GET: Should return empty list when no verifications found")
    void testGetMyFinalVerifications_EmptyList() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.getFinalVerificationsByCreator(testUserId))
                .thenReturn(Collections.emptyList());

        // ACT
        ResponseEntity<?> response = controller.getMyFinalVerifications(authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(0, responseBody.get("count"));
        assertEquals(0, ((List<?>) responseBody.get("verifications")).size());

        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("❌ GET: Should handle exception while fetching user verifications")
    void testGetMyFinalVerifications_Exception() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.getFinalVerificationsByCreator(testUserId))
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<?> response = controller.getMyFinalVerifications(authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.containsKey("error"));

        verify(authentication, times(1)).getName();
    }

    // ============================================================================
    // GET /participants/final-verify/fundraiser/{fundraiserId} - FUNDRAISER VERIFICATIONS
    // ============================================================================

    @Test
    @DisplayName("✅ GET: Should retrieve verifications for fundraiser")
    void testGetFinalVerificationsByFundraiser_WithVerifications() {
        // ARRANGE
        List<FinalVerifiedParticipant> verifications = List.of(testParticipant);
        when(finalVerificationService.getFinalVerificationsByFundraiser(testFundraiserId))
                .thenReturn(verifications);

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationsByFundraiser(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        assertEquals(1, responseBody.get("count"));
        assertEquals(verifications, responseBody.get("verifications"));

        verify(finalVerificationService, times(1))
                .getFinalVerificationsByFundraiser(testFundraiserId);
    }

    @Test
    @DisplayName("✅ GET: Should return empty list for fundraiser with no verifications")
    void testGetFinalVerificationsByFundraiser_EmptyList() {
        // ARRANGE
        when(finalVerificationService.getFinalVerificationsByFundraiser(testFundraiserId))
                .thenReturn(Collections.emptyList());

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationsByFundraiser(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        assertEquals(0, responseBody.get("count"));
    }

    @Test
    @DisplayName("❌ GET: Should handle exception while fetching fundraiser verifications")
    void testGetFinalVerificationsByFundraiser_Exception() {
        // ARRANGE
        when(finalVerificationService.getFinalVerificationsByFundraiser(testFundraiserId))
                .thenThrow(new RuntimeException("Service error"));

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationsByFundraiser(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        assertTrue(responseBody.containsKey("error"));
    }

    // ============================================================================
    // GET /participants/final-verify/stats/{fundraiserId} - VERIFICATION STATISTICS
    // ============================================================================

    @Test
    @DisplayName("✅ GET: Should retrieve verification stats with count")
    void testGetFinalVerificationStats_WithCount() {
        // ARRANGE
        Long verificationCount = 5L;
        when(finalVerificationService.getFinalVerificationCount(testFundraiserId))
                .thenReturn(verificationCount);

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationStats(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        assertEquals(5L, responseBody.get("finalVerificationCount"));

        verify(finalVerificationService, times(1))
                .getFinalVerificationCount(testFundraiserId);
    }

    @Test
    @DisplayName("✅ GET: Should handle null count and default to zero")
    void testGetFinalVerificationStats_NullCount() {
        // ARRANGE
        when(finalVerificationService.getFinalVerificationCount(testFundraiserId))
                .thenReturn(null);

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationStats(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        // FIXED: Use 0L (Long) instead of 0 (Integer) to match the ternary return type
        assertEquals(0L, responseBody.get("finalVerificationCount"));
    }

    @Test
    @DisplayName("✅ GET: Should return zero count")
    void testGetFinalVerificationStats_ZeroCount() {
        // ARRANGE
        when(finalVerificationService.getFinalVerificationCount(testFundraiserId))
                .thenReturn(0L);

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationStats(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(0L, responseBody.get("finalVerificationCount"));
    }

    @Test
    @DisplayName("❌ GET: Should handle exception while fetching stats")
    void testGetFinalVerificationStats_Exception() {
        // ARRANGE
        when(finalVerificationService.getFinalVerificationCount(testFundraiserId))
                .thenThrow(new RuntimeException("Query error"));

        // ACT
        ResponseEntity<?> response = controller.getFinalVerificationStats(testFundraiserId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals(testFundraiserId, responseBody.get("fundraiserId"));
        assertTrue(responseBody.containsKey("error"));
    }

    // ============================================================================
    // DELETE /participants/final-verify/{participantId} - REMOVE VERIFICATION
    // ============================================================================

    @Test
    @DisplayName("✅ DELETE: Should successfully remove final verification")
    void testRemoveFinalVerification_Success() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUserId);
        doNothing().when(finalVerificationService)
                .removeFinalVerification(testParticipantId, testUserId);

        // ACT
        ResponseEntity<?> response = controller.removeFinalVerification(testParticipantId, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Final verification removed successfully", responseBody.get("message"));
        assertEquals(testParticipantId, responseBody.get("participantId"));

        verify(finalVerificationService, times(1))
                .removeFinalVerification(testParticipantId, testUserId);
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("❌ DELETE: Should handle IllegalArgumentException during removal")
    void testRemoveFinalVerification_IllegalArgumentException() {
        // ARRANGE
        String errorMessage = "Verification not found";
        when(authentication.getName()).thenReturn(testUserId);
        doThrow(new IllegalArgumentException(errorMessage))
                .when(finalVerificationService)
                .removeFinalVerification(testParticipantId, testUserId);

        // ACT
        ResponseEntity<?> response = controller.removeFinalVerification(testParticipantId, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals(errorMessage, responseBody.get("error"));
        assertEquals(testParticipantId, responseBody.get("participantId"));

        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("❌ DELETE: Should handle generic Exception during removal")
    void testRemoveFinalVerification_GenericException() {
        // ARRANGE
        when(authentication.getName()).thenReturn(testUserId);
        doThrow(new RuntimeException("Database error"))
                .when(finalVerificationService)
                .removeFinalVerification(testParticipantId, testUserId);

        // ACT
        ResponseEntity<?> response = controller.removeFinalVerification(testParticipantId, authentication);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals(testParticipantId, responseBody.get("participantId"));
        assertTrue(responseBody.containsKey("error"));

        verify(authentication, times(1)).getName();
    }

    // ============================================================================
    // POST /participants/final-verify/bulk-check - BULK VERIFICATION CHECK
    // ============================================================================

    @Test
    @DisplayName("✅ POST: Should bulk check multiple participant verifications")
    void testBulkCheckFinalVerification_MultipleParticipants() {
        // ARRANGE
        List<String> participantIds = List.of("PART-001", "PART-002", "PART-003");
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        when(finalVerificationService.isParticipantFinallyVerified("PART-001")).thenReturn(true);
        when(finalVerificationService.isParticipantFinallyVerified("PART-002")).thenReturn(false);
        when(finalVerificationService.isParticipantFinallyVerified("PART-003")).thenReturn(true);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Boolean> results = (Map<String, Boolean>) responseBody.get("results");
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals(true, results.get("PART-001"));
        assertEquals(false, results.get("PART-002"));
        assertEquals(true, results.get("PART-003"));

        verify(finalVerificationService, times(1))
                .isParticipantFinallyVerified("PART-001");
        verify(finalVerificationService, times(1))
                .isParticipantFinallyVerified("PART-002");
        verify(finalVerificationService, times(1))
                .isParticipantFinallyVerified("PART-003");
    }

    @Test
    @DisplayName("✅ POST: Should bulk check single participant")
    void testBulkCheckFinalVerification_SingleParticipant() {
        // ARRANGE
        List<String> participantIds = List.of("PART-001");
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        when(finalVerificationService.isParticipantFinallyVerified("PART-001")).thenReturn(true);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Boolean> results = (Map<String, Boolean>) responseBody.get("results");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("❌ POST: Should reject bulk check with empty participant list")
    void testBulkCheckFinalVerification_EmptyList() {
        // ARRANGE
        List<String> participantIds = Collections.emptyList();
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
        assertTrue(((String) responseBody.get("error")).contains("participantIds list is required"));
    }

    @Test
    @DisplayName("❌ POST: Should reject bulk check with null participant list")
    void testBulkCheckFinalVerification_NullList() {
        // ARRANGE
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", null);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
    }

    @Test
    @DisplayName("❌ POST: Should handle exception during bulk check")
    void testBulkCheckFinalVerification_Exception() {
        // ARRANGE
        List<String> participantIds = List.of("PART-001");
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        when(finalVerificationService.isParticipantFinallyVerified("PART-001"))
                .thenThrow(new RuntimeException("Service error"));

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.containsKey("error"));
    }

    @Test
    @DisplayName("✅ POST: Should handle mixed verified and non-verified participants")
    void testBulkCheckFinalVerification_MixedResults() {
        // ARRANGE
        List<String> participantIds = List.of("PART-001", "PART-002", "PART-003", "PART-004", "PART-005");
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        when(finalVerificationService.isParticipantFinallyVerified("PART-001")).thenReturn(true);
        when(finalVerificationService.isParticipantFinallyVerified("PART-002")).thenReturn(true);
        when(finalVerificationService.isParticipantFinallyVerified("PART-003")).thenReturn(false);
        when(finalVerificationService.isParticipantFinallyVerified("PART-004")).thenReturn(false);
        when(finalVerificationService.isParticipantFinallyVerified("PART-005")).thenReturn(true);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Boolean> results = (Map<String, Boolean>) responseBody.get("results");
        assertEquals(5, results.size());

        long verifiedCount = results.values().stream().filter(v -> v).count();
        assertEquals(3, verifiedCount);
    }

    // ============================================================================
    // GET /participants/final-verify/health - HEALTH CHECK
    // ============================================================================

    @Test
    @DisplayName("✅ GET: Should perform health check")
    void testHealthCheck_Success() {
        // ACT
        ResponseEntity<?> response = controller.healthCheck();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));
        assertEquals("FinalVerifiedParticipantController", responseBody.get("service"));
        assertNotNull(responseBody.get("timestamp"));

        List<?> endpoints = (List<?>) responseBody.get("endpoints");
        assertNotNull(endpoints);
        assertEquals(8, endpoints.size());
        assertTrue(endpoints.contains("POST /participants/final-verify/{participantId}"));
        assertTrue(endpoints.contains("GET /participants/final-verify/check/{participantId}"));
        assertTrue(endpoints.contains("GET /participants/final-verify/my-verifications"));
        assertTrue(endpoints.contains("GET /participants/final-verify/fundraiser/{fundraiserId}"));
        assertTrue(endpoints.contains("GET /participants/final-verify/stats/{fundraiserId}"));
        assertTrue(endpoints.contains("DELETE /participants/final-verify/{participantId}"));
        assertTrue(endpoints.contains("POST /participants/final-verify/bulk-check"));
        assertTrue(endpoints.contains("GET /participants/final-verify/health"));
    }

    @Test
    @DisplayName("✅ GET: Health check should have non-null timestamp")
    void testHealthCheck_TimestampNotNull() {
        // ACT
        ResponseEntity<?> response = controller.healthCheck();

        // ASSERT
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody.get("timestamp"));
        assertTrue(((Long) responseBody.get("timestamp")) > 0);
    }

    // ============================================================================
    // EDGE CASES AND ADDITIONAL COVERAGE TESTS
    // ============================================================================

    @Test
    @DisplayName("✅ POST: Verify with special characters in notes")
    void testFinallyVerifyParticipant_SpecialCharactersInNotes() {
        // ARRANGE
        Map<String, String> requestBody = new HashMap<>();
        String specialNotes = "Notes with @#$%^&*() special chars: \\n\\t\\r";
        requestBody.put("notes", specialNotes);

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, specialNotes))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(finalVerificationService, times(1))
                .finallyVerifyParticipant(testParticipantId, testUserId, specialNotes);
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("✅ POST: Verify with very long notes")
    void testFinallyVerifyParticipant_LongNotes() {
        // ARRANGE
        Map<String, String> requestBody = new HashMap<>();
        String longNotes = "A".repeat(500);
        requestBody.put("notes", longNotes);

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, longNotes))
                .thenReturn(testParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("✅ GET: Check verification with special characters in participantId")
    void testCheckFinalVerification_SpecialCharacterId() {
        // ARRANGE
        String specialId = "PART-123_456-ABC";
        when(finalVerificationService.isParticipantFinallyVerified(specialId)).thenReturn(true);
        when(finalVerificationService.getFinalVerification(specialId))
                .thenReturn(Optional.of(testParticipant));

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(specialId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(specialId, responseBody.get("participantId"));
    }

    @Test
    @DisplayName("✅ DELETE: Remove verification with different user")
    void testRemoveFinalVerification_WithDifferentUser() {
        // ARRANGE
        String differentUser = "different.user@example.com";
        when(authentication.getName()).thenReturn(differentUser);
        doNothing().when(finalVerificationService)
                .removeFinalVerification(testParticipantId, differentUser);

        // ACT
        ResponseEntity<?> response = controller.removeFinalVerification(testParticipantId, authentication);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(finalVerificationService, times(1))
                .removeFinalVerification(testParticipantId, differentUser);
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("✅ GET: My verifications with multiple results and count consistency")
    void testGetMyFinalVerifications_CountConsistency() {
        // ARRANGE
        List<FinalVerifiedParticipant> verifications = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FinalVerifiedParticipant participant = new FinalVerifiedParticipant();
            participant.setId((long) i);
            participant.setParticipantId("PART-" + i);
            verifications.add(participant);
        }

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.getFinalVerificationsByCreator(testUserId))
                .thenReturn(verifications);

        // ACT
        ResponseEntity<?> response = controller.getMyFinalVerifications(authentication);

        // ASSERT
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        int count = (Integer) responseBody.get("count");
        List<?> returnedVerifications = (List<?>) responseBody.get("verifications");

        assertEquals(count, returnedVerifications.size());
        assertEquals(10, count);
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("✅ POST: Bulk check with large number of participants")
    void testBulkCheckFinalVerification_LargeList() {
        // ARRANGE
        List<String> participantIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            participantIds.add("PART-" + i);
            when(finalVerificationService.isParticipantFinallyVerified("PART-" + i))
                    .thenReturn(i % 2 == 0);
        }

        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("participantIds", participantIds);

        // ACT
        ResponseEntity<?> response = controller.bulkCheckFinalVerification(requestBody);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(true, responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Boolean> results = (Map<String, Boolean>) responseBody.get("results");
        assertEquals(100, results.size());
    }

    @Test
    @DisplayName("✅ POST: Verify response structure")
    void testFinallyVerifyParticipant_VerifyResponseStructure() {
        // ARRANGE
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("notes", "Test");

        FinalVerifiedParticipant responseParticipant = new FinalVerifiedParticipant();
        responseParticipant.setId(1L);
        responseParticipant.setParticipantId(testParticipantId);
        responseParticipant.setVerifiedBy(testUserId);
        responseParticipant.setVerifiedAt(testDateTime);

        when(authentication.getName()).thenReturn(testUserId);
        when(finalVerificationService.finallyVerifyParticipant(testParticipantId, testUserId, "Test"))
                .thenReturn(responseParticipant);

        // ACT
        ResponseEntity<?> response = controller.finallyVerifyParticipant(testParticipantId, requestBody, authentication);

        // ASSERT
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("participantId"));
        assertTrue(responseBody.containsKey("verifiedBy"));
        assertTrue(responseBody.containsKey("verifiedAt"));
        assertTrue(responseBody.containsKey("id"));
        verify(authentication, times(2)).getName();
    }

    @Test
    @DisplayName("✅ GET: Verification check response contains all expected keys")
    void testCheckFinalVerification_ResponseStructure_Verified() {
        // ARRANGE
        when(finalVerificationService.isParticipantFinallyVerified(testParticipantId)).thenReturn(true);
        when(finalVerificationService.getFinalVerification(testParticipantId))
                .thenReturn(Optional.of(testParticipant));

        // ACT
        ResponseEntity<?> response = controller.checkFinalVerification(testParticipantId);

        // ASSERT
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("participantId"));
        assertTrue(responseBody.containsKey("isFinallyVerified"));
        assertTrue(responseBody.containsKey("verifiedBy"));
        assertTrue(responseBody.containsKey("verifiedAt"));
        assertTrue(responseBody.containsKey("notes"));
        assertTrue(responseBody.containsKey("id"));
    }
}
