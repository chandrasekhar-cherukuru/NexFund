package com.chakri.fundly.controller;

import com.chakri.fundly.model.Events;
import com.chakri.fundly.model.Gift;
import com.chakri.fundly.service.EventService;
import com.chakri.fundly.service.GiftService;
import com.chakri.fundly.service.UpiService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpiController Tests")
class UpiControllerTest {

    @Mock
    private UpiService upiService;

    @Mock
    private EventService eventService;

    @Mock
    private GiftService giftService;

    @InjectMocks
    private UpiController upiController;

    private Events mockEvent;
    private Gift mockGift;
    private String testEventId;
    private String testGiftId;
    private String testUpiId;
    private String testUsername;
    private String testUpiMsg;
    private String testUpiLink;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEventId = "EVENT123";
        testGiftId = "GIFT456";
        testUpiId = "upi@bankname";
        testUsername = "testuser";
        testUpiMsg = "Payment for event";
        testUpiLink = "upi://pay?pa=upi@bankname&pn=testuser&tn=Payment%20for%20event&am=1000";

        mockEvent = new Events();
        mockEvent.setUpiId(testUpiId);
        mockEvent.setCreatedByUsername(testUsername);
        mockEvent.setUpiMsg(testUpiMsg);
        mockEvent.setEventAmount(1000);

        mockGift = new Gift();
        mockGift.setUpiId(testUpiId);
        mockGift.setCreatorUsername(testUsername);
        mockGift.setUpiMsg(testUpiMsg);
    }

    @Test
    @DisplayName("Test greet endpoint returns correct message")
    void testGreet_ReturnsCorrectMessage() {
        String result = upiController.greet();
        assertEquals("Inside UPI Controller", result);
    }

    @Test
    void testGenerateUpiLinkForEvent_WithValidEventId_ReturnsOk() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("1000"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateUpiLinkForEvent(testEventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUpiLink, response.getBody());
        verify(eventService, times(1)).getEventById(testEventId);
        verify(upiService, times(1)).createUpiLink(testUpiId, testUsername, testUpiMsg, "1000");
    }

    @Test
    void testGenerateUpiLinkForEvent_WithInvalidEventId_ReturnsBadRequest() {
        when(eventService.getEventById("INVALID123")).thenReturn(Optional.empty());

        ResponseEntity<String> response = upiController.generateUpiLinkForEvent("INVALID123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(upiService, never()).createUpiLink(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testGenerateUpiLinkForEvent_WithNullEventId_ReturnsBadRequest() {
        when(eventService.getEventById(null)).thenReturn(Optional.empty());
        ResponseEntity<String> response = upiController.generateUpiLinkForEvent(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGenerateCustomAmountUpiLink_WithValidParams_ReturnsOk() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("5000"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateCustomAmountUpiLink(testEventId, 5000);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(upiService, times(1)).createUpiLink(testUpiId, testUsername, testUpiMsg, "5000");
    }

    @Test
    void testGenerateCustomAmountUpiLink_WithInvalidEvent_ReturnsBadRequest() {
        when(eventService.getEventById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<String> response = upiController.generateCustomAmountUpiLink("INVALID", 5000);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGenerateCustomAmountUpiLink_WithZeroAmount() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("0"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateCustomAmountUpiLink(testEventId, 0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGenerateCustomAmountUpiLink_WithNegativeAmount() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("-1000"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateCustomAmountUpiLink(testEventId, -1000);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGenerateCustomAmountUpiLink_WithLargeAmount() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("1000000"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateCustomAmountUpiLink(testEventId, 1000000);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetEventPaymentDetails_WithValidEventId() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));

        ResponseEntity<Events> response = upiController.getEventPaymentDetails(testEventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUpiId, response.getBody().getUpiId());
    }

    @Test
    void testGetEventPaymentDetails_WithInvalidEventId() {
        when(eventService.getEventById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<Events> response = upiController.getEventPaymentDetails("INVALID");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetEventPaymentDetails_WithNullEventId() {
        when(eventService.getEventById(null)).thenReturn(Optional.empty());
        ResponseEntity<Events> response = upiController.getEventPaymentDetails(null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGenerateUpiLinkForGift_WithValidGiftId() {
        when(giftService.getGiftById(testGiftId)).thenReturn(Optional.of(mockGift));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateUpiLinkForGift(testGiftId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUpiLink, response.getBody());
        verify(upiService, times(1)).createUpiLink(testUpiId, testUsername, testUpiMsg);
    }

    @Test
    void testGenerateUpiLinkForGift_WithInvalidGiftId() {
        when(giftService.getGiftById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<String> response = upiController.generateUpiLinkForGift("INVALID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGenerateUpiLinkForGift_WithNullGiftId() {
        when(giftService.getGiftById(null)).thenReturn(Optional.empty());
        ResponseEntity<String> response = upiController.generateUpiLinkForGift(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetGiftPaymentDetails_WithValidGiftId() {
        when(giftService.getGiftById(testGiftId)).thenReturn(Optional.of(mockGift));

        ResponseEntity<Gift> response = upiController.getGiftPaymentDetails(testGiftId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetGiftPaymentDetails_WithInvalidGiftId() {
        when(giftService.getGiftById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<Gift> response = upiController.getGiftPaymentDetails("INVALID");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetGiftPaymentDetails_WithNullGiftId() {
        when(giftService.getGiftById(null)).thenReturn(Optional.empty());
        ResponseEntity<Gift> response = upiController.getGiftPaymentDetails(null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGenerateUpiLinkForEventLegacy_WithValidEventId() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("1000"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateUpiLinkForEventLegacy(testEventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGenerateUpiLinkForEventLegacy_WithInvalidEventId() {
        when(eventService.getEventById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<String> response = upiController.generateUpiLinkForEventLegacy("INVALID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGenerateCustomAmountUpiLinkLegacy_WithValidParams() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(upiService.createUpiLink(eq(testUpiId), eq(testUsername), eq(testUpiMsg), eq("7500"))).thenReturn(testUpiLink);

        ResponseEntity<String> response = upiController.generateCustomAmountUpiLinkLegacy(testEventId, 7500);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetEventPaymentDetailsLegacy_WithValidEventId() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));

        ResponseEntity<Events> response = upiController.getEventPaymentDetailsLegacy(testEventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetEventPaymentDetailsLegacy_WithInvalidEventId() {
        when(eventService.getEventById("INVALID")).thenReturn(Optional.empty());
        ResponseEntity<Events> response = upiController.getEventPaymentDetailsLegacy("INVALID");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testMultipleSequentialCalls() {
        Events mockEvent2 = new Events();
        mockEvent2.setUpiId("upi@other");
        mockEvent2.setCreatedByUsername("user2");
        mockEvent2.setUpiMsg("Other");
        mockEvent2.setEventAmount(2000);

        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(eventService.getEventById("EVENT2")).thenReturn(Optional.of(mockEvent2));
        when(upiService.createUpiLink(anyString(), anyString(), anyString(), anyString())).thenReturn(testUpiLink);

        ResponseEntity<String> r1 = upiController.generateUpiLinkForEvent(testEventId);
        ResponseEntity<String> r2 = upiController.generateUpiLinkForEvent("EVENT2");

        assertEquals(HttpStatus.OK, r1.getStatusCode());
        assertEquals(HttpStatus.OK, r2.getStatusCode());
        verify(upiService, times(2)).createUpiLink(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testEventAndGiftReturnDifferentTypes() {
        when(eventService.getEventById(testEventId)).thenReturn(Optional.of(mockEvent));
        when(giftService.getGiftById(testGiftId)).thenReturn(Optional.of(mockGift));

        ResponseEntity<Events> eResponse = upiController.getEventPaymentDetails(testEventId);
        ResponseEntity<Gift> gResponse = upiController.getGiftPaymentDetails(testGiftId);

        assertInstanceOf(Events.class, eResponse.getBody());
        assertInstanceOf(Gift.class, gResponse.getBody());
    }

    @Test
    void testErrorPathsDoNotCallUpiService() {
        when(eventService.getEventById("INVALID")).thenReturn(Optional.empty());
        when(giftService.getGiftById("INVALID")).thenReturn(Optional.empty());

        upiController.generateUpiLinkForEvent("INVALID");
        upiController.generateUpiLinkForGift("INVALID");

        verify(upiService, never()).createUpiLink(anyString(), anyString(), anyString(), anyString());
        verify(upiService, never()).createUpiLink(anyString(), anyString(), anyString());
    }
}
