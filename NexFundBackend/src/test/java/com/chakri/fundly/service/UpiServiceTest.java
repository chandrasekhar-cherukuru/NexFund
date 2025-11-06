package com.chakri.fundly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test class for UpiService
 * Tests all methods with maximum code coverage (80%+)
 * Covers happy paths, edge cases, and error scenarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpiService Test Suite")
class UpiServiceTest {

    @InjectMocks
    private UpiService upiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== Tests for createUpiLink(4 parameters) ====================

    @Test
    @DisplayName("Create UPI Link with 4 params - Happy path with all parameters provided")
    void testCreateUpiLinkWithAmountHappyPath() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John Doe";
        String message = "Payment for event";
        String amount = "500";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
        assertTrue(result.contains("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Name is null")
    void testCreateUpiLinkWithAmountNameIsNull() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = null;
        String message = "Payment for event";
        String amount = "500";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertFalse(result.contains("pn="));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
        assertTrue(result.contains("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Name is empty string")
    void testCreateUpiLinkWithAmountNameIsEmpty() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "";
        String message = "Payment for event";
        String amount = "500";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertFalse(result.contains("pn="));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Message is null")
    void testCreateUpiLinkWithAmountMessageIsNull() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John Doe";
        String message = null;
        String amount = "500";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertFalse(result.contains("tn="));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Message is empty string")
    void testCreateUpiLinkWithAmountMessageIsEmpty() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John Doe";
        String message = "";
        String amount = "500";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("tn="));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Amount is null")
    void testCreateUpiLinkWithAmountAmountIsNull() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John Doe";
        String message = "Payment";
        String amount = null;

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("am="));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Amount is empty string")
    void testCreateUpiLinkWithAmountAmountIsEmpty() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John Doe";
        String message = "Payment";
        String amount = "";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("am="));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Multiple null/empty combinations")
    void testCreateUpiLinkWithAmountMultipleNullEmpty() {
        // ARRANGE
        String upiId = "9876543210@upi";
        String name = null;
        String message = "";
        String amount = null;

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertFalse(result.contains("pn="));
        assertFalse(result.contains("tn="));
        assertFalse(result.contains("am="));
        assertTrue(result.endsWith("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Special characters in name")
    void testCreateUpiLinkWithAmountSpecialCharactersInName() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "John & Jane Doe";
        String message = "Payment";
        String amount = "100";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - All parameters with special characters")
    void testCreateUpiLinkWithAmountAllSpecialCharacters() {
        // ARRANGE
        String upiId = "9876543210@okaxis";
        String name = "महेश शर्मा";
        String message = "Payment for Event & Conference #2024";
        String amount = "1000.50";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertTrue(result.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Create UPI Link with 4 params - Only UPI ID (all others null/empty)")
    void testCreateUpiLinkWithAmountOnlyUpiId() {
        // ARRANGE
        String upiId = "1234567890@ybl";
        String name = null;
        String message = null;
        String amount = null;

        // ACT
        String result = upiService.createUpiLink(upiId, name, message, amount);

        // ASSERT
        assertNotNull(result);
        assertEquals("upi://pay?pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8) + "&cu=INR", result);
    }

    // ==================== Tests for createUpiLink(3 parameters) ====================

    @Test
    @DisplayName("Create UPI Link with 3 params - Happy path with all parameters provided")
    void testCreateUpiLinkWithoutAmountHappyPath() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "Jane Doe";
        String message = "Gift for you";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.startsWith("upi://pay?"));
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertFalse(result.contains("am="));
        assertTrue(result.contains("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Name is null")
    void testCreateUpiLinkWithoutAmountNameIsNull() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = null;
        String message = "Gift";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("pn="));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertFalse(result.contains("am="));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Name is empty string")
    void testCreateUpiLinkWithoutAmountNameIsEmpty() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "";
        String message = "Gift";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("pn="));
        assertFalse(result.contains("am="));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Message is null")
    void testCreateUpiLinkWithoutAmountMessageIsNull() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "Jane Doe";
        String message = null;

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertFalse(result.contains("tn="));
        assertFalse(result.contains("am="));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Message is empty string")
    void testCreateUpiLinkWithoutAmountMessageIsEmpty() {
        // ARRANGE
        String upiId = "7981398515@ybl";
        String name = "Jane Doe";
        String message = "";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertFalse(result.contains("tn="));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Multiple null/empty combinations")
    void testCreateUpiLinkWithoutAmountMultipleNullEmpty() {
        // ARRANGE
        String upiId = "9876543210@upi";
        String name = null;
        String message = "";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertFalse(result.contains("pn="));
        assertFalse(result.contains("tn="));
        assertFalse(result.contains("am="));
        assertTrue(result.endsWith("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Special characters in all parameters")
    void testCreateUpiLinkWithoutAmountSpecialCharacters() {
        // ARRANGE
        String upiId = "9876543210@okaxis";
        String name = "Rajesh Kumar & Priya";
        String message = "Donation for Charity Event 2024!";

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.contains("pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8)));
        assertTrue(result.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertTrue(result.contains("tn=" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
        assertTrue(result.contains("cu=INR"));
    }

    @Test
    @DisplayName("Create UPI Link with 3 params - Only UPI ID provided")
    void testCreateUpiLinkWithoutAmountOnlyUpiId() {
        // ARRANGE
        String upiId = "1234567890@ybl";
        String name = null;
        String message = null;

        // ACT
        String result = upiService.createUpiLink(upiId, name, message);

        // ASSERT
        assertNotNull(result);
        assertEquals("upi://pay?pa=" + URLEncoder.encode(upiId, StandardCharsets.UTF_8) + "&cu=INR", result);
        assertFalse(result.contains("am="));
    }

    // ==================== Tests for extractUpiId method ====================

    @Test
    @DisplayName("Extract UPI ID - Valid UPI ID with @ symbol at beginning")
    void testExtractUpiIdValidUpiIdAtBeginning() {
        // ARRANGE
        String inputText = "7981398515@ybl some message";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - UPI ID in the middle of text")
    void testExtractUpiIdUpiIdInMiddle() {
        // ARRANGE
        String inputText = "Please send to 7981398515@ybl for event";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - UPI ID at the end of text")
    void testExtractUpiIdUpiIdAtEnd() {
        // ARRANGE
        String inputText = "Send payment to 7981398515@ybl";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Multiple spaces between parts")
    void testExtractUpiIdMultipleSpaces() {
        // ARRANGE
        String inputText = "Payment   7981398515@ybl   urgent";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - No @ symbol found, returns trimmed input")
    void testExtractUpiIdNoAtSymbol() {
        // ARRANGE
        String inputText = "simple text without upi id";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("simple text without upi id", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Multiple @ symbols, returns first match")
    void testExtractUpiIdMultipleAtSymbols() {
        // ARRANGE
        String inputText = "7981398515@ybl and 1234567890@okaxis both upi ids";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Empty string input")
    void testExtractUpiIdEmptyString() {
        // ARRANGE
        String inputText = "";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Only whitespace")
    void testExtractUpiIdOnlyWhitespace() {
        // ARRANGE
        String inputText = "    ";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Text with special characters and @ symbol")
    void testExtractUpiIdSpecialCharacters() {
        // ARRANGE
        String inputText = "Pay #7981398515@ybl! #important";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("#7981398515@ybl!", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Only UPI ID without additional text")
    void testExtractUpiIdOnlyUpiId() {
        // ARRANGE
        String inputText = "7981398515@ybl";

        // ACT
        String result = upiService.extractUpiId(inputText);

        // ASSERT
        assertNotNull(result);
        assertEquals("7981398515@ybl", result);
    }

    @Test
    @DisplayName("Extract UPI ID - Different UPI providers (okaxis, ibl, okhdfcbank)")
    void testExtractUpiIdDifferentProviders() {
        // ARRANGE
        String inputText1 = "user1@okaxis send payment";
        String inputText2 = "user2@ibl for event";
        String inputText3 = "user3@okhdfcbank donation";

        // ACT
        String result1 = upiService.extractUpiId(inputText1);
        String result2 = upiService.extractUpiId(inputText2);
        String result3 = upiService.extractUpiId(inputText3);

        // ASSERT
        assertEquals("user1@okaxis", result1);
        assertEquals("user2@ibl", result2);
        assertEquals("user3@okhdfcbank", result3);
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Integration - Create UPI Link with 4 params and extract UPI ID")
    void testIntegrationCreateAndExtractWithAmount() {
        // ARRANGE
        String inputText = "Send payment to 7981398515@ybl for event";
        String name = "Event Organizer";
        String message = "Event Payment";
        String amount = "500";

        // ACT
        String extractedUpiId = upiService.extractUpiId(inputText);
        String upiLink = upiService.createUpiLink(extractedUpiId, name, message, amount);

        // ASSERT
        assertNotNull(extractedUpiId);
        assertEquals("7981398515@ybl", extractedUpiId);
        assertNotNull(upiLink);
        assertTrue(upiLink.contains("pa=" + URLEncoder.encode(extractedUpiId, StandardCharsets.UTF_8)));
        assertTrue(upiLink.contains("am=" + URLEncoder.encode(amount, StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Integration - Create UPI Link with 3 params and extract UPI ID")
    void testIntegrationCreateAndExtractWithoutAmount() {
        // ARRANGE
        String inputText = "Please transfer to 9876543210@okaxis";
        String name = "Recipient Name";
        String message = "Gift Transfer";

        // ACT
        String extractedUpiId = upiService.extractUpiId(inputText);
        String upiLink = upiService.createUpiLink(extractedUpiId, name, message);

        // ASSERT
        assertNotNull(extractedUpiId);
        assertEquals("9876543210@okaxis", extractedUpiId);
        assertNotNull(upiLink);
        assertTrue(upiLink.contains("pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertFalse(upiLink.contains("am="));
    }

    @Test
    @DisplayName("Integration - Complex scenario with special characters and extraction")
    void testIntegrationComplexScenario() {
        // ARRANGE
        String inputText = "Payment to राजेश@ybl for event";
        String name = "राजेश कुमार";
        String message = "इवेंट के लिए भुगतान";
        String amount = "999.99";

        // ACT
        String extractedUpiId = upiService.extractUpiId(inputText);
        String upiLink = upiService.createUpiLink(extractedUpiId, name, message, amount);

        // ASSERT
        assertNotNull(extractedUpiId);
        assertNotNull(upiLink);
        assertTrue(upiLink.contains("cu=INR"));
    }
}
