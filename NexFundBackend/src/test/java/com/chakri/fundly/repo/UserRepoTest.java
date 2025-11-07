package com.chakri.fundly.repo;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepo Test Suite")
class UserRepoTest {

    @Mock
    private UserRepo userRepo;

    private Users testUser;
    private Users googleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        // Test user with LOCAL authentication
        testUser = new Users("john_doe", "john.doe@gmail.com", "hashedPassword123");
        testUser.setId(1);
        testUser.setName("John Doe");
        testUser.setAuthProvider(AuthProvider.LOCAL);

        // Test user with GOOGLE OAuth2
        googleUser = new Users("jane.smith@gmail.com", "Jane Smith", AuthProvider.GOOGLE, "google123456");
        googleUser.setId(2);
        googleUser.setEmail("jane.smith@gmail.com");
    }

    // ======================== findByUsername Tests ========================

    @Test
    @DisplayName("Should find user by valid username")
    void testFindByUsername_WithValidUsername_ReturnsUser() {
        // ARRANGE
        String username = "john_doe";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);

        // ASSERT
        assertTrue(result.isPresent(), "User should be found");
        assertEquals(username, result.get().getUsername());
        assertEquals("john.doe@gmail.com", result.get().getEmail());
        assertEquals(AuthProvider.LOCAL, result.get().getAuthProvider());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should return empty Optional when username not found")
    void testFindByUsername_WithInvalidUsername_ReturnsEmpty() {
        // ARRANGE
        String username = "nonexistent_user";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);

        // ASSERT
        assertFalse(result.isPresent(), "User should not be found");
        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should handle null username parameter")
    void testFindByUsername_WithNullUsername_ReturnsEmpty() {
        // ARRANGE
        when(userRepo.findByUsername(null)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByUsername(null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByUsername(null);
    }

    @Test
    @DisplayName("Should handle empty string username")
    void testFindByUsername_WithEmptyUsername_ReturnsEmpty() {
        // ARRANGE
        String username = "";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should handle whitespace-only username")
    void testFindByUsername_WithWhitespaceUsername_ReturnsEmpty() {
        // ARRANGE
        String username = "   ";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should find user with special characters in username")
    void testFindByUsername_WithSpecialCharacters_ReturnsUser() {
        // ARRANGE
        String username = "john_doe-123.user";
        Users specialUser = new Users(username, "special@test.com", "password");
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(specialUser));

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should find user with case-sensitive username")
    void testFindByUsername_CaseSensitive_ReturnsCorrectUser() {
        // ARRANGE
        String username = "JohnDoe";
        Users caseUser = new Users(username, "case@test.com", "password");
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(caseUser));
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByUsername(username);
        Optional<Users> resultLowerCase = userRepo.findByUsername("johndoe");

        // ASSERT
        assertTrue(result.isPresent());
        assertFalse(resultLowerCase.isPresent());
        verify(userRepo, times(1)).findByUsername(username);
        verify(userRepo, times(1)).findByUsername("johndoe");
    }

    // ======================== findByEmail Tests ========================

    @Test
    @DisplayName("Should find user by valid email")
    void testFindByEmail_WithValidEmail_ReturnsUser() {
        // ARRANGE
        String email = "john.doe@gmail.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("john_doe", result.get().getUsername());
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void testFindByEmail_WithInvalidEmail_ReturnsEmpty() {
        // ARRANGE
        String email = "nonexistent@gmail.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should handle null email parameter")
    void testFindByEmail_WithNullEmail_ReturnsEmpty() {
        // ARRANGE
        when(userRepo.findByEmail(null)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByEmail(null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle empty string email")
    void testFindByEmail_WithEmptyEmail_ReturnsEmpty() {
        // ARRANGE
        String email = "";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should handle whitespace-only email")
    void testFindByEmail_WithWhitespaceEmail_ReturnsEmpty() {
        // ARRANGE
        String email = "   ";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should find user with valid email format")
    void testFindByEmail_WithValidEmailFormat_ReturnsUser() {
        // ARRANGE
        String email = "user+tag@example.co.uk";
        Users emailUser = new Users("user_tag", email, "password");
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(emailUser));

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should find user with different email domains")
    void testFindByEmail_WithDifferentDomains_ReturnsCorrectUser() {
        // ARRANGE
        String gmailEmail = "user@gmail.com";
        String corporateEmail = "user@company.com";

        Users gmailUser = new Users("user_gmail", gmailEmail, "password");
        Users corporateUser = new Users("user_corp", corporateEmail, "password");

        when(userRepo.findByEmail(gmailEmail)).thenReturn(Optional.of(gmailUser));
        when(userRepo.findByEmail(corporateEmail)).thenReturn(Optional.of(corporateUser));

        // ACT
        Optional<Users> result1 = userRepo.findByEmail(gmailEmail);
        Optional<Users> result2 = userRepo.findByEmail(corporateEmail);

        // ASSERT
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(gmailEmail, result1.get().getEmail());
        assertEquals(corporateEmail, result2.get().getEmail());
        verify(userRepo, times(1)).findByEmail(gmailEmail);
        verify(userRepo, times(1)).findByEmail(corporateEmail);
    }

    @Test
    @DisplayName("Should handle case-insensitive email lookup")
    void testFindByEmail_CaseInsensitive_ReturnsUser() {
        // ARRANGE
        String email = "john.doe@gmail.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertTrue(result.isPresent());
        verify(userRepo, times(1)).findByEmail(email);
    }

    // ======================== findByProviderIdAndAuthProvider Tests ========================

    @Test
    @DisplayName("Should find user by GOOGLE provider ID and auth provider")
    void testFindByProviderIdAndAuthProvider_WithGoogleProvider_ReturnsUser() {
        // ARRANGE
        String providerId = "google123456";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.of(googleUser));

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(providerId, result.get().getProviderId());
        assertEquals(authProvider, result.get().getAuthProvider());
        assertEquals("Jane Smith", result.get().getName());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should return empty Optional when provider ID not found")
    void testFindByProviderIdAndAuthProvider_WithInvalidProviderId_ReturnsEmpty() {
        // ARRANGE
        String providerId = "invalid_provider_id";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should return empty Optional when auth provider doesn't match")
    void testFindByProviderIdAndAuthProvider_WithMismatchedProvider_ReturnsEmpty() {
        // ARRANGE
        String providerId = "google123456";
        AuthProvider mismatchedProvider = AuthProvider.LOCAL;
        when(userRepo.findByProviderIdAndAuthProvider(providerId, mismatchedProvider))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, mismatchedProvider);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, mismatchedProvider);
    }

    @Test
    @DisplayName("Should handle null provider ID")
    void testFindByProviderIdAndAuthProvider_WithNullProviderId_ReturnsEmpty() {
        // ARRANGE
        AuthProvider authProvider = AuthProvider.GOOGLE;
        when(userRepo.findByProviderIdAndAuthProvider(null, authProvider))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(null, authProvider);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(null, authProvider);
    }

    @Test
    @DisplayName("Should handle null auth provider")
    void testFindByProviderIdAndAuthProvider_WithNullAuthProvider_ReturnsEmpty() {
        // ARRANGE
        String providerId = "google123456";
        when(userRepo.findByProviderIdAndAuthProvider(providerId, null))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, null);
    }

    @Test
    @DisplayName("Should handle both null parameters")
    void testFindByProviderIdAndAuthProvider_WithBothNull_ReturnsEmpty() {
        // ARRANGE
        when(userRepo.findByProviderIdAndAuthProvider(null, null))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(null, null);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(null, null);
    }

    @Test
    @DisplayName("Should handle empty string provider ID")
    void testFindByProviderIdAndAuthProvider_WithEmptyProviderId_ReturnsEmpty() {
        // ARRANGE
        String providerId = "";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should find user with numeric provider ID")
    void testFindByProviderIdAndAuthProvider_WithNumericProviderId_ReturnsUser() {
        // ARRANGE
        String providerId = "123456789";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        Users numericUser = new Users("numeric@test.com", "Numeric User", authProvider, providerId);
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.of(numericUser));

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(providerId, result.get().getProviderId());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should find user with alphanumeric provider ID")
    void testFindByProviderIdAndAuthProvider_WithAlphanumericProviderId_ReturnsUser() {
        // ARRANGE
        String providerId = "provider_abc123xyz789";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        Users alphanumericUser = new Users("alpha@test.com", "Alpha User", authProvider, providerId);
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.of(alphanumericUser));

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(providerId, result.get().getProviderId());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should handle whitespace in provider ID")
    void testFindByProviderIdAndAuthProvider_WithWhitespaceProviderId_ReturnsEmpty() {
        // ARRANGE
        String providerId = "   ";
        AuthProvider authProvider = AuthProvider.GOOGLE;
        when(userRepo.findByProviderIdAndAuthProvider(providerId, authProvider))
                .thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(providerId, authProvider);

        // ASSERT
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(providerId, authProvider);
    }

    @Test
    @DisplayName("Should find user by GOOGLE provider with different provider IDs")
    void testFindByProviderIdAndAuthProvider_MultipleGoogleUsers_ReturnsCorrectUser() {
        // ARRANGE
        String googleId1 = "google_user_1";
        String googleId2 = "google_user_2";

        Users googleUser1 = new Users("user1@gmail.com", "User One", AuthProvider.GOOGLE, googleId1);
        Users googleUser2 = new Users("user2@gmail.com", "User Two", AuthProvider.GOOGLE, googleId2);

        when(userRepo.findByProviderIdAndAuthProvider(googleId1, AuthProvider.GOOGLE))
                .thenReturn(Optional.of(googleUser1));
        when(userRepo.findByProviderIdAndAuthProvider(googleId2, AuthProvider.GOOGLE))
                .thenReturn(Optional.of(googleUser2));

        // ACT
        Optional<Users> result1 = userRepo.findByProviderIdAndAuthProvider(googleId1, AuthProvider.GOOGLE);
        Optional<Users> result2 = userRepo.findByProviderIdAndAuthProvider(googleId2, AuthProvider.GOOGLE);

        // ASSERT
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(googleId1, result1.get().getProviderId());
        assertEquals(googleId2, result2.get().getProviderId());
        assertEquals("User One", result1.get().getName());
        assertEquals("User Two", result2.get().getName());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(googleId1, AuthProvider.GOOGLE);
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(googleId2, AuthProvider.GOOGLE);
    }

    // ======================== Edge Cases and Integration Scenarios ========================

    @Test
    @DisplayName("Should differentiate between LOCAL and GOOGLE users")
    void testMultipleMethodCalls_LocalVsGoogleUsers_ReturnsCorrectUsers() {
        // ARRANGE
        when(userRepo.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepo.findByEmail("jane.smith@gmail.com")).thenReturn(Optional.of(googleUser));

        // ACT
        Optional<Users> localUser = userRepo.findByUsername("john_doe");
        Optional<Users> googleAuthUser = userRepo.findByEmail("jane.smith@gmail.com");

        // ASSERT
        assertTrue(localUser.isPresent());
        assertTrue(googleAuthUser.isPresent());
        assertEquals(AuthProvider.LOCAL, localUser.get().getAuthProvider());
        assertEquals(AuthProvider.GOOGLE, googleAuthUser.get().getAuthProvider());
        assertNotNull(localUser.get().getPassword());
        assertNull(googleAuthUser.get().getPassword());
        verify(userRepo, times(1)).findByUsername("john_doe");
        verify(userRepo, times(1)).findByEmail("jane.smith@gmail.com");
    }

    @Test
    @DisplayName("Should verify mock repository interactions")
    void testRepositoryInteractions_VerifyMockCalls() {
        // ARRANGE
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT
        userRepo.findByUsername("test");
        userRepo.findByEmail("test@email.com");

        // ASSERT
        verify(userRepo, times(1)).findByUsername("test");
        verify(userRepo, times(1)).findByEmail("test@email.com");
        verify(userRepo, times(0)).findByProviderIdAndAuthProvider(anyString(), any());
    }

    @Test
    @DisplayName("Should never call repository method after first failed attempt")
    void testRepositoryCall_NoRetry_AfterFirstFailure() {
        // ARRANGE
        when(userRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result1 = userRepo.findByUsername("nonexistent");
        Optional<Users> result2 = userRepo.findByUsername("nonexistent");

        // ASSERT
        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
        verify(userRepo, times(2)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should verify exact parameter matching in mock calls")
    void testRepositoryCall_ExactParameterMatching() {
        // ARRANGE
        Users user1 = new Users("user1", "user1@test.com", "pass1");
        Users user2 = new Users("user2", "user2@test.com", "pass2");
        when(userRepo.findByEmail("user1@test.com")).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail("user2@test.com")).thenReturn(Optional.of(user2));

        // ACT
        Optional<Users> result1 = userRepo.findByEmail("user1@test.com");
        Optional<Users> result2 = userRepo.findByEmail("user2@test.com");

        // ASSERT
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals("user1", result1.get().getUsername());
        assertEquals("user2", result2.get().getUsername());
        verify(userRepo, times(1)).findByEmail("user1@test.com");
        verify(userRepo, times(1)).findByEmail("user2@test.com");
    }

    @Test
    @DisplayName("Should handle consecutive calls with different results")
    void testRepositoryCall_ConsecutiveCallsWithDifferentResults() {
        // ARRANGE
        when(userRepo.findByUsername("john_doe"))
                .thenReturn(Optional.of(testUser))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(testUser));

        // ACT
        Optional<Users> result1 = userRepo.findByUsername("john_doe");
        Optional<Users> result2 = userRepo.findByUsername("john_doe");
        Optional<Users> result3 = userRepo.findByUsername("john_doe");

        // ASSERT
        assertTrue(result1.isPresent());
        assertFalse(result2.isPresent());
        assertTrue(result3.isPresent());
        verify(userRepo, times(3)).findByUsername("john_doe");
    }

    @Test
    @DisplayName("Should test all three methods in integration")
    void testAllMethods_Integration_WorksTogether() {
        // ARRANGE
        when(userRepo.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepo.findByEmail("jane.smith@gmail.com")).thenReturn(Optional.of(googleUser));
        when(userRepo.findByProviderIdAndAuthProvider("google123456", AuthProvider.GOOGLE))
                .thenReturn(Optional.of(googleUser));

        // ACT
        Optional<Users> userByUsername = userRepo.findByUsername("john_doe");
        Optional<Users> userByEmail = userRepo.findByEmail("jane.smith@gmail.com");
        Optional<Users> userByProvider = userRepo.findByProviderIdAndAuthProvider("google123456", AuthProvider.GOOGLE);

        // ASSERT
        assertTrue(userByUsername.isPresent());
        assertTrue(userByEmail.isPresent());
        assertTrue(userByProvider.isPresent());

        assertEquals(AuthProvider.LOCAL, userByUsername.get().getAuthProvider());
        assertEquals(AuthProvider.GOOGLE, userByEmail.get().getAuthProvider());
        assertEquals(AuthProvider.GOOGLE, userByProvider.get().getAuthProvider());

        verify(userRepo, times(1)).findByUsername("john_doe");
        verify(userRepo, times(1)).findByEmail("jane.smith@gmail.com");
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider("google123456", AuthProvider.GOOGLE);
    }

    @Test
    @DisplayName("Should handle all edge cases for GOOGLE auth provider")
    void testGoogleAuthProvider_EdgeCases_HandlesCorrectly() {
        // ARRANGE
        String googleProviderId = "123456789012345678";
        Users googleEdgeUser = new Users("edge.case@gmail.com", "Edge Case", AuthProvider.GOOGLE, googleProviderId);

        when(userRepo.findByProviderIdAndAuthProvider(googleProviderId, AuthProvider.GOOGLE))
                .thenReturn(Optional.of(googleEdgeUser));

        // ACT
        Optional<Users> result = userRepo.findByProviderIdAndAuthProvider(googleProviderId, AuthProvider.GOOGLE);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(AuthProvider.GOOGLE, result.get().getAuthProvider());
        assertNull(result.get().getPassword()); // Google OAuth users don't have passwords
        assertEquals(googleProviderId, result.get().getProviderId());
        verify(userRepo, times(1)).findByProviderIdAndAuthProvider(googleProviderId, AuthProvider.GOOGLE);
    }
}
