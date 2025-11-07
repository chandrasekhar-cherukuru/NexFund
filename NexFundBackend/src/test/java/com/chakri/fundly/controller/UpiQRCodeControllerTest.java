package com.chakri.fundly.controller;

import com.chakri.fundly.model.Events;
import com.chakri.fundly.model.Gift;
import com.chakri.fundly.model.Donation;
import com.chakri.fundly.service.EventService;
import com.chakri.fundly.service.GiftService;
import com.chakri.fundly.service.DonationService;
import com.chakri.fundly.service.UpiQRCodeService;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpiQRCodeController Unit Tests")
class UpiQRCodeControllerTest {

    @Mock
    private UpiQRCodeService upiQRCodeService;

    @Mock
    private EventService eventService;

    @Mock
    private GiftService giftService;

    @Mock
    private DonationService donationService;

    @Mock
    private Events mockEvent;

    @Mock
    private Gift mockGift;

    @Mock
    private Donation mockDonation;

    @InjectMocks
    private UpiQRCodeController upiQRCodeController;

    // ==================== generateEventQR Tests ====================

    @Test
    @DisplayName("Event QR - Happy path with valid event")
    void testGenerateEventQR_Success() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        String expectedUpiUrl = "upi://pay?pa=upi@123&pn=john_doe&tn=Please donate for charity&am=1000";
        String expectedQrCode = "base64EncodedQRCode";

        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventAmount()).thenReturn(1000);
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl("upi@123", "john_doe", "Please donate for charity", 1000))
                .thenReturn(expectedUpiUrl);
        when(upiQRCodeService.createEventQRCodeBase64("upi@123", "john_doe", "Please donate for charity", 1000))
                .thenReturn(expectedQrCode);

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventId, response.getBody().get("eventId"));
        assertEquals("Charity Event", response.getBody().get("eventTitle"));
        assertEquals("1000", response.getBody().get("amount"));
        assertEquals(expectedUpiUrl, response.getBody().get("upiUrl"));
        assertEquals(expectedQrCode, response.getBody().get("qrCodeBase64"));
        assertTrue(response.getBody().get("htmlImg").contains("data:image/png;base64"));

        verify(eventService, times(1)).getEventById(eventId);
        verify(upiQRCodeService, times(1)).createEventUpiUrl(anyString(), anyString(), anyString(), anyInt());
        verify(upiQRCodeService, times(1)).createEventQRCodeBase64(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("Event QR - Event not found returns 400 error")
    void testGenerateEventQR_EventNotFound() {
        // ARRANGE
        String eventId = "non-existent-event";
        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Event not found"));
        verify(eventService, times(1)).getEventById(eventId);
        verify(upiQRCodeService, never()).createEventUpiUrl(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("Event QR - Exception during generation returns 500 error")
    void testGenerateEventQR_ExceptionDuringGeneration() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventAmount()).thenReturn(1000);

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("QR generation failed"));

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Failed to generate Event QR code"));
    }

    // ==================== generateGiftQR Tests ====================

    @Test
    @DisplayName("Gift QR - Happy path with valid gift")
    void testGenerateGiftQR_Success() throws WriterException, IOException {
        // ARRANGE
        String giftId = "gift-456";
        String expectedUpiUrl = "upi://pay?pa=upi@456&pn=jane_smith&tn=Gift collection";
        String expectedQrCode = "base64EncodedGiftQRCode";

        when(mockGift.getUpiId()).thenReturn("upi@456");
        when(mockGift.getCreatorUsername()).thenReturn("jane_smith");
        when(mockGift.getUpiMsg()).thenReturn("Gift collection");
        when(mockGift.getTitle()).thenReturn("Birthday Gift");

        when(giftService.getGiftById(giftId)).thenReturn(Optional.of(mockGift));
        when(upiQRCodeService.createGiftUpiUrl("upi@456", "jane_smith", "Gift collection"))
                .thenReturn(expectedUpiUrl);
        when(upiQRCodeService.createGiftQRCodeBase64("upi@456", "jane_smith", "Gift collection"))
                .thenReturn(expectedQrCode);

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateGiftQR(giftId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(giftId, response.getBody().get("giftId"));
        assertEquals("Birthday Gift", response.getBody().get("giftTitle"));
        assertEquals(expectedQrCode, response.getBody().get("qrCodeBase64"));

        verify(giftService, times(1)).getGiftById(giftId);
        verify(upiQRCodeService, times(1)).createGiftUpiUrl(anyString(), anyString(), anyString());
        verify(upiQRCodeService, times(1)).createGiftQRCodeBase64(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Gift QR - Gift not found returns 400 error")
    void testGenerateGiftQR_GiftNotFound() {
        // ARRANGE
        String giftId = "non-existent-gift";
        when(giftService.getGiftById(giftId)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateGiftQR(giftId);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Gift not found"));
    }

    @Test
    @DisplayName("Gift QR - Exception during generation returns 500 error")
    void testGenerateGiftQR_ExceptionDuringGeneration() throws WriterException, IOException {
        // ARRANGE
        String giftId = "gift-456";
        when(mockGift.getUpiId()).thenReturn("upi@456");
        when(mockGift.getCreatorUsername()).thenReturn("jane_smith");
        when(mockGift.getUpiMsg()).thenReturn("Gift collection");

        when(giftService.getGiftById(giftId)).thenReturn(Optional.of(mockGift));
        when(upiQRCodeService.createGiftUpiUrl(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("QR generation failed"));

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateGiftQR(giftId);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Failed to generate Gift QR code"));
    }

    // ==================== generateDonationQR Tests ====================

    @Test
    @DisplayName("Donation QR - Happy path with valid donation")
    void testGenerateDonationQR_Success() throws WriterException, IOException {
        // ARRANGE
        String donationId = "donation-789";
        String expectedUpiUrl = "upi://pay?pa=upi@789&pn=bob_wilson&tn=Please help us";
        String expectedQrCode = "base64EncodedDonationQRCode";

        when(mockDonation.getUpiId()).thenReturn("upi@789");
        when(mockDonation.getCreatorUsername()).thenReturn("bob_wilson");
        when(mockDonation.getUpiMsg()).thenReturn("Please help us");
        when(mockDonation.getTitle()).thenReturn("Medical Fund");

        when(donationService.getDonationById(donationId)).thenReturn(Optional.of(mockDonation));
        when(upiQRCodeService.createGiftUpiUrl("upi@789", "bob_wilson", "Please help us"))
                .thenReturn(expectedUpiUrl);
        when(upiQRCodeService.createGiftQRCodeBase64("upi@789", "bob_wilson", "Please help us"))
                .thenReturn(expectedQrCode);

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateDonationQR(donationId);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donationId, response.getBody().get("donationId"));
        assertEquals("Medical Fund", response.getBody().get("donationTitle"));
        assertEquals(expectedQrCode, response.getBody().get("qrCodeBase64"));

        verify(donationService, times(1)).getDonationById(donationId);
        verify(upiQRCodeService, times(1)).createGiftUpiUrl(anyString(), anyString(), anyString());
        verify(upiQRCodeService, times(1)).createGiftQRCodeBase64(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Donation QR - Donation not found returns 400 error")
    void testGenerateDonationQR_DonationNotFound() {
        // ARRANGE
        String donationId = "non-existent-donation";
        when(donationService.getDonationById(donationId)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateDonationQR(donationId);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Donation not found"));
    }

    @Test
    @DisplayName("Donation QR - Exception during generation returns 500 error")
    void testGenerateDonationQR_ExceptionDuringGeneration() throws WriterException, IOException {
        // ARRANGE
        String donationId = "donation-789";
        when(mockDonation.getUpiId()).thenReturn("upi@789");
        when(mockDonation.getCreatorUsername()).thenReturn("bob_wilson");
        when(mockDonation.getUpiMsg()).thenReturn("Please help us");

        when(donationService.getDonationById(donationId)).thenReturn(Optional.of(mockDonation));
        when(upiQRCodeService.createGiftUpiUrl(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("QR generation failed"));

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateDonationQR(donationId);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Failed to generate Donation QR code"));
    }

    // ==================== generateEventCustomAmountQR Tests ====================

    @Test
    @DisplayName("Custom Amount QR - Happy path with valid event")
    void testGenerateEventCustomAmountQR_Success() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        int customAmount = 5000;
        String expectedUpiUrl = "upi://pay?pa=upi@123&pn=john_doe&tn=Please donate for charity&am=5000";
        String expectedQrCode = "base64EncodedCustomQRCode";

        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl("upi@123", "john_doe", "Please donate for charity", customAmount))
                .thenReturn(expectedUpiUrl);
        when(upiQRCodeService.createEventQRCodeBase64("upi@123", "john_doe", "Please donate for charity", customAmount))
                .thenReturn(expectedQrCode);

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, customAmount);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventId, response.getBody().get("eventId"));
        assertEquals("5000", response.getBody().get("customAmount"));
        assertEquals(expectedQrCode, response.getBody().get("qrCodeBase64"));

        verify(eventService, times(1)).getEventById(eventId);
        verify(upiQRCodeService, times(1)).createEventUpiUrl(anyString(), anyString(), anyString(), eq(customAmount));
        verify(upiQRCodeService, times(1)).createEventQRCodeBase64(anyString(), anyString(), anyString(), eq(customAmount));
    }

    @Test
    @DisplayName("Custom Amount QR - Zero custom amount")
    void testGenerateEventCustomAmountQR_ZeroAmount() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        int customAmount = 0;
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), eq(0)))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64(anyString(), anyString(), anyString(), eq(0)))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, customAmount);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("0", response.getBody().get("customAmount"));
    }

    @Test
    @DisplayName("Custom Amount QR - Negative custom amount")
    void testGenerateEventCustomAmountQR_NegativeAmount() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        int customAmount = -100;
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), eq(-100)))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64(anyString(), anyString(), anyString(), eq(-100)))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, customAmount);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("-100", response.getBody().get("customAmount"));
    }

    @Test
    @DisplayName("Custom Amount QR - Large amount boundary")
    void testGenerateEventCustomAmountQR_LargeAmount() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        int customAmount = Integer.MAX_VALUE;
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), eq(Integer.MAX_VALUE)))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64(anyString(), anyString(), anyString(), eq(Integer.MAX_VALUE)))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, customAmount);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(String.valueOf(Integer.MAX_VALUE), response.getBody().get("customAmount"));
    }

    @Test
    @DisplayName("Custom Amount QR - Event not found returns 400 error")
    void testGenerateEventCustomAmountQR_EventNotFound() {
        // ARRANGE
        String eventId = "non-existent-event";
        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, 5000);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Event not found"));
    }

    @Test
    @DisplayName("Custom Amount QR - Exception during generation returns 500 error")
    void testGenerateEventCustomAmountQR_ExceptionDuringGeneration() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("Custom amount QR generation failed"));

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventCustomAmountQR(eventId, 5000);

        // ASSERT
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").contains("Failed to generate custom amount Event QR code"));
    }

    // ==================== Response Validation Tests ====================

    @Test
    @DisplayName("Response - Event QR contains all required fields")
    void testGenerateEventQR_ResponseStructure() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventAmount()).thenReturn(1000);
        when(mockEvent.getEventTitle()).thenReturn("Charity Event");

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("eventId"));
        assertTrue(body.containsKey("eventTitle"));
        assertTrue(body.containsKey("amount"));
        assertTrue(body.containsKey("upiUrl"));
        assertTrue(body.containsKey("qrCodeBase64"));
        assertTrue(body.containsKey("htmlImg"));
        assertEquals(6, body.size());
    }

    @Test
    @DisplayName("Response - HTML image tag is properly formatted")
    void testGenerateEventQR_HtmlImageFormat() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        String qrCodeBase64 = "testBase64";
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventAmount()).thenReturn(1000);

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(qrCodeBase64);

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        String htmlImg = response.getBody().get("htmlImg");
        assertTrue(htmlImg.contains("<img src=\"data:image/png;base64," + qrCodeBase64 + "\""));
        assertTrue(htmlImg.contains("alt=\"Event QR Code\""));
        assertTrue(htmlImg.contains("/>"));
    }

    @Test
    @DisplayName("Verification - Correct parameters passed to service methods")
    void testGenerateEventQR_VerifyCorrectParameters() throws WriterException, IOException {
        // ARRANGE
        String eventId = "event-123";
        when(mockEvent.getUpiId()).thenReturn("upi@123");
        when(mockEvent.getCreatedByUsername()).thenReturn("john_doe");
        when(mockEvent.getUpiMsg()).thenReturn("Please donate for charity");
        when(mockEvent.getEventAmount()).thenReturn(1000);

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(upiQRCodeService.createEventUpiUrl("upi@123", "john_doe", "Please donate for charity", 1000))
                .thenReturn("upi://url");
        when(upiQRCodeService.createEventQRCodeBase64("upi@123", "john_doe", "Please donate for charity", 1000))
                .thenReturn("qrCode");

        // ACT
        upiQRCodeController.generateEventQR(eventId);

        // ASSERT
        verify(upiQRCodeService).createEventUpiUrl("upi@123", "john_doe", "Please donate for charity", 1000);
        verify(upiQRCodeService).createEventQRCodeBase64("upi@123", "john_doe", "Please donate for charity", 1000);
    }

    @Test
    @DisplayName("Response - Gift QR contains all required fields")
    void testGenerateGiftQR_ResponseStructure() throws WriterException, IOException {
        // ARRANGE
        String giftId = "gift-456";
        when(mockGift.getUpiId()).thenReturn("upi@456");
        when(mockGift.getCreatorUsername()).thenReturn("jane_smith");
        when(mockGift.getUpiMsg()).thenReturn("Gift collection");
        when(mockGift.getTitle()).thenReturn("Birthday Gift");

        when(giftService.getGiftById(giftId)).thenReturn(Optional.of(mockGift));
        when(upiQRCodeService.createGiftUpiUrl(anyString(), anyString(), anyString()))
                .thenReturn("upi://url");
        when(upiQRCodeService.createGiftQRCodeBase64(anyString(), anyString(), anyString()))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateGiftQR(giftId);

        // ASSERT
        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("giftId"));
        assertTrue(body.containsKey("giftTitle"));
        assertTrue(body.containsKey("upiUrl"));
        assertTrue(body.containsKey("qrCodeBase64"));
        assertTrue(body.containsKey("htmlImg"));
        assertEquals(5, body.size());
    }

    @Test
    @DisplayName("Response - Donation QR contains all required fields")
    void testGenerateDonationQR_ResponseStructure() throws WriterException, IOException {
        // ARRANGE
        String donationId = "donation-789";
        when(mockDonation.getUpiId()).thenReturn("upi@789");
        when(mockDonation.getCreatorUsername()).thenReturn("bob_wilson");
        when(mockDonation.getUpiMsg()).thenReturn("Please help us");
        when(mockDonation.getTitle()).thenReturn("Medical Fund");

        when(donationService.getDonationById(donationId)).thenReturn(Optional.of(mockDonation));
        when(upiQRCodeService.createGiftUpiUrl(anyString(), anyString(), anyString()))
                .thenReturn("upi://url");
        when(upiQRCodeService.createGiftQRCodeBase64(anyString(), anyString(), anyString()))
                .thenReturn("qrCode");

        // ACT
        ResponseEntity<Map<String, String>> response = upiQRCodeController.generateDonationQR(donationId);

        // ASSERT
        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("donationId"));
        assertTrue(body.containsKey("donationTitle"));
        assertTrue(body.containsKey("upiUrl"));
        assertTrue(body.containsKey("qrCodeBase64"));
        assertTrue(body.containsKey("htmlImg"));
        assertEquals(5, body.size());
    }
}
