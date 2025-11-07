package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Gift Entity Test Suite")
class GiftTest {

    @InjectMocks
    private Gift gift;

    private static final String TEST_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String TEST_TITLE = "Birthday Fund";
    private static final String TEST_DESCRIPTION = "Help celebrate my birthday";
    private static final String TEST_UPI_ID = "user@paytm";
    private static final String TEST_UPI_MSG = "Birthday gift";
    private static final String TEST_CREATOR_USERNAME = "john_doe";
    private static final String TEST_IMAGE_DATA_URL = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";

    @BeforeEach
    void setUp() {
        // Mockito annotations are processed by ExtendWith(MockitoExtension.class)
        // InjectMocks creates a new Gift instance for each test
        gift = new Gift();
    }

    // ============= Constructor Tests =============

    @Test
    @DisplayName("Should create Gift with no-arg constructor - all fields null")
    void testNoArgConstructor() {
        // Arrange & Act
        Gift testGift = new Gift();

        // Assert
        assertNull(testGift.getId());
        assertNull(testGift.getTitle());
        assertNull(testGift.getDescription());
        assertNull(testGift.getUpiId());
        assertNull(testGift.getUpiMsg());
        assertNull(testGift.getCreatorUsername());
        assertNull(testGift.getImageDataUrl());
    }

    @Test
    @DisplayName("Should create Gift with all-arg constructor - valid values")
    void testAllArgConstructorWithValidValues() {
        // Arrange & Act
        Gift testGift = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Assert
        assertEquals(TEST_ID, testGift.getId());
        assertEquals(TEST_TITLE, testGift.getTitle());
        assertEquals(TEST_DESCRIPTION, testGift.getDescription());
        assertEquals(TEST_UPI_ID, testGift.getUpiId());
        assertEquals(TEST_UPI_MSG, testGift.getUpiMsg());
        assertEquals(TEST_CREATOR_USERNAME, testGift.getCreatorUsername());
        assertEquals(TEST_IMAGE_DATA_URL, testGift.getImageDataUrl());
    }

    @Test
    @DisplayName("Should create Gift with all-arg constructor - all null values")
    void testAllArgConstructorWithNullValues() {
        // Arrange & Act
        Gift testGift = new Gift(null, null, null, null, null, null, null);

        // Assert
        assertNull(testGift.getId());
        assertNull(testGift.getTitle());
        assertNull(testGift.getDescription());
        assertNull(testGift.getUpiId());
        assertNull(testGift.getUpiMsg());
        assertNull(testGift.getCreatorUsername());
        assertNull(testGift.getImageDataUrl());
    }

    @Test
    @DisplayName("Should create Gift with all-arg constructor - empty strings")
    void testAllArgConstructorWithEmptyStrings() {
        // Arrange & Act
        Gift testGift = new Gift("", "", "", "", "", "", "");

        // Assert
        assertEquals("", testGift.getId());
        assertEquals("", testGift.getTitle());
        assertEquals("", testGift.getDescription());
        assertEquals("", testGift.getUpiId());
        assertEquals("", testGift.getUpiMsg());
        assertEquals("", testGift.getCreatorUsername());
        assertEquals("", testGift.getImageDataUrl());
    }

    @Test
    @DisplayName("Should create Gift with all-arg constructor - mixed null and values")
    void testAllArgConstructorWithMixedNullAndValues() {
        // Arrange & Act
        Gift testGift = new Gift(TEST_ID, null, TEST_DESCRIPTION,
                null, TEST_UPI_MSG, null, TEST_IMAGE_DATA_URL);

        // Assert
        assertEquals(TEST_ID, testGift.getId());
        assertNull(testGift.getTitle());
        assertEquals(TEST_DESCRIPTION, testGift.getDescription());
        assertNull(testGift.getUpiId());
        assertEquals(TEST_UPI_MSG, testGift.getUpiMsg());
        assertNull(testGift.getCreatorUsername());
        assertEquals(TEST_IMAGE_DATA_URL, testGift.getImageDataUrl());
    }

    // ============= Getter Tests =============

    @Test
    @DisplayName("Should get ID correctly")
    void testGetId() {
        // Arrange
        gift.setId(TEST_ID);

        // Act
        String result = gift.getId();

        // Assert
        assertEquals(TEST_ID, result);
    }

    @Test
    @DisplayName("Should get Title correctly")
    void testGetTitle() {
        // Arrange
        gift.setTitle(TEST_TITLE);

        // Act
        String result = gift.getTitle();

        // Assert
        assertEquals(TEST_TITLE, result);
    }

    @Test
    @DisplayName("Should get Description correctly")
    void testGetDescription() {
        // Arrange
        gift.setDescription(TEST_DESCRIPTION);

        // Act
        String result = gift.getDescription();

        // Assert
        assertEquals(TEST_DESCRIPTION, result);
    }

    @Test
    @DisplayName("Should get UPI ID correctly")
    void testGetUpiId() {
        // Arrange
        gift.setUpiId(TEST_UPI_ID);

        // Act
        String result = gift.getUpiId();

        // Assert
        assertEquals(TEST_UPI_ID, result);
    }

    @Test
    @DisplayName("Should get UPI Message correctly")
    void testGetUpiMsg() {
        // Arrange
        gift.setUpiMsg(TEST_UPI_MSG);

        // Act
        String result = gift.getUpiMsg();

        // Assert
        assertEquals(TEST_UPI_MSG, result);
    }

    @Test
    @DisplayName("Should get Creator Username correctly")
    void testGetCreatorUsername() {
        // Arrange
        gift.setCreatorUsername(TEST_CREATOR_USERNAME);

        // Act
        String result = gift.getCreatorUsername();

        // Assert
        assertEquals(TEST_CREATOR_USERNAME, result);
    }

    @Test
    @DisplayName("Should get Image Data URL correctly")
    void testGetImageDataUrl() {
        // Arrange
        gift.setImageDataUrl(TEST_IMAGE_DATA_URL);

        // Act
        String result = gift.getImageDataUrl();

        // Assert
        assertEquals(TEST_IMAGE_DATA_URL, result);
    }

    // ============= Setter Tests =============

    @Test
    @DisplayName("Should set ID correctly")
    void testSetId() {
        // Arrange & Act
        gift.setId(TEST_ID);

        // Assert
        assertEquals(TEST_ID, gift.getId());
    }

    @Test
    @DisplayName("Should set Title correctly")
    void testSetTitle() {
        // Arrange & Act
        gift.setTitle(TEST_TITLE);

        // Assert
        assertEquals(TEST_TITLE, gift.getTitle());
    }

    @Test
    @DisplayName("Should set Description correctly")
    void testSetDescription() {
        // Arrange & Act
        gift.setDescription(TEST_DESCRIPTION);

        // Assert
        assertEquals(TEST_DESCRIPTION, gift.getDescription());
    }

    @Test
    @DisplayName("Should set UPI ID correctly")
    void testSetUpiId() {
        // Arrange & Act
        gift.setUpiId(TEST_UPI_ID);

        // Assert
        assertEquals(TEST_UPI_ID, gift.getUpiId());
    }

    @Test
    @DisplayName("Should set UPI Message correctly")
    void testSetUpiMsg() {
        // Arrange & Act
        gift.setUpiMsg(TEST_UPI_MSG);

        // Assert
        assertEquals(TEST_UPI_MSG, gift.getUpiMsg());
    }

    @Test
    @DisplayName("Should set Creator Username correctly")
    void testSetCreatorUsername() {
        // Arrange & Act
        gift.setCreatorUsername(TEST_CREATOR_USERNAME);

        // Assert
        assertEquals(TEST_CREATOR_USERNAME, gift.getCreatorUsername());
    }

    @Test
    @DisplayName("Should set Image Data URL correctly")
    void testSetImageDataUrl() {
        // Arrange & Act
        gift.setImageDataUrl(TEST_IMAGE_DATA_URL);

        // Assert
        assertEquals(TEST_IMAGE_DATA_URL, gift.getImageDataUrl());
    }

    // ============= Multiple Setters Chaining Tests =============

    @Test
    @DisplayName("Should handle multiple setters sequentially")
    void testMultipleSettersSequential() {
        // Arrange & Act
        gift.setId(TEST_ID);
        gift.setTitle(TEST_TITLE);
        gift.setDescription(TEST_DESCRIPTION);
        gift.setUpiId(TEST_UPI_ID);
        gift.setUpiMsg(TEST_UPI_MSG);
        gift.setCreatorUsername(TEST_CREATOR_USERNAME);
        gift.setImageDataUrl(TEST_IMAGE_DATA_URL);

        // Assert
        assertEquals(TEST_ID, gift.getId());
        assertEquals(TEST_TITLE, gift.getTitle());
        assertEquals(TEST_DESCRIPTION, gift.getDescription());
        assertEquals(TEST_UPI_ID, gift.getUpiId());
        assertEquals(TEST_UPI_MSG, gift.getUpiMsg());
        assertEquals(TEST_CREATOR_USERNAME, gift.getCreatorUsername());
        assertEquals(TEST_IMAGE_DATA_URL, gift.getImageDataUrl());
    }

    // ============= Null Value Tests =============

    @Test
    @DisplayName("Should handle null ID when getting")
    void testGetNullId() {
        // Arrange
        gift.setId(null);

        // Act
        String result = gift.getId();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Title when getting")
    void testGetNullTitle() {
        // Arrange
        gift.setTitle(null);

        // Act
        String result = gift.getTitle();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Description when getting")
    void testGetNullDescription() {
        // Arrange
        gift.setDescription(null);

        // Act
        String result = gift.getDescription();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null UPI ID when getting")
    void testGetNullUpiId() {
        // Arrange
        gift.setUpiId(null);

        // Act
        String result = gift.getUpiId();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null UPI Message when getting")
    void testGetNullUpiMsg() {
        // Arrange
        gift.setUpiMsg(null);

        // Act
        String result = gift.getUpiMsg();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Creator Username when getting")
    void testGetNullCreatorUsername() {
        // Arrange
        gift.setCreatorUsername(null);

        // Act
        String result = gift.getCreatorUsername();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Image Data URL when getting")
    void testGetNullImageDataUrl() {
        // Arrange
        gift.setImageDataUrl(null);

        // Act
        String result = gift.getImageDataUrl();

        // Assert
        assertNull(result);
    }

    // ============= Empty String Tests =============

    @Test
    @DisplayName("Should handle empty ID string")
    void testEmptyIdString() {
        // Arrange & Act
        gift.setId("");

        // Assert
        assertEquals("", gift.getId());
    }

    @Test
    @DisplayName("Should handle empty Title string")
    void testEmptyTitleString() {
        // Arrange & Act
        gift.setTitle("");

        // Assert
        assertEquals("", gift.getTitle());
    }

    @Test
    @DisplayName("Should handle empty Description string")
    void testEmptyDescriptionString() {
        // Arrange & Act
        gift.setDescription("");

        // Assert
        assertEquals("", gift.getDescription());
    }

    @Test
    @DisplayName("Should handle empty UPI ID string")
    void testEmptyUpiIdString() {
        // Arrange & Act
        gift.setUpiId("");

        // Assert
        assertEquals("", gift.getUpiId());
    }

    // ============= Special Characters & Unicode Tests =============

    @Test
    @DisplayName("Should handle special characters in Title")
    void testSpecialCharactersInTitle() {
        // Arrange
        String titleWithSpecialChars = "Birthday Fund @#$%^&*()!";

        // Act
        gift.setTitle(titleWithSpecialChars);

        // Assert
        assertEquals(titleWithSpecialChars, gift.getTitle());
    }

    @Test
    @DisplayName("Should handle unicode characters in Description")
    void testUnicodeCharactersInDescription() {
        // Arrange
        String descriptionWithUnicode = "Help celebrate my 🎂 birthday 🎉 in Hindi: जन्मदिन";

        // Act
        gift.setDescription(descriptionWithUnicode);

        // Assert
        assertEquals(descriptionWithUnicode, gift.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in UPI Message")
    void testSpecialCharactersInUpiMsg() {
        // Arrange
        String upiMsgWithSpecialChars = "Birthday gift - ₹500/- | \"Thank you\"";

        // Act
        gift.setUpiMsg(upiMsgWithSpecialChars);

        // Assert
        assertEquals(upiMsgWithSpecialChars, gift.getUpiMsg());
    }

    // ============= Long String Tests (LONGTEXT Field) =============

    @Test
    @DisplayName("Should handle very long Image Data URL (LONGTEXT field)")
    void testVeryLongImageDataUrl() {
        // Arrange
        StringBuilder longImageUrl = new StringBuilder("data:image/png;base64,");
        for (int i = 0; i < 100000; i++) {
            longImageUrl.append("ABCDEFGHIJKLMNOP");
        }

        // Act
        gift.setImageDataUrl(longImageUrl.toString());

        // Assert
        assertEquals(longImageUrl.toString(), gift.getImageDataUrl());
        assertTrue(gift.getImageDataUrl().length() > 100000);
    }

    @Test
    @DisplayName("Should handle whitespace-only Image Data URL")
    void testWhitespaceOnlyImageDataUrl() {
        // Arrange
        String whitespaceUrl = "   \n\t   ";

        // Act
        gift.setImageDataUrl(whitespaceUrl);

        // Assert
        assertEquals(whitespaceUrl, gift.getImageDataUrl());
    }

    // ============= Lombok Generated Methods - Equals Tests =============

    @Test
    @DisplayName("Should return true when comparing two Gift objects with same values")
    void testEqualsWithSameValues() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertEquals(gift1, gift2);
    }

    @Test
    @DisplayName("Should return true when comparing same Gift object (reflexive)")
    void testEqualsReflexive() {
        // Arrange
        gift.setId(TEST_ID);
        gift.setTitle(TEST_TITLE);

        // Act & Assert
        assertEquals(gift, gift);
    }

    @Test
    @DisplayName("Should return false when comparing Gift with null")
    void testEqualsWithNull() {
        // Arrange
        gift.setId(TEST_ID);
        gift.setTitle(TEST_TITLE);

        // Act & Assert
        assertNotEquals(gift, null);
    }

    @Test
    @DisplayName("Should return false when comparing Gift with different object type")
    void testEqualsWithDifferentType() {
        // Arrange
        gift.setId(TEST_ID);

        // Act & Assert
        assertNotEquals(gift, "Not a Gift");
    }

    @Test
    @DisplayName("Should return false when comparing Gift objects with different IDs")
    void testEqualsWithDifferentId() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift("different-id", TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertNotEquals(gift1, gift2);
    }

    @Test
    @DisplayName("Should return false when comparing Gift objects with different Titles")
    void testEqualsWithDifferentTitle() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift(TEST_ID, "Different Title", TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertNotEquals(gift1, gift2);
    }

    @Test
    @DisplayName("Should return false when comparing Gift objects with different UPI IDs")
    void testEqualsWithDifferentUpiId() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                "different@upi", TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertNotEquals(gift1, gift2);
    }

    // ============= Lombok Generated Methods - HashCode Tests =============

    @Test
    @DisplayName("Should return same hashCode for two Gift objects with same values")
    void testHashCodeWithSameValues() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertEquals(gift1.hashCode(), gift2.hashCode());
    }

    @Test
    @DisplayName("Should return same hashCode for same Gift object")
    void testHashCodeConsistency() {
        // Arrange
        gift.setId(TEST_ID);
        gift.setTitle(TEST_TITLE);

        // Act
        int hashCode1 = gift.hashCode();
        int hashCode2 = gift.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should return different hashCode for Gift objects with different IDs")
    void testHashCodeWithDifferentId() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift("different-id", TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);

        // Act & Assert
        assertNotEquals(gift1.hashCode(), gift2.hashCode());
    }

    // ============= Lombok Generated Methods - ToString Tests =============

    @Test
    @DisplayName("Should generate toString with all field values")
    void testToStringWithAllValues() {
        // Arrange
        gift.setId(TEST_ID);
        gift.setTitle(TEST_TITLE);
        gift.setDescription(TEST_DESCRIPTION);
        gift.setUpiId(TEST_UPI_ID);
        gift.setUpiMsg(TEST_UPI_MSG);
        gift.setCreatorUsername(TEST_CREATOR_USERNAME);
        gift.setImageDataUrl(TEST_IMAGE_DATA_URL);

        // Act
        String result = gift.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(TEST_TITLE));
        assertTrue(result.contains(TEST_UPI_ID));
        assertTrue(result.contains(TEST_CREATOR_USERNAME));
    }

    @Test
    @DisplayName("Should generate toString with null values")
    void testToStringWithNullValues() {
        // Arrange
        gift.setId(null);
        gift.setTitle(null);

        // Act
        String result = gift.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Gift"));
    }

    @Test
    @DisplayName("Should generate different toString for different objects")
    void testToStringDifferentForDifferentObjects() {
        // Arrange
        Gift gift1 = new Gift(TEST_ID, TEST_TITLE, TEST_DESCRIPTION,
                TEST_UPI_ID, TEST_UPI_MSG, TEST_CREATOR_USERNAME,
                TEST_IMAGE_DATA_URL);
        Gift gift2 = new Gift("different-id", "different-title", "different-description",
                "different@upi", "different-msg", "different-user",
                "different-url");

        // Act
        String result1 = gift1.toString();
        String result2 = gift2.toString();

        // Assert
        assertNotEquals(result1, result2);
    }

    // ============= Boundary Condition Tests =============

    @Test
    @DisplayName("Should handle single character Title")
    void testSingleCharacterTitle() {
        // Arrange & Act
        gift.setTitle("A");

        // Assert
        assertEquals("A", gift.getTitle());
    }

    @Test
    @DisplayName("Should handle very long Creator Username")
    void testVeryLongCreatorUsername() {
        // Arrange
        StringBuilder longUsername = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longUsername.append("A");
        }

        // Act
        gift.setCreatorUsername(longUsername.toString());

        // Assert
        assertEquals(longUsername.toString(), gift.getCreatorUsername());
        assertTrue(gift.getCreatorUsername().length() == 1000);
    }

    @Test
    @DisplayName("Should handle numeric-only UPI ID")
    void testNumericOnlyUpiId() {
        // Arrange
        String numericUpiId = "1234567890@upi";

        // Act
        gift.setUpiId(numericUpiId);

        // Assert
        assertEquals(numericUpiId, gift.getUpiId());
    }

    // ============= State Mutation Tests =============

    @Test
    @DisplayName("Should update Title multiple times")
    void testUpdateTitleMultipleTimes() {
        // Arrange & Act
        gift.setTitle("First Title");
        assertEquals("First Title", gift.getTitle());

        gift.setTitle("Second Title");
        assertEquals("Second Title", gift.getTitle());

        gift.setTitle("Third Title");

        // Assert
        assertEquals("Third Title", gift.getTitle());
    }

    @Test
    @DisplayName("Should transition from null to value to null")
    void testNullToValueToNullTransition() {
        // Arrange & Act
        assertNull(gift.getDescription());

        gift.setDescription(TEST_DESCRIPTION);
        assertEquals(TEST_DESCRIPTION, gift.getDescription());

        gift.setDescription(null);

        // Assert
        assertNull(gift.getDescription());
    }

    // ============= Equivalence Partitioning Tests =============

    @Test
    @DisplayName("Should handle email-format UPI ID")
    void testEmailFormatUpiId() {
        // Arrange
        String emailFormatUpi = "user.name@paytm";

        // Act
        gift.setUpiId(emailFormatUpi);

        // Assert
        assertEquals(emailFormatUpi, gift.getUpiId());
    }

    @Test
    @DisplayName("Should handle URL-format Image Data")
    void testUrlFormatImageData() {
        // Arrange
        String urlImage = "https://example.com/image.png";

        // Act
        gift.setImageDataUrl(urlImage);

        // Assert
        assertEquals(urlImage, gift.getImageDataUrl());
    }

    @Test
    @DisplayName("Should handle base64-encoded Image Data")
    void testBase64EncodedImageData() {
        // Arrange
        String base64Image = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD";

        // Act
        gift.setImageDataUrl(base64Image);

        // Assert
        assertEquals(base64Image, gift.getImageDataUrl());
    }
}
