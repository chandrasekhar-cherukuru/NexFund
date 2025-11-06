package com.chakri.fundly.service;

import com.chakri.fundly.model.Gift;
import com.chakri.fundly.repo.GiftRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest {

    @Mock
    private GiftRepo giftRepo;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private GiftService giftService;

    private Gift testGift;
    private Gift anotherTestGift;
    private String testGiftId;
    private String testUsername;

    @BeforeEach
    void setUp() {
        // ✅ Inject mock statsService using ReflectionTestUtils
        ReflectionTestUtils.setField(giftService, "statsService", statsService);

        // Initialize test data
        testGiftId = "gift-123";
        testUsername = "testuser";

        testGift = new Gift();
        testGift.setId(testGiftId);
        testGift.setTitle("Birthday Fund");
        testGift.setDescription("Celebrating my birthday");
        testGift.setUpiId("testuser@okhdfcbank");
        testGift.setUpiMsg("Happy Birthday");
        testGift.setCreatorUsername(testUsername);
        testGift.setImageDataUrl("data:image/png;base64,ABC123");

        anotherTestGift = new Gift();
        anotherTestGift.setId("gift-456");
        anotherTestGift.setTitle("Wedding Fund");
        anotherTestGift.setDescription("Wedding celebration fund");
        anotherTestGift.setUpiId("anotheruser@okhdfcbank");
        anotherTestGift.setUpiMsg("Best Wishes");
        anotherTestGift.setCreatorUsername("anotheruser");
        anotherTestGift.setImageDataUrl("data:image/png;base64,XYZ789");
    }

    // ==================== createGift() Tests ====================

    @Test
    void testCreateGift_WithValidGift_ShouldSaveAndIncrementStats() {
        // ARRANGE
        when(giftRepo.save(testGift)).thenReturn(testGift);

        // ACT
        Gift result = giftService.createGift(testGift);

        // ASSERT
        assertNotNull(result, "Created gift should not be null");
        assertEquals(testGiftId, result.getId(), "Gift ID should match");
        assertEquals("Birthday Fund", result.getTitle(), "Gift title should match");
        verify(giftRepo, times(1)).save(testGift);
        verify(statsService, times(1)).incrementGiftPoolCount();
    }

    @Test
    void testCreateGift_WithNullTitle_ShouldStillSave() {
        // ARRANGE
        Gift giftWithNullTitle = new Gift();
        giftWithNullTitle.setId("gift-null-title");
        giftWithNullTitle.setTitle(null);
        giftWithNullTitle.setUpiId("test@bank");
        giftWithNullTitle.setCreatorUsername("user");

        when(giftRepo.save(giftWithNullTitle)).thenReturn(giftWithNullTitle);

        // ACT
        Gift result = giftService.createGift(giftWithNullTitle);

        // ASSERT
        assertNotNull(result);
        assertNull(result.getTitle(), "Gift title should be null");
        verify(giftRepo, times(1)).save(giftWithNullTitle);
        verify(statsService, times(1)).incrementGiftPoolCount();
    }

    @Test
    void testCreateGift_WithAllNullFields_ShouldStillSave() {
        // ARRANGE
        Gift emptyGift = new Gift();
        when(giftRepo.save(emptyGift)).thenReturn(emptyGift);

        // ACT
        Gift result = giftService.createGift(emptyGift);

        // ASSERT
        assertNotNull(result);
        verify(giftRepo, times(1)).save(emptyGift);
        verify(statsService, times(1)).incrementGiftPoolCount();
    }

    @Test
    void testCreateGift_StatsServiceCalledAfterSave() {
        // ARRANGE
        when(giftRepo.save(testGift)).thenReturn(testGift);

        // ACT
        giftService.createGift(testGift);

        // ASSERT
        InOrder inOrder = inOrder(giftRepo, statsService);
        inOrder.verify(giftRepo).save(testGift);
        inOrder.verify(statsService).incrementGiftPoolCount();
    }

    @Test
    void testCreateGift_WithSpecialCharactersInFields() {
        // ARRANGE
        Gift specialCharGift = new Gift();
        specialCharGift.setId("gift-special");
        specialCharGift.setTitle("Fund & Celebration #2025");
        specialCharGift.setUpiId("test@okhdfcbank");
        specialCharGift.setCreatorUsername("user@123");
        specialCharGift.setUpiMsg("₹1000/- per person!");

        when(giftRepo.save(specialCharGift)).thenReturn(specialCharGift);

        // ACT
        Gift result = giftService.createGift(specialCharGift);

        // ASSERT
        assertEquals("Fund & Celebration #2025", result.getTitle());
        verify(statsService, times(1)).incrementGiftPoolCount();
    }

    // ==================== getAllGifts() Tests ====================

    @Test
    void testGetAllGifts_WithMultipleGifts_ShouldReturnAll() {
        // ARRANGE
        List<Gift> giftList = new ArrayList<>();
        giftList.add(testGift);
        giftList.add(anotherTestGift);
        when(giftRepo.findAll()).thenReturn(giftList);

        // ACT
        List<Gift> result = giftService.getAllGifts();

        // ASSERT
        assertNotNull(result, "Gift list should not be null");
        assertEquals(2, result.size(), "Should return 2 gifts");
        assertEquals("Birthday Fund", result.get(0).getTitle());
        assertEquals("Wedding Fund", result.get(1).getTitle());
        verify(giftRepo, times(1)).findAll();
    }

    @Test
    void testGetAllGifts_WithEmptyList_ShouldReturnEmptyList() {
        // ARRANGE
        List<Gift> emptyList = new ArrayList<>();
        when(giftRepo.findAll()).thenReturn(emptyList);

        // ACT
        List<Gift> result = giftService.getAllGifts();

        // ASSERT
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result list should be empty");
        assertEquals(0, result.size());
        verify(giftRepo, times(1)).findAll();
    }

    @Test
    void testGetAllGifts_WithSingleGift_ShouldReturnSingleGift() {
        // ARRANGE
        List<Gift> singleGiftList = new ArrayList<>();
        singleGiftList.add(testGift);
        when(giftRepo.findAll()).thenReturn(singleGiftList);

        // ACT
        List<Gift> result = giftService.getAllGifts();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(testGiftId, result.get(0).getId());
    }

    // ==================== getGiftById() Tests ====================

    @Test
    void testGetGiftById_WithValidId_ShouldReturnGift() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        Optional<Gift> result = giftService.getGiftById(testGiftId);

        // ASSERT
        assertTrue(result.isPresent(), "Optional should contain a value");
        assertEquals(testGiftId, result.get().getId());
        assertEquals("Birthday Fund", result.get().getTitle());
        verify(giftRepo, times(1)).findById(testGiftId);
    }

    @Test
    void testGetGiftById_WithNonExistentId_ShouldReturnEmptyOptional() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        when(giftRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        Optional<Gift> result = giftService.getGiftById(nonExistentId);

        // ASSERT
        assertFalse(result.isPresent(), "Optional should be empty");
        assertTrue(result.isEmpty());
        verify(giftRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetGiftById_WithNullId_ShouldReturnEmptyOptional() {
        // ARRANGE
        when(giftRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        Optional<Gift> result = giftService.getGiftById(null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(giftRepo, times(1)).findById(null);
    }

    @Test
    void testGetGiftById_WithEmptyStringId_ShouldReturnEmptyOptional() {
        // ARRANGE
        when(giftRepo.findById("")).thenReturn(Optional.empty());

        // ACT
        Optional<Gift> result = giftService.getGiftById("");

        // ASSERT
        assertFalse(result.isPresent());
        verify(giftRepo, times(1)).findById("");
    }

    // ==================== deleteGift() Tests ====================

    @Test
    void testDeleteGift_WithValidId_ShouldCallDelete() {
        // ARRANGE
        doNothing().when(giftRepo).deleteById(testGiftId);

        // ACT
        giftService.deleteGift(testGiftId);

        // ASSERT
        verify(giftRepo, times(1)).deleteById(testGiftId);
    }

    @Test
    void testDeleteGift_WithNonExistentId_ShouldCallDeleteAnyway() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        doNothing().when(giftRepo).deleteById(nonExistentId);

        // ACT
        giftService.deleteGift(nonExistentId);

        // ASSERT
        verify(giftRepo, times(1)).deleteById(nonExistentId);
    }

    @Test
    void testDeleteGift_WithNullId_ShouldCallDelete() {
        // ARRANGE
        doNothing().when(giftRepo).deleteById(null);

        // ACT
        giftService.deleteGift(null);

        // ASSERT
        verify(giftRepo, times(1)).deleteById(null);
    }

    @Test
    void testDeleteGift_WithEmptyStringId_ShouldCallDelete() {
        // ARRANGE
        doNothing().when(giftRepo).deleteById("");

        // ACT
        giftService.deleteGift("");

        // ASSERT
        verify(giftRepo, times(1)).deleteById("");
    }

    // ==================== getGiftsByCreator() Tests ====================

    @Test
    void testGetGiftsByCreator_WithValidUsername_ShouldReturnGifts() {
        // ARRANGE
        List<Gift> userGifts = new ArrayList<>();
        userGifts.add(testGift);
        when(giftRepo.findByCreatorUsername(testUsername)).thenReturn(userGifts);

        // ACT
        List<Gift> result = giftService.getGiftsByCreator(testUsername);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUsername, result.get(0).getCreatorUsername());
        verify(giftRepo, times(1)).findByCreatorUsername(testUsername);
    }

    @Test
    void testGetGiftsByCreator_WithMultipleGiftsByCreator_ShouldReturnAll() {
        // ARRANGE
        Gift gift2 = new Gift();
        gift2.setId("gift-789");
        gift2.setTitle("Anniversary Fund");
        gift2.setCreatorUsername(testUsername);

        List<Gift> userGifts = new ArrayList<>();
        userGifts.add(testGift);
        userGifts.add(gift2);
        when(giftRepo.findByCreatorUsername(testUsername)).thenReturn(userGifts);

        // ACT
        List<Gift> result = giftService.getGiftsByCreator(testUsername);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(g -> g.getCreatorUsername().equals(testUsername)));
    }

    @Test
    void testGetGiftsByCreator_WithNonExistentUsername_ShouldReturnEmptyList() {
        // ARRANGE
        String nonExistentUsername = "nonexistent";
        when(giftRepo.findByCreatorUsername(nonExistentUsername)).thenReturn(new ArrayList<>());

        // ACT
        List<Gift> result = giftService.getGiftsByCreator(nonExistentUsername);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(giftRepo, times(1)).findByCreatorUsername(nonExistentUsername);
    }

    @Test
    void testGetGiftsByCreator_WithNullUsername_ShouldReturnEmptyList() {
        // ARRANGE
        when(giftRepo.findByCreatorUsername(null)).thenReturn(new ArrayList<>());

        // ACT
        List<Gift> result = giftService.getGiftsByCreator(null);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(giftRepo, times(1)).findByCreatorUsername(null);
    }

    @Test
    void testGetGiftsByCreator_WithEmptyUsername_ShouldReturnEmptyList() {
        // ARRANGE
        when(giftRepo.findByCreatorUsername("")).thenReturn(new ArrayList<>());

        // ACT
        List<Gift> result = giftService.getGiftsByCreator("");

        // ASSERT
        assertTrue(result.isEmpty());
        verify(giftRepo, times(1)).findByCreatorUsername("");
    }

    // ==================== getUpiId() Tests ====================

    @Test
    void testGetUpiId_WithValidGiftId_ShouldReturnUpiId() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        String result = giftService.getUpiId(testGiftId);

        // ASSERT
        assertNotNull(result, "UPI ID should not be null");
        assertEquals("testuser@okhdfcbank", result);
        verify(giftRepo, times(1)).findById(testGiftId);
    }

    @Test
    void testGetUpiId_WithNonExistentId_ShouldReturnNull() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        when(giftRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiId(nonExistentId);

        // ASSERT
        assertNull(result, "UPI ID should be null for non-existent gift");
        verify(giftRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetUpiId_WithGiftHavingNullUpiId_ShouldReturnNull() {
        // ARRANGE
        Gift giftWithNullUpiId = new Gift();
        giftWithNullUpiId.setId("gift-null-upi");
        giftWithNullUpiId.setUpiId(null);
        when(giftRepo.findById("gift-null-upi")).thenReturn(Optional.of(giftWithNullUpiId));

        // ACT
        String result = giftService.getUpiId("gift-null-upi");

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetUpiId_WithNullId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiId(null);

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetUpiId_WithEmptyStringId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById("")).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiId("");

        // ASSERT
        assertNull(result);
    }

    // ==================== getUpiMsg() Tests ====================

    @Test
    void testGetUpiMsg_WithValidGiftId_ShouldReturnUpiMsg() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        String result = giftService.getUpiMsg(testGiftId);

        // ASSERT
        assertNotNull(result);
        assertEquals("Happy Birthday", result);
        verify(giftRepo, times(1)).findById(testGiftId);
    }

    @Test
    void testGetUpiMsg_WithNonExistentId_ShouldReturnNull() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        when(giftRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiMsg(nonExistentId);

        // ASSERT
        assertNull(result);
        verify(giftRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetUpiMsg_WithGiftHavingNullUpiMsg_ShouldReturnNull() {
        // ARRANGE
        Gift giftWithNullUpiMsg = new Gift();
        giftWithNullUpiMsg.setId("gift-null-msg");
        giftWithNullUpiMsg.setUpiMsg(null);
        when(giftRepo.findById("gift-null-msg")).thenReturn(Optional.of(giftWithNullUpiMsg));

        // ACT
        String result = giftService.getUpiMsg("gift-null-msg");

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetUpiMsg_WithNullId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiMsg(null);

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetUpiMsg_WithEmptyStringId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById("")).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getUpiMsg("");

        // ASSERT
        assertNull(result);
    }

    // ==================== getCreatorUsername() Tests ====================

    @Test
    void testGetCreatorUsername_WithValidGiftId_ShouldReturnUsername() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        String result = giftService.getCreatorUsername(testGiftId);

        // ASSERT
        assertNotNull(result);
        assertEquals(testUsername, result);
        verify(giftRepo, times(1)).findById(testGiftId);
    }

    @Test
    void testGetCreatorUsername_WithNonExistentId_ShouldReturnNull() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        when(giftRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getCreatorUsername(nonExistentId);

        // ASSERT
        assertNull(result);
        verify(giftRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetCreatorUsername_WithGiftHavingNullUsername_ShouldReturnNull() {
        // ARRANGE
        Gift giftWithNullUsername = new Gift();
        giftWithNullUsername.setId("gift-null-user");
        giftWithNullUsername.setCreatorUsername(null);
        when(giftRepo.findById("gift-null-user")).thenReturn(Optional.of(giftWithNullUsername));

        // ACT
        String result = giftService.getCreatorUsername("gift-null-user");

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetCreatorUsername_WithNullId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getCreatorUsername(null);

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetCreatorUsername_WithEmptyStringId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById("")).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getCreatorUsername("");

        // ASSERT
        assertNull(result);
    }

    // ==================== getTitle() Tests ====================

    @Test
    void testGetTitle_WithValidGiftId_ShouldReturnTitle() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        String result = giftService.getTitle(testGiftId);

        // ASSERT
        assertNotNull(result);
        assertEquals("Birthday Fund", result);
        verify(giftRepo, times(1)).findById(testGiftId);
    }

    @Test
    void testGetTitle_WithNonExistentId_ShouldReturnNull() {
        // ARRANGE
        String nonExistentId = "gift-nonexistent";
        when(giftRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getTitle(nonExistentId);

        // ASSERT
        assertNull(result);
        verify(giftRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetTitle_WithGiftHavingNullTitle_ShouldReturnNull() {
        // ARRANGE
        Gift giftWithNullTitle = new Gift();
        giftWithNullTitle.setId("gift-null-title");
        giftWithNullTitle.setTitle(null);
        when(giftRepo.findById("gift-null-title")).thenReturn(Optional.of(giftWithNullTitle));

        // ACT
        String result = giftService.getTitle("gift-null-title");

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetTitle_WithNullId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById(null)).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getTitle(null);

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetTitle_WithEmptyStringId_ShouldReturnNull() {
        // ARRANGE
        when(giftRepo.findById("")).thenReturn(Optional.empty());

        // ACT
        String result = giftService.getTitle("");

        // ASSERT
        assertNull(result);
    }

    @Test
    void testGetTitle_WithLongTitle_ShouldReturnFullTitle() {
        // ARRANGE
        Gift giftWithLongTitle = new Gift();
        giftWithLongTitle.setId("gift-long-title");
        String longTitle = "A".repeat(500);
        giftWithLongTitle.setTitle(longTitle);
        when(giftRepo.findById("gift-long-title")).thenReturn(Optional.of(giftWithLongTitle));

        // ACT
        String result = giftService.getTitle("gift-long-title");

        // ASSERT
        assertEquals(longTitle, result);
        assertEquals(500, result.length());
    }

    // ==================== Additional Comprehensive Edge Case Tests ====================

    @Test
    void testMultipleOperationsSequence_ShouldMaintainIsolation() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));
        when(giftRepo.findAll()).thenReturn(new ArrayList<>());

        // ACT
        String upiId = giftService.getUpiId(testGiftId);
        List<Gift> allGifts = giftService.getAllGifts();

        // ASSERT
        assertEquals("testuser@okhdfcbank", upiId);
        assertTrue(allGifts.isEmpty());
        verify(giftRepo).findById(testGiftId);
        verify(giftRepo).findAll();
    }

    @Test
    void testCreateGift_VerifyNoInteractionWithUnnecessaryMethods() {
        // ARRANGE
        when(giftRepo.save(testGift)).thenReturn(testGift);

        // ACT
        giftService.createGift(testGift);

        // ASSERT
        verify(giftRepo, times(1)).save(testGift);
        verify(giftRepo, never()).findAll();
        verify(giftRepo, never()).findById(any());
    }

    @Test
    void testGetGiftById_VerifyCorrectIdPassedToRepository() {
        // ARRANGE
        when(giftRepo.findById(testGiftId)).thenReturn(Optional.of(testGift));

        // ACT
        giftService.getGiftById(testGiftId);

        // ASSERT
        verify(giftRepo).findById(testGiftId);
        verify(giftRepo, never()).findByCreatorUsername(any());
    }

    @Test
    void testGetGiftsByCreator_WithCaseSensitiveUsername() {
        // ARRANGE
        String username1 = "TestUser";
        String username2 = "testuser";
        List<Gift> result1 = new ArrayList<>();
        List<Gift> result2 = new ArrayList<>();
        result1.add(testGift);

        when(giftRepo.findByCreatorUsername(username1)).thenReturn(result1);
        when(giftRepo.findByCreatorUsername(username2)).thenReturn(result2);

        // ACT
        List<Gift> res1 = giftService.getGiftsByCreator(username1);
        List<Gift> res2 = giftService.getGiftsByCreator(username2);

        // ASSERT
        assertEquals(1, res1.size());
        assertEquals(0, res2.size());
    }
}
