package com.chakri.fundly.repo;

import com.chakri.fundly.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackRepoTest {

    @Mock
    private FeedbackRepo feedbackRepo;

    private Feedback testFeedback1;
    private Feedback testFeedback2;
    private Feedback testFeedback3;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializeTestData();
    }

    /**
     * Initialize test data for use across test methods
     */
    private void initializeTestData() {
        testDateTime = LocalDateTime.now();

        // Test Feedback 1 - Event type, approved
        testFeedback1 = new Feedback();
        testFeedback1.setId(1L);
        testFeedback1.setName("John Doe");
        testFeedback1.setEmail("john@example.com");
        testFeedback1.setMessage("Great event!");
        testFeedback1.setType("event");
        testFeedback1.setRating(5);
        testFeedback1.setCreatedAt(testDateTime);
        testFeedback1.setIsApproved(true);

        // Test Feedback 2 - Donation type, approved
        testFeedback2 = new Feedback();
        testFeedback2.setId(2L);
        testFeedback2.setName("Jane Smith");
        testFeedback2.setEmail("jane@example.com");
        testFeedback2.setMessage("Love donating to this cause");
        testFeedback2.setType("donation");
        testFeedback2.setRating(4);
        testFeedback2.setCreatedAt(testDateTime.plusHours(1));
        testFeedback2.setIsApproved(true);

        // Test Feedback 3 - GiftPool type, not approved
        testFeedback3 = new Feedback();
        testFeedback3.setId(3L);
        testFeedback3.setName("Bob Wilson");
        testFeedback3.setEmail("bob@example.com");
        testFeedback3.setMessage("Interesting gift pool");
        testFeedback3.setType("giftpool");
        testFeedback3.setRating(3);
        testFeedback3.setCreatedAt(testDateTime.plusHours(2));
        testFeedback3.setIsApproved(false);
    }

    // ==================== HAPPY PATH TESTS ====================

    /**
     * Test: findByIsApprovedTrue() returns all approved feedbacks successfully
     * Scenario: Normal execution with multiple approved feedbacks
     * Expected: List containing all approved feedbacks
     */
    @Test
    void testFindByIsApprovedTrue_WithMultipleFeedbacks_ReturnsApprovedList() {
        // ARRANGE
        List<Feedback> approvedFeedbacks = new ArrayList<>();
        approvedFeedbacks.add(testFeedback1);
        approvedFeedbacks.add(testFeedback2);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedFeedbacks);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 approved feedbacks");
        assertTrue(result.contains(testFeedback1), "Should contain first approved feedback");
        assertTrue(result.contains(testFeedback2), "Should contain second approved feedback");
        assertTrue(result.stream().allMatch(f -> f.getIsApproved() == true), "All feedbacks should be approved");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: findByIsApprovedTrue() with single approved feedback
     * Scenario: Only one approved feedback exists
     * Expected: List containing single feedback
     */
    @Test
    void testFindByIsApprovedTrue_WithSingleFeedback_ReturnsSingleApprovedFeedback() {
        // ARRANGE
        List<Feedback> singleApprovedFeedback = new ArrayList<>();
        singleApprovedFeedback.add(testFeedback1);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(singleApprovedFeedback);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return exactly 1 feedback");
        assertEquals(testFeedback1.getId(), result.get(0).getId(), "Should return correct feedback");
        assertEquals(testFeedback1.getName(), result.get(0).getName(), "Feedback name should match");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) with valid pagination
     * Scenario: Request first page with 10 items per page
     * Expected: Page object containing approved feedbacks
     */
    @Test
    void testFindByIsApprovedTrue_WithValidPageable_ReturnsPaginatedApprovedFeedbacks() {
        // ARRANGE
        List<Feedback> pageContent = new ArrayList<>();
        pageContent.add(testFeedback1);
        pageContent.add(testFeedback2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Feedback> expectedPage = new PageImpl<>(pageContent, pageable, 2);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(expectedPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getContent().size(), "Page should contain 2 feedbacks");
        assertEquals(0, result.getNumber(), "Should be first page (page 0)");
        assertEquals(10, result.getSize(), "Page size should be 10");
        assertEquals(2, result.getTotalElements(), "Total elements should be 2");
        assertTrue(result.isFirst(), "Should be first page");
        assertFalse(result.hasNext(), "Should not have next page");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) with second page
     * Scenario: Request second page with 5 items per page, total 15 items
     * Expected: Page object with correct pagination metadata and hasNext = true
     */
    @Test
    void testFindByIsApprovedTrue_WithSecondPage_ReturnCorrectPageMetadata() {
        // ARRANGE
        List<Feedback> pageContent = new ArrayList<>();
        pageContent.add(testFeedback2);

        // CORRECTED: For page 1 (second page) with size 5 to have hasNext() = true,
        // we need total > (1 + 1) * 5 = 10. So total = 15 works.
        Pageable pageable = PageRequest.of(1, 5);
        Page<Feedback> expectedPage = new PageImpl<>(pageContent, pageable, 15);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(expectedPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getContent().size(), "Page should contain 1 feedback");
        assertEquals(1, result.getNumber(), "Should be second page (page 1)");
        assertEquals(5, result.getSize(), "Page size should be 5");
        assertEquals(15, result.getTotalElements(), "Total elements should be 15");
        assertFalse(result.isFirst(), "Should not be first page");
        assertTrue(result.hasNext(), "Should have next page");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    // ==================== EDGE CASE TESTS ====================

    /**
     * Test: findByIsApprovedTrue() returns empty list
     * Scenario: No approved feedbacks exist
     * Expected: Empty list (not null)
     */
    @Test
    void testFindByIsApprovedTrue_NoApprovedFeedbacks_ReturnsEmptyList() {
        // ARRANGE
        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(new ArrayList<>());

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertNotNull(result, "Result should not be null (empty list instead)");
        assertTrue(result.isEmpty(), "Result should be empty list");
        assertEquals(0, result.size(), "Size should be 0");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) returns empty page
     * Scenario: No approved feedbacks exist but valid pagination requested
     * Expected: Empty page with correct pagination metadata
     */
    @Test
    void testFindByIsApprovedTrue_WithPageable_NoApprovedFeedbacks_ReturnsEmptyPage() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Feedback> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(emptyPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result page should not be null");
        assertTrue(result.isEmpty(), "Page should be empty");
        assertEquals(0, result.getContent().size(), "Content size should be 0");
        assertEquals(0, result.getTotalElements(), "Total elements should be 0");
        assertFalse(result.hasNext(), "Should not have next page");
        assertFalse(result.hasPrevious(), "Should not have previous page");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) with page size of 1
     * Scenario: Request with very small page size (boundary condition)
     * Expected: Page object with single item
     */
    @Test
    void testFindByIsApprovedTrue_WithPageablePageSizeOne_ReturnsSingleItemPage() {
        // ARRANGE
        List<Feedback> pageContent = new ArrayList<>();
        pageContent.add(testFeedback1);

        Pageable pageable = PageRequest.of(0, 1);
        // With page 0, size 1, and total 10: hasNext = (0 + 1) * 1 < 10 = true
        Page<Feedback> expectedPage = new PageImpl<>(pageContent, pageable, 10);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(expectedPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getSize(), "Page size should be 1");
        assertEquals(1, result.getContent().size(), "Content should have 1 item");
        assertTrue(result.hasNext(), "Should have next page");
        assertTrue(result.getTotalElements() > 1, "Total elements should be greater than 1");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) requesting page beyond available data
     * Scenario: Request page that exceeds total pages
     * Expected: Empty page but with correct pagination metadata
     */
    @Test
    void testFindByIsApprovedTrue_WithPageBeyondAvailableData_ReturnsEmptyPage() {
        // ARRANGE
        Pageable pageable = PageRequest.of(5, 10); // Page 5 when only 2 pages exist
        // Total 20 means pages 0-1 have data, page 5 should be empty
        Page<Feedback> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 20);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(emptyPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Page should be empty");
        assertEquals(5, result.getNumber(), "Should be requesting page 5");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) with very large page size
     * Scenario: Request with large page size (10000 items)
     * Expected: Page object handling large dataset
     */
    @Test
    void testFindByIsApprovedTrue_WithLargePageSize_ReturnsAllItemsInSinglePage() {
        // ARRANGE
        List<Feedback> allFeedbacks = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Feedback feedback = new Feedback();
            feedback.setId((long) i);
            feedback.setName("User " + i);
            feedback.setIsApproved(true);
            allFeedbacks.add(feedback);
        }

        Pageable pageable = PageRequest.of(0, 10000);
        Page<Feedback> expectedPage = new PageImpl<>(allFeedbacks, pageable, 100);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(expectedPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(100, result.getContent().size(), "Should contain all 100 feedbacks");
        assertEquals(10000, result.getSize(), "Page size should be 10000");
        assertFalse(result.hasNext(), "Should not have next page");
        assertTrue(result.isFirst(), "Should be first (and only) page");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue() with null values in feedback fields
     * Scenario: Approved feedbacks with some null fields
     * Expected: List containing feedbacks even with null fields
     */
    @Test
    void testFindByIsApprovedTrue_WithNullFieldsInFeedback_ReturnsApprovedList() {
        // ARRANGE
        Feedback feedbackWithNulls = new Feedback();
        feedbackWithNulls.setId(4L);
        feedbackWithNulls.setName(null); // Null field
        feedbackWithNulls.setEmail(null); // Null field
        feedbackWithNulls.setMessage("Valid message");
        feedbackWithNulls.setIsApproved(true);

        List<Feedback> approvedFeedbacks = new ArrayList<>();
        approvedFeedbacks.add(feedbackWithNulls);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedFeedbacks);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return feedback despite null fields");
        assertNull(result.get(0).getName(), "Name should be null");
        assertNull(result.get(0).getEmail(), "Email should be null");
        assertNotNull(result.get(0).getMessage(), "Message should not be null");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    // ==================== ERROR SCENARIO TESTS ====================

    /**
     * Test: findByIsApprovedTrue() throws exception
     * Scenario: Database connection error
     * Expected: Exception propagated
     */
    @Test
    void testFindByIsApprovedTrue_ThrowsException_ExceptionPropagated() {
        // ARRANGE
        when(feedbackRepo.findByIsApprovedTrue())
                .thenThrow(new RuntimeException("Database connection error"));

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> {
            feedbackRepo.findByIsApprovedTrue();
        }, "Should throw RuntimeException");

        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: findByIsApprovedTrue(Pageable) throws exception
     * Scenario: Invalid pageable parameter
     * Expected: Exception thrown
     */
    @Test
    void testFindByIsApprovedTrue_WithPageable_ThrowsException() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);

        when(feedbackRepo.findByIsApprovedTrue(pageable))
                .thenThrow(new IllegalArgumentException("Invalid page number"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            feedbackRepo.findByIsApprovedTrue(pageable);
        }, "Should throw IllegalArgumentException");

        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: findByIsApprovedTrue() with null pageable throws exception
     * Scenario: Null Pageable parameter passed
     * Expected: NullPointerException or similar error
     */
    @Test
    void testFindByIsApprovedTrue_WithNullPageable_ThrowsException() {
        // ARRANGE
        when(feedbackRepo.findByIsApprovedTrue((Pageable) null))
                .thenThrow(new IllegalArgumentException("Pageable cannot be null"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            feedbackRepo.findByIsApprovedTrue((Pageable) null);
        }, "Should throw exception for null Pageable");

        verify(feedbackRepo, times(1)).findByIsApprovedTrue((Pageable) null);
    }

    // ==================== CONDITIONAL BRANCH TESTS ====================

    /**
     * Test: Verify filtering logic - only approved feedbacks returned
     * Scenario: Mix of approved and non-approved feedbacks
     * Expected: Only approved feedbacks in result
     */
    @Test
    void testFindByIsApprovedTrue_VerifyFilteringLogic_OnlyApprovedReturned() {
        // ARRANGE
        List<Feedback> allFeedbacks = new ArrayList<>();
        allFeedbacks.add(testFeedback1); // approved
        allFeedbacks.add(testFeedback2); // approved

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(allFeedbacks);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertEquals(2, result.size(), "Should return 2 approved feedbacks");
        for (Feedback feedback : result) {
            assertTrue(feedback.getIsApproved(), "All returned feedbacks must be approved");
        }
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: Pageable boundary condition - last page
     * Scenario: Request last page of results
     * Expected: isLast() returns true, hasNext() returns false
     */
    @Test
    void testFindByIsApprovedTrue_WithLastPage_PageMetadataCorrect() {
        // ARRANGE
        List<Feedback> pageContent = new ArrayList<>();
        pageContent.add(testFeedback1);
        pageContent.add(testFeedback2);

        // Last page scenario: page 2, size 5, total 12
        // hasNext = (2 + 1) * 5 < 12 = 15 < 12 = false (last page)
        Pageable pageable = PageRequest.of(2, 5);
        Page<Feedback> lastPage = new PageImpl<>(pageContent, pageable, 12);

        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(lastPage);

        // ACT
        Page<Feedback> result = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertFalse(result.hasNext(), "Should not have next page (last page)");
        assertTrue(result.hasPrevious(), "Should have previous page");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    // ==================== DATA CONSISTENCY TESTS ====================

    /**
     * Test: Verify feedback properties are preserved after retrieval
     * Scenario: Retrieve approved feedbacks and verify all fields
     * Expected: All feedback properties maintained correctly
     */
    @Test
    void testFindByIsApprovedTrue_VerifyDataIntegrity_AllPropertiesPreserved() {
        // ARRANGE
        List<Feedback> approvedFeedbacks = new ArrayList<>();
        approvedFeedbacks.add(testFeedback1);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(approvedFeedbacks);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        Feedback retrievedFeedback = result.get(0);
        assertEquals(testFeedback1.getId(), retrievedFeedback.getId(), "ID should match");
        assertEquals(testFeedback1.getName(), retrievedFeedback.getName(), "Name should match");
        assertEquals(testFeedback1.getEmail(), retrievedFeedback.getEmail(), "Email should match");
        assertEquals(testFeedback1.getMessage(), retrievedFeedback.getMessage(), "Message should match");
        assertEquals(testFeedback1.getType(), retrievedFeedback.getType(), "Type should match");
        assertEquals(testFeedback1.getRating(), retrievedFeedback.getRating(), "Rating should match");
        assertEquals(testFeedback1.getCreatedAt(), retrievedFeedback.getCreatedAt(), "CreatedAt should match");
        assertEquals(testFeedback1.getIsApproved(), retrievedFeedback.getIsApproved(), "IsApproved should match");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
    }

    /**
     * Test: Verify multiple method calls
     * Scenario: Call both findByIsApprovedTrue methods in sequence
     * Expected: Both methods called correctly with appropriate mocking
     */
    @Test
    void testBothMethods_CalledInSequence_BothReturnCorrectResults() {
        // ARRANGE
        List<Feedback> nonPagedResult = new ArrayList<>();
        nonPagedResult.add(testFeedback1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Feedback> pagedResult = new PageImpl<>(nonPagedResult, pageable, 1);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(nonPagedResult);
        when(feedbackRepo.findByIsApprovedTrue(pageable)).thenReturn(pagedResult);

        // ACT
        List<Feedback> nonPagedResultActual = feedbackRepo.findByIsApprovedTrue();
        Page<Feedback> pagedResultActual = feedbackRepo.findByIsApprovedTrue(pageable);

        // ASSERT
        assertNotNull(nonPagedResultActual, "Non-paged result should not be null");
        assertNotNull(pagedResultActual, "Paged result should not be null");
        assertEquals(1, nonPagedResultActual.size(), "Non-paged result should have 1 item");
        assertEquals(1, pagedResultActual.getContent().size(), "Paged result should have 1 item");
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
        verify(feedbackRepo, times(1)).findByIsApprovedTrue(pageable);
    }

    /**
     * Test: Verify no interaction with database for unrelated methods
     * Scenario: Call only one method and verify no other method called
     * Expected: Only the called method was invoked
     */
    @Test
    void testVerifyNoUnwantedInteractions_OnlyRequestedMethodCalled() {
        // ARRANGE
        List<Feedback> expectedResult = new ArrayList<>();
        expectedResult.add(testFeedback1);

        when(feedbackRepo.findByIsApprovedTrue()).thenReturn(expectedResult);

        // ACT
        List<Feedback> result = feedbackRepo.findByIsApprovedTrue();

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return 1 feedback");

        // Verify only findByIsApprovedTrue() was called, not the pageable version
        verify(feedbackRepo, times(1)).findByIsApprovedTrue();
        verify(feedbackRepo, never()).findByIsApprovedTrue(any(Pageable.class));
    }
}
