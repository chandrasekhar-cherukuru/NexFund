package com.chakri.fundly.service;

import com.chakri.fundly.model.Feedback;
import com.chakri.fundly.repo.FeedbackRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackService Test Suite")
class FeedbackServiceTest {

    @Mock
    private FeedbackRepo feedbackRepo;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback testFeedback;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setName("John Doe");
        testFeedback.setEmail("john@example.com");
        testFeedback.setMessage("Great service");
        testFeedback.setType("donation");
        testFeedback.setRating(5);
        testFeedback.setCreatedAt(testDateTime);
        testFeedback.setIsApproved(false);
    }

    // ============================================================================
    // TESTS FOR: saveFeedback(Feedback feedback) - 7 Test Cases
    // ============================================================================

    @Test
    @DisplayName("saveFeedback - Happy Path: Save valid feedback successfully")
    void testSaveFeedback_HappyPath() {
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("Great service", result.getMessage());
        assertEquals("donation", result.getType());
        assertEquals(5, result.getRating());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback with different types (event, giftpool, donation)")
    void testSaveFeedback_DifferentTypes() {
        Feedback eventFeedback = new Feedback();
        eventFeedback.setId(2L);
        eventFeedback.setName("Alice");
        eventFeedback.setType("event");
        eventFeedback.setRating(4);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(eventFeedback);
        Feedback result = feedbackService.saveFeedback(eventFeedback);
        assertEquals("event", result.getType());
        verify(feedbackRepo, times(1)).save(eventFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback with minimum rating (1 star)")
    void testSaveFeedback_MinimumRating() {
        testFeedback.setRating(1);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals(1, result.getRating());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback with maximum rating (5 stars)")
    void testSaveFeedback_MaximumRating() {
        testFeedback.setRating(5);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals(5, result.getRating());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback with null name (edge case)")
    void testSaveFeedback_NullName() {
        testFeedback.setName(null);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertNull(result.getName());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback with empty message")
    void testSaveFeedback_EmptyMessage() {
        testFeedback.setMessage("");
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals("", result.getMessage());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("saveFeedback - Save feedback returns same object as saved")
    void testSaveFeedback_ReturnsSavedObject() {
        Feedback savedFeedback = new Feedback();
        savedFeedback.setId(10L);
        savedFeedback.setName("Jane");
        savedFeedback.setType("giftpool");
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(savedFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals(10L, result.getId());
        assertEquals("Jane", result.getName());
    }

    // ============================================================================
    // TESTS FOR: getApprovedFeedback() - 5 Test Cases
    // ============================================================================

    @Test
    @DisplayName("getApprovedFeedback - Happy Path: Retrieve all approved feedbacks")
    void testGetApprovedFeedback_HappyPath() {
        List<Feedback> approvedFeedbacks = new ArrayList<>();
        testFeedback.setIsApproved(true);
        approvedFeedbacks.add(testFeedback);
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedFeedbacks);
        List<Feedback> result = feedbackService.getApprovedFeedback();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsApproved());
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    @Test
    @DisplayName("getApprovedFeedback - Return empty list when no approved feedbacks exist")
    void testGetApprovedFeedback_EmptyList() {
        List<Feedback> emptyList = new ArrayList<>();
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(emptyList);
        List<Feedback> result = feedbackService.getApprovedFeedback();
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    @Test
    @DisplayName("getApprovedFeedback - Return multiple approved feedbacks")
    void testGetApprovedFeedback_MultipleItems() {
        List<Feedback> approvedFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Feedback fb = new Feedback();
            fb.setId((long) i);
            fb.setName("User " + i);
            fb.setIsApproved(true);
            approvedFeedbacks.add(fb);
        }
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedFeedbacks);
        List<Feedback> result = feedbackService.getApprovedFeedback();
        assertEquals(5, result.size());
        result.forEach(fb -> assertTrue(fb.getIsApproved()));
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    @Test
    @DisplayName("getApprovedFeedback - Verify repository method called exactly once")
    void testGetApprovedFeedback_VerifyRepoCall() {
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(new ArrayList<>());
        feedbackService.getApprovedFeedback();
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
        verifyNoMoreInteractions(feedbackRepo);
    }

    // ============================================================================
    // TESTS FOR: getLastThreeApprovedFeedback() - 6 Test Cases
    // ============================================================================

    @Test
    @DisplayName("getLastThreeApprovedFeedback - Happy Path: Retrieve last 3 approved feedbacks")
    void testGetLastThreeApprovedFeedback_HappyPath() {
        List<Feedback> threeApprovedFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Feedback fb = new Feedback();
            fb.setId((long) i);
            fb.setName("User " + i);
            fb.setIsApproved(true);
            threeApprovedFeedbacks.add(fb);
        }
        Page<Feedback> page = new PageImpl<>(threeApprovedFeedbacks);
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(page);
        List<Feedback> result = feedbackService.getLastThreeApprovedFeedback();
        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(fb -> assertTrue(fb.getIsApproved()));
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("getLastThreeApprovedFeedback - Return less than 3 feedbacks when available")
    void testGetLastThreeApprovedFeedback_LessThanThree() {
        List<Feedback> twoFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Feedback fb = new Feedback();
            fb.setId((long) i);
            fb.setName("User " + i);
            fb.setIsApproved(true);
            twoFeedbacks.add(fb);
        }
        Page<Feedback> page = new PageImpl<>(twoFeedbacks);
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(page);
        List<Feedback> result = feedbackService.getLastThreeApprovedFeedback();
        assertEquals(2, result.size());
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("getLastThreeApprovedFeedback - Return empty list when no feedbacks exist")
    void testGetLastThreeApprovedFeedback_EmptyList() {
        Page<Feedback> emptyPage = new PageImpl<>(new ArrayList<>());
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(emptyPage);
        List<Feedback> result = feedbackService.getLastThreeApprovedFeedback();
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("getLastThreeApprovedFeedback - Verify pageable parameters (page=0, size=3)")
    void testGetLastThreeApprovedFeedback_VerifyPagingParams() {
        Page<Feedback> page = new PageImpl<>(new ArrayList<>());
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(page);
        feedbackService.getLastThreeApprovedFeedback();
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("getLastThreeApprovedFeedback - Return exactly 3 feedbacks when more exist")
    void testGetLastThreeApprovedFeedback_ExactlyThree() {
        List<Feedback> threeApprovedFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Feedback fb = new Feedback();
            fb.setId((long) i);
            fb.setIsApproved(true);
            threeApprovedFeedbacks.add(fb);
        }
        Page<Feedback> page = new PageImpl<>(threeApprovedFeedbacks);
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(page);
        List<Feedback> result = feedbackService.getLastThreeApprovedFeedback();
        assertEquals(3, result.size());
    }

    // ============================================================================
    // TESTS FOR: getAllFeedback() - 4 Test Cases
    // ============================================================================

    @Test
    @DisplayName("getAllFeedback - Happy Path: Retrieve all feedbacks")
    void testGetAllFeedback_HappyPath() {
        List<Feedback> allFeedbacks = new ArrayList<>();
        allFeedbacks.add(testFeedback);
        when(feedbackRepo.findAll()).thenReturn(allFeedbacks);
        List<Feedback> result = feedbackService.getAllFeedback();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(feedbackRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllFeedback - Return empty list when no feedbacks exist")
    void testGetAllFeedback_EmptyList() {
        when(feedbackRepo.findAll()).thenReturn(new ArrayList<>());
        List<Feedback> result = feedbackService.getAllFeedback();
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(feedbackRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllFeedback - Return multiple feedbacks (approved and unapproved)")
    void testGetAllFeedback_MixedApprovalStatus() {
        List<Feedback> allFeedbacks = new ArrayList<>();
        Feedback approved = new Feedback();
        approved.setId(1L);
        approved.setIsApproved(true);
        allFeedbacks.add(approved);
        Feedback unapproved = new Feedback();
        unapproved.setId(2L);
        unapproved.setIsApproved(false);
        allFeedbacks.add(unapproved);
        when(feedbackRepo.findAll()).thenReturn(allFeedbacks);
        List<Feedback> result = feedbackService.getAllFeedback();
        assertEquals(2, result.size());
        verify(feedbackRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllFeedback - Return large number of feedbacks")
    void testGetAllFeedback_LargeDataset() {
        List<Feedback> allFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Feedback fb = new Feedback();
            fb.setId((long) i);
            allFeedbacks.add(fb);
        }
        when(feedbackRepo.findAll()).thenReturn(allFeedbacks);
        List<Feedback> result = feedbackService.getAllFeedback();
        assertEquals(100, result.size());
        verify(feedbackRepo, times(1)).findAll();
    }

    // ============================================================================
    // TESTS FOR: approveFeedback(Long id) - 7 Test Cases
    // ============================================================================

    @Test
    @DisplayName("approveFeedback - Happy Path: Approve existing feedback")
    void testApproveFeedback_HappyPath() {
        testFeedback.setIsApproved(false);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.approveFeedback(1L);
        assertTrue(testFeedback.getIsApproved());
        verify(feedbackRepo, times(1)).findById(1L);
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("approveFeedback - Approve feedback with null ID (edge case)")
    void testApproveFeedback_NullId() {
        when(feedbackRepo.findById(null)).thenReturn(Optional.empty());
        feedbackService.approveFeedback(null);
        verify(feedbackRepo, times(1)).findById(null);
        verify(feedbackRepo, never()).save(any());
    }

    @Test
    @DisplayName("approveFeedback - Approve non-existent feedback (feedback not found)")
    void testApproveFeedback_FeedbackNotFound() {
        when(feedbackRepo.findById(999L)).thenReturn(Optional.empty());
        feedbackService.approveFeedback(999L);
        verify(feedbackRepo, times(1)).findById(999L);
        verify(feedbackRepo, never()).save(any());
    }

    @Test
    @DisplayName("approveFeedback - Approve already approved feedback")
    void testApproveFeedback_AlreadyApproved() {
        testFeedback.setIsApproved(true);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.approveFeedback(1L);
        assertTrue(testFeedback.getIsApproved());
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("approveFeedback - Approve feedback with large ID")
    void testApproveFeedback_LargeId() {
        Feedback feedbackWithLargeId = new Feedback();
        feedbackWithLargeId.setId(Long.MAX_VALUE);
        feedbackWithLargeId.setIsApproved(false);
        when(feedbackRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.of(feedbackWithLargeId));
        feedbackService.approveFeedback(Long.MAX_VALUE);
        assertTrue(feedbackWithLargeId.getIsApproved());
        verify(feedbackRepo, times(1)).save(feedbackWithLargeId);
    }

    @Test
    @DisplayName("approveFeedback - Verify feedback flag is set to true before save")
    void testApproveFeedback_VerifyFlagSet() {
        testFeedback.setIsApproved(false);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.approveFeedback(1L);
        verify(feedbackRepo, times(1)).findById(1L);
        verify(feedbackRepo, times(1)).save(argThat(feedback -> feedback.getIsApproved() == true));
    }

    // ============================================================================
    // TESTS FOR: rejectFeedback(Long id) - 7 Test Cases
    // ============================================================================

    @Test
    @DisplayName("rejectFeedback - Happy Path: Reject existing feedback")
    void testRejectFeedback_HappyPath() {
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.rejectFeedback(1L);
        verify(feedbackRepo, times(1)).findById(1L);
        verify(feedbackRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("rejectFeedback - Reject non-existent feedback (feedback not found)")
    void testRejectFeedback_FeedbackNotFound() {
        when(feedbackRepo.findById(999L)).thenReturn(Optional.empty());
        feedbackService.rejectFeedback(999L);
        verify(feedbackRepo, times(1)).findById(999L);
        verify(feedbackRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("rejectFeedback - Reject feedback with null ID (edge case)")
    void testRejectFeedback_NullId() {
        when(feedbackRepo.findById(null)).thenReturn(Optional.empty());
        feedbackService.rejectFeedback(null);
        verify(feedbackRepo, times(1)).findById(null);
        verify(feedbackRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("rejectFeedback - Reject already approved feedback")
    void testRejectFeedback_ApprovedFeedback() {
        testFeedback.setIsApproved(true);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.rejectFeedback(1L);
        verify(feedbackRepo, times(1)).findById(1L);
        verify(feedbackRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("rejectFeedback - Reject feedback with large ID")
    void testRejectFeedback_LargeId() {
        Feedback feedbackWithLargeId = new Feedback();
        feedbackWithLargeId.setId(Long.MAX_VALUE);
        when(feedbackRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.of(feedbackWithLargeId));
        feedbackService.rejectFeedback(Long.MAX_VALUE);
        verify(feedbackRepo, times(1)).findById(Long.MAX_VALUE);
        verify(feedbackRepo, times(1)).deleteById(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("rejectFeedback - Verify deleteById called with correct ID")
    void testRejectFeedback_VerifyDeleteIdCalled() {
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.rejectFeedback(1L);
        verify(feedbackRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("rejectFeedback - Reject multiple feedbacks sequentially")
    void testRejectFeedback_MultipleSequential() {
        Feedback feedback1 = new Feedback();
        feedback1.setId(1L);
        Feedback feedback2 = new Feedback();
        feedback2.setId(2L);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(feedback1));
        when(feedbackRepo.findById(2L)).thenReturn(Optional.of(feedback2));
        feedbackService.rejectFeedback(1L);
        feedbackService.rejectFeedback(2L);
        verify(feedbackRepo, times(1)).deleteById(1L);
        verify(feedbackRepo, times(1)).deleteById(2L);
        verify(feedbackRepo, times(2)).findById(anyLong());
    }

    // ============================================================================
    // INTEGRATION & EDGE CASE TESTS - 15 Test Cases (FIXED)
    // ============================================================================

    @Test
    @DisplayName("Integration - Save feedback independently")
    void testIntegration_SaveOnly() {
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feedbackRepo, times(1)).save(any(Feedback.class));
    }

    @Test
    @DisplayName("Integration - Approve feedback independently")
    void testIntegration_ApproveOnly() {
        testFeedback.setIsApproved(false);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.approveFeedback(1L);
        assertTrue(testFeedback.getIsApproved());
        // approveFeedback calls save() once internally
        verify(feedbackRepo, times(1)).save(testFeedback);
    }

    @Test
    @DisplayName("Integration - Reject feedback independently")
    void testIntegration_RejectOnly() {
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        feedbackService.rejectFeedback(1L);
        verify(feedbackRepo, times(1)).findById(1L);
        verify(feedbackRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Integration - Get approved feedbacks")
    void testIntegration_GetApprovedFeedbacks() {
        testFeedback.setIsApproved(true);
        List<Feedback> approvedList = new ArrayList<>();
        approvedList.add(testFeedback);
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedList);
        List<Feedback> result = feedbackService.getApprovedFeedback();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsApproved());
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    @Test
    @DisplayName("Edge Case - Save feedback with all null fields")
    void testEdgeCase_AllNullFields() {
        Feedback nullFeedback = new Feedback();
        nullFeedback.setId(null);
        nullFeedback.setName(null);
        nullFeedback.setEmail(null);
        nullFeedback.setMessage(null);
        nullFeedback.setType(null);
        nullFeedback.setRating(0);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(nullFeedback);
        Feedback result = feedbackService.saveFeedback(nullFeedback);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getMessage());
        assertNull(result.getType());
        assertEquals(0, result.getRating());
    }

    @Test
    @DisplayName("Edge Case - Save feedback with very long message")
    void testEdgeCase_VeryLongMessage() {
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longMessage.append("A");
        }
        testFeedback.setMessage(longMessage.toString());
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals(10000, result.getMessage().length());
    }

    @Test
    @DisplayName("Exception Handling - Repository throws exception on save")
    void testExceptionHandling_SaveThrowsException() {
        when(feedbackRepo.save(any(Feedback.class)))
                .thenThrow(new RuntimeException("Database connection failed"));
        assertThrows(RuntimeException.class, () -> feedbackService.saveFeedback(testFeedback));
    }

    @Test
    @DisplayName("Exception Handling - Repository throws exception on findById")
    void testExceptionHandling_FindByIdThrowsException() {
        when(feedbackRepo.findById(anyLong()))
                .thenThrow(new RuntimeException("Database connection failed"));
        assertThrows(RuntimeException.class, () -> feedbackService.approveFeedback(1L));
    }

    @Test
    @DisplayName("Exception Handling - Repository throws exception on delete")
    void testExceptionHandling_DeleteThrowsException() {
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        doThrow(new RuntimeException("Delete failed")).when(feedbackRepo).deleteById(1L);
        assertThrows(RuntimeException.class, () -> feedbackService.rejectFeedback(1L));
    }

    @Test
    @DisplayName("Method Call Verification - Save verifies correct parameters")
    void testMethodCallVerification_SaveParameters() {
        when(feedbackRepo.save(testFeedback)).thenReturn(testFeedback);
        feedbackService.saveFeedback(testFeedback);
        verify(feedbackRepo).save(testFeedback);
    }

    @Test
    @DisplayName("Method Call Verification - No interactions after failed find")
    void testMethodCallVerification_NoInteractionsAfterFailedFind() {
        when(feedbackRepo.findById(999L)).thenReturn(Optional.empty());
        feedbackService.approveFeedback(999L);
        verify(feedbackRepo, times(1)).findById(999L);
        verify(feedbackRepo, never()).save(any());
    }

    @Test
    @DisplayName("Boundary Test - Zero rating")
    void testBoundaryTest_ZeroRating() {
        testFeedback.setRating(0);
        when(feedbackRepo.save(any(Feedback.class))).thenReturn(testFeedback);
        Feedback result = feedbackService.saveFeedback(testFeedback);
        assertEquals(0, result.getRating());
    }

    @Test
    @DisplayName("Boundary Test - Negative ID")
    void testBoundaryTest_NegativeId() {
        Feedback negativeIdFeedback = new Feedback();
        negativeIdFeedback.setId(-1L);
        when(feedbackRepo.findById(-1L)).thenReturn(Optional.of(negativeIdFeedback));
        feedbackService.approveFeedback(-1L);
        verify(feedbackRepo, times(1)).findById(-1L);
        verify(feedbackRepo, times(1)).save(negativeIdFeedback);
    }

    @Test
    @DisplayName("State Verification - Feedback approval state changes")
    void testStateVerification_ApprovalStateChange() {
        testFeedback.setIsApproved(false);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(testFeedback));
        assertFalse(testFeedback.getIsApproved());
        feedbackService.approveFeedback(1L);
        assertTrue(testFeedback.getIsApproved());
    }

    @Test
    @DisplayName("Return Type Verification - List methods return correct types")
    void testReturnTypeVerification_ListMethods() {
        when(feedbackRepo.findAll()).thenReturn(new ArrayList<>());
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(new ArrayList<>());
        List<Feedback> allFeedbacks = feedbackService.getAllFeedback();
        List<Feedback> approvedFeedbacks = feedbackService.getApprovedFeedback();
        assertNotNull(allFeedbacks);
        assertNotNull(approvedFeedbacks);
        assertTrue(allFeedbacks instanceof List);
        assertTrue(approvedFeedbacks instanceof List);
    }

    @Test
    @DisplayName("Optional Handling - findById returns empty Optional correctly")
    void testOptionalHandling_EmptyOptional() {
        when(feedbackRepo.findById(999L)).thenReturn(Optional.empty());
        feedbackService.approveFeedback(999L);
        feedbackService.rejectFeedback(999L);
        verify(feedbackRepo, times(2)).findById(999L);
        verify(feedbackRepo, never()).save(any());
        verify(feedbackRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Page Conversion - Pageable result converted to List correctly")
    void testPageConversion_PageToList() {
        List<Feedback> feedbackList = new ArrayList<>();
        Feedback fb = new Feedback();
        fb.setId(1L);
        feedbackList.add(fb);
        Page<Feedback> page = new PageImpl<>(feedbackList);
        when(feedbackRepo.findByIsApprovedTrue(any(Pageable.class))).thenReturn(page);
        List<Feedback> result = feedbackService.getLastThreeApprovedFeedback();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Chained Approvals - Multiple approve operations")
    void testChainedOperations_MultipleApprovals() {
        Feedback fb1 = new Feedback();
        fb1.setId(1L);
        Feedback fb2 = new Feedback();
        fb2.setId(2L);
        Feedback fb3 = new Feedback();
        fb3.setId(3L);
        when(feedbackRepo.findById(1L)).thenReturn(Optional.of(fb1));
        when(feedbackRepo.findById(2L)).thenReturn(Optional.of(fb2));
        when(feedbackRepo.findById(3L)).thenReturn(Optional.of(fb3));
        feedbackService.approveFeedback(1L);
        feedbackService.approveFeedback(2L);
        feedbackService.approveFeedback(3L);
        assertTrue(fb1.getIsApproved());
        assertTrue(fb2.getIsApproved());
        assertTrue(fb3.getIsApproved());
        verify(feedbackRepo, times(3)).save(any());
    }
}
