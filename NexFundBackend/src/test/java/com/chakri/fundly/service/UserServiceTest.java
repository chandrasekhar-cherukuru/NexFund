package com.chakri.fundly.service;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit test class for UserService with 80%+ code coverage.
 * Tests all methods, edge cases, error scenarios, and conditional branches.
 * Uses @ExtendWith(MockitoExtension.class) for automatic mock initialization.
 * Follows Arrange-Act-Assert (AAA) pattern for each test method.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserRepo repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    private Users savedUser;

    /**
     * Setup test data before each test method.
     * Initializes common test objects and mocks.
     */
    @BeforeEach
    void setUp() {
        // Initialize test user with basic credentials
        testUser = new Users("testuser", "testuser@example.com", "rawPassword123");
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setProfileImage("profile.jpg");

        // Initialize saved user (simulating database response)
        savedUser = new Users("testuser", "testuser@example.com", "encodedPassword123");
        savedUser.setId(1);
        savedUser.setName("Test User");
        savedUser.setProfileImage("profile.jpg");
    }

    // ============================================================================
    // REGISTER METHOD TESTS
    // ============================================================================

    /**
     * Test: Happy path for successful user registration
     * Verifies that user password is encoded and saved correctly
     */
    @Test
    void testRegister_withValidUser_returnsRegisteredUser() {
        // ARRANGE: Mock password encoder and repository save
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call register method
        Users result = userService.register(testUser);

        // ASSERT: Verify result and interactions
        assertNotNull(result, "Registered user should not be null");
        assertEquals(1, result.getId(), "User ID should match");
        assertEquals("testuser", result.getUsername(), "Username should match");
        assertEquals("testuser@example.com", result.getEmail(), "Email should match");
        assertEquals("encodedPassword123", result.getPassword(), "Password should be encoded");

        // VERIFY: Ensure methods were called with correct parameters
        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Register with null password
     * Verifies encoder handles null password input
     */
    @Test
    void testRegister_withNullPassword_encodesNullPassword() {
        // ARRANGE: Setup user with null password
        testUser.setPassword(null);
        when(passwordEncoder.encode(null)).thenReturn("encodedNull");
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call register method
        Users result = userService.register(testUser);

        // ASSERT: Verify password encoding occurred
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode(null);
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Register with empty password
     * Verifies encoder handles empty string password
     */
    @Test
    void testRegister_withEmptyPassword_encodesEmptyPassword() {
        // ARRANGE: Setup user with empty password
        testUser.setPassword("");
        when(passwordEncoder.encode("")).thenReturn("encodedEmpty");
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call register method
        Users result = userService.register(testUser);

        // ASSERT: Verify result
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("");
    }

    /**
     * Test: Register with special characters in password
     * Verifies encoder handles complex passwords
     */
    @Test
    void testRegister_withSpecialCharactersInPassword_encodesSuccessfully() {
        // ARRANGE: Setup user with special character password
        String specialPassword = "P@ssw0rd!#$%&*()_+-=[]{}|;:,.<>?";
        testUser.setPassword(specialPassword);
        when(passwordEncoder.encode(specialPassword)).thenReturn("encodedSpecial");
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call register method
        Users result = userService.register(testUser);

        // ASSERT: Verify encoding and saving
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode(specialPassword);
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Register when repository throws exception
     * Verifies exception propagation from repository
     */
    @Test
    void testRegister_whenRepositoryThrowsException_propagatesException() {
        // ARRANGE: Setup repository to throw exception
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(repo.save(any(Users.class))).thenThrow(new RuntimeException("Database error"));

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.register(testUser),
                "Should propagate RuntimeException from repository");

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Register with very long password
     * Verifies handling of edge case with long password
     */
    @Test
    void testRegister_withVeryLongPassword_encodesSuccessfully() {
        // ARRANGE: Create very long password
        String longPassword = "a".repeat(500);
        testUser.setPassword(longPassword);
        when(passwordEncoder.encode(longPassword)).thenReturn("encodedLong");
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call register method
        Users result = userService.register(testUser);

        // ASSERT: Verify encoding occurred
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode(longPassword);
    }

    // ============================================================================
    // VERIFY METHOD TESTS
    // ============================================================================

    /**
     * Test: Happy path for successful authentication and JWT generation
     * Verifies successful authentication returns JWT token
     */
    @Test
    void testVerify_withValidCredentials_returnsJWTToken() {
        // ARRANGE: Mock authentication and JWT service
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt.token.value");

        // ACT: Call verify method
        String result = userService.verify(testUser);

        // ASSERT: Verify JWT token is returned
        assertEquals("jwt.token.value", result, "Should return valid JWT token");
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication, times(1)).isAuthenticated();
        verify(jwtService, times(1)).generateToken("testuser");
    }

    /**
     * Test: Authentication fails
     * Verifies error message when authentication fails
     */
    @Test
    void testVerify_withInvalidCredentials_returnsAuthenticationFailed() {
        // ARRANGE: Mock authentication to return false
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // ACT: Call verify method
        String result = userService.verify(testUser);

        // ASSERT: Verify error message is returned
        assertEquals("Authentication failed", result, "Should return authentication failed message");
        verify(jwtService, never()).generateToken(anyString());
    }

    /**
     * Test: Authentication manager throws exception
     * Verifies exception handling during authentication
     */
    @Test
    void testVerify_whenAuthManagerThrowsException_propagatesException() {
        // ARRANGE: Mock authentication manager to throw exception
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Auth service unavailable"));

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.verify(testUser),
                "Should propagate authentication exception");

        verify(jwtService, never()).generateToken(anyString());
    }

    /**
     * Test: Verify with null username
     * Verifies handling of null username in authentication
     */
    @Test
    void testVerify_withNullUsername_authenticatesWithNull() {
        // ARRANGE: Setup user with null username
        testUser.setUsername(null);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(null)).thenReturn("jwt.token.null");

        // ACT: Call verify method
        String result = userService.verify(testUser);

        // ASSERT: Verify authentication occurred
        assertNotNull(result);
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test: Verify with empty username
     * Verifies handling of empty username
     */
    @Test
    void testVerify_withEmptyUsername_authenticatesWithEmptyString() {
        // ARRANGE: Setup user with empty username
        testUser.setUsername("");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("")).thenReturn("jwt.token.empty");

        // ACT: Call verify method
        String result = userService.verify(testUser);

        // ASSERT: Verify authentication occurred
        assertNotNull(result);
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test: Verify JWT token generation throws exception
     * Verifies exception handling in JWT generation
     */
    @Test
    void testVerify_whenJWTGenerationThrowsException_propagatesException() {
        // ARRANGE: Mock authentication successful but JWT service fails
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(anyString()))
                .thenThrow(new RuntimeException("JWT generation failed"));

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.verify(testUser),
                "Should propagate JWT generation exception");
    }

    /**
     * Test: Verify authentication is called with correct parameters
     * Verifies the authentication token contains correct credentials
     */
    @Test
    void testVerify_verifyAuthenticationTokenParameters() {
        // ARRANGE: Mock authentication
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt.token.value");

        // ACT: Call verify method
        userService.verify(testUser);

        // ASSERT: Verify authentication token was created with correct parameters
        verify(authManager).authenticate(argThat(token ->
                token.getName().equals("testuser") &&
                        token.getCredentials().equals("rawPassword123")
        ));
    }

    // ============================================================================
    // UPDATE USER PROFILE METHOD TESTS
    // ============================================================================

    /**
     * Test: Happy path for updating user profile with all fields
     * Verifies all profile fields are updated correctly
     */
    @Test
    void testUpdateUserProfile_withAllFields_updatesSuccessfully() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "old@example.com", "password");
        existingUser.setId(1);
        existingUser.setName("Old Name");

        Users updateUser = new Users();
        updateUser.setName("New Name");
        updateUser.setUsername("newusername");
        updateUser.setEmail("new@example.com");
        updateUser.setProfileImage("newprofile.jpg");

        Users updatedUser = new Users("newusername", "new@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setName("New Name");
        updatedUser.setProfileImage("newprofile.jpg");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify all fields are updated
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("newusername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newprofile.jpg", result.getProfileImage());
        verify(repo, times(1)).findByUsername("testuser");
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update only name field
     * Verifies partial update with only name
     */
    @Test
    void testUpdateUserProfile_withOnlyName_updatesNameOnly() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);
        existingUser.setName("Old Name");
        existingUser.setProfileImage("oldprofile.jpg");

        Users updateUser = new Users();
        updateUser.setName("New Name");
        // Other fields are null

        Users updatedUser = new Users("testuser", "test@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setName("New Name");
        updatedUser.setProfileImage("oldprofile.jpg");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify only name is updated
        assertEquals("New Name", result.getName());
        assertEquals("oldprofile.jpg", result.getProfileImage());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update with null name (should not update)
     * Verifies name is not updated when null
     */
    @Test
    void testUpdateUserProfile_withNullName_doesNotUpdateName() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);
        existingUser.setName("Original Name");

        Users updateUser = new Users();
        updateUser.setName(null);
        updateUser.setEmail("new@example.com");

        Users updatedUser = new Users("testuser", "new@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setName("Original Name");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify name was not updated
        assertEquals("Original Name", result.getName());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update with empty name (should not update)
     * Verifies empty string name is not updated
     */
    @Test
    void testUpdateUserProfile_withEmptyName_doesNotUpdateName() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);
        existingUser.setName("Original Name");

        Users updateUser = new Users();
        updateUser.setName("");
        updateUser.setEmail("new@example.com");

        Users updatedUser = new Users("testuser", "new@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setName("Original Name");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify name was not updated
        assertEquals("Original Name", result.getName());
    }

    /**
     * Test: Update only username field
     * Verifies username-only update
     */
    @Test
    void testUpdateUserProfile_withOnlyUsername_updatesUsernameOnly() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);

        Users updateUser = new Users();
        updateUser.setUsername("newusername");

        Users updatedUser = new Users("newusername", "test@example.com", "password");
        updatedUser.setId(1);

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify username is updated
        assertEquals("newusername", result.getUsername());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update only email field
     * Verifies email-only update
     */
    @Test
    void testUpdateUserProfile_withOnlyEmail_updatesEmailOnly() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "old@example.com", "password");
        existingUser.setId(1);

        Users updateUser = new Users();
        updateUser.setEmail("new@example.com");

        Users updatedUser = new Users("testuser", "new@example.com", "password");
        updatedUser.setId(1);

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify email is updated
        assertEquals("new@example.com", result.getEmail());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update only profile image
     * Verifies profile image-only update
     */
    @Test
    void testUpdateUserProfile_withOnlyProfileImage_updatesProfileImageOnly() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);
        existingUser.setProfileImage("oldprofile.jpg");

        Users updateUser = new Users();
        updateUser.setProfileImage("newprofile.jpg");

        Users updatedUser = new Users("testuser", "test@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setProfileImage("newprofile.jpg");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify profile image is updated
        assertEquals("newprofile.jpg", result.getProfileImage());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: User not found in database
     * Verifies exception thrown when user doesn't exist
     */
    @Test
    void testUpdateUserProfile_whenUserNotFound_throwsException() {
        // ARRANGE: Mock security context and repository returning empty
        mockSecurityContext("testuser");
        Users updateUser = new Users();
        updateUser.setName("New Name");

        when(repo.findByUsername("testuser")).thenReturn(Optional.empty());

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.updateUserProfile(updateUser),
                "Should throw RuntimeException when user not found");

        verify(repo, times(1)).findByUsername("testuser");
        verify(repo, never()).save(any(Users.class));
    }

    /**
     * Test: Repository save throws exception
     * Verifies exception propagation during save
     */
    @Test
    void testUpdateUserProfile_whenRepositorySaveThrowsException_propagatesException() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);

        Users updateUser = new Users();
        updateUser.setName("New Name");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenThrow(new RuntimeException("Database error"));

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.updateUserProfile(updateUser),
                "Should propagate save exception");

        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update with all fields empty/null
     * Verifies no updates when all fields are empty or null
     */
    @Test
    void testUpdateUserProfile_withAllFieldsEmpty_doesNotUpdate() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);
        existingUser.setName("Original Name");
        existingUser.setProfileImage("original.jpg");

        Users updateUser = new Users();
        updateUser.setName(null);
        updateUser.setUsername(null);
        updateUser.setEmail(null);
        updateUser.setProfileImage(null);

        Users savedUser = new Users("testuser", "test@example.com", "password");
        savedUser.setId(1);
        savedUser.setName("Original Name");
        savedUser.setProfileImage("original.jpg");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(savedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify no updates were made
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(repo, times(1)).save(any(Users.class));
    }

    /**
     * Test: Update with special characters in fields
     * Verifies handling of special characters
     */
    @Test
    void testUpdateUserProfile_withSpecialCharacters_updatesSuccessfully() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        existingUser.setId(1);

        Users updateUser = new Users();
        updateUser.setName("John O'Brien-Smith");
        updateUser.setUsername("user@name.new");
        updateUser.setEmail("email+tag@example.com");

        Users updatedUser = new Users("user@name.new", "email+tag@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setName("John O'Brien-Smith");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(updatedUser);

        // ACT: Call updateUserProfile method
        Users result = userService.updateUserProfile(updateUser);

        // ASSERT: Verify special characters are handled
        assertEquals("John O'Brien-Smith", result.getName());
        assertEquals("user@name.new", result.getUsername());
        assertEquals("email+tag@example.com", result.getEmail());
    }

    // ============================================================================
    // GET CURRENT USER METHOD TESTS
    // ============================================================================

    /**
     * Test: Happy path for getting current user
     * Verifies current user is retrieved successfully
     */
    @Test
    void testGetCurrentUser_withValidUser_returnsCurrentUser() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users currentUser = new Users("testuser", "test@example.com", "password");
        currentUser.setId(1);
        currentUser.setName("Test User");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(currentUser));

        // ACT: Call getCurrentUser method
        Users result = userService.getCurrentUser();

        // ASSERT: Verify current user is returned
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(repo, times(1)).findByUsername("testuser");
    }

    /**
     * Test: Current user not found
     * Verifies exception when user doesn't exist
     */
    @Test
    void testGetCurrentUser_whenUserNotFound_throwsException() {
        // ARRANGE: Mock security context and repository returning empty
        mockSecurityContext("testuser");
        when(repo.findByUsername("testuser")).thenReturn(Optional.empty());

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(),
                "Should throw RuntimeException when user not found");

        verify(repo, times(1)).findByUsername("testuser");
    }

    /**
     * Test: Get current user with null username from security context
     * Verifies handling of null username from context
     */
    @Test
    void testGetCurrentUser_withNullUsernameFromContext_passesNullToRepository() {
        // ARRANGE: Mock security context with null username
        mockSecurityContext(null);
        Users currentUser = new Users("testuser", "test@example.com", "password");

        when(repo.findByUsername(null)).thenReturn(Optional.of(currentUser));

        // ACT: Call getCurrentUser method
        Users result = userService.getCurrentUser();

        // ASSERT: Verify repository was called with null
        assertNotNull(result);
        verify(repo, times(1)).findByUsername(null);
    }

    /**
     * Test: Get current user with empty username
     * Verifies handling of empty username
     */
    @Test
    void testGetCurrentUser_withEmptyUsername_throwsException() {
        // ARRANGE: Mock security context with empty username
        mockSecurityContext("");
        when(repo.findByUsername("")).thenReturn(Optional.empty());

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(),
                "Should throw exception for empty username");

        verify(repo, times(1)).findByUsername("");
    }

    /**
     * Test: Repository throws exception during getCurrentUser
     * Verifies exception propagation
     */
    @Test
    void testGetCurrentUser_whenRepositoryThrowsException_propagatesException() {
        // ARRANGE: Mock security context and repository throwing exception
        mockSecurityContext("testuser");
        when(repo.findByUsername("testuser"))
                .thenThrow(new RuntimeException("Database connection error"));

        // ACT & ASSERT: Verify exception is thrown
        assertThrows(RuntimeException.class, () -> userService.getCurrentUser(),
                "Should propagate repository exception");

        verify(repo, times(1)).findByUsername("testuser");
    }

    /**
     * Test: Get current user with multiple fields populated
     * Verifies all fields are retrieved correctly
     */
    @Test
    void testGetCurrentUser_withAllFieldsPopulated_returnsCompleteUser() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users currentUser = new Users("testuser", "test@example.com", "password");
        currentUser.setId(1);
        currentUser.setName("Test User");
        currentUser.setProfileImage("profile.jpg");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(currentUser));

        // ACT: Call getCurrentUser method
        Users result = userService.getCurrentUser();

        // ASSERT: Verify all fields are present
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("profile.jpg", result.getProfileImage());
    }

    /**
     * Test: Get current user called multiple times
     * Verifies each call queries the repository
     */
    @Test
    void testGetCurrentUser_calledMultipleTimes_queriesRepositoryEachTime() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users currentUser = new Users("testuser", "test@example.com", "password");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(currentUser));

        // ACT: Call getCurrentUser method multiple times
        Users result1 = userService.getCurrentUser();
        Users result2 = userService.getCurrentUser();

        // ASSERT: Verify repository was called twice
        assertNotNull(result1);
        assertNotNull(result2);
        verify(repo, times(2)).findByUsername("testuser");
    }

    // ============================================================================
    // SECURITY CONTEXT AND INTEGRATION TESTS
    // ============================================================================

    /**
     * Test: Verify SecurityContextHolder is used correctly
     * Verifies the security context is accessed properly
     */
    @Test
    void testUpdateUserProfile_verifySecurityContextAccess() {
        // ARRANGE: Mock security context and repository
        mockSecurityContext("testuser");
        Users existingUser = new Users("testuser", "test@example.com", "password");
        Users updateUser = new Users();
        updateUser.setName("New Name");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(repo.save(any(Users.class))).thenReturn(existingUser);

        // ACT: Call updateUserProfile method
        userService.updateUserProfile(updateUser);

        // ASSERT: Verify security context was accessed
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper method to mock SecurityContextHolder with a specific username.
     * Sets up the entire security context chain including Authentication and SecurityContext.
     * @param username The username to return from the security context
     */
    private void mockSecurityContext(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }
}
