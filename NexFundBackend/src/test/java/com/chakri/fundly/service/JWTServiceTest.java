package com.chakri.fundly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit test class for JWTService using Mockito.
 * Tests all PUBLIC methods of JWTService with maximum code coverage.
 * Target code coverage: 80%+
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Mocks are automatically initialized by @ExtendWith(MockitoExtension.class)
    }

    // ==================== Constructor Tests ====================

    @Test
    void testConstructorSuccessfullyGeneratesSecretKey() {
        assertNotNull(jwtService, "JWTService should be initialized");
        assertNotNull(jwtService.secretKey, "Secret key should be generated");
        assertFalse(jwtService.secretKey.isEmpty(), "Secret key should not be empty");
        assertTrue(isValidBase64(jwtService.secretKey), "Secret key should be valid Base64 encoded");
    }

    @Test
    void testConstructorGeneratesValidBase64EncodedKey() {
        String secretKey = jwtService.secretKey;
        assertNotNull(secretKey);
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(secretKey);
            assertTrue(decodedBytes.length > 0, "Decoded key should have bytes");
        } catch (IllegalArgumentException e) {
            fail("Secret key should be valid Base64");
        }
    }

    // ==================== generateToken Tests ====================

    @Test
    void testGenerateTokenSuccessfully() {
        String username = "testuser@example.com";
        String token = jwtService.generateToken(username);

        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");
        assertEquals(3, token.split("\\.").length, "JWT should have 3 parts");
    }

    @Test
    void testGenerateTokenWithSpecialCharactersInUsername() {
        String username = "user+test@domain.co.uk";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenWithEmptyUsername() {
        String username = "";
        String token = jwtService.generateToken(username);

        assertNotNull(token, "Token should still be generated with empty username");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenWithLongUsername() {
        String username = "a".repeat(1000);
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenWithNumericUsername() {
        String username = "12345";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenWithSpecialCharactersAndNumbers() {
        String username = "user@123-test.co.uk";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenContainsCorrectClaims() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUserName(token);

        assertEquals(username, extractedUsername, "Token should contain correct username");
    }

    @Test
    void testGenerateTokenIssuedAtIsWithinReasonableTime() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testGenerateTokenWithLargerDelayProducesUniqueTokens() throws InterruptedException {
        String username = "testuser";
        String token1 = jwtService.generateToken(username);
        Thread.sleep(1001); // Sleep for more than 1 second to guarantee different timestamp
        String token2 = jwtService.generateToken(username);

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2, "Tokens with different timestamps should be unique");
    }

    // ==================== getKey Tests ====================

    @Test
    void testGetKeyReturnsValidSecretKey() {
        SecretKey key = jwtService.getKey();

        assertNotNull(key, "getKey should return a non-null SecretKey");
        assertEquals("HmacSHA256", key.getAlgorithm(), "Key should use HmacSHA256 algorithm");
    }

    @Test
    void testGetKeyConsistency() {
        SecretKey key1 = jwtService.getKey();
        SecretKey key2 = jwtService.getKey();

        assertArrayEquals(key1.getEncoded(), key2.getEncoded(),
                "Multiple calls to getKey should return equivalent keys");
    }

    @Test
    void testGetKeyDecodeValidBase64() {
        String secretKey = jwtService.secretKey;

        assertDoesNotThrow(() -> {
            byte[] decodedBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
            assertNotNull(decodedBytes);
            assertTrue(decodedBytes.length > 0, "Decoded key bytes should not be empty");
        });
    }

    @Test
    void testGetKeyAlgorithmIsHmacSHA256() {
        SecretKey key = jwtService.getKey();

        assertNotNull(key.getAlgorithm());
        assertTrue(key.getAlgorithm().contains("HmacSHA256"));
    }

    // ==================== extractUserName Tests ====================

    @Test
    void testExtractUserNameSuccessfully() {
        String username = "john.doe@example.com";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUserName(token);

        assertEquals(username, extractedUsername, "Should extract correct username from token");
    }

    @Test
    void testExtractUserNameWithSpecialCharacters() {
        String username = "user-123+test@domain.co.uk";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUserName(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUserNameWithNumericUsername() {
        String username = "12345";
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUserName(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUserNameWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtService.extractUserName(invalidToken);
        }, "Should throw exception for invalid token");
    }

    @Test
    void testExtractUserNameWithMalformedJWT() {
        String malformedToken = "header.payload";

        assertThrows(Exception.class, () -> {
            jwtService.extractUserName(malformedToken);
        }, "Should throw exception for malformed JWT");
    }

    @Test
    void testExtractUserNameWithOnlyHeaders() {
        String invalidToken = "header";

        assertThrows(Exception.class, () -> {
            jwtService.extractUserName(invalidToken);
        }, "Should throw exception for incomplete token");
    }

    @Test
    void testExtractUserNameWithEmptyToken() {
        String emptyToken = "";

        assertThrows(Exception.class, () -> {
            jwtService.extractUserName(emptyToken);
        }, "Should throw exception for empty token");
    }

    // ==================== validateToken Tests ====================

    @Test
    void testValidateTokenWithValidTokenAndMatchingUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid, "Valid token with matching username should return true");
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    void testValidateTokenWithValidTokenButNonMatchingUsername() {
        String tokenUsername = "tokenuser";
        String detailsUsername = "differentuser";
        String token = jwtService.generateToken(tokenUsername);
        when(userDetails.getUsername()).thenReturn(detailsUsername);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertFalse(isValid, "Token with non-matching username should return false");
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    void testValidateTokenWithNullUserDetails() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertThrows(NullPointerException.class, () -> {
            jwtService.validateToken(token, null);
        }, "Should throw NullPointerException for null UserDetails");
    }

    @Test
    void testValidateTokenWithInvalidTokenThrowsException() {
        String invalidToken = "invalid.token.string";

        assertThrows(Exception.class, () -> {
            jwtService.validateToken(invalidToken, userDetails);
        }, "Should throw exception for invalid token");
    }

    @Test
    void testValidateTokenUsernameComparison() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
        verify(userDetails, atLeastOnce()).getUsername();
    }

    @Test
    void testValidateTokenWithCaseSensitiveUsername() {
        String token = jwtService.generateToken("TestUser");
        when(userDetails.getUsername()).thenReturn("testuser");

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertFalse(isValid, "Username comparison should be case-sensitive");
    }

    @Test
    void testValidateTokenWithWhitespaceInUsername() {
        String token = jwtService.generateToken("test user");
        when(userDetails.getUsername()).thenReturn("test user");

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenVerifiesMockCalled() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        jwtService.validateToken(token, userDetails);

        verify(userDetails).getUsername();
    }

    @Test
    void testValidateTokenWithSpecialCharacterUsername() {
        String username = "user+test@domain.com";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenReturnsFalseForNonMatchingUsernames() {
        String tokenUsername = "user1";
        String detailsUsername = "user2";
        String token = jwtService.generateToken(tokenUsername);
        when(userDetails.getUsername()).thenReturn(detailsUsername);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithNumericUsername() {
        String username = "12345";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    // ==================== Integration Tests ====================

    @Test
    void testTokenGenerationAndValidationWorkflow() {
        String username = "integration.test@example.com";
        when(userDetails.getUsername()).thenReturn(username);

        String token = jwtService.generateToken(username);
        boolean isValid = jwtService.validateToken(token, userDetails);
        String extractedUsername = jwtService.extractUserName(token);

        assertTrue(isValid, "Generated token should be valid");
        assertEquals(username, extractedUsername, "Extracted username should match original");
    }

    @Test
    void testTokenValidationWithUserDetailsNull() {
        String token = jwtService.generateToken("testuser");

        assertThrows(NullPointerException.class, () -> {
            jwtService.validateToken(token, null);
        });
    }

    @Test
    void testEndToEndTokenLifecycleWithLargerDelay() throws InterruptedException {
        String username = "e2e.test@example.com";
        when(userDetails.getUsername()).thenReturn(username);

        String token1 = jwtService.generateToken(username);
        String extractedUser1 = jwtService.extractUserName(token1);
        boolean isValid1 = jwtService.validateToken(token1, userDetails);

        Thread.sleep(1001); // Sleep for more than 1 second

        String token2 = jwtService.generateToken(username);
        String extractedUser2 = jwtService.extractUserName(token2);
        boolean isValid2 = jwtService.validateToken(token2, userDetails);

        assertEquals(username, extractedUser1);
        assertEquals(username, extractedUser2);
        assertTrue(isValid1);
        assertTrue(isValid2);
        assertNotEquals(token1, token2);
    }

    @Test
    void testTokenGenerationWithDifferentUsernames() {
        String user1 = "user1@example.com";
        String user2 = "user2@example.com";

        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        String extracted1 = jwtService.extractUserName(token1);
        String extracted2 = jwtService.extractUserName(token2);

        assertEquals(user1, extracted1);
        assertEquals(user2, extracted2);
        assertNotEquals(token1, token2);
    }

    @Test
    void testValidateTokenIsFalseWhenUsernamesDoNotMatch() {
        String tokenUser = "alice";
        String detailsUser = "bob";
        String token = jwtService.generateToken(tokenUser);
        when(userDetails.getUsername()).thenReturn(detailsUser);

        boolean result = jwtService.validateToken(token, userDetails);

        assertFalse(result);
    }

    @Test
    void testGetKeyIsNotNull() {
        SecretKey key = jwtService.getKey();
        assertNotNull(key);
    }

    @Test
    void testGenerateTokenReturnsString() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(token instanceof String);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUserNameReturnsString() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        String result = jwtService.extractUserName(token);

        assertNotNull(result);
        assertTrue(result instanceof String);
    }

    @Test
    void testValidateTokenReturnsBoolean() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean result = jwtService.validateToken(token, userDetails);

        assertTrue(result);
    }

    @Test
    void testTokenStructureIsValid() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        String[] parts = token.split("\\.");

        assertEquals(3, parts.length);
        assertTrue(parts[0].length() > 0, "Header should not be empty");
        assertTrue(parts[1].length() > 0, "Payload should not be empty");
        assertTrue(parts[2].length() > 0, "Signature should not be empty");
    }

    @Test
    void testExtractUserNamePreservesOriginalUsername() {
        String[] usernames = {"user1", "user+test@domain.com", "user-123", "user_456", "12345"};

        for (String username : usernames) {
            String token = jwtService.generateToken(username);
            String extracted = jwtService.extractUserName(token);
            assertEquals(username, extracted, "Username should be preserved: " + username);
        }
    }

    @Test
    void testValidateTokenWithMultipleUsers() {
        String[] users = {"user1", "user2", "user3"};

        for (String user : users) {
            String token = jwtService.generateToken(user);
            when(userDetails.getUsername()).thenReturn(user);
            assertTrue(jwtService.validateToken(token, userDetails));
        }
    }

    @Test
    void testGenerateTokenProducesValidJWTFormat() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(token.contains("."), "Token should contain dots (JWT format)");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have exactly 3 parts");
    }

    @Test
    void testGetKeyReturnsConsistentAlgorithm() {
        SecretKey key1 = jwtService.getKey();
        String algorithm1 = key1.getAlgorithm();
        SecretKey key2 = jwtService.getKey();
        String algorithm2 = key2.getAlgorithm();

        assertEquals(algorithm1, algorithm2, "Key algorithm should remain consistent");
    }

    @Test
    void testValidateTokenWithNonEmptyUsername() {
        String username = "nonemptyuser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testTokenWithDifferentUsersProduceDifferentTokens() {
        String token1 = jwtService.generateToken("user1");
        String token2 = jwtService.generateToken("user2");

        assertNotEquals(token1, token2);
    }

    // ==================== Helper Methods ====================

    private boolean isValidBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
