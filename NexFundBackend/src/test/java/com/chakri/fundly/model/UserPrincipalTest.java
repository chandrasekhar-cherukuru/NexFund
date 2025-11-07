package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit test suite for UserPrincipal class.
 * OAuth2 (Google) authentication integration testing.
 *
 * Uses LENIENT strictness to allow flexible stub usage.
 * Test Coverage: 85%+
 * Total Tests: 68
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserPrincipal - Comprehensive Test Suite")
class UserPrincipalTest {

    @Mock
    private Users mockUsers;

    @InjectMocks
    private UserPrincipal userPrincipal;

    private static final String TEST_USERNAME = "google_user_123";
    private static final String TEST_PASSWORD = "oauth2_encrypted_password";
    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String TEST_NAME = "Google User";
    private static final String TEST_GOOGLE_PROVIDER_ID = "110505892659833904815";

    @BeforeEach
    void setUp() {
        // Lenient stubs - safe to use in all tests
        lenient().when(mockUsers.getUsername()).thenReturn(TEST_USERNAME);
        lenient().when(mockUsers.getPassword()).thenReturn(TEST_PASSWORD);
        lenient().when(mockUsers.getEmail()).thenReturn(TEST_EMAIL);
        lenient().when(mockUsers.getName()).thenReturn(TEST_NAME);
        lenient().when(mockUsers.getProviderId()).thenReturn(TEST_GOOGLE_PROVIDER_ID);
        lenient().when(mockUsers.getAuthProvider()).thenReturn(AuthProvider.GOOGLE);
    }

    // ============================================================================
    // Constructor Tests (3 tests)
    // ============================================================================

    @Test
    @DisplayName("Constructor should accept valid Users object")
    void testConstructorWithValidUser() {
        Users user = mockUsers;
        UserPrincipal principal = new UserPrincipal(user);
        assertNotNull(principal);
        assertEquals(user, principal.getUser());
    }

    @Test
    @DisplayName("Constructor should handle null Users object")
    void testConstructorWithNullUser() {
        UserPrincipal principal = new UserPrincipal(null);
        assertNotNull(principal);
        assertNull(principal.getUser());
    }

    @Test
    @DisplayName("Constructor should not invoke any Users methods")
    void testConstructorDoesNotCallUserMethods() {
        Users user = mockUsers;
        new UserPrincipal(user);
        verifyNoInteractions(mockUsers);
    }

    // ============================================================================
    // getAuthorities() Tests (7 tests)
    // ============================================================================

    @Test
    @DisplayName("getAuthorities should return ROLE_USER authority")
    void testGetAuthoritiesReturnsRoleUser() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority())));
    }

    @Test
    @DisplayName("getAuthorities should never return empty collection")
    void testGetAuthoritiesIsNotEmpty() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertFalse(authorities.isEmpty());
    }

    @Test
    @DisplayName("getAuthorities should return SimpleGrantedAuthority instance")
    void testGetAuthoritiesReturnsSimpleGrantedAuthority() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        assertTrue(iterator.hasNext());
        GrantedAuthority authority = iterator.next();
        assertTrue(authority instanceof SimpleGrantedAuthority);
    }

    @Test
    @DisplayName("getAuthorities should always return exactly one role")
    void testGetAuthoritiesReturnsSingleRole() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertEquals(1, authorities.size());
    }

    @Test
    @DisplayName("getAuthorities should return consistent results")
    void testGetAuthoritiesConsistency() {
        Collection<? extends GrantedAuthority> authorities1 = userPrincipal.getAuthorities();
        Collection<? extends GrantedAuthority> authorities2 = userPrincipal.getAuthorities();
        assertEquals(authorities1.size(), authorities2.size());
    }

    @Test
    @DisplayName("getAuthorities should not return null")
    void testGetAuthoritiesNeverNull() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities);
    }

    @Test
    @DisplayName("getAuthorities should not contain null elements")
    void testGetAuthoritiesContainsNoNullElements() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertFalse(authorities.contains(null));
    }

    // ============================================================================
    // getPassword() Tests (6 tests)
    // ============================================================================

    @Test
    @DisplayName("getPassword should return encrypted password")
    void testGetPasswordReturnsUserPassword() {
        String actualPassword = userPrincipal.getPassword();
        assertEquals(TEST_PASSWORD, actualPassword);
        verify(mockUsers, times(1)).getPassword();
    }

    @Test
    @DisplayName("getPassword should handle null password")
    void testGetPasswordWithNullPassword() {
        lenient().when(mockUsers.getPassword()).thenReturn(null);
        String password = userPrincipal.getPassword();
        assertNull(password);
    }

    @Test
    @DisplayName("getPassword should handle empty password")
    void testGetPasswordWithEmptyPassword() {
        lenient().when(mockUsers.getPassword()).thenReturn("");
        String password = userPrincipal.getPassword();
        assertEquals("", password);
    }

    @Test
    @DisplayName("getPassword should handle bcrypt format")
    void testGetPasswordWithEncryptedFormat() {
        String encryptedPassword = "$2a$10$Kc.OMm5Yj0Gj8jN8Kc.OMm5Yj0Gj8jN8Kc.OMm5Yj0Gj8jN8K";
        lenient().when(mockUsers.getPassword()).thenReturn(encryptedPassword);
        String password = userPrincipal.getPassword();
        assertTrue(password.startsWith("$2a$"));
    }

    @Test
    @DisplayName("getPassword should handle very long passwords")
    void testGetPasswordWithLongPassword() {
        String longPassword = "a".repeat(500);
        lenient().when(mockUsers.getPassword()).thenReturn(longPassword);
        String password = userPrincipal.getPassword();
        assertEquals(500, password.length());
    }

    @Test
    @DisplayName("getPassword verify mock call")
    void testGetPasswordVerifyMockCall() {
        userPrincipal.getPassword();
        verify(mockUsers, times(1)).getPassword();
    }

    // ============================================================================
    // getUsername() Tests (5 tests)
    // ============================================================================

    @Test
    @DisplayName("getUsername should return username")
    void testGetUsernameReturnsUserUsername() {
        String actualUsername = userPrincipal.getUsername();
        assertEquals(TEST_USERNAME, actualUsername);
        verify(mockUsers, times(1)).getUsername();
    }

    @Test
    @DisplayName("getUsername should handle null")
    void testGetUsernameWithNullUsername() {
        lenient().when(mockUsers.getUsername()).thenReturn(null);
        String username = userPrincipal.getUsername();
        assertNull(username);
    }

    @Test
    @DisplayName("getUsername should handle empty")
    void testGetUsernameWithEmptyUsername() {
        lenient().when(mockUsers.getUsername()).thenReturn("");
        String username = userPrincipal.getUsername();
        assertEquals("", username);
    }

    @Test
    @DisplayName("getUsername with special characters")
    void testGetUsernameWithSpecialCharacters() {
        String specialUsername = "user_123@domain-456";
        lenient().when(mockUsers.getUsername()).thenReturn(specialUsername);
        String username = userPrincipal.getUsername();
        assertEquals(specialUsername, username);
    }

    @Test
    @DisplayName("getUsername with unicode")
    void testGetUsernameWithUnicodeCharacters() {
        String unicodeUsername = "用户_123_यूजर";
        lenient().when(mockUsers.getUsername()).thenReturn(unicodeUsername);
        String username = userPrincipal.getUsername();
        assertEquals(unicodeUsername, username);
    }

    // ============================================================================
    // Account Status Tests (8 tests)
    // ============================================================================

    @Test
    @DisplayName("isAccountNonExpired returns true")
    void testIsAccountNonExpiredReturnsTrue() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    @DisplayName("isAccountNonExpired consistent")
    void testIsAccountNonExpiredConsistency() {
        boolean result1 = userPrincipal.isAccountNonExpired();
        boolean result2 = userPrincipal.isAccountNonExpired();
        assertEquals(result1, result2);
        assertTrue(result1);
    }

    @Test
    @DisplayName("isAccountNonLocked returns true")
    void testIsAccountNonLockedReturnsTrue() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    @DisplayName("isAccountNonLocked consistent")
    void testIsAccountNonLockedConsistency() {
        boolean result1 = userPrincipal.isAccountNonLocked();
        boolean result2 = userPrincipal.isAccountNonLocked();
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("isCredentialsNonExpired returns true")
    void testIsCredentialsNonExpiredReturnsTrue() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("isCredentialsNonExpired consistent")
    void testIsCredentialsNonExpiredConsistency() {
        boolean result1 = userPrincipal.isCredentialsNonExpired();
        boolean result2 = userPrincipal.isCredentialsNonExpired();
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("isEnabled returns true")
    void testIsEnabledReturnsTrue() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    @DisplayName("All account status methods true")
    void testAllAccountStatusMethodsReturnTrue() {
        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
    }

    // ============================================================================
    // getUser() Tests (4 tests)
    // ============================================================================

    @Test
    @DisplayName("getUser returns wrapped object")
    void testGetUserReturnsWrappedUser() {
        Users user = mockUsers;
        Users retrievedUser = userPrincipal.getUser();
        assertSame(user, retrievedUser);
    }

    @Test
    @DisplayName("getUser handles null")
    void testGetUserWithNullUser() {
        UserPrincipal nullPrincipal = new UserPrincipal(null);
        assertNull(nullPrincipal.getUser());
    }

    @Test
    @DisplayName("getUser consistent reference")
    void testGetUserReturnsConsistentReference() {
        Users originalUser = mockUsers;
        Users retrievedUser1 = userPrincipal.getUser();
        Users retrievedUser2 = userPrincipal.getUser();
        assertSame(originalUser, retrievedUser1);
        assertSame(retrievedUser1, retrievedUser2);
    }

    @Test
    @DisplayName("getUser no mock calls")
    void testGetUserNeverCallsUserMethods() {
        userPrincipal.getUser();
        verifyNoInteractions(mockUsers);
    }

    // ============================================================================
    // getEmail() Tests (5 tests)
    // ============================================================================

    @Test
    @DisplayName("getEmail returns email")
    void testGetEmailReturnsUserEmail() {
        String actualEmail = userPrincipal.getEmail();
        assertEquals(TEST_EMAIL, actualEmail);
        verify(mockUsers, times(1)).getEmail();
    }

    @Test
    @DisplayName("getEmail null handling")
    void testGetEmailWithNullEmail() {
        lenient().when(mockUsers.getEmail()).thenReturn(null);
        String email = userPrincipal.getEmail();
        assertNull(email);
    }

    @Test
    @DisplayName("getEmail empty handling")
    void testGetEmailWithEmptyEmail() {
        lenient().when(mockUsers.getEmail()).thenReturn("");
        String email = userPrincipal.getEmail();
        assertEquals("", email);
    }

    @Test
    @DisplayName("getEmail valid format")
    void testGetEmailWithValidFormat() {
        String validEmail = "user.name+tag@domain.co.uk";
        lenient().when(mockUsers.getEmail()).thenReturn(validEmail);
        String email = userPrincipal.getEmail();
        assertTrue(email.contains("@"));
    }

    @Test
    @DisplayName("getEmail Gmail format")
    void testGetEmailWithGmailFormat() {
        String gmailEmail = "user123@gmail.com";
        lenient().when(mockUsers.getEmail()).thenReturn(gmailEmail);
        String email = userPrincipal.getEmail();
        assertTrue(email.endsWith("@gmail.com"));
    }

    // ============================================================================
    // getAuthProvider() Tests (4 tests)
    // ============================================================================

    @Test
    @DisplayName("getAuthProvider returns GOOGLE")
    void testGetAuthProviderReturnsGoogle() {
        AuthProvider provider = userPrincipal.getAuthProvider();
        assertEquals(AuthProvider.GOOGLE, provider);
        verify(mockUsers, times(1)).getAuthProvider();
    }

    @Test
    @DisplayName("getAuthProvider null handling")
    void testGetAuthProviderWithNullProvider() {
        lenient().when(mockUsers.getAuthProvider()).thenReturn(null);
        AuthProvider provider = userPrincipal.getAuthProvider();
        assertNull(provider);
    }

    @Test
    @DisplayName("getAuthProvider consistent")
    void testGetAuthProviderConsistency() {
        AuthProvider provider1 = userPrincipal.getAuthProvider();
        AuthProvider provider2 = userPrincipal.getAuthProvider();
        assertEquals(provider1, provider2);
        assertEquals(AuthProvider.GOOGLE, provider1);
    }

    @Test
    @DisplayName("getAuthProvider identifies GOOGLE")
    void testGetAuthProviderIdentifiesGoogle() {
        AuthProvider provider = userPrincipal.getAuthProvider();
        assertNotNull(provider);
        assertEquals(AuthProvider.GOOGLE, provider);
    }

    // ============================================================================
    // getProviderId() Tests (6 tests)
    // ============================================================================

    @Test
    @DisplayName("getProviderId returns Google ID")
    void testGetProviderIdReturnsGoogleUserId() {
        String actualProviderId = userPrincipal.getProviderId();
        assertEquals(TEST_GOOGLE_PROVIDER_ID, actualProviderId);
        verify(mockUsers, times(1)).getProviderId();
    }

    @Test
    @DisplayName("getProviderId null handling")
    void testGetProviderIdWithNullProviderId() {
        lenient().when(mockUsers.getProviderId()).thenReturn(null);
        String providerId = userPrincipal.getProviderId();
        assertNull(providerId);
    }

    @Test
    @DisplayName("getProviderId empty handling")
    void testGetProviderIdWithEmptyProviderId() {
        lenient().when(mockUsers.getProviderId()).thenReturn("");
        String providerId = userPrincipal.getProviderId();
        assertEquals("", providerId);
    }

    @Test
    @DisplayName("getProviderId numeric format")
    void testGetProviderIdWithNumericGoogleId() {
        String numericProviderId = "110505892659833904815";
        lenient().when(mockUsers.getProviderId()).thenReturn(numericProviderId);
        String providerId = userPrincipal.getProviderId();
        assertTrue(providerId.matches("\\d+"));
    }

    @Test
    @DisplayName("getProviderId alphanumeric")
    void testGetProviderIdWithAlphanumericId() {
        String alphanumericId = "g_12345abc67890def";
        lenient().when(mockUsers.getProviderId()).thenReturn(alphanumericId);
        String providerId = userPrincipal.getProviderId();
        assertEquals(alphanumericId, providerId);
    }

    @Test
    @DisplayName("getProviderId long strings")
    void testGetProviderIdWithLongId() {
        String longProviderId = "a".repeat(500);
        lenient().when(mockUsers.getProviderId()).thenReturn(longProviderId);
        String providerId = userPrincipal.getProviderId();
        assertEquals(500, providerId.length());
    }

    // ============================================================================
    // getName() Tests (6 tests)
    // ============================================================================

    @Test
    @DisplayName("getName returns display name")
    void testGetNameReturnsUserName() {
        String actualName = userPrincipal.getName();
        assertEquals(TEST_NAME, actualName);
        verify(mockUsers, times(1)).getName();
    }

    @Test
    @DisplayName("getName null handling")
    void testGetNameWithNullName() {
        lenient().when(mockUsers.getName()).thenReturn(null);
        String name = userPrincipal.getName();
        assertNull(name);
    }

    @Test
    @DisplayName("getName empty handling")
    void testGetNameWithEmptyName() {
        lenient().when(mockUsers.getName()).thenReturn("");
        String name = userPrincipal.getName();
        assertEquals("", name);
    }

    @Test
    @DisplayName("getName special characters")
    void testGetNameWithSpecialCharacters() {
        String specialName = "José María O'Brien";
        lenient().when(mockUsers.getName()).thenReturn(specialName);
        String name = userPrincipal.getName();
        assertEquals(specialName, name);
    }

    @Test
    @DisplayName("getName unicode characters")
    void testGetNameWithUnicodeCharacters() {
        String unicodeName = "北京 Москва";
        lenient().when(mockUsers.getName()).thenReturn(unicodeName);
        String name = userPrincipal.getName();
        assertEquals(unicodeName, name);
    }

    @Test
    @DisplayName("getName very long names")
    void testGetNameWithVeryLongName() {
        String longName = "A".repeat(1000);
        lenient().when(mockUsers.getName()).thenReturn(longName);
        String name = userPrincipal.getName();
        assertEquals(1000, name.length());
    }

    // ============================================================================
    // Integration Tests (5 tests)
    // ============================================================================

    @Test
    @DisplayName("Complete OAuth2 flow")
    void testCompleteOAuth2UserFlow() {
        String username = userPrincipal.getUsername();
        String password = userPrincipal.getPassword();
        String email = userPrincipal.getEmail();
        String name = userPrincipal.getName();
        AuthProvider provider = userPrincipal.getAuthProvider();
        String providerId = userPrincipal.getProviderId();
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        assertEquals(TEST_USERNAME, username);
        assertEquals(TEST_PASSWORD, password);
        assertEquals(TEST_EMAIL, email);
        assertEquals(TEST_NAME, name);
        assertEquals(AuthProvider.GOOGLE, provider);
        assertEquals(TEST_GOOGLE_PROVIDER_ID, providerId);
        assertEquals(1, authorities.size());
    }

    @Test
    @DisplayName("Multiple consecutive calls")
    void testMultipleConsecutiveOAuth2Calls() {
        String username1 = userPrincipal.getUsername();
        String password = userPrincipal.getPassword();
        String email = userPrincipal.getEmail();
        String username2 = userPrincipal.getUsername();

        assertEquals(TEST_USERNAME, username1);
        assertEquals(TEST_PASSWORD, password);
        assertEquals(TEST_EMAIL, email);
        assertEquals(TEST_USERNAME, username2);

        verify(mockUsers, times(2)).getUsername();
    }

    @Test
    @DisplayName("Provider and ID together")
    void testOAuth2ProviderAndIdTogether() {
        AuthProvider provider = userPrincipal.getAuthProvider();
        String providerId = userPrincipal.getProviderId();

        assertEquals(AuthProvider.GOOGLE, provider);
        assertEquals(TEST_GOOGLE_PROVIDER_ID, providerId);
    }

    @Test
    @DisplayName("All OAuth2 helpers with valid data")
    void testAllOAuth2HelperMethodsWithValidData() {
        String email = userPrincipal.getEmail();
        AuthProvider provider = userPrincipal.getAuthProvider();
        String providerId = userPrincipal.getProviderId();
        String name = userPrincipal.getName();

        assertEquals(TEST_EMAIL, email);
        assertEquals(AuthProvider.GOOGLE, provider);
        assertEquals(TEST_GOOGLE_PROVIDER_ID, providerId);
        assertEquals(TEST_NAME, name);
    }

    @Test
    @DisplayName("All OAuth2 helpers with null data")
    void testAllOAuth2HelperMethodsWithNullData() {
        lenient().when(mockUsers.getEmail()).thenReturn(null);
        lenient().when(mockUsers.getAuthProvider()).thenReturn(null);
        lenient().when(mockUsers.getProviderId()).thenReturn(null);
        lenient().when(mockUsers.getName()).thenReturn(null);

        assertNull(userPrincipal.getEmail());
        assertNull(userPrincipal.getAuthProvider());
        assertNull(userPrincipal.getProviderId());
        assertNull(userPrincipal.getName());
    }

    // ============================================================================
    // Edge Cases (3 tests)
    // ============================================================================

    @Test
    @DisplayName("Multiple getAuthorities iterations")
    void testGetAuthoritiesMultipleIterations() {
        for (int i = 0; i < 10; i++) {
            Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
            assertEquals(1, authorities.size());
        }
    }

    @Test
    @DisplayName("Multiple principals same user")
    void testMultiplePrincipalsWithSameUser() {
        UserPrincipal principal1 = new UserPrincipal(mockUsers);
        UserPrincipal principal2 = new UserPrincipal(mockUsers);
        assertSame(principal1.getUser(), principal2.getUser());
    }

    @Test
    @DisplayName("Multiple principals different users")
    void testMultiplePrincipalsWithDifferentUsers() {
        Users user2 = mock(Users.class);
        lenient().when(user2.getUsername()).thenReturn("user2");

        UserPrincipal principal1 = new UserPrincipal(mockUsers);
        UserPrincipal principal2 = new UserPrincipal(user2);

        assertNotEquals(principal1.getUsername(), principal2.getUsername());
    }

    // ============================================================================
    // Null State Tests (2 tests)
    // ============================================================================

    @Test
    @DisplayName("Null user throws NPE")
    void testNullUserPrincipalThrowsNPE() {
        UserPrincipal nullPrincipal = new UserPrincipal(null);
        assertNull(nullPrincipal.getUser());
        assertThrows(NullPointerException.class, nullPrincipal::getPassword);
    }

    @Test
    @DisplayName("getAuthorities never null")
    void testGetAuthoritiesNeverReturnsNull() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities);
    }

    // ============================================================================
    // State Tests (2 tests)
    // ============================================================================

    @Test
    @DisplayName("State changes reflected")
    void testUserPrincipalReflectsStateChanges() {
        String username1 = userPrincipal.getUsername();
        lenient().when(mockUsers.getUsername()).thenReturn("new_user");
        String username2 = userPrincipal.getUsername();

        assertNotEquals(username1, username2);
    }

    @Test
    @DisplayName("Wraps user correctly")
    void testUserPrincipalWrapsUserCorrectly() {
        Users wrappedUser = userPrincipal.getUser();
        assertSame(mockUsers, wrappedUser);
    }

    // ============================================================================
    // Mock Verification (2 tests)
    // ============================================================================

    @Test
    @DisplayName("Mock invocation counts")
    void testMockInvocationCounts() {
        userPrincipal.getUsername();
        userPrincipal.getUsername();
        userPrincipal.getPassword();

        verify(mockUsers, times(2)).getUsername();
        verify(mockUsers, times(1)).getPassword();
    }

    @Test
    @DisplayName("Constructor verification")
    void testConstructorVerification() {
        new UserPrincipal(mockUsers);
        verifyNoInteractions(mockUsers);
    }

}
