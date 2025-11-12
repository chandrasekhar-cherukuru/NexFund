package com.chakri.fundly.config;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.config.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2User Comprehensive Test Suite")
class CustomOAuth2UserTest {

    @Mock
    private Users mockUser;

    private Map<String, Object> testAttributes;
    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAttributes = new HashMap<>();
        testAttributes.put("sub", "123456789");
        testAttributes.put("name", "John Doe");
        testAttributes.put("email", "john.doe@example.com");
    }

    @Test
    @DisplayName("Should successfully create CustomOAuth2User with valid user and attributes")
    void testConstructorWithValidUserAndAttributes() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        assertNotNull(customOAuth2User);
        assertEquals(mockUser, customOAuth2User.getUser());
        assertEquals(testAttributes, customOAuth2User.getAttributes());
    }

    @Test
    @DisplayName("Should return attributes map correctly")
    void testGetAttributesReturnsCorrectMap() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Map<String, Object> returnedAttributes = customOAuth2User.getAttributes();

        assertNotNull(returnedAttributes);
        assertEquals(testAttributes, returnedAttributes);
        assertEquals("123456789", returnedAttributes.get("sub"));
        assertEquals("john.doe@example.com", returnedAttributes.get("email"));
    }

    @Test
    @DisplayName("Should return user object correctly")
    void testGetUserReturnsCorrectUser() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Users returnedUser = customOAuth2User.getUser();

        assertNotNull(returnedUser);
        assertEquals(mockUser, returnedUser);
    }

    @Test
    @DisplayName("Should return ROLE_USER authority")
    void testGetAuthoritiesReturnsRoleUser() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> authorities = customOAuth2User.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority())));
    }

    @Test
    @DisplayName("Should return authority as SimpleGrantedAuthority instance")
    void testGetAuthoritiesReturnTypeIsSimpleGrantedAuthority() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> authorities = customOAuth2User.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.iterator().hasNext());
        assertTrue(authorities.iterator().next() instanceof SimpleGrantedAuthority);
    }

    @Test
    @DisplayName("Should return user name when user.getName() returns non-null value")
    void testGetNameReturnsUserNameWhenNotNull() {
        String expectedName = "Alice Smith";
        when(mockUser.getName()).thenReturn(expectedName);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(expectedName, result);
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should return username when user.getName() returns null")
    void testGetNameReturnsUsernameWhenNameIsNull() {
        String expectedUsername = "bob.johnson";
        when(mockUser.getName()).thenReturn(null);
        when(mockUser.getUsername()).thenReturn(expectedUsername);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(expectedUsername, result);
        verify(mockUser, times(1)).getName();
        verify(mockUser, times(1)).getUsername();
    }

    @Test
    @DisplayName("Should handle empty string as name value")
    void testGetNameReturnsEmptyString() {
        when(mockUser.getName()).thenReturn("");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals("", result);
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle both name and username as null")
    void testGetNameBothNameAndUsernameNull() {
        when(mockUser.getName()).thenReturn(null);
        when(mockUser.getUsername()).thenReturn(null);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertNull(result);
        verify(mockUser, times(1)).getName();
        verify(mockUser, times(1)).getUsername();
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void testGetNameWithSpecialCharacters() {
        String nameWithSpecialChars = "José García-López";
        when(mockUser.getName()).thenReturn(nameWithSpecialChars);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(nameWithSpecialChars, result);
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle very long name string")
    void testGetNameWithVeryLongString() {
        String longName = "A".repeat(1000);
        when(mockUser.getName()).thenReturn(longName);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(longName, result);
        assertEquals(1000, result.length());
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle single character name")
    void testGetNameWithSingleCharacter() {
        when(mockUser.getName()).thenReturn("X");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals("X", result);
        assertEquals(1, result.length());
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle name with numbers")
    void testGetNameWithNumbers() {
        String nameWithNumbers = "User123 Number456";
        when(mockUser.getName()).thenReturn(nameWithNumbers);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(nameWithNumbers, result);
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle name with whitespace only")
    void testGetNameWithWhitespaceOnly() {
        String whitespaceOnlyName = "   ";
        when(mockUser.getName()).thenReturn(whitespaceOnlyName);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(whitespaceOnlyName, result);
        assertEquals(3, result.length());
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle name with quotes and special symbols")
    void testGetNameWithQuotesAndSymbols() {
        String specialName = "John \"The Developer\" O'Brien-Smith & Co.";
        when(mockUser.getName()).thenReturn(specialName);
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals(specialName, result);
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should handle empty attributes map")
    void testGetAttributesWithEmptyMap() {
        Map<String, Object> emptyAttributes = new HashMap<>();
        customOAuth2User = new CustomOAuth2User(mockUser, emptyAttributes);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should handle attributes with null values")
    void testGetAttributesWithNullValues() {
        Map<String, Object> attributesWithNulls = new HashMap<>();
        attributesWithNulls.put("key1", null);
        attributesWithNulls.put("key2", "value");
        customOAuth2User = new CustomOAuth2User(mockUser, attributesWithNulls);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertNotNull(result);
        assertTrue(result.containsKey("key1"));
        assertNull(result.get("key1"));
        assertEquals("value", result.get("key2"));
    }

    @Test
    @DisplayName("Should handle attributes with complex object values")
    void testGetAttributesWithComplexObjects() {
        Map<String, Object> complexAttributes = new HashMap<>();
        Map<String, String> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        complexAttributes.put("complex", nestedMap);
        customOAuth2User = new CustomOAuth2User(mockUser, complexAttributes);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertNotNull(result);
        assertTrue(result.containsKey("complex"));
        assertEquals(nestedMap, result.get("complex"));
    }

    @Test
    @DisplayName("Should handle large attributes map")
    void testGetAttributesWithLargeMap() {
        Map<String, Object> largeAttributes = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            largeAttributes.put("key" + i, "value" + i);
        }
        customOAuth2User = new CustomOAuth2User(mockUser, largeAttributes);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertNotNull(result);
        assertEquals(100, result.size());
        assertEquals("value50", result.get("key50"));
    }

    @Test
    @DisplayName("Should handle null attributes map in constructor")
    void testConstructorWithNullAttributes() {
        customOAuth2User = new CustomOAuth2User(mockUser, null);

        assertNotNull(customOAuth2User);
        assertNull(customOAuth2User.getAttributes());
    }

    @Test
    @DisplayName("Should handle null user in constructor")
    void testConstructorWithNullUser() {
        customOAuth2User = new CustomOAuth2User(null, testAttributes);

        assertNotNull(customOAuth2User);
        assertNull(customOAuth2User.getUser());
    }

    @Test
    @DisplayName("Should return same object reference for attributes")
    void testGetAttributesReturnsSameReference() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Map<String, Object> result1 = customOAuth2User.getAttributes();
        Map<String, Object> result2 = customOAuth2User.getAttributes();

        assertSame(result1, result2);
        assertSame(testAttributes, result1);
    }

    @Test
    @DisplayName("Should handle attributes with numeric values")
    void testGetAttributesWithNumericValues() {
        Map<String, Object> attributesWithNumbers = new HashMap<>();
        attributesWithNumbers.put("integer", 42);
        attributesWithNumbers.put("long", 9999999999L);
        attributesWithNumbers.put("double", 3.14159);
        attributesWithNumbers.put("float", 2.71f);

        customOAuth2User = new CustomOAuth2User(mockUser, attributesWithNumbers);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertEquals(42, result.get("integer"));
        assertEquals(9999999999L, result.get("long"));
        assertEquals(3.14159, result.get("double"));
        assertEquals(2.71f, result.get("float"));
    }

    @Test
    @DisplayName("Should handle attributes with boolean values")
    void testGetAttributesWithBooleanValues() {
        Map<String, Object> attributesWithBooleans = new HashMap<>();
        attributesWithBooleans.put("verified", true);
        attributesWithBooleans.put("active", false);

        customOAuth2User = new CustomOAuth2User(mockUser, attributesWithBooleans);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertTrue((Boolean) result.get("verified"));
        assertFalse((Boolean) result.get("active"));
    }

    @Test
    @DisplayName("Should return immutable single-element collection")
    void testGetAuthoritiesReturnsUnmodifiableCollection() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> authorities = customOAuth2User.getAuthorities();

        assertNotNull(authorities);
        assertThrows(UnsupportedOperationException.class, authorities::clear);
    }

    @Test
    @DisplayName("Should always return authorities collection with consistent size")
    void testGetAuthoritiesConsistency() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> authorities1 = customOAuth2User.getAuthorities();
        Collection<? extends GrantedAuthority> authorities2 = customOAuth2User.getAuthorities();

        assertEquals(authorities1.size(), authorities2.size());
        assertTrue(authorities1.stream()
                .map(GrantedAuthority::getAuthority)
                .allMatch(a -> authorities2.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                        .contains(a)));
    }

    @Test
    @DisplayName("Should return consistent authority string")
    void testGetAuthoritiesStringFormat() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> authorities = customOAuth2User.getAuthorities();
        String authorityString = authorities.iterator().next().getAuthority();

        assertEquals("ROLE_USER", authorityString);
        assertTrue(authorityString.startsWith("ROLE_"));
    }

    @Test
    @DisplayName("Should work correctly with multiple method calls in sequence")
    void testMultipleMethodCallsSequence() {
        when(mockUser.getName()).thenReturn("Sequence Test User");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        assertNotNull(customOAuth2User.getUser());
        assertNotNull(customOAuth2User.getAttributes());
        assertNotNull(customOAuth2User.getAuthorities());
        assertEquals("Sequence Test User", customOAuth2User.getName());
        assertEquals(1, customOAuth2User.getAuthorities().size());
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should maintain consistent results across multiple calls")
    void testMultipleGetMethodCalls() {
        when(mockUser.getName()).thenReturn("Consistent Name");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result1 = customOAuth2User.getName();
        String result2 = customOAuth2User.getName();

        assertEquals(result1, result2);
        verify(mockUser, times(4)).getName();
    }

    @Test
    @DisplayName("Should work with typical OAuth2 Google response attributes")
    void testWithGoogleOAuth2Attributes() {
        Map<String, Object> googleAttributes = new HashMap<>();
        googleAttributes.put("sub", "google-user-id-123");
        googleAttributes.put("name", "Google User");
        googleAttributes.put("email", "user@gmail.com");
        googleAttributes.put("email_verified", true);
        googleAttributes.put("picture", "https://example.com/photo.jpg");

        customOAuth2User = new CustomOAuth2User(mockUser, googleAttributes);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertEquals(5, result.size());
        assertEquals("user@gmail.com", result.get("email"));
        assertTrue((Boolean) result.get("email_verified"));
    }

    @Test
    @DisplayName("Should work with typical OAuth2 GitHub response attributes")
    void testWithGitHubOAuth2Attributes() {
        Map<String, Object> githubAttributes = new HashMap<>();
        githubAttributes.put("login", "github-user");
        githubAttributes.put("id", 987654);
        githubAttributes.put("avatar_url", "https://avatars.githubusercontent.com/u/987654");
        githubAttributes.put("type", "User");

        customOAuth2User = new CustomOAuth2User(mockUser, githubAttributes);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertEquals(4, result.size());
        assertEquals(987654, result.get("id"));
        assertEquals("User", result.get("type"));
    }

    @Test
    @DisplayName("Should handle constructor being called multiple times with different data")
    void testMultipleInstantiationsWithDifferentData() {
        CustomOAuth2User user1 = new CustomOAuth2User(mockUser, testAttributes);

        Map<String, Object> secondAttributes = new HashMap<>();
        secondAttributes.put("id", "second");
        CustomOAuth2User user2 = new CustomOAuth2User(mockUser, secondAttributes);

        assertNotNull(user1);
        assertNotNull(user2);
        assertNotEquals(user1.getAttributes(), user2.getAttributes());
    }

    @Test
    @DisplayName("Should correctly implement OAuth2User interface contract")
    void testOAuth2UserInterfaceContract() {
        when(mockUser.getName()).thenReturn("Interface Test User");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        assertNotNull(customOAuth2User.getAttributes());
        assertNotNull(customOAuth2User.getAuthorities());
        assertNotNull(customOAuth2User.getName());
        verify(mockUser, times(2)).getName();
    }

    @Test
    @DisplayName("Should return same user reference from getUser()")
    void testGetUserReturnsSameReference() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Users user1 = customOAuth2User.getUser();
        Users user2 = customOAuth2User.getUser();

        assertSame(user1, user2);
        assertSame(mockUser, user1);
    }

    @Test
    @DisplayName("Should handle user with null name falling back to username")
    void testGetNameFallbackFlow() {
        when(mockUser.getName()).thenReturn(null);
        when(mockUser.getUsername()).thenReturn("testuser");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals("testuser", result);
        verify(mockUser, times(1)).getName();
        verify(mockUser, times(1)).getUsername();
    }

    @Test
    @DisplayName("Should handle attributes with list values")
    void testGetAttributesWithListValues() {
        Map<String, Object> attributesWithList = new HashMap<>();
        attributesWithList.put("roles", java.util.Arrays.asList("admin", "user"));

        customOAuth2User = new CustomOAuth2User(mockUser, attributesWithList);

        Map<String, Object> result = customOAuth2User.getAttributes();

        assertNotNull(result.get("roles"));
        assertEquals(2, ((java.util.List<?>) result.get("roles")).size());
    }

    @Test
    @DisplayName("Should preserve user reference after multiple operations")
    void testUserReferencePreservation() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Users user1 = customOAuth2User.getUser();
        Map<String, Object> attrs = customOAuth2User.getAttributes();
        Collection<? extends GrantedAuthority> auth = customOAuth2User.getAuthorities();
        Users user2 = customOAuth2User.getUser();

        assertSame(user1, user2);
        assertSame(mockUser, user2);
    }

    @Test
    @DisplayName("Should handle attributes being modified externally")
    void testAttributeIndependence() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Map<String, Object> attrs1 = customOAuth2User.getAttributes();
        attrs1.put("new_key", "new_value");

        Map<String, Object> attrs2 = customOAuth2User.getAttributes();

        assertTrue(attrs2.containsKey("new_key"));
        assertEquals("new_value", attrs2.get("new_key"));
    }

    @Test
    @DisplayName("Should handle authorities being fetched multiple times")
    void testMultipleAuthoritiesFetch() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> auth1 = customOAuth2User.getAuthorities();
        Collection<? extends GrantedAuthority> auth2 = customOAuth2User.getAuthorities();
        Collection<? extends GrantedAuthority> auth3 = customOAuth2User.getAuthorities();

        assertEquals(auth1.size(), auth2.size());
        assertEquals(auth2.size(), auth3.size());
    }

    @Test
    @DisplayName("Should handle both constructor parameters being null")
    void testBothParametersNull() {
        customOAuth2User = new CustomOAuth2User(null, null);

        assertNull(customOAuth2User.getUser());
        assertNull(customOAuth2User.getAttributes());
        assertNotNull(customOAuth2User.getAuthorities());
    }

    @Test
    @DisplayName("Should maintain immutability of authorities across calls")
    void testAuthoritiesImmutabilityAcrossCalls() {
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Collection<? extends GrantedAuthority> auth1 = customOAuth2User.getAuthorities();

        assertThrows(UnsupportedOperationException.class, () -> {
            auth1.clear();
        });

        Collection<? extends GrantedAuthority> auth2 = customOAuth2User.getAuthorities();
        assertEquals(1, auth2.size());
    }

    @Test
    @DisplayName("Should handle name resolution with fallback")
    void testNameResolutionWithBothNullAndNonNull() {
        when(mockUser.getName()).thenReturn(null);
        when(mockUser.getUsername()).thenReturn("fallback_user");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        String result = customOAuth2User.getName();

        assertEquals("fallback_user", result);
    }

    @Test
    @DisplayName("Should handle interface contract with all methods callable")
    void testAllMethodsCallable() {
        when(mockUser.getName()).thenReturn("Test");
        customOAuth2User = new CustomOAuth2User(mockUser, testAttributes);

        Map<String, Object> attrs = customOAuth2User.getAttributes();
        Collection<? extends GrantedAuthority> auth = customOAuth2User.getAuthorities();
        String name = customOAuth2User.getName();

        assertNotNull(attrs);
        assertNotNull(auth);
        assertNotNull(name);
    }

    @Test
    @DisplayName("Should preserve original attributes without modification")
    void testAttributesPreservation() {
        Map<String, Object> originalAttrs = new HashMap<>();
        originalAttrs.put("key", "value");

        customOAuth2User = new CustomOAuth2User(mockUser, originalAttrs);
        Map<String, Object> retrieved = customOAuth2User.getAttributes();

        assertTrue(retrieved.containsKey("key"));
        assertEquals("value", retrieved.get("key"));
    }
}
