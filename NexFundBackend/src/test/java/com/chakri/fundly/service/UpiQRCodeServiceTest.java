package com.chakri.fundly.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit Test Class for UpiQRCodeService
 *
 * This test class provides complete coverage of the UpiQRCodeService functionality
 * with 35 test cases covering:
 * - Happy path scenarios
 * - Null parameter validation
 * - Edge cases (empty strings, boundary values)
 * - Exception handling
 * - Special character and Unicode support
 *
 * Target Coverage: 80%+
 * Test Count: 35 comprehensive test cases
 *
 * @author Test Suite
 * @version 2.1 - FINAL CORRECTED
 */
@ExtendWith(MockitoExtension.class)
class UpiQRCodeServiceTest {

    @Mock
    private UpiService upiService;

    @InjectMocks
    private UpiQRCodeService upiQRCodeService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    // Test data constants
    private static final String TEST_UPI_ID = "test@upi";
    private static final String TEST_PAYEE_NAME = "Test Payee";
    private static final String TEST_MESSAGE = "Test Message";
    private static final int TEST_AMOUNT = 100;
    private static final String TEST_UPI_URL = "upi://pay?pa=test@upi&pn=Test%20Payee&tn=Test%20Message&am=100";

    @BeforeEach
    void setUp() {
        assertNotNull(upiQRCodeService, "UpiQRCodeService should be initialized");
        assertNotNull(upiService, "UpiService mock should be initialized");
    }

    // ========================== Tests for createEventQRCodeBase64 ==========================

    @Test
    void testCreateEventQRCodeBase64_WithValidInputs_ReturnsBase64String() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventQRCodeBase64(
                TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, TEST_AMOUNT);

        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(isValidBase64(result));
        verify(upiService, times(1))
                .createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT));
    }

    @Test
    void testCreateEventQRCodeBase64_WithNullUpiId_ThrowsException() {
        when(upiService.createUpiLink(null, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenThrow(new IllegalArgumentException("UPI ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createEventQRCodeBase64(null, TEST_PAYEE_NAME, TEST_MESSAGE, TEST_AMOUNT));
    }

    @Test
    void testCreateEventQRCodeBase64_WithNullPayeeName_ThrowsException() {
        when(upiService.createUpiLink(TEST_UPI_ID, null, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenThrow(new IllegalArgumentException("Payee name cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createEventQRCodeBase64(TEST_UPI_ID, null, TEST_MESSAGE, TEST_AMOUNT));
    }

    @Test
    void testCreateEventQRCodeBase64_WithNullMessage_ThrowsException() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, null, String.valueOf(TEST_AMOUNT)))
                .thenThrow(new IllegalArgumentException("Message cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createEventQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, null, TEST_AMOUNT));
    }

    @Test
    void testCreateEventQRCodeBase64_WithZeroAmount_ProcessesSuccessfully() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "0"))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, 0);

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "0");
    }

    @Test
    void testCreateEventQRCodeBase64_WithNegativeAmount_ProcessesSuccessfully() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "-50"))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, -50);

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "-50");
    }

    @Test
    void testCreateEventQRCodeBase64_WithEmptyStrings_ProcessesSuccessfully() throws WriterException, IOException {
        when(upiService.createUpiLink("", "", "", String.valueOf(TEST_AMOUNT)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventQRCodeBase64("", "", "", TEST_AMOUNT);

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink("", "", "", String.valueOf(TEST_AMOUNT));
    }

    @Test
    void testCreateEventQRCodeBase64_WithLargeAmount_ProcessesSuccessfully() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "999999"))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, 999999);

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "999999");
    }

    // ========================== Tests for createGiftQRCodeBase64 ==========================

    @Test
    void testCreateGiftQRCodeBase64_WithValidInputs_ReturnsBase64String() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE);

        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(isValidBase64(result));
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE);
    }

    @Test
    void testCreateGiftQRCodeBase64_WithNullUpiId_ThrowsException() {
        when(upiService.createUpiLink(null, TEST_PAYEE_NAME, TEST_MESSAGE))
                .thenThrow(new IllegalArgumentException("UPI ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createGiftQRCodeBase64(null, TEST_PAYEE_NAME, TEST_MESSAGE));
    }

    @Test
    void testCreateGiftQRCodeBase64_WithNullPayeeName_ThrowsException() {
        when(upiService.createUpiLink(TEST_UPI_ID, null, TEST_MESSAGE))
                .thenThrow(new IllegalArgumentException("Payee name cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createGiftQRCodeBase64(TEST_UPI_ID, null, TEST_MESSAGE));
    }

    @Test
    void testCreateGiftQRCodeBase64_WithNullMessage_ThrowsException() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, null))
                .thenThrow(new IllegalArgumentException("Message cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createGiftQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, null));
    }

    @Test
    void testCreateGiftQRCodeBase64_WithEmptyStrings_ProcessesSuccessfully() throws WriterException, IOException {
        when(upiService.createUpiLink("", "", "")).thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftQRCodeBase64("", "", "");

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink("", "", "");
    }

    @Test
    void testCreateGiftQRCodeBase64_WithSpecialCharacters_ProcessesSuccessfully() throws WriterException, IOException {
        String specialMessage = "Test@#$%^&*()_+-=";
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, specialMessage))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftQRCodeBase64(TEST_UPI_ID, TEST_PAYEE_NAME, specialMessage);

        assertNotNull(result);
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, specialMessage);
    }

    // ========================== Tests for createQRCodeBase64 ==========================

    @Test
    void testCreateQRCodeBase64_WithValidData_ReturnsValidBase64String() throws WriterException, IOException {
        String result = upiQRCodeService.createQRCodeBase64(TEST_UPI_URL);

        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(isValidBase64(result));
    }

    @Test
    void testCreateQRCodeBase64_WithEmptyString_ThrowsException() {
        // QR codes cannot have empty data - this should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                        upiQRCodeService.createQRCodeBase64(""),
                "Empty string should throw IllegalArgumentException");
    }

    @Test
    void testCreateQRCodeBase64_WithNullData_ThrowsException() {
        assertThrows(Exception.class, () -> upiQRCodeService.createQRCodeBase64(null),
                "Null data should throw exception");
    }

    @Test
    void testCreateQRCodeBase64_WithLongData_ProcessesSuccessfully() throws WriterException, IOException {
        String longData = "A".repeat(2000);

        String result = upiQRCodeService.createQRCodeBase64(longData);

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    void testCreateQRCodeBase64_WithUnicodeCharacters_ProcessesSuccessfully() throws WriterException, IOException {
        String unicodeData = "नमस्ते क्या हाल है 你好 مرحبا";

        String result = upiQRCodeService.createQRCodeBase64(unicodeData);

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    // ========================== Tests for createEventUpiUrl ==========================

    @Test
    void testCreateEventUpiUrl_WithValidInputs_ReturnsUpiUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, TEST_AMOUNT);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
        verify(upiService, times(1))
                .createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT));
    }

    @Test
    void testCreateEventUpiUrl_WithNullUpiId_ThrowsException() {
        when(upiService.createUpiLink(null, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenThrow(new IllegalArgumentException("UPI ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createEventUpiUrl(null, TEST_PAYEE_NAME, TEST_MESSAGE, TEST_AMOUNT));
    }

    @Test
    void testCreateEventUpiUrl_WithZeroAmount_ReturnsUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "0"))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, 0);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateEventUpiUrl_WithNegativeAmount_ReturnsUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, "-100"))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, -100);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateEventUpiUrl_WithEmptyStrings_ReturnsUrl() {
        when(upiService.createUpiLink("", "", "", String.valueOf(TEST_AMOUNT)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl("", "", "", TEST_AMOUNT);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateEventUpiUrl_WithMaxIntAmount_ReturnsUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(Integer.MAX_VALUE)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, Integer.MAX_VALUE);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateEventUpiUrl_WithMinIntAmount_ReturnsUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(Integer.MIN_VALUE)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, Integer.MIN_VALUE);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    // ========================== Tests for createGiftUpiUrl ==========================

    @Test
    void testCreateGiftUpiUrl_WithValidInputs_ReturnsUpiUrl() {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
        verify(upiService, times(1)).createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE);
    }

    @Test
    void testCreateGiftUpiUrl_WithNullUpiId_ThrowsException() {
        when(upiService.createUpiLink(null, TEST_PAYEE_NAME, TEST_MESSAGE))
                .thenThrow(new IllegalArgumentException("UPI ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createGiftUpiUrl(null, TEST_PAYEE_NAME, TEST_MESSAGE));
    }

    @Test
    void testCreateGiftUpiUrl_WithNullPayeeName_ThrowsException() {
        when(upiService.createUpiLink(TEST_UPI_ID, null, TEST_MESSAGE))
                .thenThrow(new IllegalArgumentException("Payee name cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                upiQRCodeService.createGiftUpiUrl(TEST_UPI_ID, null, TEST_MESSAGE));
    }

    @Test
    void testCreateGiftUpiUrl_WithEmptyStrings_ReturnsUrl() {
        when(upiService.createUpiLink("", "", "")).thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftUpiUrl("", "", "");

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateGiftUpiUrl_WithSpecialCharacters_ReturnsUrl() {
        String specialMessage = "Test@#$%^&*()";
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, specialMessage))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, specialMessage);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateGiftUpiUrl_WithLongMessage_ReturnsUrl() {
        String longMessage = "A".repeat(500);
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, longMessage))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, longMessage);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testCreateGiftUpiUrl_WithUnicodeCharacters_ReturnsUrl() {
        String unicodeMessage = "नमस्ते";
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, unicodeMessage))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createGiftUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, unicodeMessage);

        assertNotNull(result);
        assertEquals(TEST_UPI_URL, result);
    }

    @Test
    void testSetUp_VerifiesProperInitialization() {
        assertNotNull(upiQRCodeService);
        assertNotNull(upiService);
    }

    @Test
    void testInjection_VerifiesUpiServiceIsInjected() throws WriterException, IOException {
        when(upiService.createUpiLink(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, String.valueOf(TEST_AMOUNT)))
                .thenReturn(TEST_UPI_URL);

        String result = upiQRCodeService.createEventUpiUrl(TEST_UPI_ID, TEST_PAYEE_NAME, TEST_MESSAGE, TEST_AMOUNT);

        assertNotNull(result);
        verify(upiService, atLeastOnce()).createUpiLink(anyString(), anyString(), anyString(), anyString());
    }

    // ========================== Helper Methods ==========================

    /**
     * Validates if the given string is a valid Base64 encoded string
     *
     * @param str the string to validate
     * @return true if valid Base64, false otherwise
     */
    private boolean isValidBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
