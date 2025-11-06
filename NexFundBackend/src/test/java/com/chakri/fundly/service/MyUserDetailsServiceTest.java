package com.chakri.fundly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.AuthProvider;
import com.chakri.fundly.model.Users;
import com.chakri.fundly.model.UserPrincipal;
import com.chakri.fundly.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("MyUserDetailsService Test Suite")
class MyUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        testUser = new Users();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");
        testUser.setEmail("testuser@example.com");
        testUser.setAuthProvider(AuthProvider.LOCAL);
    }

    // ============== HAPPY PATH TESTS ==============

    @Test
    @DisplayName("Should load user by username successfully when user exists")
    void testLoadUserByUsername_UserExists_Success() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails, "UserDetails should not be null");
        assertInstanceOf(UserPrincipal.class, userDetails, "UserDetails should be instance of UserPrincipal");
        assertEquals(username, userDetails.getUsername(), "Username should match");
        verify(userRepo, times(1)).findByUsername(username);
        verify(userRepo, never()).deleteAll();
    }

    @Test
    @DisplayName("Should return UserPrincipal wrapping Users entity")
    void testLoadUserByUsername_ReturnsUserPrincipal() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails instanceof UserPrincipal);
    }

    // ============== PARAMETERIZED AUTH PROVIDER TESTS ==============

    @ParameterizedTest
    @EnumSource(AuthProvider.class)
    @DisplayName("Should load user with all auth provider enums")
    void testLoadUserByUsername_AllAuthProviderEnums(AuthProvider provider) {
        // ARRANGE
        testUser.setAuthProvider(provider);
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepo, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should retrieve user with LOCAL auth provider")
    void testLoadUserByUsername_LocalAuthProvider() {
        // ARRANGE
        String username = "testuser";
        testUser.setAuthProvider(AuthProvider.LOCAL);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should retrieve user with GOOGLE auth provider")
    void testLoadUserByUsername_GoogleAuthProvider() {
        // ARRANGE
        String username = "testuser";
        testUser.setAuthProvider(AuthProvider.GOOGLE);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepo, times(1)).findByUsername(username);
    }

    // ============== EDGE CASES & NULL HANDLING ==============

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsername_UserNotFound_ThrowsException() {
        // ARRANGE
        String username = "nonexistentuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(username),
                "Should throw UsernameNotFoundException when user not found"
        );

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should throw exception with correct message format when user not found")
    void testLoadUserByUsername_UserNotFound_CorrectErrorMessage() {
        // ARRANGE
        String username = "missinguser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(username)
        );

        assertTrue(exception.getMessage().contains("User not found with username"));
        assertTrue(exception.getMessage().contains(username));
    }

    @Test
    @DisplayName("Should handle null username parameter by checking repo behavior")
    void testLoadUserByUsername_NullUsername_RepoInvoked() {
        // ARRANGE
        when(userRepo.findByUsername(null)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(null)
        );
        verify(userRepo, times(1)).findByUsername(null);
    }

    @Test
    @DisplayName("Should handle empty string username")
    void testLoadUserByUsername_EmptyUsername_ThrowsException() {
        // ARRANGE
        String emptyUsername = "";
        when(userRepo.findByUsername(emptyUsername)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(emptyUsername)
        );
        verify(userRepo, times(1)).findByUsername(emptyUsername);
    }

    @Test
    @DisplayName("Should handle null auth provider")
    void testLoadUserByUsername_NullAuthProvider_Success() {
        // ARRANGE
        String username = "testuser";
        testUser.setAuthProvider(null);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    // ============== BOUNDARY CONDITIONS ==============

    @Test
    @DisplayName("Should load user with very long username")
    void testLoadUserByUsername_VeryLongUsername_Success() {
        // ARRANGE
        String longUsername = "a".repeat(500);
        testUser.setUsername(longUsername);
        when(userRepo.findByUsername(longUsername)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(longUsername);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(longUsername, userDetails.getUsername());
        verify(userRepo, times(1)).findByUsername(longUsername);
    }

    @Test
    @DisplayName("Should load user with special characters in username")
    void testLoadUserByUsername_SpecialCharactersInUsername_Success() {
        // ARRANGE
        String specialUsername = "user@#$%^&*()";
        testUser.setUsername(specialUsername);
        when(userRepo.findByUsername(specialUsername)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(specialUsername);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(specialUsername, userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user with whitespace in username")
    void testLoadUserByUsername_UsernameWithWhitespace_Success() {
        // ARRANGE
        String usernameWithSpace = "test user";
        testUser.setUsername(usernameWithSpace);
        when(userRepo.findByUsername(usernameWithSpace)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(usernameWithSpace);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(usernameWithSpace, userDetails.getUsername());
    }

    // ============== REPOSITORY INTERACTION TESTS ==============

    @Test
    @DisplayName("Should call repo exactly once when loading user")
    void testLoadUserByUsername_RepoCalledOnce() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        verify(userRepo, times(1)).findByUsername(username);
        verify(userRepo, only()).findByUsername(username);
    }

    @Test
    @DisplayName("Should pass correct username to repository")
    void testLoadUserByUsername_CorrectUsernamePassedToRepo() {
        // ARRANGE
        String username = "correctusername";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        verify(userRepo).findByUsername(username);
    }

    @Test
    @DisplayName("Should not call any other repository methods")
    void testLoadUserByUsername_OnlyFindByUsernameCalledOnRepo() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        verify(userRepo, times(1)).findByUsername(username);
        verify(userRepo, never()).findAll();
        verify(userRepo, never()).save(any());
        verify(userRepo, never()).delete(any());
    }

    // ============== MULTIPLE INVOCATIONS ==============

    @Test
    @DisplayName("Should handle multiple sequential calls correctly")
    void testLoadUserByUsername_MultipleSequentialCalls_Success() {
        // ARRANGE
        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setAuthProvider(AuthProvider.LOCAL);
        Users user2 = new Users();
        user2.setUsername("user2");
        user2.setAuthProvider(AuthProvider.GOOGLE);

        when(userRepo.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepo.findByUsername("user2")).thenReturn(Optional.of(user2));

        // ACT
        UserDetails details1 = myUserDetailsService.loadUserByUsername("user1");
        UserDetails details2 = myUserDetailsService.loadUserByUsername("user2");

        // ASSERT
        assertNotNull(details1);
        assertNotNull(details2);
        assertEquals("user1", details1.getUsername());
        assertEquals("user2", details2.getUsername());
        verify(userRepo, times(1)).findByUsername("user1");
        verify(userRepo, times(1)).findByUsername("user2");
    }

    @Test
    @DisplayName("Should handle repeated calls for same username")
    void testLoadUserByUsername_RepeatedCallsSameUsername() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails details1 = myUserDetailsService.loadUserByUsername(username);
        UserDetails details2 = myUserDetailsService.loadUserByUsername(username);
        UserDetails details3 = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(details1);
        assertNotNull(details2);
        assertNotNull(details3);
        assertEquals(details1.getUsername(), details2.getUsername());
        assertEquals(details2.getUsername(), details3.getUsername());
        verify(userRepo, times(3)).findByUsername(username);
    }

    // ============== EXCEPTION PROPAGATION TESTS ==============

    @Test
    @DisplayName("Should propagate UsernameNotFoundException with original message")
    void testLoadUserByUsername_ExceptionMessagePreserved() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> myUserDetailsService.loadUserByUsername(username)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(username));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException not other exceptions")
    void testLoadUserByUsername_CorrectExceptionType() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT & ASSERT
        Exception exception = assertThrows(
                Exception.class,
                () -> myUserDetailsService.loadUserByUsername(username)
        );

        assertTrue(exception instanceof UsernameNotFoundException);
    }

    // ============== USER PRINCIPAL WRAPPING TESTS ==============

    @Test
    @DisplayName("Should wrap Users object in UserPrincipal")
    void testLoadUserByUsername_WrapsUserInPrincipal() {
        // ARRANGE
        String username = "testuser";
        testUser.setId(123);
        testUser.setEmail("test@example.com");
        testUser.setAuthProvider(AuthProvider.LOCAL);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertTrue(userDetails instanceof UserPrincipal);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    @DisplayName("Should maintain user data through wrapping")
    void testLoadUserByUsername_UserDataMaintainedAfterWrapping() {
        // ARRANGE
        String username = "testuser";
        testUser.setId(999);
        testUser.setEmail("test@email.com");
        testUser.setPassword("secure_password");
        testUser.setAuthProvider(AuthProvider.LOCAL);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("secure_password", userDetails.getPassword());
    }

    // ============== OPTIONAL HANDLING TESTS ==============

    @Test
    @DisplayName("Should handle Optional.empty() correctly")
    void testLoadUserByUsername_OptionalEmpty_ThrowsException() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UsernameNotFoundException.class, () ->
                myUserDetailsService.loadUserByUsername(username)
        );
    }

    @Test
    @DisplayName("Should handle Optional.of() correctly")
    void testLoadUserByUsername_OptionalPresent_ReturnsUser() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    // ============== DIFFERENT USER CONFIGURATIONS ==============

    @Test
    @DisplayName("Should load user with minimal data configuration")
    void testLoadUserByUsername_MinimalUserData() {
        // ARRANGE
        Users minimalUser = new Users();
        minimalUser.setUsername("minimal");
        when(userRepo.findByUsername("minimal")).thenReturn(Optional.of(minimalUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("minimal");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("minimal", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user with complete data configuration")
    void testLoadUserByUsername_CompleteUserData() {
        // ARRANGE
        Users completeUser = new Users();
        completeUser.setId(1);
        completeUser.setUsername("complete");
        completeUser.setPassword("password123");
        completeUser.setEmail("complete@example.com");
        completeUser.setAuthProvider(AuthProvider.LOCAL);
        when(userRepo.findByUsername("complete")).thenReturn(Optional.of(completeUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("complete");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("complete", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    @DisplayName("Should handle case-sensitive username matching")
    void testLoadUserByUsername_CaseSensitive() {
        // ARRANGE
        when(userRepo.findByUsername("TestUser")).thenReturn(Optional.of(testUser));
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());

        // ACT & ASSERT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("TestUser");
        assertNotNull(userDetails);

        assertThrows(UsernameNotFoundException.class, () ->
                myUserDetailsService.loadUserByUsername("testuser")
        );
    }

    // ============== USER ID VARIATIONS ==============

    @Test
    @DisplayName("Should load user with different ID types")
    void testLoadUserByUsername_DifferentUserIds() {
        // ARRANGE
        int[] ids = {1, 100, 999, 5000};

        for (int id : ids) {
            testUser.setId(id);
            when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // ACT
            UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

            // ASSERT
            assertNotNull(userDetails);
            assertEquals("testuser", userDetails.getUsername());
        }
    }

    @Test
    @DisplayName("Should load user with zero ID")
    void testLoadUserByUsername_ZeroUserId() {
        // ARRANGE
        testUser.setId(0);
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    // ============== EMAIL VARIATIONS ==============

    @Test
    @DisplayName("Should load user with null email")
    void testLoadUserByUsername_NullEmail() {
        // ARRANGE
        testUser.setEmail(null);
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user with empty email")
    void testLoadUserByUsername_EmptyEmail() {
        // ARRANGE
        testUser.setEmail("");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    // ============== PASSWORD VARIATIONS ==============

    @Test
    @DisplayName("Should load user with null password")
    void testLoadUserByUsername_NullPassword() {
        // ARRANGE
        testUser.setPassword(null);
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user with empty password")
    void testLoadUserByUsername_EmptyPassword() {
        // ARRANGE
        testUser.setPassword("");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    // ============== LOGGING VERIFICATION ==============

    @Test
    @DisplayName("Should successfully load user regardless of logging")
    void testLoadUserByUsername_LoggingDoesNotAffectFunctionality() {
        // ARRANGE
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userRepo, times(1)).findByUsername(username);
    }

    // ============== UNICODE & INTERNATIONAL CHARACTERS ==============

    @Test
    @DisplayName("Should load user with unicode characters in username")
    void testLoadUserByUsername_UnicodeCharacters() {
        // ARRANGE
        String unicodeUsername = "user_你好_مرحبا";
        testUser.setUsername(unicodeUsername);
        when(userRepo.findByUsername(unicodeUsername)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(unicodeUsername);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(unicodeUsername, userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user with unicode in email")
    void testLoadUserByUsername_UnicodeEmail() {
        // ARRANGE
        testUser.setEmail("user_你好@example.com");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }
}
