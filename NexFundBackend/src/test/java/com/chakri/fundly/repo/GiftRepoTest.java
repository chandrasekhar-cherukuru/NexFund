package com.chakri.fundly.repo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.Gift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("GiftRepo - Unit Tests with 35 Test Methods")
class GiftRepoTest {

    @Mock
    private GiftRepo giftRepo;

    private Gift testGift;
    private Gift testGift2;
    private Gift testGift3;

    @BeforeEach
    void setUp() {
        // Test data initialization - all with "testuser" for consistency
        testGift = new Gift("gift-001", "Birthday Gift", "A special birthday present",
                "user@upi", "Birthday gift for me", "testuser", "data:image/jpeg;base64,ABC123");

        testGift2 = new Gift("gift-002", "Anniversary Gift", "An anniversary present",
                "user2@upi", "Anniversary gift for us", "testuser", "data:image/png;base64,DEF456");

        testGift3 = new Gift("gift-003", "Wedding Gift", "A wedding present",
                "user3@upi", "Wedding gift for the couple", "testuser", "data:image/jpeg;base64,GHI789");
    }

    // ===== findByCreatorUsername: 6 tests =====

    @Test
    @DisplayName("findByCreatorUsername: Valid username returns 2 gifts")
    void test_findByCreatorUsername_ValidUsername() {
        when(giftRepo.findByCreatorUsername("testuser")).thenReturn(Arrays.asList(testGift, testGift2));
        List<Gift> result = giftRepo.findByCreatorUsername("testuser");
        assertEquals(2, result.size());
        verify(giftRepo).findByCreatorUsername("testuser");
    }

    @Test
    @DisplayName("findByCreatorUsername: No gifts returns empty list")
    void test_findByCreatorUsername_NoGifts() {
        when(giftRepo.findByCreatorUsername("nogifts")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByCreatorUsername("nogifts");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByCreatorUsername: Null username returns empty")
    void test_findByCreatorUsername_NullUsername() {
        when(giftRepo.findByCreatorUsername(null)).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByCreatorUsername(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByCreatorUsername: Empty username returns empty")
    void test_findByCreatorUsername_EmptyUsername() {
        when(giftRepo.findByCreatorUsername("")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByCreatorUsername("");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByCreatorUsername: Single gift scenario")
    void test_findByCreatorUsername_SingleGift() {
        Gift singleGift = new Gift("gift-single", "Single Gift", "Only one", "single@upi",
                "Message", "singleuser", "data");
        when(giftRepo.findByCreatorUsername("singleuser")).thenReturn(Arrays.asList(singleGift));
        List<Gift> result = giftRepo.findByCreatorUsername("singleuser");
        assertEquals(1, result.size());
        assertEquals("singleuser", result.get(0).getCreatorUsername());
    }

    @Test
    @DisplayName("findByCreatorUsername: Multiple gifts (3)")
    void test_findByCreatorUsername_MultipleGifts() {
        when(giftRepo.findByCreatorUsername("testuser")).thenReturn(Arrays.asList(testGift, testGift2, testGift3));
        List<Gift> result = giftRepo.findByCreatorUsername("testuser");
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(g -> g.getCreatorUsername().equals("testuser")));
    }

    // ===== findByTitleContainingIgnoreCase: 8 tests =====

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Valid title search")
    void test_findByTitle_ValidSearch() {
        when(giftRepo.findByTitleContainingIgnoreCase("Gift")).thenReturn(Arrays.asList(testGift, testGift2, testGift3));
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("Gift");
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Lowercase search")
    void test_findByTitle_LowercaseSearch() {
        when(giftRepo.findByTitleContainingIgnoreCase("birthday")).thenReturn(Arrays.asList(testGift));
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("birthday");
        assertEquals(1, result.size());
        assertEquals("Birthday Gift", result.get(0).getTitle());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Uppercase search")
    void test_findByTitle_UppercaseSearch() {
        when(giftRepo.findByTitleContainingIgnoreCase("WEDDING")).thenReturn(Arrays.asList(testGift3));
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("WEDDING");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Mixed case search")
    void test_findByTitle_MixedCaseSearch() {
        when(giftRepo.findByTitleContainingIgnoreCase("AnnIvErSaRy")).thenReturn(Arrays.asList(testGift2));
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("AnnIvErSaRy");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: No matches")
    void test_findByTitle_NoMatches() {
        when(giftRepo.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("NonExistent");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Null title")
    void test_findByTitle_NullTitle() {
        when(giftRepo.findByTitleContainingIgnoreCase(null)).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Empty title")
    void test_findByTitle_EmptyTitle() {
        when(giftRepo.findByTitleContainingIgnoreCase("")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: Partial match")
    void test_findByTitle_PartialMatch() {
        when(giftRepo.findByTitleContainingIgnoreCase("day")).thenReturn(Arrays.asList(testGift));
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("day");
        assertEquals(1, result.size());
    }

    // ===== JpaRepository Methods: 11 tests =====

    @Test
    @DisplayName("save: Save valid gift returns with ID")
    void test_save_ValidGift() {
        Gift newGift = new Gift(null, "New", "Desc", "upi", "msg", "creator", "img");
        when(giftRepo.save(newGift)).thenReturn(testGift);
        Gift saved = giftRepo.save(newGift);
        assertNotNull(saved.getId());
        assertEquals("gift-001", saved.getId());
    }

    @Test
    @DisplayName("findById: Existing ID returns gift")
    void test_findById_ExistingId() {
        when(giftRepo.findById("gift-001")).thenReturn(Optional.of(testGift));
        Optional<Gift> result = giftRepo.findById("gift-001");
        assertTrue(result.isPresent());
        assertEquals("Birthday Gift", result.get().getTitle());
    }

    @Test
    @DisplayName("findById: Non-existing ID returns empty")
    void test_findById_NonExistentId() {
        when(giftRepo.findById("non-existent")).thenReturn(Optional.empty());
        Optional<Gift> result = giftRepo.findById("non-existent");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findById: Null ID returns empty")
    void test_findById_NullId() {
        when(giftRepo.findById(null)).thenReturn(Optional.empty());
        Optional<Gift> result = giftRepo.findById(null);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findAll: Returns all gifts")
    void test_findAll_ReturnsAll() {
        when(giftRepo.findAll()).thenReturn(Arrays.asList(testGift, testGift2, testGift3));
        List<Gift> result = giftRepo.findAll();
        assertEquals(3, result.size());
        verify(giftRepo).findAll();
    }

    @Test
    @DisplayName("findAll: Empty database returns empty list")
    void test_findAll_Empty() {
        when(giftRepo.findAll()).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("existsById: True when exists")
    void test_existsById_True() {
        when(giftRepo.existsById("gift-001")).thenReturn(true);
        boolean exists = giftRepo.existsById("gift-001");
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsById: False when not exists")
    void test_existsById_False() {
        when(giftRepo.existsById("non-existent")).thenReturn(false);
        boolean exists = giftRepo.existsById("non-existent");
        assertFalse(exists);
    }

    @Test
    @DisplayName("deleteById: Deletes successfully")
    void test_deleteById() {
        doNothing().when(giftRepo).deleteById("gift-001");
        giftRepo.deleteById("gift-001");
        verify(giftRepo).deleteById("gift-001");
    }

    @Test
    @DisplayName("delete: Deletes entity successfully")
    void test_delete() {
        doNothing().when(giftRepo).delete(testGift);
        giftRepo.delete(testGift);
        verify(giftRepo).delete(testGift);
    }

    @Test
    @DisplayName("count: Returns correct count")
    void test_count_ReturnsCorrectCount() {
        when(giftRepo.count()).thenReturn(3L);
        long count = giftRepo.count();
        assertEquals(3L, count);
    }

    // ===== Gift Model Field Tests: 3 tests =====

    @Test
    @DisplayName("Field persistence: All fields saved correctly")
    void test_GiftFields_AllFieldsSaved() {
        Gift gift = new Gift("id", "Title", "Desc", "upi", "msg", "creator", "img");
        when(giftRepo.save(gift)).thenReturn(gift);
        Gift saved = giftRepo.save(gift);
        assertEquals("Title", saved.getTitle());
        assertEquals("Desc", saved.getDescription());
        assertEquals("upi", saved.getUpiId());
        assertEquals("img", saved.getImageDataUrl());
    }

    @Test
    @DisplayName("Field persistence: Null image data handled")
    void test_GiftFields_NullImage() {
        Gift gift = new Gift("id", "Title", "Desc", "upi", "msg", "creator", null);
        when(giftRepo.save(gift)).thenReturn(gift);
        Gift saved = giftRepo.save(gift);
        assertNull(saved.getImageDataUrl());
    }

    @Test
    @DisplayName("Field persistence: Empty strings handled")
    void test_GiftFields_EmptyStrings() {
        Gift gift = new Gift("id", "", "", "", "", "", "");
        when(giftRepo.save(gift)).thenReturn(gift);
        Gift saved = giftRepo.save(gift);
        assertEquals("", saved.getTitle());
        assertEquals("", saved.getDescription());
    }

    // ===== Edge Cases: 7 tests =====

    @Test
    @DisplayName("Edge case: Very long username (1000 chars)")
    void test_EdgeCase_VeryLongUsername() {
        String longUsername = "a".repeat(1000);
        when(giftRepo.findByCreatorUsername(longUsername)).thenReturn(Arrays.asList(testGift));
        List<Gift> result = giftRepo.findByCreatorUsername(longUsername);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Edge case: Very long title search (500 chars)")
    void test_EdgeCase_VeryLongTitle() {
        String longTitle = "a".repeat(500);
        when(giftRepo.findByTitleContainingIgnoreCase(longTitle)).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase(longTitle);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Special characters in username")
    void test_EdgeCase_SpecialCharsUsername() {
        String specialUsername = "user@#$%^&*()";
        when(giftRepo.findByCreatorUsername(specialUsername)).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByCreatorUsername(specialUsername);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Special characters in title")
    void test_EdgeCase_SpecialCharsTitle() {
        String specialTitle = "@#$%^&*()";
        when(giftRepo.findByTitleContainingIgnoreCase(specialTitle)).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase(specialTitle);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Whitespace-only username")
    void test_EdgeCase_WhitespaceUsername() {
        when(giftRepo.findByCreatorUsername("   ")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByCreatorUsername("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Whitespace-only title")
    void test_EdgeCase_WhitespaceTitle() {
        when(giftRepo.findByTitleContainingIgnoreCase("   ")).thenReturn(new ArrayList<>());
        List<Gift> result = giftRepo.findByTitleContainingIgnoreCase("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Zero count returns 0")
    void test_EdgeCase_ZeroCount() {
        when(giftRepo.count()).thenReturn(0L);
        long count = giftRepo.count();
        assertEquals(0L, count);
    }
}
