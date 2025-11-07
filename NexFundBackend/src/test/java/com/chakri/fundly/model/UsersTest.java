package com.chakri.fundly.model;

import com.chakri.fundly.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Users Model Tests - Comprehensive Coverage")
class UsersTest {

    private Users users;

    @BeforeEach
    void setUp() {
        // Initialize a fresh Users instance for each test
        users = new Users();
    }

    // ================== DEFAULT CONSTRUCTOR TESTS ==================

    @Test
    @DisplayName("Should create Users instance with default constructor")
    void testDefaultConstructor() {
        // Arrange
        Users user = new Users();

        // Act & Assert
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
        assertNull(user.getProviderId());
        assertNull(user.getName());
        assertNull(user.getProfileImage());
    }

    // ================== CONSTRUCTOR WITH USERNAME, EMAIL, PASSWORD ==================

    @Test
    @DisplayName("Should create Users with username, email, password constructor")
    void testConstructorWithBasicCredentials() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // Act
        Users user = new Users(username, email, password);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
        assertNull(user.getProviderId());
        assertNull(user.getName());
        assertNull(user.getProfileImage());
    }

    @Test
    @DisplayName("Should handle empty strings in basic credentials constructor")
    void testConstructorWithEmptyCredentials() {
        // Arrange
        String username = "";
        String email = "";
        String password = "";

        // Act
        Users user = new Users(username, email, password);

        // Assert
        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
    }

    @Test
    @DisplayName("Should handle null values in basic credentials constructor")
    void testConstructorWithNullCredentials() {
        // Arrange & Act
        Users user = new Users(null, null, null);

        // Assert
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
    }

    // ================== CONSTRUCTOR WITH OAUTH/PROVIDER ==================

    @Test
    @DisplayName("Should create Users with email, name, authProvider, providerId constructor for Google OAuth")
    void testConstructorWithGoogleOAuth() {
        // Arrange
        String email = "oauth@example.com";
        String name = "Google User";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        String providerId = "google123456";

        // Act
        Users user = new Users(email, name, authProvider, providerId);

        // Assert
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(name, user.getUsername()); // Username is set to name
        assertEquals(authProvider, user.getAuthProvider());
        assertEquals(providerId, user.getProviderId());
        assertNull(user.getPassword());
        assertNull(user.getProfileImage());
    }

    @Test
    @DisplayName("Should create Users with email, name, authProvider, providerId constructor for LOCAL")
    void testConstructorWithLocalAuthProvider() {
        // Arrange
        String email = "local@example.com";
        String name = "Local User";
        AuthProvider authProvider = AuthProvider.LOCAL;
        String providerId = "local123";

        // Act
        Users user = new Users(email, name, authProvider, providerId);

        // Assert
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(name, user.getUsername());
        assertEquals(authProvider, user.getAuthProvider());
        assertEquals(providerId, user.getProviderId());
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Should handle different AuthProvider enum values (LOCAL and GOOGLE)")
    void testConstructorWithDifferentAuthProviders() {
        // Arrange & Act & Assert
        for (AuthProvider provider : AuthProvider.values()) {
            Users user = new Users("email@test.com", "name", provider, "providerId");
            assertEquals(provider, user.getAuthProvider());
        }
    }

    @Test
    @DisplayName("Should handle null authProvider in OAuth constructor")
    void testConstructorWithNullAuthProvider() {
        // Arrange & Act
        Users user = new Users("email@test.com", "name", null, "providerId");

        // Assert
        assertNull(user.getAuthProvider());
    }

    @Test
    @DisplayName("Should handle null providerId in OAuth constructor")
    void testConstructorWithNullProviderId() {
        // Arrange & Act
        Users user = new Users("email@test.com", "name", AuthProvider.GOOGLE, null);

        // Assert
        assertNull(user.getProviderId());
    }

    @Test
    @DisplayName("Should handle empty strings in OAuth constructor")
    void testConstructorWithEmptyOAuthValues() {
        // Arrange & Act
        Users user = new Users("", "", AuthProvider.GOOGLE, "");

        // Assert
        assertEquals("", user.getEmail());
        assertEquals("", user.getName());
        assertEquals("", user.getUsername());
        assertEquals("", user.getProviderId());
    }

    // ================== ID GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get id correctly")
    void testSetAndGetId() {
        // Arrange
        int testId = 42;

        // Act
        users.setId(testId);
        int retrievedId = users.getId();

        // Assert
        assertEquals(testId, retrievedId);
    }

    @Test
    @DisplayName("Should handle zero id value")
    void testIdWithZeroValue() {
        // Arrange & Act
        users.setId(0);

        // Assert
        assertEquals(0, users.getId());
    }

    @Test
    @DisplayName("Should handle negative id value")
    void testIdWithNegativeValue() {
        // Arrange & Act
        users.setId(-1);

        // Assert
        assertEquals(-1, users.getId());
    }

    @Test
    @DisplayName("Should handle large id value")
    void testIdWithLargeValue() {
        // Arrange
        int largeId = Integer.MAX_VALUE;

        // Act
        users.setId(largeId);

        // Assert
        assertEquals(largeId, users.getId());
    }

    // ================== USERNAME GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get username correctly")
    void testSetAndGetUsername() {
        // Arrange
        String testUsername = "testuser123";

        // Act
        users.setUsername(testUsername);
        String retrievedUsername = users.getUsername();

        // Assert
        assertEquals(testUsername, retrievedUsername);
    }

    @Test
    @DisplayName("Should handle null username")
    void testUsernameWithNull() {
        // Arrange & Act
        users.setUsername(null);

        // Assert
        assertNull(users.getUsername());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testUsernameWithEmptyString() {
        // Arrange & Act
        users.setUsername("");

        // Assert
        assertEquals("", users.getUsername());
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void testUsernameWithSpecialCharacters() {
        // Arrange
        String specialUsername = "user@#$%_123";

        // Act
        users.setUsername(specialUsername);

        // Assert
        assertEquals(specialUsername, users.getUsername());
    }

    @Test
    @DisplayName("Should handle username with spaces")
    void testUsernameWithSpaces() {
        // Arrange
        String usernameWithSpaces = "user name with spaces";

        // Act
        users.setUsername(usernameWithSpaces);

        // Assert
        assertEquals(usernameWithSpaces, users.getUsername());
    }

    @Test
    @DisplayName("Should handle very long username")
    void testUsernameWithLongValue() {
        // Arrange
        String longUsername = "a".repeat(255);

        // Act
        users.setUsername(longUsername);

        // Assert
        assertEquals(longUsername, users.getUsername());
    }

    // ================== EMAIL GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get email correctly")
    void testSetAndGetEmail() {
        // Arrange
        String testEmail = "test@example.com";

        // Act
        users.setEmail(testEmail);
        String retrievedEmail = users.getEmail();

        // Assert
        assertEquals(testEmail, retrievedEmail);
    }

    @Test
    @DisplayName("Should handle null email")
    void testEmailWithNull() {
        // Arrange & Act
        users.setEmail(null);

        // Assert
        assertNull(users.getEmail());
    }

    @Test
    @DisplayName("Should handle empty email")
    void testEmailWithEmptyString() {
        // Arrange & Act
        users.setEmail("");

        // Assert
        assertEquals("", users.getEmail());
    }

    @Test
    @DisplayName("Should handle various email formats")
    void testEmailWithVariousFormats() {
        // Arrange
        String[] emails = {
                "user@domain.com",
                "user.name@domain.co.uk",
                "user+tag@domain.com",
                "user_name@domain-name.com",
                "123@456.789"
        };

        // Act & Assert
        for (String email : emails) {
            users.setEmail(email);
            assertEquals(email, users.getEmail());
        }
    }

    // ================== PASSWORD GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get password correctly")
    void testSetAndGetPassword() {
        // Arrange
        String testPassword = "securePassword123!";

        // Act
        users.setPassword(testPassword);
        String retrievedPassword = users.getPassword();

        // Assert
        assertEquals(testPassword, retrievedPassword);
    }

    @Test
    @DisplayName("Should handle null password")
    void testPasswordWithNull() {
        // Arrange & Act
        users.setPassword(null);

        // Assert
        assertNull(users.getPassword());
    }

    @Test
    @DisplayName("Should handle empty password")
    void testPasswordWithEmptyString() {
        // Arrange & Act
        users.setPassword("");

        // Assert
        assertEquals("", users.getPassword());
    }

    @Test
    @DisplayName("Should handle password with special characters")
    void testPasswordWithSpecialCharacters() {
        // Arrange
        String complexPassword = "P@$$w0rd!#%&*()";

        // Act
        users.setPassword(complexPassword);

        // Assert
        assertEquals(complexPassword, users.getPassword());
    }

    @Test
    @DisplayName("Should handle very long password")
    void testPasswordWithLongValue() {
        // Arrange
        String longPassword = "p".repeat(500);

        // Act
        users.setPassword(longPassword);

        // Assert
        assertEquals(longPassword, users.getPassword());
    }

    // ================== AUTH PROVIDER GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get authProvider correctly for GOOGLE")
    void testSetAndGetAuthProviderGoogle() {
        // Arrange
        AuthProvider testProvider = AuthProvider.GOOGLE;

        // Act
        users.setAuthProvider(testProvider);
        AuthProvider retrievedProvider = users.getAuthProvider();

        // Assert
        assertEquals(testProvider, retrievedProvider);
    }

    @Test
    @DisplayName("Should set and get authProvider correctly for LOCAL")
    void testSetAndGetAuthProviderLocal() {
        // Arrange
        AuthProvider testProvider = AuthProvider.LOCAL;

        // Act
        users.setAuthProvider(testProvider);
        AuthProvider retrievedProvider = users.getAuthProvider();

        // Assert
        assertEquals(testProvider, retrievedProvider);
    }

    @Test
    @DisplayName("Should handle all AuthProvider enum values (LOCAL and GOOGLE)")
    void testAuthProviderWithAllEnumValues() {
        // Arrange, Act & Assert
        for (AuthProvider provider : AuthProvider.values()) {
            users.setAuthProvider(provider);
            assertEquals(provider, users.getAuthProvider());
        }
    }

    @Test
    @DisplayName("Should handle null authProvider")
    void testAuthProviderWithNull() {
        // Arrange & Act
        users.setAuthProvider(null);

        // Assert
        assertNull(users.getAuthProvider());
    }

    @Test
    @DisplayName("Should default authProvider to LOCAL in default constructor")
    void testDefaultAuthProvider() {
        // Arrange & Act
        Users newUser = new Users();

        // Assert
        assertEquals(AuthProvider.LOCAL, newUser.getAuthProvider());
    }

    // ================== PROVIDER ID GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get providerId correctly")
    void testSetAndGetProviderId() {
        // Arrange
        String testProviderId = "provider_123456";

        // Act
        users.setProviderId(testProviderId);
        String retrievedProviderId = users.getProviderId();

        // Assert
        assertEquals(testProviderId, retrievedProviderId);
    }

    @Test
    @DisplayName("Should handle null providerId")
    void testProviderIdWithNull() {
        // Arrange & Act
        users.setProviderId(null);

        // Assert
        assertNull(users.getProviderId());
    }

    @Test
    @DisplayName("Should handle empty providerId")
    void testProviderIdWithEmptyString() {
        // Arrange & Act
        users.setProviderId("");

        // Assert
        assertEquals("", users.getProviderId());
    }

    @Test
    @DisplayName("Should handle providerId with special characters")
    void testProviderIdWithSpecialCharacters() {
        // Arrange
        String specialProviderId = "provider-123_456.789";

        // Act
        users.setProviderId(specialProviderId);

        // Assert
        assertEquals(specialProviderId, users.getProviderId());
    }

    // ================== NAME GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get name correctly")
    void testSetAndGetName() {
        // Arrange
        String testName = "John Doe";

        // Act
        users.setName(testName);
        String retrievedName = users.getName();

        // Assert
        assertEquals(testName, retrievedName);
    }

    @Test
    @DisplayName("Should handle null name")
    void testNameWithNull() {
        // Arrange & Act
        users.setName(null);

        // Assert
        assertNull(users.getName());
    }

    @Test
    @DisplayName("Should handle empty name")
    void testNameWithEmptyString() {
        // Arrange & Act
        users.setName("");

        // Assert
        assertEquals("", users.getName());
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void testNameWithSpecialCharacters() {
        // Arrange
        String specialName = "Jean-Pierre O'Brien-García";

        // Act
        users.setName(specialName);

        // Assert
        assertEquals(specialName, users.getName());
    }

    @Test
    @DisplayName("Should handle name with numbers")
    void testNameWithNumbers() {
        // Arrange
        String nameWithNumbers = "User123 Name456";

        // Act
        users.setName(nameWithNumbers);

        // Assert
        assertEquals(nameWithNumbers, users.getName());
    }

    // ================== PROFILE IMAGE GETTER/SETTER TESTS ==================

    @Test
    @DisplayName("Should set and get profileImage correctly")
    void testSetAndGetProfileImage() {
        // Arrange
        String testProfileImage = "data:image/jpeg;base64,/9j/4AAQSkZJRg...";

        // Act
        users.setProfileImage(testProfileImage);
        String retrievedProfileImage = users.getProfileImage();

        // Assert
        assertEquals(testProfileImage, retrievedProfileImage);
    }

    @Test
    @DisplayName("Should handle null profileImage")
    void testProfileImageWithNull() {
        // Arrange & Act
        users.setProfileImage(null);

        // Assert
        assertNull(users.getProfileImage());
    }

    @Test
    @DisplayName("Should handle empty profileImage")
    void testProfileImageWithEmptyString() {
        // Arrange & Act
        users.setProfileImage("");

        // Assert
        assertEquals("", users.getProfileImage());
    }

    @Test
    @DisplayName("Should handle very large profileImage (LONGTEXT)")
    void testProfileImageWithLargeData() {
        // Arrange
        String largeProfileImage = "data:image/jpeg;base64," + "A".repeat(10000);

        // Act
        users.setProfileImage(largeProfileImage);

        // Assert
        assertEquals(largeProfileImage, users.getProfileImage());
        assertTrue(users.getProfileImage().length() > 10000);
    }

    @Test
    @DisplayName("Should handle profileImage with various base64 formats")
    void testProfileImageWithVariousFormats() {
        // Arrange
        String[] profileImages = {
                "data:image/png;base64,iVBORw0KGgo...",
                "data:image/jpeg;base64,/9j/4AAQ...",
                "https://example.com/image.jpg",
                "file:///path/to/image.png"
        };

        // Act & Assert
        for (String image : profileImages) {
            users.setProfileImage(image);
            assertEquals(image, users.getProfileImage());
        }
    }

    // ================== TOSTRING METHOD TESTS ==================

    @Test
    @DisplayName("Should generate toString with all fields populated (Google OAuth)")
    void testToStringWithAllFieldsGoogle() {
        // Arrange
        users.setId(1);
        users.setUsername("testuser");
        users.setEmail("test@example.com");
        users.setName("Test User");
        users.setAuthProvider(AuthProvider.GOOGLE);
        users.setProviderId("google_12345");
        users.setProfileImage("data:image/jpeg;base64,/9j/4AAQSkZJRg...");

        // Act
        String result = users.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("username='testuser'"));
        assertTrue(result.contains("email='test@example.com'"));
        assertTrue(result.contains("name='Test User'"));
        assertTrue(result.contains("authProvider=" + AuthProvider.GOOGLE));
        assertTrue(result.contains("profileImage=present"));
    }

    @Test
    @DisplayName("Should generate toString with all fields populated (LOCAL)")
    void testToStringWithAllFieldsLocal() {
        // Arrange
        users.setId(2);
        users.setUsername("localuser");
        users.setEmail("local@example.com");
        users.setName("Local User");
        users.setAuthProvider(AuthProvider.LOCAL);
        users.setPassword("hashedPassword");
        users.setProfileImage("data:image/jpeg;base64,/9j/4AAQSkZJRg...");

        // Act
        String result = users.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id=2"));
        assertTrue(result.contains("username='localuser'"));
        assertTrue(result.contains("email='local@example.com'"));
        assertTrue(result.contains("name='Local User'"));
        assertTrue(result.contains("authProvider=" + AuthProvider.LOCAL));
        assertTrue(result.contains("profileImage=present"));
    }

    @Test
    @DisplayName("Should generate toString with null profileImage")
    void testToStringWithNullProfileImage() {
        // Arrange
        users.setId(1);
        users.setUsername("testuser");
        users.setEmail("test@example.com");
        users.setName("Test User");
        users.setAuthProvider(AuthProvider.GOOGLE);
        users.setProfileImage(null);

        // Act
        String result = users.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("profileImage=null"));
        assertFalse(result.contains("profileImage=present"));
    }

    @Test
    @DisplayName("Should generate toString with empty profileImage")
    void testToStringWithEmptyProfileImage() {
        // Arrange
        users.setId(2);
        users.setUsername("user2");
        users.setEmail("user2@example.com");
        users.setName("User Two");
        users.setAuthProvider(AuthProvider.GOOGLE);
        users.setProfileImage("");

        // Act
        String result = users.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("profileImage=present"));
    }

    @Test
    @DisplayName("Should generate toString with minimal data")
    void testToStringWithMinimalData() {
        // Arrange
        Users minimalUser = new Users();

        // Act
        String result = minimalUser.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Users{"));
        assertTrue(result.contains("id=0"));
        assertTrue(result.contains("username='null'"));
        assertTrue(result.contains("profileImage=null"));
    }

    // ================== INTEGRATION TESTS ==================

    @Test
    @DisplayName("Should update user data through multiple operations")
    void testMultipleSetOperations() {
        // Arrange
        users.setId(1);
        users.setUsername("original");
        users.setEmail("original@test.com");

        // Act
        users.setUsername("updated");
        users.setEmail("updated@test.com");
        users.setPassword("newPassword");
        users.setName("Updated User");
        users.setAuthProvider(AuthProvider.GOOGLE);
        users.setProviderId("google_updated");

        // Assert
        assertEquals(1, users.getId());
        assertEquals("updated", users.getUsername());
        assertEquals("updated@test.com", users.getEmail());
        assertEquals("newPassword", users.getPassword());
        assertEquals("Updated User", users.getName());
        assertEquals(AuthProvider.GOOGLE, users.getAuthProvider());
        assertEquals("google_updated", users.getProviderId());
    }

    @Test
    @DisplayName("Should maintain data integrity through getter and setter cycles")
    void testDataIntegrityThroughGetterSetterCycles() {
        // Arrange
        String originalUsername = "testuser";
        String originalEmail = "test@example.com";

        // Act
        users.setUsername(originalUsername);
        users.setEmail(originalEmail);
        String retrievedUsername = users.getUsername();
        String retrievedEmail = users.getEmail();

        // Assert
        assertEquals(originalUsername, retrievedUsername);
        assertEquals(originalEmail, retrievedEmail);
    }

    @Test
    @DisplayName("Should create users through different constructors with expected defaults")
    void testConstructorDefaults() {
        // Arrange & Act
        Users localAuthUser = new Users("user1", "user1@test.com", "password1");
        Users googleOAuthUser = new Users("oauth@test.com", "Google User", AuthProvider.GOOGLE, "google_12345");

        // Assert
        assertEquals(AuthProvider.LOCAL, localAuthUser.getAuthProvider());
        assertNull(localAuthUser.getName());
        assertNull(localAuthUser.getProviderId());

        assertEquals(AuthProvider.GOOGLE, googleOAuthUser.getAuthProvider());
        assertEquals("Google User", googleOAuthUser.getName());
        assertEquals("Google User", googleOAuthUser.getUsername());
        assertEquals("google_12345", googleOAuthUser.getProviderId());
        assertNull(googleOAuthUser.getPassword());
    }

    @Test
    @DisplayName("Should handle edge case with maximum values")
    void testMaximumValueEdgeCases() {
        // Arrange
        users.setId(Integer.MAX_VALUE);
        String maxLengthString = "x".repeat(1000);

        // Act
        users.setUsername(maxLengthString);
        users.setEmail(maxLengthString + "@test.com");
        users.setPassword(maxLengthString);
        users.setName(maxLengthString);
        users.setProviderId(maxLengthString);
        users.setProfileImage(maxLengthString);

        // Assert
        assertEquals(Integer.MAX_VALUE, users.getId());
        assertEquals(maxLengthString, users.getUsername());
        assertEquals(maxLengthString + "@test.com", users.getEmail());
        assertEquals(maxLengthString, users.getPassword());
        assertEquals(maxLengthString, users.getName());
        assertEquals(maxLengthString, users.getProviderId());
        assertEquals(maxLengthString, users.getProfileImage());
    }

    @Test
    @DisplayName("Should handle Unicode characters in string fields")
    void testUnicodeCharactersInStringFields() {
        // Arrange
        String unicodeString = "用户 مستخدم Пользователь 🎉";

        // Act
        users.setUsername(unicodeString);
        users.setEmail(unicodeString + "@test.com");
        users.setName(unicodeString);
        users.setProviderId(unicodeString);

        // Assert
        assertEquals(unicodeString, users.getUsername());
        assertEquals(unicodeString + "@test.com", users.getEmail());
        assertEquals(unicodeString, users.getName());
        assertEquals(unicodeString, users.getProviderId());
    }

    @Test
    @DisplayName("Should handle Google OAuth flow scenario")
    void testGoogleOAuthFlowScenario() {
        // Arrange
        String googleEmail = "user@gmail.com";
        String googleName = "John Doe";
        String googleProviderId = "118039459483729486192";

        // Act
        Users googleUser = new Users(googleEmail, googleName, AuthProvider.GOOGLE, googleProviderId);

        // Assert
        assertEquals(googleEmail, googleUser.getEmail());
        assertEquals(googleName, googleUser.getName());
        assertEquals(googleName, googleUser.getUsername());
        assertEquals(AuthProvider.GOOGLE, googleUser.getAuthProvider());
        assertEquals(googleProviderId, googleUser.getProviderId());
        assertNull(googleUser.getPassword());
        assertNull(googleUser.getProfileImage());
    }

    @Test
    @DisplayName("Should handle transitioning from LOCAL to GOOGLE authentication")
    void testAuthenticationTransition() {
        // Arrange
        users.setUsername("testuser");
        users.setEmail("test@gmail.com");
        users.setPassword("oldPassword");
        users.setAuthProvider(AuthProvider.LOCAL);

        // Act - Transition to Google OAuth
        users.setAuthProvider(AuthProvider.GOOGLE);
        users.setProviderId("118039459483729486192");
        users.setPassword(null); // Clear password for OAuth

        // Assert
        assertEquals("testuser", users.getUsername());
        assertEquals("test@gmail.com", users.getEmail());
        assertEquals(AuthProvider.GOOGLE, users.getAuthProvider());
        assertEquals("118039459483729486192", users.getProviderId());
        assertNull(users.getPassword());
    }
}
