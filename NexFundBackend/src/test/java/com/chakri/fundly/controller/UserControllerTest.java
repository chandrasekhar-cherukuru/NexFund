package com.chakri.fundly.controller;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.model.AuthProvider;
import com.chakri.fundly.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Test Suite")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Users testUser;
    private Users registeredUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new Users("testuser", "test@example.com", "password123");
        testUser.setId(1);

        registeredUser = new Users("testuser", "test@example.com", "hashedPassword");
        registeredUser.setId(1);
        registeredUser.setName("Test User");
    }

    // ===========================
    // REGISTER ENDPOINT TESTS
    // ===========================

    @Test
    @DisplayName("register_ValidUser_ReturnsOkResponseWithRegisteredUser")
    void testRegisterValidUser() {
        // Arrange
        when(userService.register(any(Users.class))).thenReturn(registeredUser);

        // Act
        ResponseEntity<?> response = userController.register(testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredUser, response.getBody());
        verify(userService, times(1)).register(any(Users.class));
    }

    @Test
    @DisplayName("register_NullUser_ReturnsBadRequestWithError")
    void testRegisterNullUser() {
        // Arrange
        when(userService.register(null)).thenThrow(new IllegalArgumentException("User cannot be null"));

        // Act
        ResponseEntity<?> response = userController.register(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertTrue(errorBody.containsKey("error"));
        assertTrue(errorBody.get("error").toString().contains("User cannot be null"));
    }

    @Test
    @DisplayName("register_DuplicateEmail_ReturnsBadRequestWithError")
    void testRegisterDuplicateEmail() {
        // Arrange
        when(userService.register(any(Users.class))).thenThrow(
                new RuntimeException("Email already exists")
        );

        // Act
        ResponseEntity<?> response = userController.register(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals("Email already exists", errorBody.get("error"));
    }

    @Test
    @DisplayName("register_ServiceThrowsException_ReturnsBadRequestWithErrorMessage")
    void testRegisterServiceException() {
        // Arrange
        String errorMessage = "Database connection failed";
        when(userService.register(any(Users.class))).thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = userController.register(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorBody.get("error"));
        verify(userService, times(1)).register(any(Users.class));
    }

    @Test
    @DisplayName("register_UserWithAllFields_ReturnsCompleteUserObject")
    void testRegisterUserWithAllFields() {
        // Arrange
        Users completeUser = new Users("john_doe", "john@example.com", "password123");
        completeUser.setId(1);
        completeUser.setName("John Doe");
        completeUser.setProfileImage("image_url");
        completeUser.setAuthProvider(AuthProvider.LOCAL);

        when(userService.register(any(Users.class))).thenReturn(completeUser);

        // Act
        ResponseEntity<?> response = userController.register(completeUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Users returnedUser = (Users) response.getBody();
        assertNotNull(returnedUser);
        assertEquals("john_doe", returnedUser.getUsername());
        assertEquals("john@example.com", returnedUser.getEmail());
        assertEquals("John Doe", returnedUser.getName());
    }

    // ===========================
    // LOGIN ENDPOINT TESTS
    // ===========================

    @Test
    @DisplayName("login_ValidCredentials_ReturnsJWTToken")
    void testLoginValidCredentials() {
        // Arrange
        String expectedToken = "jwt_token_123";
        when(userService.verify(any(Users.class))).thenReturn(expectedToken);

        // Act
        String result = userController.login(testUser);

        // Assert
        assertEquals(expectedToken, result);
        verify(userService, times(1)).verify(any(Users.class));
    }

    @Test
    @DisplayName("login_InvalidCredentials_ReturnsAuthenticationFailedMessage")
    void testLoginInvalidCredentials() {
        // Arrange
        when(userService.verify(any(Users.class))).thenReturn("Authentication failed");

        // Act
        String result = userController.login(testUser);

        // Assert
        assertEquals("Authentication failed", result);
        verify(userService, times(1)).verify(any(Users.class));
    }

    @Test
    @DisplayName("login_NullUser_ReturnsAuthenticationFailedMessage")
    void testLoginNullUser() {
        // Arrange
        when(userService.verify(null)).thenReturn("Authentication failed");

        // Act
        String result = userController.login(null);

        // Assert
        assertEquals("Authentication failed", result);
    }

    @Test
    @DisplayName("login_EmptyPassword_ReturnsAuthenticationFailedMessage")
    void testLoginEmptyPassword() {
        // Arrange
        Users userWithEmptyPassword = new Users("testuser", "test@example.com", "");
        when(userService.verify(any(Users.class))).thenReturn("Authentication failed");

        // Act
        String result = userController.login(userWithEmptyPassword);

        // Assert
        assertEquals("Authentication failed", result);
    }

    @Test
    @DisplayName("login_EmptyUsername_ReturnsAuthenticationFailedMessage")
    void testLoginEmptyUsername() {
        // Arrange
        Users userWithEmptyUsername = new Users("", "test@example.com", "password123");
        when(userService.verify(any(Users.class))).thenReturn("Authentication failed");

        // Act
        String result = userController.login(userWithEmptyUsername);

        // Assert
        assertEquals("Authentication failed", result);
    }

    // ===========================
    // UPDATE PROFILE ENDPOINT TESTS
    // ===========================

    @Test
    @DisplayName("updateProfile_ValidUserUpdate_ReturnsOkWithUpdatedUser")
    void testUpdateProfileValid() {
        // Arrange
        Users updatedUser = new Users("newusername", "newemail@example.com", "password123");
        updatedUser.setId(1);
        updatedUser.setName("Updated Name");

        when(userService.updateUserProfile(any(Users.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateProfile(updatedUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).updateUserProfile(any(Users.class));
    }

    @Test
    @DisplayName("updateProfile_UserNotFound_ReturnsBadRequestWithError")
    void testUpdateProfileUserNotFound() {
        // Arrange
        when(userService.updateUserProfile(any(Users.class))).thenThrow(
                new RuntimeException("User not found")
        );

        // Act
        ResponseEntity<?> response = userController.updateProfile(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals("User not found", errorBody.get("error"));
    }

    @Test
    @DisplayName("updateProfile_NullUser_ReturnsBadRequestWithError")
    void testUpdateProfileNullUser() {
        // Arrange
        when(userService.updateUserProfile(null)).thenThrow(
                new IllegalArgumentException("User cannot be null")
        );

        // Act
        ResponseEntity<?> response = userController.updateProfile(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertTrue(errorBody.get("error").toString().contains("User cannot be null"));
    }

    @Test
    @DisplayName("updateProfile_EmptyFieldsUpdate_ReturnsOkWithPartialUpdate")
    void testUpdateProfileEmptyFields() {
        // Arrange
        Users userWithEmptyFields = new Users("", "", "");
        Users updatedUser = new Users("existinguser", "existing@example.com", "password");
        updatedUser.setId(1);

        when(userService.updateUserProfile(any(Users.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateProfile(userWithEmptyFields);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("updateProfile_UpdateProfileImageOnly_ReturnsOkWithUpdatedImage")
    void testUpdateProfileImageOnly() {
        // Arrange
        Users userUpdateImage = new Users("testuser", "test@example.com", "password");
        userUpdateImage.setProfileImage("new_image_url");

        Users updatedUser = new Users("testuser", "test@example.com", "password");
        updatedUser.setId(1);
        updatedUser.setProfileImage("new_image_url");

        when(userService.updateUserProfile(any(Users.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateProfile(userUpdateImage);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Users result = (Users) response.getBody();
        assertEquals("new_image_url", result.getProfileImage());
    }

    @Test
    @DisplayName("updateProfile_ServiceThrowsRuntimeException_ReturnsBadRequest")
    void testUpdateProfileServiceException() {
        // Arrange
        String errorMessage = "Database error during update";
        when(userService.updateUserProfile(any(Users.class))).thenThrow(
                new RuntimeException(errorMessage)
        );

        // Act
        ResponseEntity<?> response = userController.updateProfile(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorBody.get("error"));
    }

    // ===========================
    // GET PROFILE ENDPOINT TESTS
    // ===========================

    @Test
    @DisplayName("getProfile_UserExists_ReturnsOkWithUserData")
    void testGetProfileUserExists() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(registeredUser);

        // Act
        ResponseEntity<?> response = userController.getProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredUser, response.getBody());
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("getProfile_UserNotFound_ReturnsBadRequestWithError")
    void testGetProfileUserNotFound() {
        // Arrange
        when(userService.getCurrentUser()).thenThrow(new RuntimeException("User not found"));

        // Act
        ResponseEntity<?> response = userController.getProfile();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals("User not found", errorBody.get("error"));
    }

    @Test
    @DisplayName("getProfile_SecurityContextEmpty_ReturnsBadRequestWithError")
    void testGetProfileSecurityContextEmpty() {
        // Arrange
        when(userService.getCurrentUser()).thenThrow(
                new RuntimeException("Authentication required")
        );

        // Act
        ResponseEntity<?> response = userController.getProfile();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertTrue(errorBody.containsKey("error"));
    }

    @Test
    @DisplayName("getProfile_ReturnsCompleteUserProfile")
    void testGetProfileReturnsCompleteProfile() {
        // Arrange
        Users completeProfile = new Users("john_doe", "john@example.com", "password");
        completeProfile.setId(1);
        completeProfile.setName("John Doe");
        completeProfile.setProfileImage("profile_image_url");
        completeProfile.setAuthProvider(AuthProvider.GOOGLE);

        when(userService.getCurrentUser()).thenReturn(completeProfile);

        // Act
        ResponseEntity<?> response = userController.getProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Users result = (Users) response.getBody();
        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertEquals("profile_image_url", result.getProfileImage());
    }

    @Test
    @DisplayName("getProfile_ServiceThrowsException_ReturnsBadRequestWithErrorMessage")
    void testGetProfileServiceException() {
        // Arrange
        String errorMessage = "Internal server error";
        when(userService.getCurrentUser()).thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = userController.getProfile();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorBody = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, errorBody.get("error"));
    }

    @Test
    @DisplayName("getProfile_MultipleCallsWithoutReinitialization_ReturnsSameUserData")
    void testGetProfileMultipleCalls() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(registeredUser);

        // Act
        ResponseEntity<?> response1 = userController.getProfile();
        ResponseEntity<?> response2 = userController.getProfile();

        // Assert
        assertEquals(response1.getBody(), response2.getBody());
        verify(userService, times(2)).getCurrentUser();
    }
}
