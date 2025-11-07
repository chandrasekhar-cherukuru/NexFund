package com.chakri.fundly.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.Stats;
import com.chakri.fundly.service.StatsService;
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

import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatsController Test Suite")
class StatsControllerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    private Stats testStats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testStats = new Stats();
        testStats.setId(1L);
        testStats.setEventCount(10L);
        testStats.setDonationCount(25L);
        testStats.setGiftPoolCount(5L);
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test
    @DisplayName("Should return stats with correct data when service returns valid stats")
    void testGetStats_WhenStatsExists_ReturnsOkResponse() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK");

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(10L, responseBody.get("events"), "Event count should match");
        assertEquals(25L, responseBody.get("donations"), "Donation count should match");
        assertEquals(5L, responseBody.get("giftPools"), "Gift pool count should match");

        verify(statsService, times(1)).getStats();
        verifyNoMoreInteractions(statsService);
    }

    @Test
    @DisplayName("Should return response with all zero counts when stats initialized")
    void testGetStats_WhenStatsInitialized_ReturnsZeroCounts() {
        // ARRANGE
        Stats initializedStats = new Stats();
        initializedStats.setId(1L);
        initializedStats.setEventCount(0L);
        initializedStats.setDonationCount(0L);
        initializedStats.setGiftPoolCount(0L);
        when(statsService.getStats()).thenReturn(initializedStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(0L, responseBody.get("events"));
        assertEquals(0L, responseBody.get("donations"));
        assertEquals(0L, responseBody.get("giftPools"));
    }

    @Test
    @DisplayName("Should include all three fields in response map")
    void testGetStats_ResponseContainsAllRequiredFields() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertTrue(responseBody.containsKey("events"), "Response should contain 'events' key");
        assertTrue(responseBody.containsKey("donations"), "Response should contain 'donations' key");
        assertTrue(responseBody.containsKey("giftPools"), "Response should contain 'giftPools' key");
        assertEquals(3, responseBody.size(), "Response should contain exactly 3 fields");
    }

    @Test
    @DisplayName("Should return valid response entity with correct type")
    void testGetStats_ReturnsResponseEntityWithCorrectType() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertNotNull(response);
        assertTrue(response.hasBody(), "Response should have a body");
        assertTrue(response.getBody() instanceof Map, "Body should be a Map");
    }

    @Test
    @DisplayName("Should return all count values correctly")
    void testGetStats_AllCountsReturnedCorrectly() {
        // ARRANGE
        testStats.setEventCount(100L);
        testStats.setDonationCount(200L);
        testStats.setGiftPoolCount(50L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(100L, body.get("events"));
        assertEquals(200L, body.get("donations"));
        assertEquals(50L, body.get("giftPools"));
    }

    // ==================== NULL HANDLING TESTS ====================

    @Test
    @DisplayName("Should return BAD_REQUEST with error message when service returns null and NPE is thrown")
    void testGetStats_WhenStatsIsNull_CatchesNullPointerException() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(null);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.containsKey("error"));
        assertNotNull(errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should handle null event count in stats gracefully")
    void testGetStats_WhenEventCountIsNull_HandlesGracefully() {
        // ARRANGE
        testStats.setEventCount(null);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNull(body.get("events"), "Event count should be null in response");
    }

    @Test
    @DisplayName("Should handle null donation count in stats gracefully")
    void testGetStats_WhenDonationCountIsNull_HandlesGracefully() {
        // ARRANGE
        testStats.setDonationCount(null);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNull(body.get("donations"), "Donation count should be null in response");
    }

    @Test
    @DisplayName("Should handle null gift pool count in stats gracefully")
    void testGetStats_WhenGiftPoolCountIsNull_HandlesGracefully() {
        // ARRANGE
        testStats.setGiftPoolCount(null);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNull(body.get("giftPools"), "Gift pool count should be null in response");
    }

    @Test
    @DisplayName("Should include all counts even when one is null")
    void testGetStats_MixedNullAndNonNullCounts_ReturnsAllFields() {
        // ARRANGE
        testStats.setEventCount(50L);
        testStats.setDonationCount(null);
        testStats.setGiftPoolCount(10L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(50L, body.get("events"));
        assertNull(body.get("donations"));
        assertEquals(10L, body.get("giftPools"));
        assertEquals(3, body.size());
    }

    // ==================== ERROR SCENARIOS ====================

    @Test
    @DisplayName("Should return bad request with error message when service throws RuntimeException")
    void testGetStats_WhenServiceThrowsRuntimeException_ReturnsBadRequest() {
        // ARRANGE
        String errorMessage = "Database connection failed";
        when(statsService.getStats()).thenThrow(new RuntimeException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertTrue(errorResponse.containsKey("error"), "Response should contain 'error' key");
        assertEquals(errorMessage, errorResponse.get("error"), "Error message should match");
    }

    @Test
    @DisplayName("Should return bad request with default message when RuntimeException has no message")
    void testGetStats_WhenRuntimeExceptionHasNullMessage_ReturnsDefaultMessage() {
        // ARRANGE
        when(statsService.getStats()).thenThrow(new RuntimeException());

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals("An error occurred", errorResponse.get("error"),
                "Should return default error message when exception message is null");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with custom message")
    void testGetStats_WhenIllegalArgumentExceptionThrown_ReturnsCustomMessage() {
        // ARRANGE
        String errorMessage = "Invalid stats configuration";
        when(statsService.getStats()).thenThrow(new IllegalArgumentException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should handle NullPointerException from service")
    void testGetStats_WhenNullPointerExceptionThrown_ReturnsBadRequest() {
        // ARRANGE
        String errorMessage = "Null pointer encountered";
        when(statsService.getStats()).thenThrow(new NullPointerException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should handle ArithmeticException from service")
    void testGetStats_WhenArithmeticExceptionThrown_ReturnsBadRequest() {
        // ARRANGE
        String errorMessage = "Division by zero";
        when(statsService.getStats()).thenThrow(new ArithmeticException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should have only error key in error response")
    void testGetStats_ErrorResponseContainsOnlyErrorKey() {
        // ARRANGE
        when(statsService.getStats()).thenThrow(new RuntimeException("Test error"));

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(1, errorResponse.size(), "Error response should contain only 1 key");
        assertTrue(errorResponse.containsKey("error"));
    }

    @Test
    @DisplayName("Should handle IndexOutOfBoundsException from service")
    void testGetStats_WhenIndexOutOfBoundsExceptionThrown_ReturnsBadRequest() {
        // ARRANGE
        String errorMessage = "Index out of bounds";
        when(statsService.getStats()).thenThrow(new IndexOutOfBoundsException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    // ==================== BOUNDARY VALUE TESTS ====================

    @Test
    @DisplayName("Should handle maximum long values for counts")
    void testGetStats_WithMaximumLongValues_ReturnsCorrectly() {
        // ARRANGE
        testStats.setEventCount(Long.MAX_VALUE);
        testStats.setDonationCount(Long.MAX_VALUE);
        testStats.setGiftPoolCount(Long.MAX_VALUE);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(Long.MAX_VALUE, responseBody.get("events"));
        assertEquals(Long.MAX_VALUE, responseBody.get("donations"));
        assertEquals(Long.MAX_VALUE, responseBody.get("giftPools"));
    }

    @Test
    @DisplayName("Should handle zero values correctly")
    void testGetStats_WithZeroValues_ReturnsZeros() {
        // ARRANGE
        testStats.setEventCount(0L);
        testStats.setDonationCount(0L);
        testStats.setGiftPoolCount(0L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(0L, responseBody.get("events"));
        assertEquals(0L, responseBody.get("donations"));
        assertEquals(0L, responseBody.get("giftPools"));
    }

    @Test
    @DisplayName("Should handle very large count values")
    void testGetStats_WithLargeCountValues_ReturnsCorrectly() {
        // ARRANGE
        testStats.setEventCount(999999999L);
        testStats.setDonationCount(888888888L);
        testStats.setGiftPoolCount(777777777L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(999999999L, responseBody.get("events"));
        assertEquals(888888888L, responseBody.get("donations"));
        assertEquals(777777777L, responseBody.get("giftPools"));
    }

    @Test
    @DisplayName("Should handle minimum long values (negative)")
    void testGetStats_WithNegativeValues_ReturnsCorrectly() {
        // ARRANGE
        testStats.setEventCount(-100L);
        testStats.setDonationCount(-50L);
        testStats.setGiftPoolCount(-25L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(-100L, responseBody.get("events"));
        assertEquals(-50L, responseBody.get("donations"));
        assertEquals(-25L, responseBody.get("giftPools"));
    }

    @Test
    @DisplayName("Should handle single high value with low values")
    void testGetStats_WithMixedHighAndLowValues_ReturnsCorrectly() {
        // ARRANGE
        testStats.setEventCount(9999999999L);
        testStats.setDonationCount(1L);
        testStats.setGiftPoolCount(1L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(9999999999L, responseBody.get("events"));
        assertEquals(1L, responseBody.get("donations"));
        assertEquals(1L, responseBody.get("giftPools"));
    }

    // ==================== VERIFY METHOD CALLS ====================

    @Test
    @DisplayName("Should call statsService getStats method exactly once on success")
    void testGetStats_VerifiesServiceMethodCalledOnce() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        statsController.getStats();

        // ASSERT
        verify(statsService, times(1)).getStats();
    }

    @Test
    @DisplayName("Should call statsService getStats method exactly once on error")
    void testGetStats_VerifiesServiceMethodCalledOnceOnError() {
        // ARRANGE
        when(statsService.getStats()).thenThrow(new RuntimeException("Error"));

        // ACT
        statsController.getStats();

        // ASSERT
        verify(statsService, times(1)).getStats();
    }

    @Test
    @DisplayName("Should not call increment methods during get stats")
    void testGetStats_VerifiesIncrementMethodsNotCalled() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        statsController.getStats();

        // ASSERT
        verify(statsService, never()).incrementEventCount();
        verify(statsService, never()).incrementDonationCount();
        verify(statsService, never()).incrementGiftPoolCount();
    }

    // ==================== RESPONSE ENTITY TESTS ====================

    @Test
    @DisplayName("Should return ResponseEntity object, not null")
    void testGetStats_ReturnsValidResponseEntity() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertNotNull(response, "ResponseEntity should not be null");
        assertNotNull(response.getBody(), "ResponseEntity body should not be null");
    }

    @Test
    @DisplayName("Should return correct HTTP status codes for success")
    void testGetStats_ReturnsCorrectHttpStatusCodes() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> successResponse = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, successResponse.getStatusCode());
        assertTrue(successResponse.getStatusCode().is2xxSuccessful(), "Success response should be 2xx");
    }

    @Test
    @DisplayName("Should return error response with 4xx status code on exception")
    void testGetStats_ReturnsErrorResponseWithCorrectStatus() {
        // ARRANGE
        when(statsService.getStats()).thenThrow(new RuntimeException("Test error"));

        // ACT
        ResponseEntity<?> errorResponse = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertTrue(errorResponse.getStatusCode().is4xxClientError(), "Error response should be 4xx");
    }

    @Test
    @DisplayName("Should have proper HTTP headers in response")
    void testGetStats_ResponseHasProperHeaders() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertNotNull(response.getHeaders());
    }

    // ==================== MULTIPLE EXECUTIONS ====================

    @Test
    @DisplayName("Should handle multiple consecutive calls correctly")
    void testGetStats_MultipleConsecutiveCalls_ReturnsConsistentResults() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response1 = statsController.getStats();
        ResponseEntity<?> response2 = statsController.getStats();
        ResponseEntity<?> response3 = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        Map<String, Object> body1 = (Map<String, Object>) response1.getBody();
        Map<String, Object> body2 = (Map<String, Object>) response2.getBody();
        Map<String, Object> body3 = (Map<String, Object>) response3.getBody();

        assertEquals(body1, body2);
        assertEquals(body2, body3);

        verify(statsService, times(3)).getStats();
    }

    @Test
    @DisplayName("Should handle exception after successful call")
    void testGetStats_ExceptionAfterSuccessfulCall_HandlesGracefully() {
        // ARRANGE
        when(statsService.getStats())
                .thenReturn(testStats)
                .thenThrow(new RuntimeException("Service unavailable"));

        // ACT
        ResponseEntity<?> successResponse = statsController.getStats();
        ResponseEntity<?> errorResponse = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.OK, successResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    }

    @Test
    @DisplayName("Should handle alternating success and failure calls")
    void testGetStats_AlternatingSuccessAndFailure_HandlesCorrectly() {
        // ARRANGE
        when(statsService.getStats())
                .thenReturn(testStats)
                .thenThrow(new RuntimeException("Error"))
                .thenReturn(testStats)
                .thenThrow(new RuntimeException("Error"));

        // ACT & ASSERT
        assertEquals(HttpStatus.OK, statsController.getStats().getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, statsController.getStats().getStatusCode());
        assertEquals(HttpStatus.OK, statsController.getStats().getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, statsController.getStats().getStatusCode());
    }

    @Test
    @DisplayName("Should maintain state across multiple calls")
    void testGetStats_MaintainsStateAcrossMultipleCalls() {
        // ARRANGE
        testStats.setEventCount(5L);
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response1 = statsController.getStats();
        testStats.setEventCount(10L);
        ResponseEntity<?> response2 = statsController.getStats();

        // ASSERT
        Map<String, Object> body1 = (Map<String, Object>) response1.getBody();
        Map<String, Object> body2 = (Map<String, Object>) response2.getBody();

        assertEquals(5L, body1.get("events"));
        assertEquals(10L, body2.get("events"));
    }

    // ==================== EDGE CASES - DATA CONSISTENCY ====================

    @Test
    @DisplayName("Should return consistent field names in response")
    void testGetStats_ConsistentFieldNamesInResponse() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertTrue(body.keySet().contains("events"));
        assertTrue(body.keySet().contains("donations"));
        assertTrue(body.keySet().contains("giftPools"));
    }

    @Test
    @DisplayName("Should not return null values for numeric fields when stats is valid")
    void testGetStats_NoNullNumericFieldsWhenStatsValid() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertNotNull(body.get("events"));
        assertNotNull(body.get("donations"));
        assertNotNull(body.get("giftPools"));
    }

    @Test
    @DisplayName("Should preserve numeric types in response")
    void testGetStats_PreservesNumericTypesInResponse() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertTrue(body.get("events") instanceof Long);
        assertTrue(body.get("donations") instanceof Long);
        assertTrue(body.get("giftPools") instanceof Long);
    }

    @Test
    @DisplayName("Should handle stats object with different data types correctly")
    void testGetStats_DifferentStatsDataTypes() {
        // ARRANGE
        Stats mixedStats = new Stats();
        mixedStats.setId(1L);
        mixedStats.setEventCount(100L);
        mixedStats.setDonationCount(0L);
        mixedStats.setGiftPoolCount(Long.MAX_VALUE);
        when(statsService.getStats()).thenReturn(mixedStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(100L, body.get("events"));
        assertEquals(0L, body.get("donations"));
        assertEquals(Long.MAX_VALUE, body.get("giftPools"));
    }

    @Test
    @DisplayName("Should handle error messages with special characters")
    void testGetStats_ErrorMessageWithSpecialCharacters() {
        // ARRANGE
        String errorMessage = "Error: Database connection failed @ localhost:3306!";
        when(statsService.getStats()).thenThrow(new RuntimeException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should handle very long error messages")
    void testGetStats_VeryLongErrorMessage() {
        // ARRANGE
        String longMessage = "A".repeat(1000);
        when(statsService.getStats()).thenThrow(new RuntimeException(longMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();

        // ASSERT
        assertEquals(longMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException from service")
    void testGetStats_WhenNumberFormatExceptionThrown_ReturnsBadRequest() {
        // ARRANGE
        String errorMessage = "Invalid number format";
        when(statsService.getStats()).thenThrow(new NumberFormatException(errorMessage));

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    @DisplayName("Should return response body as map type always")
    void testGetStats_ResponseBodyAlwaysMap() {
        // ARRANGE
        when(statsService.getStats()).thenReturn(testStats);

        // ACT
        ResponseEntity<?> response = statsController.getStats();

        // ASSERT
        assertTrue(response.getBody() instanceof Map, "Response body must be a Map instance");
    }
}
