package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test class for Donation entity with Mockito
 * Follows AAA (Arrange-Act-Assert) pattern and covers all code paths
 * Aims for 80%+ code coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Donation Entity Tests")
class DonationTest {

    @InjectMocks
    private Donation donation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        donation = new Donation();
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create Donation with default no-arg constructor")
    void testNoArgConstructor() {
        // Arrange & Act
        Donation newDonation = new Donation();

        // Assert
        assertNotNull(newDonation);
        assertNull(newDonation.getId());
        assertNull(newDonation.getTitle());
        assertNull(newDonation.getDescription());
        assertNull(newDonation.getUpiId());
        assertNull(newDonation.getUpiMsg());
        assertNull(newDonation.getCreatorUsername());
        assertNull(newDonation.getImageDataUrl());
        assertEquals(0, newDonation.getAmount());
    }

    @Test
    @DisplayName("Should create Donation with all-arg constructor")
    void testAllArgConstructor() {
        // Arrange
        String expectedId = "550e8400-e29b-41d4-a716-446655440000";
        String expectedTitle = "Help the Needy";
        String expectedDescription = "Raising funds for education";
        String expectedUpiId = "test@upi";
        String expectedUpiMsg = "Please donate";
        String expectedUsername = "john_doe";
        String expectedImageUrl = "data:image/png;base64,...";
        int expectedAmount = 5000;

        // Act
        Donation newDonation = new Donation(
                expectedId,
                expectedTitle,
                expectedDescription,
                expectedUpiId,
                expectedUpiMsg,
                expectedUsername,
                expectedImageUrl,
                expectedAmount
        );

        // Assert
        assertNotNull(newDonation);
        assertEquals(expectedId, newDonation.getId());
        assertEquals(expectedTitle, newDonation.getTitle());
        assertEquals(expectedDescription, newDonation.getDescription());
        assertEquals(expectedUpiId, newDonation.getUpiId());
        assertEquals(expectedUpiMsg, newDonation.getUpiMsg());
        assertEquals(expectedUsername, newDonation.getCreatorUsername());
        assertEquals(expectedImageUrl, newDonation.getImageDataUrl());
        assertEquals(expectedAmount, newDonation.getAmount());
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    @DisplayName("Should set and get ID correctly")
    void testIdGetterSetter() {
        // Arrange
        String testId = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        donation.setId(testId);

        // Assert
        assertEquals(testId, donation.getId());
    }

    @Test
    @DisplayName("Should handle null ID")
    void testIdSetterWithNull() {
        // Arrange & Act
        donation.setId(null);

        // Assert
        assertNull(donation.getId());
    }

    @Test
    @DisplayName("Should set and get title correctly")
    void testTitleGetterSetter() {
        // Arrange
        String testTitle = "Emergency Medical Fund";

        // Act
        donation.setTitle(testTitle);

        // Assert
        assertEquals(testTitle, donation.getTitle());
    }

    @Test
    @DisplayName("Should handle null title")
    void testTitleSetterWithNull() {
        // Arrange & Act
        donation.setTitle(null);

        // Assert
        assertNull(donation.getTitle());
    }

    @Test
    @DisplayName("Should handle empty title")
    void testTitleSetterWithEmpty() {
        // Arrange
        String emptyTitle = "";

        // Act
        donation.setTitle(emptyTitle);

        // Assert
        assertEquals("", donation.getTitle());
        assertTrue(donation.getTitle().isEmpty());
    }

    @Test
    @DisplayName("Should set and get description correctly")
    void testDescriptionGetterSetter() {
        // Arrange
        String testDescription = "This fund helps underprivileged students pursue higher education";

        // Act
        donation.setDescription(testDescription);

        // Assert
        assertEquals(testDescription, donation.getDescription());
    }

    @Test
    @DisplayName("Should handle null description")
    void testDescriptionSetterWithNull() {
        // Arrange & Act
        donation.setDescription(null);

        // Assert
        assertNull(donation.getDescription());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testDescriptionSetterWithEmpty() {
        // Arrange
        String emptyDescription = "";

        // Act
        donation.setDescription(emptyDescription);

        // Assert
        assertEquals("", donation.getDescription());
        assertTrue(donation.getDescription().isEmpty());
    }

    @Test
    @DisplayName("Should set and get UPI ID correctly")
    void testUpiIdGetterSetter() {
        // Arrange
        String testUpiId = "fundraiser@upi";

        // Act
        donation.setUpiId(testUpiId);

        // Assert
        assertEquals(testUpiId, donation.getUpiId());
    }

    @Test
    @DisplayName("Should handle null UPI ID")
    void testUpiIdSetterWithNull() {
        // Arrange & Act
        donation.setUpiId(null);

        // Assert
        assertNull(donation.getUpiId());
    }

    @Test
    @DisplayName("Should handle various UPI ID formats")
    void testUpiIdWithVariousFormats() {
        // Arrange & Act & Assert
        donation.setUpiId("user@okhdfcbank");
        assertEquals("user@okhdfcbank", donation.getUpiId());

        donation.setUpiId("test@ibl");
        assertEquals("test@ibl", donation.getUpiId());

        donation.setUpiId("donate@airtel");
        assertEquals("donate@airtel", donation.getUpiId());
    }

    @Test
    @DisplayName("Should set and get UPI message correctly")
    void testUpiMsgGetterSetter() {
        // Arrange
        String testUpiMsg = "Your contribution will change lives";

        // Act
        donation.setUpiMsg(testUpiMsg);

        // Assert
        assertEquals(testUpiMsg, donation.getUpiMsg());
    }

    @Test
    @DisplayName("Should handle null UPI message")
    void testUpiMsgSetterWithNull() {
        // Arrange & Act
        donation.setUpiMsg(null);

        // Assert
        assertNull(donation.getUpiMsg());
    }

    @Test
    @DisplayName("Should handle empty UPI message")
    void testUpiMsgSetterWithEmpty() {
        // Arrange
        String emptyUpiMsg = "";

        // Act
        donation.setUpiMsg(emptyUpiMsg);

        // Assert
        assertEquals("", donation.getUpiMsg());
        assertTrue(donation.getUpiMsg().isEmpty());
    }

    @Test
    @DisplayName("Should set and get creator username correctly")
    void testCreatorUsernameGetterSetter() {
        // Arrange
        String testUsername = "charity_warrior_2024";

        // Act
        donation.setCreatorUsername(testUsername);

        // Assert
        assertEquals(testUsername, donation.getCreatorUsername());
    }

    @Test
    @DisplayName("Should handle null creator username")
    void testCreatorUsernameSetterWithNull() {
        // Arrange & Act
        donation.setCreatorUsername(null);

        // Assert
        assertNull(donation.getCreatorUsername());
    }

    @Test
    @DisplayName("Should handle empty creator username")
    void testCreatorUsernameSetterWithEmpty() {
        // Arrange
        String emptyUsername = "";

        // Act
        donation.setCreatorUsername(emptyUsername);

        // Assert
        assertEquals("", donation.getCreatorUsername());
        assertTrue(donation.getCreatorUsername().isEmpty());
    }

    @Test
    @DisplayName("Should set and get image data URL correctly")
    void testImageDataUrlGetterSetter() {
        // Arrange
        String testImageUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";

        // Act
        donation.setImageDataUrl(testImageUrl);

        // Assert
        assertEquals(testImageUrl, donation.getImageDataUrl());
    }

    @Test
    @DisplayName("Should handle null image data URL")
    void testImageDataUrlSetterWithNull() {
        // Arrange & Act
        donation.setImageDataUrl(null);

        // Assert
        assertNull(donation.getImageDataUrl());
    }

    @Test
    @DisplayName("Should handle empty image data URL")
    void testImageDataUrlSetterWithEmpty() {
        // Arrange
        String emptyImageUrl = "";

        // Act
        donation.setImageDataUrl(emptyImageUrl);

        // Assert
        assertEquals("", donation.getImageDataUrl());
        assertTrue(donation.getImageDataUrl().isEmpty());
    }

    @Test
    @DisplayName("Should handle large image data URL")
    void testImageDataUrlWithLargeData() {
        // Arrange
        StringBuilder largeImageBuilder = new StringBuilder("data:image/png;base64,");
        for (int i = 0; i < 10000; i++) {
            largeImageBuilder.append("A");
        }
        String largeImageUrl = largeImageBuilder.toString();

        // Act
        donation.setImageDataUrl(largeImageUrl);

        // Assert
        assertEquals(largeImageUrl, donation.getImageDataUrl());
        assertTrue(donation.getImageDataUrl().length() > 10000);
    }

    @Test
    @DisplayName("Should set and get amount correctly")
    void testAmountGetterSetter() {
        // Arrange
        int testAmount = 10000;

        // Act
        donation.setAmount(testAmount);

        // Assert
        assertEquals(testAmount, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle zero amount")
    void testAmountSetterWithZero() {
        // Arrange & Act
        donation.setAmount(0);

        // Assert
        assertEquals(0, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle positive amount values")
    void testAmountSetterWithPositiveValues() {
        // Arrange & Act & Assert
        donation.setAmount(100);
        assertEquals(100, donation.getAmount());

        donation.setAmount(1000000);
        assertEquals(1000000, donation.getAmount());

        donation.setAmount(1);
        assertEquals(1, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle negative amount values")
    void testAmountSetterWithNegativeValues() {
        // Arrange & Act
        donation.setAmount(-500);

        // Assert
        assertEquals(-500, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle maximum integer amount")
    void testAmountSetterWithMaxInteger() {
        // Arrange
        int maxAmount = Integer.MAX_VALUE;

        // Act
        donation.setAmount(maxAmount);

        // Assert
        assertEquals(maxAmount, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle minimum integer amount")
    void testAmountSetterWithMinInteger() {
        // Arrange
        int minAmount = Integer.MIN_VALUE;

        // Act
        donation.setAmount(minAmount);

        // Assert
        assertEquals(minAmount, donation.getAmount());
    }

    // ==================== Multiple Field Tests ====================

    @Test
    @DisplayName("Should set and get all fields correctly together")
    void testAllFieldsSetterGetter() {
        // Arrange
        String id = "550e8400-e29b-41d4-a716-446655440000";
        String title = "Education Fund";
        String description = "Empowering students with quality education";
        String upiId = "education@upi";
        String upiMsg = "Support education";
        String username = "educator_2024";
        String imageUrl = "data:image/png;base64,ABC123";
        int amount = 50000;

        // Act
        donation.setId(id);
        donation.setTitle(title);
        donation.setDescription(description);
        donation.setUpiId(upiId);
        donation.setUpiMsg(upiMsg);
        donation.setCreatorUsername(username);
        donation.setImageDataUrl(imageUrl);
        donation.setAmount(amount);

        // Assert
        assertEquals(id, donation.getId());
        assertEquals(title, donation.getTitle());
        assertEquals(description, donation.getDescription());
        assertEquals(upiId, donation.getUpiId());
        assertEquals(upiMsg, donation.getUpiMsg());
        assertEquals(username, donation.getCreatorUsername());
        assertEquals(imageUrl, donation.getImageDataUrl());
        assertEquals(amount, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle all null fields simultaneously")
    void testAllFieldsNull() {
        // Arrange & Act
        donation.setId(null);
        donation.setTitle(null);
        donation.setDescription(null);
        donation.setUpiId(null);
        donation.setUpiMsg(null);
        donation.setCreatorUsername(null);
        donation.setImageDataUrl(null);
        donation.setAmount(0);

        // Assert
        assertNull(donation.getId());
        assertNull(donation.getTitle());
        assertNull(donation.getDescription());
        assertNull(donation.getUpiId());
        assertNull(donation.getUpiMsg());
        assertNull(donation.getCreatorUsername());
        assertNull(donation.getImageDataUrl());
        assertEquals(0, donation.getAmount());
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("Should handle special characters in title")
    void testTitleWithSpecialCharacters() {
        // Arrange
        String specialTitle = "Help for 🎓 Students @2024!#$%";

        // Act
        donation.setTitle(specialTitle);

        // Assert
        assertEquals(specialTitle, donation.getTitle());
    }

    @Test
    @DisplayName("Should handle special characters in description")
    void testDescriptionWithSpecialCharacters() {
        // Arrange
        String specialDescription = "Funds for <html>disabled</html> & \"vulnerable\" 'children'";

        // Act
        donation.setDescription(specialDescription);

        // Assert
        assertEquals(specialDescription, donation.getDescription());
    }

    @Test
    @DisplayName("Should handle very long strings in all fields")
    void testVeryLongStrings() {
        // Arrange
        String longString = "A".repeat(5000);

        // Act
        donation.setTitle(longString);
        donation.setDescription(longString);
        donation.setUpiMsg(longString);
        donation.setCreatorUsername(longString);

        // Assert
        assertEquals(longString, donation.getTitle());
        assertEquals(longString, donation.getDescription());
        assertEquals(longString, donation.getUpiMsg());
        assertEquals(longString, donation.getCreatorUsername());
    }

    @Test
    @DisplayName("Should handle whitespace-only strings correctly")
    void testWhitespaceOnlyStrings() {
        // Arrange
        String whitespace = "   \t\n   ";

        // Act
        donation.setTitle(whitespace);
        donation.setDescription(whitespace);

        // Assert
        assertEquals(whitespace, donation.getTitle());
        assertEquals(whitespace, donation.getDescription());
        assertTrue(donation.getTitle().trim().isEmpty());
        assertTrue(donation.getDescription().trim().isEmpty());
    }

    @Test
    @DisplayName("Should handle Unicode characters in text fields")
    void testUnicodeCharactersInFields() {
        // Arrange
        String unicodeTitle = "धर्मार्थ निधि - विश्व आबादी की मदद";
        String unicodeDescription = "援助资金 - 帮助全世界";
        String unicodeUsername = "ユーザー_2024";

        // Act
        donation.setTitle(unicodeTitle);
        donation.setDescription(unicodeDescription);
        donation.setCreatorUsername(unicodeUsername);

        // Assert
        assertEquals(unicodeTitle, donation.getTitle());
        assertEquals(unicodeDescription, donation.getDescription());
        assertEquals(unicodeUsername, donation.getCreatorUsername());
    }

    // ==================== State Mutation Tests ====================

    @Test
    @DisplayName("Should properly update existing field values")
    void testFieldValueMutation() {
        // Arrange
        String initialTitle = "First Title";
        String updatedTitle = "Updated Title";

        // Act
        donation.setTitle(initialTitle);
        assertEquals(initialTitle, donation.getTitle());
        donation.setTitle(updatedTitle);

        // Assert
        assertEquals(updatedTitle, donation.getTitle());
        assertNotEquals(initialTitle, donation.getTitle());
    }

    @Test
    @DisplayName("Should handle repeated updates to same field")
    void testRepeatedFieldUpdates() {
        // Arrange & Act & Assert
        for (int i = 0; i < 100; i++) {
            String value = "Amount_" + i;
            donation.setTitle(value);
            assertEquals(value, donation.getTitle());
        }
    }

    // ==================== Boundary Value Tests ====================

    @Test
    @DisplayName("Should handle single character strings")
    void testSingleCharacterStrings() {
        // Arrange
        String singleChar = "A";

        // Act
        donation.setTitle(singleChar);
        donation.setDescription(singleChar);

        // Assert
        assertEquals(singleChar, donation.getTitle());
        assertEquals(singleChar, donation.getDescription());
        assertEquals(1, donation.getTitle().length());
    }

    @Test
    @DisplayName("Should handle amount boundary at zero")
    void testAmountBoundaryAtZero() {
        // Arrange & Act
        donation.setAmount(0);

        // Assert
        assertEquals(0, donation.getAmount());
    }

    @Test
    @DisplayName("Should distinguish between positive and negative amounts")
    void testAmountSignificance() {
        // Arrange & Act
        donation.setAmount(100);
        int positiveAmount = donation.getAmount();

        donation.setAmount(-100);
        int negativeAmount = donation.getAmount();

        // Assert
        assertTrue(positiveAmount > 0);
        assertTrue(negativeAmount < 0);
        assertNotEquals(positiveAmount, negativeAmount);
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Should maintain field independence - changing one field should not affect others")
    void testFieldIndependence() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        int amount = 1000;

        // Act
        donation.setTitle(title);
        donation.setDescription(description);
        donation.setAmount(amount);

        // Modify one field
        donation.setTitle("New Title");

        // Assert
        assertEquals("New Title", donation.getTitle());
        assertEquals(description, donation.getDescription());
        assertEquals(amount, donation.getAmount());
    }

    @Test
    @DisplayName("Should preserve state across multiple operations")
    void testStatePreservation() {
        // Arrange
        String expectedId = "test-id-123";
        String expectedTitle = "Community Fund";
        int expectedAmount = 75000;

        // Act
        donation.setId(expectedId);
        donation.setTitle(expectedTitle);
        String titleCheck1 = donation.getTitle();

        donation.setAmount(expectedAmount);
        String titleCheck2 = donation.getTitle();

        // Assert
        assertEquals(expectedTitle, titleCheck1);
        assertEquals(expectedTitle, titleCheck2);
        assertEquals(expectedId, donation.getId());
        assertEquals(expectedAmount, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle complete object lifecycle")
    void testCompleteObjectLifecycle() {
        // Arrange - Create empty object
        Donation lifecycleDonation = new Donation();
        assertNull(lifecycleDonation.getId());

        // Act - Initialize all fields
        lifecycleDonation.setId("lifecycle-id");
        lifecycleDonation.setTitle("Lifecycle Test");
        lifecycleDonation.setDescription("Testing complete lifecycle");
        lifecycleDonation.setUpiId("lifecycle@upi");
        lifecycleDonation.setUpiMsg("Support lifecycle");
        lifecycleDonation.setCreatorUsername("lifecycle_user");
        lifecycleDonation.setImageDataUrl("data:image/png;base64,test");
        lifecycleDonation.setAmount(5000);

        // Assert - Verify all fields are set
        assertNotNull(lifecycleDonation.getId());
        assertNotNull(lifecycleDonation.getTitle());
        assertNotNull(lifecycleDonation.getDescription());
        assertNotNull(lifecycleDonation.getUpiId());
        assertNotNull(lifecycleDonation.getUpiMsg());
        assertNotNull(lifecycleDonation.getCreatorUsername());
        assertNotNull(lifecycleDonation.getImageDataUrl());
        assertTrue(lifecycleDonation.getAmount() > 0);

        // Act - Clear fields
        lifecycleDonation.setId(null);
        lifecycleDonation.setTitle(null);

        // Assert - Verify fields are cleared
        assertNull(lifecycleDonation.getId());
        assertNull(lifecycleDonation.getTitle());
        assertNotNull(lifecycleDonation.getDescription()); // Others remain unchanged
    }

    @Test
    @DisplayName("Should create distinct instances with no shared state")
    void testInstanceIndependence() {
        // Arrange
        Donation donation1 = new Donation();
        Donation donation2 = new Donation();

        // Act
        donation1.setTitle("Donation 1");
        donation2.setTitle("Donation 2");
        donation1.setAmount(1000);
        donation2.setAmount(2000);

        // Assert
        assertEquals("Donation 1", donation1.getTitle());
        assertEquals("Donation 2", donation2.getTitle());
        assertEquals(1000, donation1.getAmount());
        assertEquals(2000, donation2.getAmount());
        assertNotEquals(donation1.getTitle(), donation2.getTitle());
    }

    @Test
    @DisplayName("Should handle sequential field assignments")
    void testSequentialFieldAssignments() {
        // Arrange & Act
        donation.setId("id-1");
        donation.setTitle("Title 1");
        donation.setDescription("Desc 1");
        donation.setUpiId("upi1@bank");
        donation.setUpiMsg("Msg 1");
        donation.setCreatorUsername("user1");
        donation.setImageDataUrl("image1");
        donation.setAmount(1000);

        // Assert each assignment
        assertEquals("id-1", donation.getId());
        assertEquals("Title 1", donation.getTitle());
        assertEquals("Desc 1", donation.getDescription());
        assertEquals("upi1@bank", donation.getUpiId());
        assertEquals("Msg 1", donation.getUpiMsg());
        assertEquals("user1", donation.getCreatorUsername());
        assertEquals("image1", donation.getImageDataUrl());
        assertEquals(1000, donation.getAmount());
    }

    @Test
    @DisplayName("Should handle null-to-value transitions")
    void testNullToValueTransitions() {
        // Arrange
        donation.setTitle(null);
        assertNull(donation.getTitle());

        // Act
        donation.setTitle("New Title");

        // Assert
        assertEquals("New Title", donation.getTitle());
        assertNotNull(donation.getTitle());
    }

    @Test
    @DisplayName("Should handle value-to-null transitions")
    void testValueToNullTransitions() {
        // Arrange
        donation.setTitle("Existing Title");
        assertEquals("Existing Title", donation.getTitle());

        // Act
        donation.setTitle(null);

        // Assert
        assertNull(donation.getTitle());
    }
}
