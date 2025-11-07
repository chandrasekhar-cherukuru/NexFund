package com.chakri.fundly.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthProvider Enum Tests")
class AuthProviderTest {

    // ======================== Happy Path Tests ========================

    @Test
    @DisplayName("Should correctly define LOCAL enum constant")
    void testLocalEnumConstantExists() {
        // Arrange & Act
        AuthProvider provider = AuthProvider.LOCAL;

        // Assert
        assertNotNull(provider, "LOCAL provider should not be null");
        assertEquals("LOCAL", provider.name(), "LOCAL provider name should be 'LOCAL'");
        assertEquals(0, provider.ordinal(), "LOCAL provider ordinal should be 0");
    }

    @Test
    @DisplayName("Should correctly define GOOGLE enum constant")
    void testGoogleEnumConstantExists() {
        // Arrange & Act
        AuthProvider provider = AuthProvider.GOOGLE;

        // Assert
        assertNotNull(provider, "GOOGLE provider should not be null");
        assertEquals("GOOGLE", provider.name(), "GOOGLE provider name should be 'GOOGLE'");
        assertEquals(1, provider.ordinal(), "GOOGLE provider ordinal should be 1");
    }

    @Test
    @DisplayName("Should return correct enum values count")
    void testEnumValuesCount() {
        // Arrange & Act
        AuthProvider[] values = AuthProvider.values();

        // Assert
        assertNotNull(values, "values() should not return null");
        assertEquals(2, values.length, "AuthProvider should have exactly 2 enum constants");
    }

    @Test
    @DisplayName("Should return enum values in correct order")
    void testEnumValuesOrder() {
        // Arrange & Act
        AuthProvider[] values = AuthProvider.values();

        // Assert
        assertEquals(AuthProvider.LOCAL, values[0], "First enum constant should be LOCAL");
        assertEquals(AuthProvider.GOOGLE, values[1], "Second enum constant should be GOOGLE");
    }

    // ======================== Enum Conversion Tests ========================

    @Test
    @DisplayName("Should convert string to LOCAL enum using valueOf")
    void testValueOfLocalProvider() {
        // Arrange & Act
        AuthProvider provider = AuthProvider.valueOf("LOCAL");

        // Assert
        assertNotNull(provider, "valueOf should not return null for 'LOCAL'");
        assertEquals(AuthProvider.LOCAL, provider, "Should convert 'LOCAL' string to LOCAL enum");
    }

    @Test
    @DisplayName("Should convert string to GOOGLE enum using valueOf")
    void testValueOfGoogleProvider() {
        // Arrange & Act
        AuthProvider provider = AuthProvider.valueOf("GOOGLE");

        // Assert
        assertNotNull(provider, "valueOf should not return null for 'GOOGLE'");
        assertEquals(AuthProvider.GOOGLE, provider, "Should convert 'GOOGLE' string to GOOGLE enum");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid enum string")
    void testValueOfInvalidProvider() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("INVALID");
        }, "Should throw IllegalArgumentException for invalid enum constant name");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null string in valueOf")
    void testValueOfNullProvider() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> {
            AuthProvider.valueOf(null);
        }, "Should throw NullPointerException when valueOf receives null");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty string in valueOf")
    void testValueOfEmptyStringProvider() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("");
        }, "Should throw IllegalArgumentException for empty string");
    }

    // ======================== Enum Comparison Tests ========================

    @Test
    @DisplayName("Should correctly compare enum instances with equals")
    void testEnumEqualsComparison() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.LOCAL;

        // Act & Assert
        assertEquals(provider1, provider2, "Two LOCAL providers should be equal");
        assertTrue(provider1.equals(provider2), "equals() should return true for same enum constant");
    }

    @Test
    @DisplayName("Should return false when comparing different enum constants")
    void testEnumNotEqualsComparison() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.GOOGLE;

        // Act & Assert
        assertNotEquals(provider1, provider2, "LOCAL and GOOGLE should not be equal");
        assertFalse(provider1.equals(provider2), "equals() should return false for different enum constants");
    }

    @Test
    @DisplayName("Should use reference equality for enum instances")
    void testEnumReferenceEquality() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.LOCAL;

        // Act & Assert
        assertSame(provider1, provider2, "Same enum constants should reference the same object");
        assertTrue(provider1 == provider2, "Reference equality should work for enum constants");
    }

    @Test
    @DisplayName("Should not use reference equality for different enum constants")
    void testEnumReferenceInequality() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.GOOGLE;

        // Act & Assert
        assertNotSame(provider1, provider2, "Different enum constants should not reference the same object");
        assertFalse(provider1 == provider2, "Reference inequality should hold for different enum constants");
    }

    // ======================== Parameterized Tests ========================

    @ParameterizedTest(name = "Testing enum constant: {0}")
    @EnumSource(AuthProvider.class)
    @DisplayName("Should verify all enum constants are non-null")
    void testAllEnumConstantsNonNull(AuthProvider provider) {
        // Arrange, Act & Assert
        assertNotNull(provider, "All enum constants should be non-null");
    }

    @ParameterizedTest(name = "Testing enum name: {0}")
    @EnumSource(AuthProvider.class)
    @DisplayName("Should verify all enum constants have non-empty names")
    void testAllEnumConstantsHaveValidNames(AuthProvider provider) {
        // Arrange, Act & Assert
        assertNotNull(provider.name(), "Enum name should not be null");
        assertFalse(provider.name().isEmpty(), "Enum name should not be empty");
        assertTrue(provider.name().matches("[A-Z_]+"), "Enum name should contain only uppercase letters");
    }

    @ParameterizedTest(name = "Testing enum ordinal for: {0}")
    @EnumSource(AuthProvider.class)
    @DisplayName("Should verify all enum constants have valid ordinals")
    void testAllEnumConstantsHaveValidOrdinals(AuthProvider provider) {
        // Arrange & Act
        int ordinal = provider.ordinal();

        // Assert
        assertGreaterThanOrEqual(ordinal, 0, "Ordinal should be greater than or equal to 0");
        assertLessThan(ordinal, AuthProvider.values().length, "Ordinal should be less than total enum count");
    }

    @ParameterizedTest(name = "Testing valueOf for: {0}")
    @ValueSource(strings = {"LOCAL", "GOOGLE"})
    @DisplayName("Should convert valid enum strings back to enum constants")
    void testValidValueOfConversions(String providerName) {
        // Arrange & Act
        AuthProvider provider = AuthProvider.valueOf(providerName);

        // Assert
        assertNotNull(provider, "valueOf should return non-null for valid enum string");
        assertEquals(providerName, provider.name(), "Converted enum should have matching name");
    }

    // ======================== Edge Cases Tests ========================

    @Test
    @DisplayName("Should handle case-sensitive valueOf correctly")
    void testValueOfCaseSensitivity() {
        // Arrange, Act & Assert - lowercase should fail
        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("local");
        }, "valueOf should be case-sensitive and fail for lowercase");

        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("google");
        }, "valueOf should be case-sensitive and fail for lowercase");
    }

    @Test
    @DisplayName("Should handle whitespace in valueOf correctly")
    void testValueOfWithWhitespace() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf(" LOCAL");
        }, "valueOf should fail with leading whitespace");

        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("LOCAL ");
        }, "valueOf should fail with trailing whitespace");

        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("LO CAL");
        }, "valueOf should fail with internal whitespace");
    }

    @Test
    @DisplayName("Should handle partial string matching in valueOf")
    void testValueOfPartialStringMatching() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("LOC");
        }, "valueOf should not match partial strings");

        assertThrows(IllegalArgumentException.class, () -> {
            AuthProvider.valueOf("LOCAL_EXTRA");
        }, "valueOf should not match strings with extra characters");
    }

    // ======================== HashCode and ToString Tests ========================

    @Test
    @DisplayName("Should generate consistent hashCode for same enum constant")
    void testEnumHashCodeConsistency() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.LOCAL;

        // Act & Assert
        assertEquals(provider1.hashCode(), provider2.hashCode(), "Same enum constants should have same hashCode");
    }

    @Test
    @DisplayName("Should generate different hashCode for different enum constants")
    void testEnumHashCodeDifference() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.GOOGLE;

        // Act & Assert
        assertNotEquals(provider1.hashCode(), provider2.hashCode(), "Different enum constants should have different hashCode");
    }

    @Test
    @DisplayName("Should return string representation of enum constant")
    void testEnumToString() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;

        // Act
        String result = provider.toString();

        // Assert
        assertNotNull(result, "toString should not return null");
        assertEquals("LOCAL", result, "toString should return the enum constant name");
    }

    @Test
    @DisplayName("Should return correct string representation for all enum constants")
    void testAllEnumConstantsToString() {
        // Arrange & Act & Assert
        assertEquals("LOCAL", AuthProvider.LOCAL.toString(), "LOCAL toString should return 'LOCAL'");
        assertEquals("GOOGLE", AuthProvider.GOOGLE.toString(), "GOOGLE toString should return 'GOOGLE'");
    }

    // ======================== Collection and Iteration Tests ========================

    @Test
    @DisplayName("Should be able to create EnumSet with all values")
    void testEnumSetCreation() {
        // Arrange & Act
        EnumSet<AuthProvider> enumSet = EnumSet.allOf(AuthProvider.class);

        // Assert
        assertNotNull(enumSet, "EnumSet should not be null");
        assertEquals(2, enumSet.size(), "EnumSet should contain all 2 enum constants");
        assertTrue(enumSet.contains(AuthProvider.LOCAL), "EnumSet should contain LOCAL");
        assertTrue(enumSet.contains(AuthProvider.GOOGLE), "EnumSet should contain GOOGLE");
    }

    @Test
    @DisplayName("Should be able to create EnumSet with specific values")
    void testEnumSetWithSpecificValues() {
        // Arrange & Act
        EnumSet<AuthProvider> enumSet = EnumSet.of(AuthProvider.LOCAL);

        // Assert
        assertNotNull(enumSet, "EnumSet should not be null");
        assertEquals(1, enumSet.size(), "EnumSet should contain only 1 value");
        assertTrue(enumSet.contains(AuthProvider.LOCAL), "EnumSet should contain LOCAL");
        assertFalse(enumSet.contains(AuthProvider.GOOGLE), "EnumSet should not contain GOOGLE");
    }

    @Test
    @DisplayName("Should be able to iterate through all enum constants")
    void testEnumIteration() {
        // Arrange
        AuthProvider[] expectedProviders = {AuthProvider.LOCAL, AuthProvider.GOOGLE};
        AuthProvider[] values = AuthProvider.values();

        // Act & Assert
        assertEquals(expectedProviders.length, values.length, "Should have 2 enum constants");
        assertArrayEquals(expectedProviders, values, "Enum constants should match in order");
    }

    @Test
    @DisplayName("Should be able to use enum in collections")
    void testEnumInCollections() {
        // Arrange & Act
        var providerList = Arrays.asList(AuthProvider.values());

        // Assert
        assertNotNull(providerList, "List should not be null");
        assertEquals(2, providerList.size(), "List should contain 2 providers");
        assertTrue(providerList.contains(AuthProvider.LOCAL), "List should contain LOCAL");
        assertTrue(providerList.contains(AuthProvider.GOOGLE), "List should contain GOOGLE");
    }

    // ======================== Type and Class Tests ========================

    @Test
    @DisplayName("Should verify enum type is correct")
    void testEnumType() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;

        // Act & Assert
        assertTrue(provider instanceof AuthProvider, "Instance should be of type AuthProvider");
        assertEquals(AuthProvider.class, provider.getClass(), "Class should be AuthProvider");
    }

    @Test
    @DisplayName("Should verify enum is superclass Enum")
    void testEnumSuperclass() {
        // Arrange & Act
        Class<?> superclass = AuthProvider.class.getSuperclass();

        // Assert
        assertNotNull(superclass, "Superclass should not be null");
        assertEquals(Enum.class, superclass, "AuthProvider should extend Enum");
    }

    @Test
    @DisplayName("Should verify enum is final class")
    void testEnumIsFinal() {
        // Arrange & Act
        int modifiers = AuthProvider.class.getModifiers();

        // Assert
        assertTrue(java.lang.reflect.Modifier.isFinal(modifiers),
                "Enum class should be final - all enums are final");
    }

    @Test
    @DisplayName("Should verify value-only enum is not abstract")
    void testEnumModifiers() {
        // Arrange & Act
        int modifiers = AuthProvider.class.getModifiers();

        // Assert - Value-only enums (without abstract methods) are NOT abstract
        // Only enums with abstract methods are marked as abstract by the compiler
        assertFalse(java.lang.reflect.Modifier.isAbstract(modifiers),
                "Value-only enum should NOT be abstract - only enums with abstract methods are abstract");
    }

    // ======================== Null Handling Tests ========================

    @Test
    @DisplayName("Should handle null comparison gracefully")
    void testNullComparison() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;

        // Act & Assert
        assertNotNull(provider, "Provider should not be null");
        assertFalse(provider.equals(null), "Provider should not equal null");
        assertNotEquals(provider, null, "Provider should not be equal to null");
    }

    @Test
    @DisplayName("Should handle comparison with other types")
    void testComparisonWithOtherTypes() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;

        // Act & Assert
        assertFalse(provider.equals("LOCAL"), "Enum should not equal string with same name");
        assertFalse(provider.equals(0), "Enum should not equal integer");
        assertFalse(provider.equals(new Object()), "Enum should not equal arbitrary object");
    }

    // ======================== Immutability Tests ========================

    @Test
    @DisplayName("Should verify enum constants are immutable")
    void testEnumImmutability() {
        // Arrange
        AuthProvider provider1 = AuthProvider.LOCAL;
        AuthProvider provider2 = AuthProvider.LOCAL;

        // Act & Assert - Enums are thread-safe singletons
        assertSame(provider1, provider2, "Multiple references to same enum constant should be identical");
        assertTrue(provider1 == provider2, "Enum constants are guaranteed to be singletons");
    }

    // ======================== Boundary and Special Cases ========================

    @Test
    @DisplayName("Should handle enum comparison in if-else statements")
    void testEnumInConditionals() {
        // Arrange & Act
        AuthProvider provider = AuthProvider.LOCAL;
        boolean isLocal = provider == AuthProvider.LOCAL;
        boolean isGoogle = provider == AuthProvider.GOOGLE;

        // Assert
        assertTrue(isLocal, "LOCAL provider should match LOCAL comparison");
        assertFalse(isGoogle, "LOCAL provider should not match GOOGLE comparison");
    }

    @Test
    @DisplayName("Should handle enum in switch statements")
    void testEnumInSwitchStatement() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;
        String result;

        // Act
        switch (provider) {
            case LOCAL:
                result = "Using local authentication";
                break;
            case GOOGLE:
                result = "Using Google OAuth";
                break;
            default:
                result = "Unknown provider";
        }

        // Assert
        assertEquals("Using local authentication", result, "Switch should match LOCAL case");
    }

    @Test
    @DisplayName("Should handle enum in switch statement for GOOGLE")
    void testEnumInSwitchStatementGoogle() {
        // Arrange
        AuthProvider provider = AuthProvider.GOOGLE;
        String result;

        // Act
        switch (provider) {
            case LOCAL:
                result = "Using local authentication";
                break;
            case GOOGLE:
                result = "Using Google OAuth";
                break;
            default:
                result = "Unknown provider";
        }

        // Assert
        assertEquals("Using Google OAuth", result, "Switch should match GOOGLE case");
    }

    @Test
    @DisplayName("Should maintain ordinal sequence")
    void testEnumOrdinalSequence() {
        // Arrange & Act
        AuthProvider[] values = AuthProvider.values();

        // Assert
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].ordinal(), "Ordinal at index " + i + " should be " + i);
        }
    }

    @Test
    @DisplayName("Should be serializable")
    void testEnumSerializability() {
        // Arrange
        AuthProvider provider = AuthProvider.LOCAL;

        // Act & Assert
        assertTrue(java.io.Serializable.class.isAssignableFrom(AuthProvider.class),
                "Enum should be serializable");
    }

    @Test
    @DisplayName("Should compare all enum constants for inequality")
    void testAllEnumConstantsInequality() {
        // Arrange
        AuthProvider[] values = AuthProvider.values();

        // Act & Assert
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j],
                        "Enum constant at index " + i + " should not equal constant at index " + j);
            }
        }
    }

    @Test
    @DisplayName("Should have deterministic values array")
    void testEnumValuesArrayDeterministic() {
        // Arrange & Act
        AuthProvider[] values1 = AuthProvider.values();
        AuthProvider[] values2 = AuthProvider.values();

        // Assert
        assertArrayEquals(values1, values2, "values() should always return the same array content");
        assertEquals(values1.length, values2.length, "values() array length should be deterministic");
    }

    // ======================== Helper Assertion Methods ========================

    private void assertLessThan(int value, int expected, String message) {
        assertTrue(value < expected, message + " [expected: < " + expected + ", actual: " + value + "]");
    }

    private void assertGreaterThanOrEqual(int value, int expected, String message) {
        assertTrue(value >= expected, message + " [expected: >= " + expected + ", actual: " + value + "]");
    }
}
