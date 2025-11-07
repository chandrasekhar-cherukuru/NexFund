package com.chakri.fundly.config;

import com.chakri.fundly.service.JWTService;
import com.chakri.fundly.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JWT Filter Tests")
class JWTFilterTest {

    @Mock private JWTService jwtService;
    @Mock private ApplicationContext context;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private MyUserDetailsService myUserDetailsService;
    @Mock private UserDetails userDetails;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @InjectMocks private JWTFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("Authorization Header Tests")
    class AuthorizationHeaderTests {

        @Test
        @DisplayName("Should skip token extraction when Authorization header is null")
        void testAuthorizationHeaderNull() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService, never()).extractUserName(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should skip token extraction when Authorization header is empty")
        void testAuthorizationHeaderEmpty() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("");
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService, never()).extractUserName(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should skip token extraction when Authorization header lacks Bearer prefix")
        void testAuthorizationHeaderInvalidPrefix() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Basic xyz123");
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService, never()).extractUserName(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should extract token with valid Bearer prefix")
        void testAuthorizationHeaderValidBearer() throws ServletException, IOException {
            String token = "validToken123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle Bearer prefix with empty token")
        void testAuthorizationHeaderBearerOnly() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");
            when(jwtService.extractUserName("")).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName("");
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Username Extraction Tests")
    class UsernameExtractionTests {

        @Test
        @DisplayName("Should skip authentication when username is null")
        void testUsernameIsNull() throws ServletException, IOException {
            String token = "validToken123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should skip authentication when username is empty")
        void testUsernameIsEmpty() throws ServletException, IOException {
            String token = "validToken123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn("");
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername("")).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should extract valid username from token")
        void testUsernameExtractedSuccessfully() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(context).getBean(eq(MyUserDetailsService.class));
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Security Context Authentication Tests")
    class SecurityContextAuthenticationTests {

        @Test
        @DisplayName("Should skip authentication when existing authentication present")
        void testExistingAuthenticationPresent() throws ServletException, IOException {
            String token = "validToken123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn("testUser");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(context, never()).getBean(eq(MyUserDetailsService.class));
            verify(jwtService, never()).validateToken(anyString(), any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should proceed with authentication when no existing authentication")
        void testNoExistingAuthentication() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(myUserDetailsService).loadUserByUsername(username);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("User Details Loading Tests")
    class UserDetailsLoadingTests {

        @Test
        @DisplayName("Should load user details by username")
        void testLoadUserDetailsByUsername() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(myUserDetailsService).loadUserByUsername(username);
            verify(jwtService).validateToken(token, userDetails);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle user details loading exception")
        void testUserDetailsLoadingException() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenThrow(new RuntimeException("User not found"));
            assertThrows(RuntimeException.class, () -> jwtFilter.doFilterInternal(request, response, filterChain));
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate token successfully")
        void testTokenValidationSuccess() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).validateToken(token, userDetails);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should skip authentication when token validation fails")
        void testTokenValidationFailure() throws ServletException, IOException {
            String token = "invalidToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(false);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).validateToken(token, userDetails);
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should validate token with correct parameters")
        void testTokenValidationWithCorrectParameters() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).validateToken(eq(token), eq(userDetails));
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Authentication Token Tests")
    class AuthenticationTokenTests {

        @Test
        @DisplayName("Should create token with user authorities")
        void testAuthenticationTokenCreatedWithAuthorities() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should set authentication details from request")
        void testAuthenticationDetailsSetFromRequest() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should set authentication in SecurityContextHolder")
        void testAuthenticationSetInSecurityContext() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Nested
    @DisplayName("Filter Chain Tests")
    class FilterChainTests {

        @Test
        @DisplayName("Should always call filterChain.doFilter")
        void testFilterChainAlwaysCalled() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should call filterChain with correct parameters")
        void testFilterChainCalledWithCorrectParameters() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(eq(request), eq(response));
        }

        @Test
        @DisplayName("Should continue filter chain after validation failure")
        void testFilterChainContinuesAfterValidationFailure() throws ServletException, IOException {
            String token = "invalidToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(false);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle token with special characters")
        void testTokenWithSpecialCharacters() throws ServletException, IOException {
            String token = "token-with_special.chars@123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle username with special characters")
        void testUsernameWithSpecialCharacters() throws ServletException, IOException {
            String token = "validToken123";
            String username = "test.user@domain.com";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(myUserDetailsService).loadUserByUsername(username);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should handle very long token")
        void testVeryLongToken() throws ServletException, IOException {
            String token = "a".repeat(1000);
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle empty authorities list")
        void testEmptyAuthoritiesList() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should handle multiple authorities")
        void testMultipleAuthorities() throws ServletException, IOException {
            String token = "validToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("Complete successful authentication flow")
        void testCompleteSuccessfulAuthenticationFlow() throws ServletException, IOException {
            String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc1NzMzNDY1MiwiZXhwIjoxNzU3MzM0NzYwfQ.validSignature";
            String username = "testuser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService).extractUserName(token);
            verify(myUserDetailsService).loadUserByUsername(username);
            verify(jwtService).validateToken(token, userDetails);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Bypass filter without Bearer token")
        void testRequestBypassesFilterWithoutBearerToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(jwtService, never()).extractUserName(anyString());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Bypass authentication when already authenticated")
        void testRequestBypassesAuthenticationWhenAlreadyAuthenticated() throws ServletException, IOException {
            String token = "validToken123";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn("testUser");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(context, never()).getBean(eq(MyUserDetailsService.class));
            verify(jwtService, never()).validateToken(anyString(), any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Continue when token validation fails")
        void testRequestContinuesWhenTokenValidationFails() throws ServletException, IOException {
            String token = "invalidToken123";
            String username = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUserName(token)).thenReturn(username);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(context.getBean(eq(MyUserDetailsService.class))).thenReturn(myUserDetailsService);
            when(myUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(false);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }
}
