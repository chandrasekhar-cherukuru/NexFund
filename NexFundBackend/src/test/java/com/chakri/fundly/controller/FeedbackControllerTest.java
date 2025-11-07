package com.chakri.fundly.controller;

import com.chakri.fundly.model.Feedback;
import com.chakri.fundly.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackController Unit Tests")
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    private Feedback testFeedback;
    private Feedback savedFeedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        testFeedback = new Feedback();
        testFeedback.setName("John Doe");
        testFeedback.setEmail("john@example.com");
        testFeedback.setMessage("Great service!");
        testFeedback.setType("event");
        testFeedback.setRating(5);

        savedFeedback = new Feedback();
        savedFeedback.setId(1L);
        savedFeedback.setName("John Doe");
        savedFeedback.setEmail("john@example.com");
        savedFeedback.setMessage("Great service!");
        savedFeedback.setType("event");
        savedFeedback.setRating(5);
        savedFeedback.setIsApproved(true);
        savedFeedback.setCreatedAt(LocalDateTime.now());
    }

    // ==================== submitFeedback() Tests ====================

    @Test
    @DisplayName("Should successfully submit feedback with valid data")
    void testSubmitFeedback_ValidData_Success() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Feedback submitted successfully!", responseBody.get("message"));
        assertEquals(1L, responseBody.get("feedbackId"));
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with null name")
    void testSubmitFeedback_NullName_BadRequest() {
        // ARRANGE
        testFeedback.setName(null);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Name is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with empty name")
    void testSubmitFeedback_EmptyName_BadRequest() {
        // ARRANGE
        testFeedback.setName("");

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Name is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with null message")
    void testSubmitFeedback_NullMessage_BadRequest() {
        // ARRANGE
        testFeedback.setMessage(null);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Message is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with empty message")
    void testSubmitFeedback_EmptyMessage_BadRequest() {
        // ARRANGE
        testFeedback.setMessage("");

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Message is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with rating below 1")
    void testSubmitFeedback_RatingBelowOne_BadRequest() {
        // ARRANGE
        testFeedback.setRating(0);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating must be between 1 and 5", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with negative rating")
    void testSubmitFeedback_NegativeRating_BadRequest() {
        // ARRANGE
        testFeedback.setRating(-5);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating must be between 1 and 5", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with rating above 5")
    void testSubmitFeedback_RatingAboveFive_BadRequest() {
        // ARRANGE
        testFeedback.setRating(6);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating must be between 1 and 5", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with rating of 100")
    void testSubmitFeedback_RatingMuchAboveFive_BadRequest() {
        // ARRANGE
        testFeedback.setRating(100);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating must be between 1 and 5", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with null type")
    void testSubmitFeedback_NullType_BadRequest() {
        // ARRANGE
        testFeedback.setType(null);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Type is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should reject feedback with empty type")
    void testSubmitFeedback_EmptyType_BadRequest() {
        // ARRANGE
        testFeedback.setType("");

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Type is required", responseBody.get("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with valid rating 1")
    void testSubmitFeedback_ValidRatingOne_Success() {
        // ARRANGE
        testFeedback.setRating(1);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with valid rating 5")
    void testSubmitFeedback_ValidRatingFive_Success() {
        // ARRANGE
        testFeedback.setRating(5);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with valid rating 3")
    void testSubmitFeedback_ValidRatingThree_Success() {
        // ARRANGE
        testFeedback.setRating(3);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle exception during feedback submission")
    void testSubmitFeedback_ServiceException_InternalServerError() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertEquals("Database connection error", responseBody.get("error"));
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should set isApproved to true before saving")
    void testSubmitFeedback_ApprovalFlagSet_Success() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(argThat(feedback ->
                feedback.getIsApproved() != null && feedback.getIsApproved()
        ));
    }

    @Test
    @DisplayName("Should set createdAt timestamp before saving")
    void testSubmitFeedback_CreatedAtSet_Success() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(argThat(feedback ->
                feedback.getCreatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should return feedback ID in response")
    void testSubmitFeedback_FeedbackIdReturned_Success() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("feedbackId"));
        assertEquals(1L, responseBody.get("feedbackId"));
    }

    @Test
    @DisplayName("Should accept feedback with donation type")
    void testSubmitFeedback_DonationType_Success() {
        // ARRANGE
        testFeedback.setType("donation");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with giftpool type")
    void testSubmitFeedback_GiftpoolType_Success() {
        // ARRANGE
        testFeedback.setType("giftpool");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with event type")
    void testSubmitFeedback_EventType_Success() {
        // ARRANGE
        testFeedback.setType("event");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle null feedback object gracefully")
    void testSubmitFeedback_NullFeedback_InternalServerError() {
        // ARRANGE & ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(null);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    // ==================== getApprovedFeedback() Tests ====================

    @Test
    @DisplayName("Should return approved feedbacks successfully")
    void testGetApprovedFeedback_Success() {
        // ARRANGE
        List<Feedback> approvedFeedbacks = new ArrayList<>();
        approvedFeedbacks.add(savedFeedback);
        when(feedbackService.getApprovedFeedback()).thenReturn(approvedFeedbacks);

        // ACT
        ResponseEntity<?> response = feedbackController.getApprovedFeedback();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals("John Doe", responseBody.get(0).getName());
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    @Test
    @DisplayName("Should return empty list when no approved feedbacks")
    void testGetApprovedFeedback_EmptyList_Success() {
        // ARRANGE
        List<Feedback> emptyList = new ArrayList<>();
        when(feedbackService.getApprovedFeedback()).thenReturn(emptyList);

        // ACT
        ResponseEntity<?> response = feedbackController.getApprovedFeedback();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.size());
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    @Test
    @DisplayName("Should return multiple approved feedbacks")
    void testGetApprovedFeedback_MultipleFeedbacks_Success() {
        // ARRANGE
        List<Feedback> approvedFeedbacks = new ArrayList<>();

        Feedback feedback1 = new Feedback();
        feedback1.setId(1L);
        feedback1.setName("User 1");
        feedback1.setMessage("Great!");
        feedback1.setIsApproved(true);

        Feedback feedback2 = new Feedback();
        feedback2.setId(2L);
        feedback2.setName("User 2");
        feedback2.setMessage("Excellent!");
        feedback2.setIsApproved(true);

        approvedFeedbacks.add(feedback1);
        approvedFeedbacks.add(feedback2);

        when(feedbackService.getApprovedFeedback()).thenReturn(approvedFeedbacks);

        // ACT
        ResponseEntity<?> response = feedbackController.getApprovedFeedback();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    @Test
    @DisplayName("Should handle exception when fetching approved feedbacks")
    void testGetApprovedFeedback_ServiceException_InternalServerError() {
        // ARRANGE
        when(feedbackService.getApprovedFeedback())
                .thenThrow(new RuntimeException("Database error"));

        // ACT
        ResponseEntity<?> response = feedbackController.getApprovedFeedback();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertEquals("Database error", responseBody.get("error"));
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    @Test
    @DisplayName("Should verify service is called exactly once")
    void testGetApprovedFeedback_ServiceCalledOnce() {
        // ARRANGE
        when(feedbackService.getApprovedFeedback()).thenReturn(new ArrayList<>());

        // ACT
        feedbackController.getApprovedFeedback();

        // ASSERT
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    @Test
    @DisplayName("Should handle null return from service gracefully")
    void testGetApprovedFeedback_ServiceReturnsNull_InternalServerError() {
        // ARRANGE
        when(feedbackService.getApprovedFeedback()).thenReturn(null);

        // ACT
        ResponseEntity<?> response = feedbackController.getApprovedFeedback();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        verify(feedbackService, times(1)).getApprovedFeedback();
    }

    // ==================== getAllFeedbacks() Tests ====================

    @Test
    @DisplayName("Should return all feedbacks successfully")
    void testGetAllFeedbacks_Success() {
        // ARRANGE
        List<Feedback> allFeedbacks = new ArrayList<>();
        allFeedbacks.add(savedFeedback);
        when(feedbackService.getAllFeedback()).thenReturn(allFeedbacks);

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should return empty list when no feedbacks exist")
    void testGetAllFeedbacks_EmptyList_Success() {
        // ARRANGE
        List<Feedback> emptyList = new ArrayList<>();
        when(feedbackService.getAllFeedback()).thenReturn(emptyList);

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(0, responseBody.size());
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should return multiple feedbacks with mixed approval status")
    void testGetAllFeedbacks_MixedApprovalStatus_Success() {
        // ARRANGE
        List<Feedback> allFeedbacks = new ArrayList<>();

        Feedback approved = new Feedback();
        approved.setId(1L);
        approved.setIsApproved(true);

        Feedback notApproved = new Feedback();
        notApproved.setId(2L);
        notApproved.setIsApproved(false);

        allFeedbacks.add(approved);
        allFeedbacks.add(notApproved);

        when(feedbackService.getAllFeedback()).thenReturn(allFeedbacks);

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should handle exception when fetching all feedbacks")
    void testGetAllFeedbacks_ServiceException_InternalServerError() {
        // ARRANGE
        when(feedbackService.getAllFeedback())
                .thenThrow(new RuntimeException("Service unavailable"));

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should verify getAllFeedback is called exactly once")
    void testGetAllFeedbacks_ServiceCalledOnce() {
        // ARRANGE
        when(feedbackService.getAllFeedback()).thenReturn(new ArrayList<>());

        // ACT
        feedbackController.getAllFeedbacks();

        // ASSERT
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should return large list of feedbacks")
    void testGetAllFeedbacks_LargeDataSet_Success() {
        // ARRANGE
        List<Feedback> largeFeedbackList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Feedback feedback = new Feedback();
            feedback.setId((long) i);
            feedback.setName("User " + i);
            largeFeedbackList.add(feedback);
        }
        when(feedbackService.getAllFeedback()).thenReturn(largeFeedbackList);

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Feedback> responseBody = (List<Feedback>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(100, responseBody.size());
        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    @DisplayName("Should handle null return from service")
    void testGetAllFeedbacks_ServiceReturnsNull_InternalServerError() {
        // ARRANGE
        when(feedbackService.getAllFeedback()).thenReturn(null);

        // ACT
        ResponseEntity<?> response = feedbackController.getAllFeedbacks();

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        verify(feedbackService, times(1)).getAllFeedback();
    }

    // ==================== approveFeedback() Tests ====================

    @Test
    @DisplayName("Should approve feedback successfully")
    void testApproveFeedback_ValidId_Success() {
        // ARRANGE
        Long feedbackId = 1L;

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Feedback approved successfully", responseBody.get("message"));
        verify(feedbackService, times(1)).approveFeedback(feedbackId);
    }

    @Test
    @DisplayName("Should approve feedback with ID 1")
    void testApproveFeedback_IdOne_Success() {
        // ARRANGE
        Long feedbackId = 1L;

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).approveFeedback(1L);
    }

    @Test
    @DisplayName("Should approve feedback with large ID")
    void testApproveFeedback_LargeId_Success() {
        // ARRANGE
        Long feedbackId = 999999L;

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).approveFeedback(999999L);
    }

    @Test
    @DisplayName("Should handle exception during approval")
    void testApproveFeedback_ServiceException_InternalServerError() {
        // ARRANGE
        Long feedbackId = 1L;
        doThrow(new RuntimeException("Feedback not found")).when(feedbackService)
                .approveFeedback(feedbackId);

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertEquals("Feedback not found", responseBody.get("error"));
        verify(feedbackService, times(1)).approveFeedback(feedbackId);
    }

    @Test
    @DisplayName("Should call service with correct ID")
    void testApproveFeedback_CorrectIdPassed() {
        // ARRANGE
        Long feedbackId = 5L;

        // ACT
        feedbackController.approveFeedback(feedbackId);

        // ASSERT
        verify(feedbackService, times(1)).approveFeedback(5L);
    }

    @Test
    @DisplayName("Should return OK status code")
    void testApproveFeedback_StatusCodeOk() {
        // ARRANGE
        Long feedbackId = 1L;

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle service exception with specific message")
    void testApproveFeedback_Exception_CustomMessage() {
        // ARRANGE
        Long feedbackId = 1L;
        String errorMessage = "Feedback ID does not exist";
        doThrow(new RuntimeException(errorMessage)).when(feedbackService)
                .approveFeedback(feedbackId);

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, responseBody.get("error"));
    }

    @Test
    @DisplayName("Should approve feedback with ID 2")
    void testApproveFeedback_IdTwo_Success() {
        // ARRANGE
        Long feedbackId = 2L;

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).approveFeedback(2L);
    }

    @Test
    @DisplayName("Should handle exception with NPE during approval")
    void testApproveFeedback_ServiceThrowsNullPointerException() {
        // ARRANGE
        Long feedbackId = 1L;
        doThrow(new NullPointerException("Resource not found")).when(feedbackService)
                .approveFeedback(feedbackId);

        // ACT
        ResponseEntity<?> response = feedbackController.approveFeedback(feedbackId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Resource not found", responseBody.get("error"));
    }

    // ==================== Edge Cases and Integration Tests ====================

    @Test
    @DisplayName("Should handle feedback with whitespace in name")
    void testSubmitFeedback_WhitespaceName_Success() {
        // ARRANGE
        testFeedback.setName("   John Doe   ");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle feedback with special characters in message")
    void testSubmitFeedback_SpecialCharactersMessage_Success() {
        // ARRANGE
        testFeedback.setMessage("Great! @#$%^&*() feedback");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with null email (optional field)")
    void testSubmitFeedback_NullEmail_Success() {
        // ARRANGE
        testFeedback.setEmail(null);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with very long name")
    void testSubmitFeedback_VeryLongName_Success() {
        // ARRANGE
        testFeedback.setName("A".repeat(1000));
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should accept feedback with very long message")
    void testSubmitFeedback_VeryLongMessage_Success() {
        // ARRANGE
        testFeedback.setMessage("Message ".repeat(500));
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle numeric name string")
    void testSubmitFeedback_NumericName_Success() {
        // ARRANGE
        testFeedback.setName("12345");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle boundary rating 1")
    void testSubmitFeedback_BoundaryRatingOne_Success() {
        // ARRANGE
        testFeedback.setRating(1);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle boundary rating 5")
    void testSubmitFeedback_BoundaryRatingFive_Success() {
        // ARRANGE
        testFeedback.setRating(5);
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should verify approval flow in submission")
    void testSubmitFeedback_VerifyApprovalFlow() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        feedbackController.submitFeedback(testFeedback);

        // ASSERT
        verify(feedbackService).saveFeedback(argThat(f ->
                f.getIsApproved() != null && f.getIsApproved() && f.getCreatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should not call service for validation errors")
    void testSubmitFeedback_MultipleValidationErrors_NoServiceCall() {
        // ARRANGE
        testFeedback.setName("");
        testFeedback.setMessage("");
        testFeedback.setRating(0);
        testFeedback.setType("");

        // ACT
        feedbackController.submitFeedback(testFeedback);

        // ASSERT
        verify(feedbackService, never()).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should validate name before message")
    void testSubmitFeedback_ValidationOrder_NameFirst() {
        // ARRANGE
        testFeedback.setName(null);
        testFeedback.setMessage(null);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Name is required", responseBody.get("error"));
    }

    @Test
    @DisplayName("Should handle rating with boundary value 0")
    void testSubmitFeedback_RatingZero_BadRequest() {
        // ARRANGE
        testFeedback.setRating(0);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Rating must be between 1 and 5", responseBody.get("error"));
    }

    @Test
    @DisplayName("Should handle service returning null feedback")
    void testSubmitFeedback_ServiceReturnsNull_Exception() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(null);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle feedback with Unicode characters")
    void testSubmitFeedback_UnicodeCharacters_Success() {
        // ARRANGE
        testFeedback.setName("José García");
        testFeedback.setMessage("很好的服务！ 🎉");
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should handle all feedback types in sequence")
    void testSubmitFeedback_AllTypes_Success() {
        // ARRANGE
        String[] types = {"event", "donation", "giftpool"};
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        for (String type : types) {
            testFeedback.setType(type);

            // ACT
            ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        verify(feedbackService, times(3)).saveFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Should verify exact error message for each validation")
    void testSubmitFeedback_ExactErrorMessages() {
        // ARRANGE & ACT & ASSERT
        testFeedback.setName(null);
        ResponseEntity<?> response1 = feedbackController.submitFeedback(testFeedback);
        Map<String, Object> body1 = (Map<String, Object>) response1.getBody();
        assertEquals("Name is required", body1.get("error"));

        testFeedback.setName("John");
        testFeedback.setMessage(null);
        ResponseEntity<?> response2 = feedbackController.submitFeedback(testFeedback);
        Map<String, Object> body2 = (Map<String, Object>) response2.getBody();
        assertEquals("Message is required", body2.get("error"));

        testFeedback.setMessage("Good");
        testFeedback.setRating(6);
        ResponseEntity<?> response3 = feedbackController.submitFeedback(testFeedback);
        Map<String, Object> body3 = (Map<String, Object>) response3.getBody();
        assertEquals("Rating must be between 1 and 5", body3.get("error"));

        testFeedback.setRating(3);
        testFeedback.setType(null);
        ResponseEntity<?> response4 = feedbackController.submitFeedback(testFeedback);
        Map<String, Object> body4 = (Map<String, Object>) response4.getBody();
        assertEquals("Type is required", body4.get("error"));
    }

    @Test
    @DisplayName("Should return correct response message on success")
    void testSubmitFeedback_ResponseMessageCorrect() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Feedback submitted successfully!", body.get("message"));
    }

    @Test
    @DisplayName("Should handle multiple sequential submissions")
    void testSubmitFeedback_MultipleSequentialSubmissions() {
        // ARRANGE
        when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(savedFeedback);

        // ACT
        ResponseEntity<?> response1 = feedbackController.submitFeedback(testFeedback);
        ResponseEntity<?> response2 = feedbackController.submitFeedback(testFeedback);
        ResponseEntity<?> response3 = feedbackController.submitFeedback(testFeedback);

        // ASSERT
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        verify(feedbackService, times(3)).saveFeedback(any(Feedback.class));
    }
}
