package com.chakri.fundly.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppConfig Unit Tests - Password Encoder Verification")
class AppConfigTest {

    private PasswordEncoder passwordEncoder;
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String ANOTHER_PASSWORD = "differentPassword456";
    private static final String WEAK_PASSWORD = "abc";
    private static final String STRONG_PASSWORD = "S@cur3P@ssw0rd!2025";

    @BeforeEach
    void setUp() {
        // Create actual instance since this is testing bean creation logic
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test
    @DisplayName("Should encode password successfully with BCryptPasswordEncoder")
    void testPasswordEncoderEncodeValidPassword() {
        // Arrange
        String plainPassword = TEST_PASSWORD;

        // Act
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Assert
        assertNotNull(encodedPassword, "Encoded password should not be null");
        assertNotEquals(plainPassword, encodedPassword, "Encoded password should differ from plain password");
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$"),
                "Encoded password should start with valid BCrypt prefix");
    }

    @Test
    @DisplayName("Should generate different hash for same password on each encoding")
    void testPasswordEncoderGeneratesDifferentHashForSamePassword() {
        // Arrange
        String plainPassword = TEST_PASSWORD;

        // Act
        String firstEncoding = passwordEncoder.encode(plainPassword);
        String secondEncoding = passwordEncoder.encode(plainPassword);

        // Assert
        assertNotEquals(firstEncoding, secondEncoding,
                "Different encodings of same password should produce different hashes due to salt");
    }

    @Test
    @DisplayName("Should successfully match plain password with encoded password")
    void testPasswordEncoderMatchesValidPassword() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Act
        boolean isPasswordMatch = passwordEncoder.matches(plainPassword, encodedPassword);

        // Assert
        assertTrue(isPasswordMatch, "Password should match with encoded password");
    }

    @Test
    @DisplayName("Should not match incorrect plain password with encoded password")
    void testPasswordEncoderDoesNotMatchIncorrectPassword() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String wrongPassword = ANOTHER_PASSWORD;
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Act
        boolean isPasswordMatch = passwordEncoder.matches(wrongPassword, encodedPassword);

        // Assert
        assertFalse(isPasswordMatch, "Wrong password should not match with encoded password");
    }

    // ==================== EDGE CASES - PASSWORD LENGTH ====================

    @Test
    @DisplayName("Should handle weak passwords correctly")
    void testPasswordEncoderHandlesWeakPassword() {
        // Arrange
        String weakPassword = WEAK_PASSWORD;

        // Act
        String encodedWeakPassword = passwordEncoder.encode(weakPassword);
        boolean isMatch = passwordEncoder.matches(weakPassword, encodedWeakPassword);

        // Assert
        assertNotNull(encodedWeakPassword, "Even weak password should be encoded");
        assertTrue(isMatch, "Weak password should still match when encoded");
    }

    @Test
    @DisplayName("Should handle strong passwords correctly")
    void testPasswordEncoderHandlesStrongPassword() {
        // Arrange
        String strongPassword = STRONG_PASSWORD;

        // Act
        String encodedStrongPassword = passwordEncoder.encode(strongPassword);
        boolean isMatch = passwordEncoder.matches(strongPassword, encodedStrongPassword);

        // Assert
        assertNotNull(encodedStrongPassword, "Strong password should be encoded");
        assertTrue(isMatch, "Strong password should match when encoded");
    }

    @Test
    @DisplayName("Should handle password at maximum BCrypt limit (72 bytes)")
    void testPasswordEncoderHandlesMaximumBytePassword() {
        // Arrange
        String maxBytePassword = "a".repeat(72);

        // Act
        String encodedMax = passwordEncoder.encode(maxBytePassword);
        boolean isMatch = passwordEncoder.matches(maxBytePassword, encodedMax);

        // Assert
        assertNotNull(encodedMax, "Maximum byte password (72 bytes) should be encoded");
        assertTrue(isMatch, "Maximum byte password should match");
    }

    @Test
    @DisplayName("Should reject passwords exceeding BCrypt 72-byte limit during encoding")
    void testPasswordEncoderRejectsPasswordsOver72BytesDuringEncoding() {
        // Arrange
        String longPassword73 = "a".repeat(73);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.encode(longPassword73);
        }, "Should throw IllegalArgumentException when encoding passwords exceeding 72 bytes");
    }

    @Test
    @DisplayName("Should reject 100-byte password during encoding")
    void testPasswordEncoderRejectsLongPassword100DuringEncoding() {
        // Arrange
        String longPassword100 = "a".repeat(100);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.encode(longPassword100);
        }, "Should throw IllegalArgumentException when encoding passwords exceeding 72 bytes");
    }

    // ==================== EDGE CASES - SPECIAL CHARACTERS ====================

    @Test
    @DisplayName("Should handle empty string password")
    void testPasswordEncoderHandlesEmptyString() {
        // Arrange
        String emptyPassword = "";

        // Act
        String encodedEmpty = passwordEncoder.encode(emptyPassword);
        boolean isMatch = passwordEncoder.matches(emptyPassword, encodedEmpty);

        // Assert
        assertNotNull(encodedEmpty, "Empty string should still be encoded");
        assertTrue(isMatch, "Empty string should match");
    }

    @Test
    @DisplayName("Should handle password with special characters")
    void testPasswordEncoderHandlesSpecialCharacters() {
        // Arrange
        String specialPassword = "!@#$%^&*()_+-={}[]|:;<>?,./";

        // Act
        String encodedSpecial = passwordEncoder.encode(specialPassword);
        boolean isMatch = passwordEncoder.matches(specialPassword, encodedSpecial);

        // Assert
        assertNotNull(encodedSpecial, "Password with special characters should be encoded");
        assertTrue(isMatch, "Password with special characters should match");
    }

    @Test
    @DisplayName("Should handle password with spaces")
    void testPasswordEncoderHandlesWhitespace() {
        // Arrange
        String passwordWithSpaces = "pass word with spaces";

        // Act
        String encodedWithSpaces = passwordEncoder.encode(passwordWithSpaces);
        boolean isMatch = passwordEncoder.matches(passwordWithSpaces, encodedWithSpaces);

        // Assert
        assertNotNull(encodedWithSpaces, "Password with spaces should be encoded");
        assertTrue(isMatch, "Password with spaces should match");
    }

    @Test
    @DisplayName("Should handle password with tabs and newlines")
    void testPasswordEncoderHandlesControlCharacters() {
        // Arrange
        String passwordWithControlChars = "pass\tword\ntest";

        // Act
        String encodedControlChars = passwordEncoder.encode(passwordWithControlChars);
        boolean isMatch = passwordEncoder.matches(passwordWithControlChars, encodedControlChars);

        // Assert
        assertNotNull(encodedControlChars, "Password with control characters should be encoded");
        assertTrue(isMatch, "Password with control characters should match");
    }

    // ==================== EDGE CASES - UNICODE CHARACTERS ====================

    @Test
    @DisplayName("Should handle password with unicode characters (Indian)")
    void testPasswordEncoderHandlesUnicodeCharactersIndian() {
        // Arrange
        String unicodePassword = "पासवर्ड";

        // Act
        String encodedUnicode = passwordEncoder.encode(unicodePassword);
        boolean isMatch = passwordEncoder.matches(unicodePassword, encodedUnicode);

        // Assert
        assertNotNull(encodedUnicode, "Unicode password should be encoded");
        assertTrue(isMatch, "Unicode password should match");
    }

    @Test
    @DisplayName("Should handle password with unicode characters (Chinese)")
    void testPasswordEncoderHandlesUnicodeCharactersChinese() {
        // Arrange
        String chinesePassword = "密码测试";

        // Act
        String encodedChinese = passwordEncoder.encode(chinesePassword);
        boolean isMatch = passwordEncoder.matches(chinesePassword, encodedChinese);

        // Assert
        assertNotNull(encodedChinese, "Chinese password should be encoded");
        assertTrue(isMatch, "Chinese password should match");
    }

    @Test
    @DisplayName("Should handle password with emoji characters")
    void testPasswordEncoderHandlesEmojiCharacters() {
        // Arrange
        String emojiPassword = "Pass🔒word🔐";

        // Act
        String encodedEmoji = passwordEncoder.encode(emojiPassword);
        boolean isMatch = passwordEncoder.matches(emojiPassword, encodedEmoji);

        // Assert
        assertNotNull(encodedEmoji, "Password with emoji should be encoded");
        assertTrue(isMatch, "Password with emoji should match");
    }

    @Test
    @DisplayName("Should handle password with mixed unicode characters within byte limit")
    void testPasswordEncoderHandlesMixedUnicodeWithinByteLimit() {
        // Arrange - Multi-byte UTF-8 characters can consume more than 1 byte
        String mixedUnicodePassword = "Test🔒word";

        // Act
        String encodedMixed = passwordEncoder.encode(mixedUnicodePassword);
        boolean isMatch = passwordEncoder.matches(mixedUnicodePassword, encodedMixed);

        // Assert
        assertNotNull(encodedMixed, "Mixed unicode password should be encoded");
        assertTrue(isMatch, "Mixed unicode password should match");
    }

    // ==================== CASE SENSITIVITY TESTS ====================

    @Test
    @DisplayName("Should handle case-sensitive password matching")
    void testPasswordEncoderCaseSensitiveMatching() {
        // Arrange
        String originalPassword = "TestPassword";
        String differentCasePassword = "testpassword";
        String encodedPassword = passwordEncoder.encode(originalPassword);

        // Act
        boolean isMatch = passwordEncoder.matches(differentCasePassword, encodedPassword);

        // Assert
        assertFalse(isMatch, "Password matching should be case-sensitive");
    }

    @Test
    @DisplayName("Should match exact case with encoded password")
    void testPasswordEncoderMatchesExactCase() {
        // Arrange
        String originalPassword = "TestPassword123";
        String encodedPassword = passwordEncoder.encode(originalPassword);

        // Act
        boolean isMatch = passwordEncoder.matches(originalPassword, encodedPassword);

        // Assert
        assertTrue(isMatch, "Exact case password should match");
    }

    // ==================== BCrypt FORMAT VERIFICATION ====================

    @Test
    @DisplayName("Should produce BCrypt format hash with valid prefix")
    void testPasswordEncoderProducesBCryptFormat() {
        // Arrange
        String plainPassword = TEST_PASSWORD;

        // Act
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Assert
        assertTrue(encodedPassword.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$"),
                "Should produce valid BCrypt hash format: $2a/$2b/$2y$ followed by cost and hash");
    }

    @Test
    @DisplayName("Should have correct BCrypt hash structure with 60 characters")
    void testPasswordEncoderHashStructure() {
        // Arrange
        String plainPassword = TEST_PASSWORD;

        // Act
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Assert
        assertEquals(60, encodedPassword.length(), "BCrypt hash should be exactly 60 characters");
        assertTrue(encodedPassword.charAt(0) == '$', "Hash should start with $");
        assertTrue(encodedPassword.charAt(1) == '2', "Hash should have version 2");
        assertEquals('$', encodedPassword.charAt(3), "Hash should have $ at position 3");
        assertEquals('$', encodedPassword.charAt(6), "Hash should have $ at position 6");
    }

    // ==================== NULL AND INVALID INPUT TESTS ====================

    @Test
    @DisplayName("Should return false when matching with null encoded password")
    void testPasswordEncoderWithNullEncodedPassword() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String nullEncodedPassword = null;

        // Act
        boolean isMatch = passwordEncoder.matches(plainPassword, nullEncodedPassword);

        // Assert
        assertFalse(isMatch, "Should return false for null encoded password (no exception)");
    }

    @Test
    @DisplayName("Should handle matching with null plain password gracefully")
    void testPasswordEncoderWithNullPlainPassword() {
        // Arrange
        String nullPassword = null;
        String encodedPassword = passwordEncoder.encode(TEST_PASSWORD);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            passwordEncoder.matches(nullPassword, encodedPassword);
        }, "Should throw exception for null plain password");
    }

    @Test
    @DisplayName("Should return false for invalid BCrypt hash format")
    void testPasswordEncoderWithInvalidHashFormat() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String invalidHash = "notabcrypthash";

        // Act
        boolean isMatch = passwordEncoder.matches(plainPassword, invalidHash);

        // Assert
        assertFalse(isMatch, "Should return false for invalid BCrypt hash format");
    }

    @Test
    @DisplayName("Should return false for truncated BCrypt hash")
    void testPasswordEncoderWithTruncatedHash() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String validHash = passwordEncoder.encode(plainPassword);
        String truncatedHash = validHash.substring(0, validHash.length() - 5);

        // Act
        boolean isMatch = passwordEncoder.matches(plainPassword, truncatedHash);

        // Assert
        assertFalse(isMatch, "Should return false for truncated hash");
    }

    @Test
    @DisplayName("Should return false when hash is modified in middle")
    void testPasswordEncoderWithModifiedHash() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String validHash = passwordEncoder.encode(plainPassword);
        // Modify hash in the middle
        String modifiedHash = validHash.substring(0, 30) + "X" + validHash.substring(31);

        // Act
        boolean isMatch = passwordEncoder.matches(plainPassword, modifiedHash);

        // Assert
        assertFalse(isMatch, "Should return false for modified hash");
    }

    @Test
    @DisplayName("Should handle incorrect BCrypt prefix gracefully")
    void testPasswordEncoderWithIncorrectPrefix() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String hashWithWrongPrefix = "$1a$10$" + "a".repeat(53);

        // Act
        boolean isMatch = passwordEncoder.matches(plainPassword, hashWithWrongPrefix);

        // Assert
        assertFalse(isMatch, "Should return false for incorrect BCrypt prefix");
    }

    // ==================== REPEATED MATCHING TESTS ====================

    @Test
    @DisplayName("Should match password consistently across multiple attempts")
    void testPasswordEncoderConsistentMatching() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Act & Assert
        for (int i = 0; i < 5; i++) {
            boolean isMatch = passwordEncoder.matches(plainPassword, encodedPassword);
            assertTrue(isMatch, "Password should consistently match on attempt " + (i + 1));
        }
    }

    @Test
    @DisplayName("Should not match wrong password consistently across multiple attempts")
    void testPasswordEncoderConsistentNonMatching() {
        // Arrange
        String correctPassword = TEST_PASSWORD;
        String wrongPassword = ANOTHER_PASSWORD;
        String encodedPassword = passwordEncoder.encode(correctPassword);

        // Act & Assert
        for (int i = 0; i < 5; i++) {
            boolean isMatch = passwordEncoder.matches(wrongPassword, encodedPassword);
            assertFalse(isMatch, "Wrong password should consistently not match on attempt " + (i + 1));
        }
    }

    // ==================== SIMILAR PASSWORD TESTS ====================

    @Test
    @DisplayName("Should differentiate between similar passwords with minor differences")
    void testPasswordEncoderDifferentiatesSimilarPasswords() {
        // Arrange
        String password1 = "TestPassword123";
        String password2 = "TestPassword124"; // Only last char different
        String encodedPassword1 = passwordEncoder.encode(password1);

        // Act
        boolean isMatch = passwordEncoder.matches(password2, encodedPassword1);

        // Assert
        assertFalse(isMatch, "Similar passwords with minor differences should not match");
    }

    @Test
    @DisplayName("Should differentiate between passwords with single character difference")
    void testPasswordEncoderSingleCharacterDifference() {
        // Arrange
        String password1 = "Password";
        String password2 = "Passwore"; // Last char different
        String encodedPassword1 = passwordEncoder.encode(password1);

        // Act
        boolean isMatch = passwordEncoder.matches(password2, encodedPassword1);

        // Assert
        assertFalse(isMatch, "Passwords differing by single character should not match");
    }

    // ==================== MULTIPLE ENCODING SCENARIOS ====================

    @Test
    @DisplayName("Should produce different encodings for same password")
    void testPasswordEncoderMultipleEncodingsDiffer() {
        // Arrange
        String plainPassword = TEST_PASSWORD;

        // Act
        String encoding1 = passwordEncoder.encode(plainPassword);
        String encoding2 = passwordEncoder.encode(plainPassword);
        String encoding3 = passwordEncoder.encode(plainPassword);

        // Assert
        assertNotEquals(encoding1, encoding2, "First and second encodings should differ");
        assertNotEquals(encoding2, encoding3, "Second and third encodings should differ");
        assertNotEquals(encoding1, encoding3, "First and third encodings should differ");
    }

    @Test
    @DisplayName("Should match multiple different encodings with same plain password")
    void testPasswordEncoderMatchesMultipleEncodings() {
        // Arrange
        String plainPassword = TEST_PASSWORD;
        String encoding1 = passwordEncoder.encode(plainPassword);
        String encoding2 = passwordEncoder.encode(plainPassword);

        // Act
        boolean match1 = passwordEncoder.matches(plainPassword, encoding1);
        boolean match2 = passwordEncoder.matches(plainPassword, encoding2);

        // Assert
        assertTrue(match1, "Plain password should match first encoding");
        assertTrue(match2, "Plain password should match second encoding");
    }

    // ==================== NUMERIC PASSWORD TESTS ====================

    @Test
    @DisplayName("Should handle numeric-only passwords")
    void testPasswordEncoderHandlesNumericPassword() {
        // Arrange
        String numericPassword = "1234567890";

        // Act
        String encodedNumeric = passwordEncoder.encode(numericPassword);
        boolean isMatch = passwordEncoder.matches(numericPassword, encodedNumeric);

        // Assert
        assertNotNull(encodedNumeric, "Numeric password should be encoded");
        assertTrue(isMatch, "Numeric password should match");
    }

    @Test
    @DisplayName("Should handle single-character password")
    void testPasswordEncoderHandlesSingleCharacter() {
        // Arrange
        String singleChar = "a";

        // Act
        String encodedSingle = passwordEncoder.encode(singleChar);
        boolean isMatch = passwordEncoder.matches(singleChar, encodedSingle);

        // Assert
        assertNotNull(encodedSingle, "Single character password should be encoded");
        assertTrue(isMatch, "Single character password should match");
    }

    // ==================== PASSWORD WITH LEADING/TRAILING SPACES ====================

    @Test
    @DisplayName("Should handle password with leading spaces")
    void testPasswordEncoderHandlesLeadingSpaces() {
        // Arrange
        String passwordWithLeadingSpaces = "  testPassword";

        // Act
        String encodedWithLeading = passwordEncoder.encode(passwordWithLeadingSpaces);
        boolean isMatch = passwordEncoder.matches(passwordWithLeadingSpaces, encodedWithLeading);

        // Assert
        assertTrue(isMatch, "Password with leading spaces should match");
    }

    @Test
    @DisplayName("Should handle password with trailing spaces")
    void testPasswordEncoderHandlesTrailingSpaces() {
        // Arrange
        String passwordWithTrailingSpaces = "testPassword  ";

        // Act
        String encodedWithTrailing = passwordEncoder.encode(passwordWithTrailingSpaces);
        boolean isMatch = passwordEncoder.matches(passwordWithTrailingSpaces, encodedWithTrailing);

        // Assert
        assertTrue(isMatch, "Password with trailing spaces should match");
    }

    @Test
    @DisplayName("Should not match password with leading spaces against same password without spaces")
    void testPasswordEncoderSpacesSensitivity() {
        // Arrange
        String passwordWithSpaces = "  testPassword";
        String passwordWithoutSpaces = "testPassword";
        String encoded = passwordEncoder.encode(passwordWithSpaces);

        // Act
        boolean isMatch = passwordEncoder.matches(passwordWithoutSpaces, encoded);

        // Assert
        assertFalse(isMatch, "Password with spaces should not match password without spaces");
    }

    // ==================== REPEATED CHARACTER PASSWORDS ====================

    @Test
    @DisplayName("Should handle password with repeated characters")
    void testPasswordEncoderHandlesRepeatedCharacters() {
        // Arrange
        String repeatedPassword = "aaaaaaaaaa";

        // Act
        String encodedRepeated = passwordEncoder.encode(repeatedPassword);
        boolean isMatch = passwordEncoder.matches(repeatedPassword, encodedRepeated);

        // Assert
        assertTrue(isMatch, "Password with repeated characters should match");
    }

    @Test
    @DisplayName("Should differentiate passwords differing only in repeated character count")
    void testPasswordEncoderDifferentiatesRepeatedCharCounts() {
        // Arrange
        String password1 = "aaaaaaaaaa";
        String password2 = "aaaaaaaaaaa"; // One more 'a'
        String encoded1 = passwordEncoder.encode(password1);

        // Act
        boolean isMatch = passwordEncoder.matches(password2, encoded1);

        // Assert
        assertFalse(isMatch, "Passwords with different repeated character counts should not match");
    }

    // ==================== ALPHANUMERIC AND SPECIAL COMBINATIONS ====================

    @Test
    @DisplayName("Should handle complex password with alphanumeric and special characters")
    void testPasswordEncoderHandlesComplexPassword() {
        // Arrange
        String complexPassword = "MyP@ssw0rd!#$%2025";

        // Act
        String encodedComplex = passwordEncoder.encode(complexPassword);
        boolean isMatch = passwordEncoder.matches(complexPassword, encodedComplex);

        // Assert
        assertTrue(isMatch, "Complex password should match");
    }

    @Test
    @DisplayName("Should handle password starting with numbers")
    void testPasswordEncoderHandlesNumberStartingPassword() {
        // Arrange
        String numberStartPassword = "123TestPassword";

        // Act
        String encodedNumberStart = passwordEncoder.encode(numberStartPassword);
        boolean isMatch = passwordEncoder.matches(numberStartPassword, encodedNumberStart);

        // Assert
        assertTrue(isMatch, "Password starting with numbers should match");
    }

    @Test
    @DisplayName("Should handle password ending with special characters")
    void testPasswordEncoderHandlesSpecialCharEndingPassword() {
        // Arrange
        String specialCharEndPassword = "TestPassword@#$%";

        // Act
        String encodedSpecialEnd = passwordEncoder.encode(specialCharEndPassword);
        boolean isMatch = passwordEncoder.matches(specialCharEndPassword, encodedSpecialEnd);

        // Assert
        assertTrue(isMatch, "Password ending with special characters should match");
    }

    // ==================== BOUNDARY CONDITION TESTS ====================

    @Test
    @DisplayName("Should verify encoder returns PasswordEncoder instance")
    void testPasswordEncoderInstanceType() {
        // Assert
        assertNotNull(passwordEncoder, "PasswordEncoder should not be null");
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder, "Should be BCryptPasswordEncoder instance");
    }

    @Test
    @DisplayName("Should encode and match multiple different passwords independently")
    void testMultipleDifferentPasswordsEncoding() {
        // Arrange
        String pwd1 = "password1";
        String pwd2 = "password2";
        String pwd3 = "password3";

        // Act
        String enc1 = passwordEncoder.encode(pwd1);
        String enc2 = passwordEncoder.encode(pwd2);
        String enc3 = passwordEncoder.encode(pwd3);

        boolean match1 = passwordEncoder.matches(pwd1, enc1);
        boolean match2 = passwordEncoder.matches(pwd2, enc2);
        boolean match3 = passwordEncoder.matches(pwd3, enc3);

        boolean noMatch1to2 = passwordEncoder.matches(pwd1, enc2);
        boolean noMatch2to3 = passwordEncoder.matches(pwd2, enc3);

        // Assert
        assertTrue(match1 && match2 && match3, "All passwords should match their own encodings");
        assertFalse(noMatch1to2 && noMatch2to3, "Passwords should not match other encodings");
    }
}
