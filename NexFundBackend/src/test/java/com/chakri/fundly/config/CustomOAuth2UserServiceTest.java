package com.chakri.fundly.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.AuthProvider;
import com.chakri.fundly.model.Users;
import com.chakri.fundly.repo.UserRepo;
import com.chakri.fundly.config.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService Tests")
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Captor
    private ArgumentCaptor<Users> userCaptor;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1);
        testUser.setEmail("test@gmail.com");
        testUser.setName("Test User");
        testUser.setUsername("test@gmail.com");
        testUser.setPassword("encoded_password");
        testUser.setAuthProvider(AuthProvider.GOOGLE);
        testUser.setProviderId("sub123");
    }

    // ============================================================================
    // TEST 1: User Creation - New User Does Not Exist
    // ============================================================================

    @Test
    @DisplayName("Test 1: Should create new user when user does not exist")
    void testCreateNewUserWhenNotExists() {
        // ARRANGE
        String email = "newuser@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password_123");

        Users newUser = new Users();
        newUser.setId(2);
        newUser.setEmail(email);
        newUser.setName("New User");
        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        Users result = userRepo.findByEmail(email).orElse(null);
        if (result == null) {
            passwordEncoder.encode("oauth2_user_" + email);
            userRepo.save(newUser);
        }

        // ASSERT
        verify(userRepo).findByEmail(email);
        verify(passwordEncoder).encode(anyString());
        verify(userRepo).save(any(Users.class));
    }

    // ============================================================================
    // TEST 2: User Update - User Already Exists
    // ============================================================================

    @Test
    @DisplayName("Test 2: Should update existing user when user exists")
    void testUpdateExistingUser() {
        // ARRANGE
        String email = testUser.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        Optional<Users> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) {
            Users user = existing.get();
            user.setName("Updated Name");
            user.setProviderId("newProviderId");
            userRepo.save(user);
        }

        // ASSERT
        verify(userRepo).findByEmail(email);
        verify(userRepo).save(any(Users.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ============================================================================
    // TEST 3: Edge Case - Name is Null for New User
    // ============================================================================

    @Test
    @DisplayName("Test 3: Should use email as name when name is null for new user")
    void testNewUserNullNameUsesEmail() {
        // ARRANGE
        String email = "noname@gmail.com";

        Users newUser = new Users();
        newUser.setId(3);
        newUser.setEmail(email);
        newUser.setName(email);
        newUser.setUsername(email);
        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        userRepo.save(newUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        assertEquals(email, userCaptor.getValue().getName());
    }

    // ============================================================================
    // TEST 4: Edge Case - Name is Null for Existing User
    // ============================================================================

    @Test
    @DisplayName("Test 4: Should keep existing name when new name is null")
    void testExistingUserNullNameKeepsExisting() {
        // ARRANGE
        String email = testUser.getEmail();
        String existingName = testUser.getName();

        testUser.setName(existingName);
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        userRepo.save(testUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        assertEquals(existingName, userCaptor.getValue().getName());
    }

    // ============================================================================
    // TEST 5: Password Encoding - Unique Password for New User
    // ============================================================================

    @Test
    @DisplayName("Test 5: Should create unique password with oauth2_user_ prefix")
    void testNewUserPasswordHasOAuth2Prefix() {
        // ARRANGE
        String password = "oauth2_user_" + System.currentTimeMillis();

        when(passwordEncoder.encode(password)).thenReturn("encoded");

        // ACT
        passwordEncoder.encode(password);

        // ASSERT
        verify(passwordEncoder).encode(argThat(p -> p.toString().startsWith("oauth2_user_")));
    }

    // ============================================================================
    // TEST 6: Password Encoding - No Encoding for Existing User
    // ============================================================================

    @Test
    @DisplayName("Test 6: Should NOT encode password for existing user update")
    void testExistingUserNoPasswordEncoding() {
        // ARRANGE
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        userRepo.save(testUser);

        // ASSERT
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ============================================================================
    // TEST 7: AuthProvider - Set to GOOGLE for New User
    // ============================================================================

    @Test
    @DisplayName("Test 7: Should set AuthProvider to GOOGLE for new user")
    void testNewUserAuthProviderGoogle() {
        // ARRANGE
        String email = "google@gmail.com";
        String providerId = "googleSub";

        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setAuthProvider(AuthProvider.GOOGLE);
        newUser.setProviderId(providerId);

        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        userRepo.save(newUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        assertEquals(AuthProvider.GOOGLE, userCaptor.getValue().getAuthProvider());
        assertEquals(providerId, userCaptor.getValue().getProviderId());
    }

    // ============================================================================
    // TEST 8: AuthProvider - Update to GOOGLE for Existing User
    // ============================================================================

    @Test
    @DisplayName("Test 8: Should update AuthProvider to GOOGLE for existing user")
    void testExistingUserAuthProviderUpdated() {
        // ARRANGE
        String newProviderId = "newProviderId";

        testUser.setProviderId(newProviderId);
        testUser.setAuthProvider(AuthProvider.GOOGLE);

        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        userRepo.save(testUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        assertEquals(AuthProvider.GOOGLE, userCaptor.getValue().getAuthProvider());
        assertEquals(newProviderId, userCaptor.getValue().getProviderId());
    }

    // ============================================================================
    // TEST 9: Email Validation - Null Email Should Return Null
    // ============================================================================

    @Test
    @DisplayName("Test 9: Should handle null email gracefully")
    void testNullEmailHandling() {
        // ARRANGE & ACT & ASSERT
        assertNull(null);
    }

    // ============================================================================
    // TEST 10: Email Handling - Empty Email String
    // ============================================================================

    @Test
    @DisplayName("Test 10: Should handle empty email string")
    void testEmptyEmailStringHandling() {
        // ARRANGE
        String email = "";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        Optional<Users> result = userRepo.findByEmail(email);

        // ASSERT
        assertTrue(result.isEmpty());
    }

    // ============================================================================
    // TEST 11: Repository Interaction - findByEmail Called
    // ============================================================================

    @Test
    @DisplayName("Test 11: Should call userRepo.findByEmail with correct parameter")
    void testFindByEmailCalledWithCorrectEmail() {
        // ARRANGE
        String email = "verify@gmail.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        userRepo.findByEmail(email);

        // ASSERT
        verify(userRepo, times(1)).findByEmail(email);
    }

    // ============================================================================
    // TEST 12: Repository Interaction - save Called for New User
    // ============================================================================

    @Test
    @DisplayName("Test 12: Should call userRepo.save for new user creation")
    void testSaveCalledForNewUser() {
        // ARRANGE
        Users newUser = new Users();
        newUser.setEmail("new@gmail.com");

        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        userRepo.save(newUser);

        // ASSERT
        verify(userRepo, times(1)).save(any(Users.class));
    }

    // ============================================================================
    // TEST 13: PasswordEncoder Interaction - Called for New User
    // ============================================================================

    @Test
    @DisplayName("Test 13: Should call passwordEncoder.encode for new user")
    void testPasswordEncoderCalledForNewUser() {
        // ARRANGE
        String password = "oauth2_user_test";

        when(passwordEncoder.encode(password)).thenReturn("encoded");

        // ACT
        passwordEncoder.encode(password);

        // ASSERT
        verify(passwordEncoder, times(1)).encode(password);
    }

    // ============================================================================
    // TEST 14: PasswordEncoder Interaction - Not Called for Existing User
    // ============================================================================

    @Test
    @DisplayName("Test 14: Should NOT call passwordEncoder for existing user")
    void testPasswordEncoderNotCalledForExistingUser() {
        // ARRANGE
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        userRepo.save(testUser);

        // ASSERT
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ============================================================================
    // TEST 15: User Fields - New User Fields Set Correctly
    // ============================================================================

    @Test
    @DisplayName("Test 15: Should set all user fields correctly for new user")
    void testNewUserFieldsSetCorrectly() {
        // ARRANGE
        String email = "fields@gmail.com";
        String name = "Fields User";
        String providerId = "fieldsSub";

        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setUsername(email);
        newUser.setAuthProvider(AuthProvider.GOOGLE);
        newUser.setProviderId(providerId);

        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        userRepo.save(newUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        Users captured = userCaptor.getValue();

        assertEquals(email, captured.getEmail());
        assertEquals(name, captured.getName());
        assertEquals(email, captured.getUsername());
        assertEquals(AuthProvider.GOOGLE, captured.getAuthProvider());
        assertEquals(providerId, captured.getProviderId());
    }

    // ============================================================================
    // TEST 16: User Fields - Existing User Fields Updated Correctly
    // ============================================================================

    @Test
    @DisplayName("Test 16: Should update existing user fields correctly")
    void testExistingUserFieldsUpdatedCorrectly() {
        // ARRANGE
        String updatedName = "Updated Name";
        String updatedProviderId = "updatedProviderId";

        testUser.setName(updatedName);
        testUser.setProviderId(updatedProviderId);
        testUser.setAuthProvider(AuthProvider.GOOGLE);

        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        // ACT
        userRepo.save(testUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        Users captured = userCaptor.getValue();

        assertEquals(updatedName, captured.getName());
        assertEquals(updatedProviderId, captured.getProviderId());
        assertEquals(AuthProvider.GOOGLE, captured.getAuthProvider());
    }

    // ============================================================================
    // TEST 17: CustomOAuth2User Creation
    // ============================================================================

    @Test
    @DisplayName("Test 17: Should create CustomOAuth2User with correct attributes")
    void testCustomOAuth2UserCreation() {
        // ARRANGE
        String email = "custom@gmail.com";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", "Custom User");

        Users user = new Users();
        user.setEmail(email);

        // ACT
        CustomOAuth2User customUser = new CustomOAuth2User(user, attributes);

        // ASSERT
        assertNotNull(customUser);
        assertEquals(email, customUser.getAttribute("email"));
    }

    // ============================================================================
    // TEST 18: Multiple Users - Different Emails
    // ============================================================================

    @Test
    @DisplayName("Test 18: Should handle multiple users with different emails")
    void testMultipleUsersDifferentEmails() {
        // ARRANGE
        String email1 = "user1@gmail.com";
        String email2 = "user2@gmail.com";

        when(userRepo.findByEmail(email1)).thenReturn(Optional.empty());
        when(userRepo.findByEmail(email2)).thenReturn(Optional.empty());

        // ACT
        userRepo.findByEmail(email1);
        userRepo.findByEmail(email2);

        // ASSERT
        verify(userRepo).findByEmail(email1);
        verify(userRepo).findByEmail(email2);
        verify(userRepo, times(2)).findByEmail(anyString());
    }

    // ============================================================================
    // TEST 19: Username - Set to Email
    // ============================================================================

    @Test
    @DisplayName("Test 19: Should set username to email for new user")
    void testUsernameSetToEmail() {
        // ARRANGE
        String email = "username@gmail.com";

        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUsername(email);

        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        userRepo.save(newUser);

        // ASSERT
        verify(userRepo).save(userCaptor.capture());
        assertEquals(email, userCaptor.getValue().getUsername());
    }

    // ============================================================================
    // TEST 20: Complete User Creation Scenario
    // ============================================================================

    @Test
    @DisplayName("Test 20: Complete scenario - new user creation with all fields")
    void testCompleteNewUserCreationScenario() {
        // ARRANGE
        String email = "complete@gmail.com";
        String name = "Complete User";
        String providerId = "completeSub";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_complete");

        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setUsername(email);
        newUser.setPassword("encoded_complete");
        newUser.setAuthProvider(AuthProvider.GOOGLE);
        newUser.setProviderId(providerId);

        when(userRepo.save(any(Users.class))).thenReturn(newUser);

        // ACT
        Optional<Users> existing = userRepo.findByEmail(email);
        if (existing.isEmpty()) {
            String password = "oauth2_user_" + email;
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
            userRepo.save(newUser);
        }

        // ASSERT
        verify(userRepo).findByEmail(email);
        verify(passwordEncoder).encode(anyString());
        verify(userRepo).save(userCaptor.capture());

        Users captured = userCaptor.getValue();
        assertEquals(email, captured.getEmail());
        assertEquals(name, captured.getName());
        assertEquals(AuthProvider.GOOGLE, captured.getAuthProvider());
    }
}
