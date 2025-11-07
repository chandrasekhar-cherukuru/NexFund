package com.chakri.fundly.config;

import com.chakri.fundly.service.JWTService;
import com.chakri.fundly.model.AuthProvider;
import com.chakri.fundly.model.Users;
import com.chakri.fundly.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private OAuth2User oauth2User;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler handler;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_PROVIDER_ID = "google_12345";
    private static final String REDIRECT_URI = "http://localhost:3000/auth/callback";
    private static final String JWT_TOKEN = "jwt_token_xyz123";
    private static final String ENCODED_PASSWORD = "encoded_password_hash";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(handler, "redirectUri", REDIRECT_URI);
    }

    @Test
    void testOidcUserAuthenticationWithExistingUser() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setName(TEST_NAME);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        String expectedRedirectUrl = REDIRECT_URI + "?token=" + URLEncoder.encode(JWT_TOKEN, StandardCharsets.UTF_8);
        verify(response).sendRedirect(expectedRedirectUrl);
        verify(userRepo, never()).save(any());
        verify(jwtService).generateToken(TEST_EMAIL);
    }

    @Test
    void testOidcUserAuthenticationWithNewUser() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getEmail().equals(TEST_EMAIL) &&
                        user.getName().equals(TEST_NAME) &&
                        user.getUsername().equals(TEST_EMAIL) &&
                        user.getPassword().equals(ENCODED_PASSWORD) &&
                        user.getAuthProvider() == AuthProvider.GOOGLE &&
                        user.getProviderId().equals(TEST_PROVIDER_ID)
        ));
        verify(jwtService).generateToken(TEST_EMAIL);
        verify(response).sendRedirect(contains(JWT_TOKEN));
    }

    @Test
    void testOAuth2UserAuthenticationWithExistingUser() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setName(TEST_NAME);

        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("name")).thenReturn(TEST_NAME);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect(contains(JWT_TOKEN));
        verify(userRepo, never()).save(any());
        verify(jwtService).generateToken(TEST_EMAIL);
    }

    @Test
    void testOAuth2UserAuthenticationWithNewUser() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("name")).thenReturn(TEST_NAME);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getEmail().equals(TEST_EMAIL) &&
                        user.getName().equals(TEST_NAME) &&
                        user.getUsername().equals(TEST_EMAIL)
        ));
        verify(jwtService).generateToken(TEST_EMAIL);
        verify(response).sendRedirect(contains(JWT_TOKEN));
    }

    @Test
    void testOidcUserWithNullEmail() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(null);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        assertThrows(IOException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
        verify(userRepo, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testOAuth2UserWithNullEmail() {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(null);
        when(oauth2User.getAttribute("name")).thenReturn(TEST_NAME);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_PROVIDER_ID);

        assertThrows(IOException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
        verify(userRepo, never()).findByEmail(any());
    }

    @Test
    void testOidcUserWithNullName() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(null);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getName().equals(TEST_EMAIL)
        ));
        verify(response).sendRedirect(contains(JWT_TOKEN));
    }

    @Test
    void testOAuth2UserWithNullName() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("name")).thenReturn(null);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getName().equals(TEST_EMAIL)
        ));
    }

    @Test
    void testOidcUserWithNullProviderId() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(null);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getProviderId() == null
        ));
    }

    @Test
    void testOAuth2UserWithNullProviderId() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("name")).thenReturn(TEST_NAME);
        when(oauth2User.getAttribute("sub")).thenReturn(null);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getProviderId() == null
        ));
    }

    @Test
    void testEmailWithSpecialCharacters() throws IOException {
        String specialEmail = "test+special@example.co.uk";
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(specialEmail);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(specialEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(specialEmail)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getEmail().equals(specialEmail)
        ));
    }

    @Test
    void testVeryLongName() throws IOException {
        String longName = "A".repeat(1000);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(longName);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getName().equals(longName)
        ));
    }

    @Test
    void testEmptyStringName() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn("");
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getName().equals("")
        ));
    }

    @Test
    void testJwtTokenWithSpecialCharacters() throws IOException {
        String tokenWithSpecialChars = "jwt.token+special=chars/data";
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(tokenWithSpecialChars);

        handler.onAuthenticationSuccess(request, response, authentication);

        String encodedToken = URLEncoder.encode(tokenWithSpecialChars, StandardCharsets.UTF_8);
        String expectedUrl = REDIRECT_URI + "?token=" + encodedToken;
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void testUnknownPrincipalType() {
        String unknownPrincipal = "unknown_principal_string";
        when(authentication.getPrincipal()).thenReturn(unknownPrincipal);

        assertThrows(IOException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
        verify(userRepo, never()).findByEmail(any());
    }

    @Test
    void testUserRepoFindThrowsException() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
    }

    @Test
    void testUserRepoSaveThrowsException() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(userRepo.save(any())).thenThrow(new RuntimeException("save error"));

        assertThrows(RuntimeException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
    }

    @Test
    void testJwtServiceThrowsException() {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);
        existingUser.setEmail(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenThrow(new RuntimeException("JWT error"));

        assertThrows(RuntimeException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
    }

    @Test
    void testResponseSendRedirectThrowsException() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);
        doThrow(new IOException("response error")).when(response).sendRedirect(anyString());

        assertThrows(IOException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );
    }

    @Test
    void testOidcUserBranchExecution() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(oidcUser).getEmail();
        verify(oidcUser).getFullName();
        verify(oidcUser).getSubject();
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testOAuth2UserBranchExecution() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(TEST_EMAIL);
        when(oauth2User.getAttribute("name")).thenReturn(TEST_NAME);
        when(oauth2User.getAttribute("sub")).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(oauth2User, atLeastOnce()).getAttribute("email");
        verify(oauth2User, atLeastOnce()).getAttribute("name");
        verify(oauth2User, atLeastOnce()).getAttribute("sub");
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testExistingUserBranchExecution() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);
        existingUser.setEmail(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo, never()).save(any());
        verify(jwtService).generateToken(TEST_EMAIL);
    }

    @Test
    void testNewUserBranchExecution() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(any(Users.class));
        verify(jwtService).generateToken(TEST_EMAIL);
    }

    @Test
    void testPasswordEncodingFormat() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());

        String capturedPassword = passwordCaptor.getValue();
        assertTrue(capturedPassword.startsWith("oauth2_user_"));
        assertTrue(capturedPassword.length() > 12);
    }

    @Test
    void testAuthProviderSetToGoogle() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getAuthProvider() == AuthProvider.GOOGLE
        ));
    }

    @Test
    void testCompleteFlowWithUrlEncoding() throws IOException {
        String complexToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(complexToken);

        handler.onAuthenticationSuccess(request, response, authentication);

        String encodedToken = URLEncoder.encode(complexToken, StandardCharsets.UTF_8);
        String expectedUrl = REDIRECT_URI + "?token=" + encodedToken;
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void testRedirectUriWithTrailingSlash() throws IOException {
        String redirectUriWithSlash = REDIRECT_URI + "/";
        ReflectionTestUtils.setField(handler, "redirectUri", redirectUriWithSlash);

        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        String expectedUrl = redirectUriWithSlash + "?token=" + URLEncoder.encode(JWT_TOKEN, StandardCharsets.UTF_8);
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void testRedirectUriWithExistingParameters() throws IOException {
        String redirectUriWithParams = REDIRECT_URI + "?session=xyz";
        ReflectionTestUtils.setField(handler, "redirectUri", redirectUriWithParams);

        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        String expectedUrl = redirectUriWithParams + "?token=" + URLEncoder.encode(JWT_TOKEN, StandardCharsets.UTF_8);
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void testMultipleSuccessfulAuthentications() throws IOException {
        Users user1 = new Users();
        user1.setUsername("user1@example.com");
        Users user2 = new Users();
        user2.setUsername("user2@example.com");

        when(userRepo.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));
        when(jwtService.generateToken("user1@example.com")).thenReturn("token1");
        when(jwtService.generateToken("user2@example.com")).thenReturn("token2");

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("user1@example.com");
        when(oidcUser.getFullName()).thenReturn("User One");
        when(oidcUser.getSubject()).thenReturn("sub1");
        handler.onAuthenticationSuccess(request, response, authentication);

        reset(authentication, oidcUser);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("user2@example.com");
        when(oidcUser.getFullName()).thenReturn("User Two");
        when(oidcUser.getSubject()).thenReturn("sub2");
        handler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtService).generateToken("user1@example.com");
        verify(jwtService).generateToken("user2@example.com");
        verify(response, times(2)).sendRedirect(anyString());
    }

    @Test
    void testMockInvocationCounts() throws IOException {
        Users existingUser = new Users();
        existingUser.setUsername(TEST_EMAIL);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(authentication, times(1)).getPrincipal();
        verify(oidcUser, times(1)).getEmail();
        verify(oidcUser, times(1)).getFullName();
        verify(oidcUser, times(1)).getSubject();
        verify(userRepo, times(1)).findByEmail(TEST_EMAIL);
        verify(jwtService, times(1)).generateToken(TEST_EMAIL);
        verify(response, times(1)).sendRedirect(anyString());
    }

    @Test
    void testUsernameSetToEmail() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(TEST_EMAIL);
        when(oidcUser.getFullName()).thenReturn(TEST_NAME);
        when(oidcUser.getSubject()).thenReturn(TEST_PROVIDER_ID);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_EMAIL)).thenReturn(JWT_TOKEN);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepo).save(argThat(user ->
                user.getUsername().equals(TEST_EMAIL)
        ));
    }
}
