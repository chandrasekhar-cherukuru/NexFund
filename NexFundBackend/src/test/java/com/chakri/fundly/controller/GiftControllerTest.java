package com.chakri.fundly.controller;

import com.chakri.fundly.model.Gift;
import com.chakri.fundly.service.GiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftControllerTest {

    @Mock
    private GiftService giftService;

    @InjectMocks
    private GiftController giftController;

    private Gift testGift;
    private Gift testGift2;
    private Gift testGift3;
    private List<Gift> giftList;

    @BeforeEach
    void setUp() {
        testGift = new Gift();
        testGift.setId("gift-001");
        testGift.setTitle("Birthday Gift");
        testGift.setDescription("A wonderful birthday gift");
        testGift.setUpiId("user@upi");
        testGift.setUpiMsg("Happy Birthday");
        testGift.setCreatorUsername("john_doe");
        testGift.setImageDataUrl("data:image/png;base64,ABC123");

        testGift2 = new Gift();
        testGift2.setId("gift-002");
        testGift2.setTitle("Wedding Gift");
        testGift2.setUpiId("user2@upi");
        testGift2.setCreatorUsername("jane_doe");

        testGift3 = new Gift();
        testGift3.setId("gift-003");
        testGift3.setTitle("Anniversary Gift");
        testGift3.setCreatorUsername("alex_smith");

        giftList = new ArrayList<>();
        giftList.add(testGift);
        giftList.add(testGift2);
        giftList.add(testGift3);
    }

    @Test
    void testGreet_ReturnsWelcomeMessage() {
        String result = giftController.greet();
        assertNotNull(result);
        assertEquals("Inside Gift Controller", result);
    }

    @Test
    void testGetGifts_ReturnsAllGifts() {
        when(giftService.getAllGifts()).thenReturn(giftList);
        List<Gift> result = giftController.getGifts();
        assertEquals(3, result.size());
        verify(giftService, times(1)).getAllGifts();
    }

    @Test
    void testGetGifts_ReturnsEmptyList() {
        when(giftService.getAllGifts()).thenReturn(new ArrayList<>());
        List<Gift> result = giftController.getGifts();
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateGift_SuccessfulCreation() {
        when(giftService.createGift(any())).thenReturn(testGift);
        ResponseEntity<Gift> result = giftController.createGift(new Gift());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("gift-001", result.getBody().getId());
    }

    @Test
    void testGetGiftById_Found() {
        when(giftService.getGiftById("gift-001")).thenReturn(Optional.of(testGift));
        ResponseEntity<Gift> result = giftController.getGiftById("gift-001");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetGiftById_NotFound() {
        when(giftService.getGiftById("non-existent")).thenReturn(Optional.empty());
        ResponseEntity<Gift> result = giftController.getGiftById("non-existent");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testDeleteGift_Success() {
        doNothing().when(giftService).deleteGift("gift-001");
        ResponseEntity<Void> result = giftController.deleteGift("gift-001");
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testSendEmail_Success() {
        ResponseEntity<Void> result = giftController.sendEmail(testGift);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetGiftsByCreator_FoundGifts() {
        when(giftService.getGiftsByCreator("john_doe")).thenReturn(List.of(testGift));
        ResponseEntity<List<Gift>> result = giftController.getGiftsByCreator("john_doe");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testGetGiftsByCreator_NoGifts() {
        when(giftService.getGiftsByCreator("unknown")).thenReturn(new ArrayList<>());
        ResponseEntity<List<Gift>> result = giftController.getGiftsByCreator("unknown");
        assertTrue(result.getBody().isEmpty());
    }
}
