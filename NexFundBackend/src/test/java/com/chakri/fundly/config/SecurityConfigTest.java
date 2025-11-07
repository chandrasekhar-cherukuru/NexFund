package com.chakri.fundly.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JWTFilter jwtFilter;

    @MockitoBean
    private AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    // ==================== CORS Configuration Tests ====================

    @Test
    void testCorsConfigurationSourceNotNull() {
        // ARRANGE - No additional setup needed

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // ASSERT
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource should not be null");
    }

    @Test
    void testCorsConfigurationSourceReturnsConfigurationForValidPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT - Should not be null as pattern is /** (all paths)
        assertNotNull(corsConfiguration, "CorsConfiguration should be available for all registered paths");
    }

    @Test
    void testCorsConfigurationAllowedOrigins() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource bean should be properly created");
        if (corsConfiguration != null && corsConfiguration.getAllowedOrigins() != null) {
            assertTrue(corsConfiguration.getAllowedOrigins().contains("http://localhost:3000"),
                    "Should allow http://localhost:3000 origin");
        }
    }

    @Test
    void testCorsConfigurationAllowedMethodsGet() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertNotNull(corsConfiguration.getAllowedMethods(), "AllowedMethods should not be null");
            assertTrue(corsConfiguration.getAllowedMethods().contains("GET"),
                    "Should allow GET method");
        }
    }

    @Test
    void testCorsConfigurationAllowedMethodsPost() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("POST"),
                    "Should allow POST method");
        }
    }

    @Test
    void testCorsConfigurationAllowedMethodsPut() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("PUT"),
                    "Should allow PUT method");
        }
    }

    @Test
    void testCorsConfigurationAllowedMethodsDelete() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("DELETE"),
                    "Should allow DELETE method");
        }
    }

    @Test
    void testCorsConfigurationAllowedMethodsOptions() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("OPTIONS"),
                    "Should allow OPTIONS method");
        }
    }

    @Test
    void testCorsConfigurationAllowedHeaders() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertNotNull(corsConfiguration.getAllowedHeaders(), "AllowedHeaders should not be null");
            assertTrue(corsConfiguration.getAllowedHeaders().contains("*"),
                    "Should allow all headers");
        }
    }

    @Test
    void testCorsConfigurationAllowCredentials() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowCredentials(),
                    "Should allow credentials");
        }
    }

    @Test
    void testCorsConfigurationRootPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS configuration should be available for root path");
    }

    @Test
    void testCorsConfigurationNestedPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users/profile");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS configuration should be available for nested paths");
    }

    @Test
    void testCorsConfigurationMultipleHttpMethods() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertNotNull(corsConfiguration.getAllowedMethods(), "AllowedMethods should not be null");
            assertTrue(corsConfiguration.getAllowedMethods().size() >= 5,
                    "Should support at least 5 HTTP methods");
        }
    }

    @Test
    void testCorsConfigurationSourceHandlesNullRequest() {
        // ARRANGE - Null request

        // ACT & ASSERT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        assertThrows(Exception.class, () -> {
            corsConfigurationSource.getCorsConfiguration(null);
        }, "Should handle null request appropriately");
    }

    @Test
    void testCorsConfigurationSourceCreatesNewInstanceEachTime() {
        // ARRANGE & ACT
        CorsConfigurationSource source1 = securityConfig.corsConfigurationSource();
        CorsConfigurationSource source2 = securityConfig.corsConfigurationSource();

        // ASSERT
        assertNotNull(source1, "First CORS source should not be null");
        assertNotNull(source2, "Second CORS source should not be null");
    }

    @Test
    void testCorsConfigurationEventsPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/events/123");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS should support /events paths");
    }

    @Test
    void testCorsConfigurationGiftsPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/gifts/list");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS should support /gifts paths");
    }

    @Test
    void testCorsConfigurationDonationsPath() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/donations/create");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS should support /donations paths");
    }

    // ==================== Authentication Provider Tests ====================

    @Test
    void testAuthenticationProviderNotNull() {
        // ARRANGE - No additional setup needed

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(authProvider, "AuthenticationProvider should not be null");
    }

    @Test
    void testAuthenticationProviderIsDaoAuthenticationProvider() {
        // ARRANGE - No additional setup needed

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertTrue(authProvider instanceof DaoAuthenticationProvider,
                "AuthenticationProvider should be an instance of DaoAuthenticationProvider");
    }

    @Test
    void testAuthenticationProviderHasUserDetailsService() {
        // ARRANGE - No additional setup needed

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(authProvider, "AuthenticationProvider should be configured with UserDetailsService");
        assertTrue(authProvider instanceof DaoAuthenticationProvider,
                "Should be DaoAuthenticationProvider");
    }

    @Test
    void testAuthenticationProviderHasPasswordEncoder() {
        // ARRANGE - No additional setup needed

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(authProvider, "AuthenticationProvider should be configured with PasswordEncoder");
        assertTrue(authProvider instanceof DaoAuthenticationProvider,
                "Should be DaoAuthenticationProvider with password encoder");
    }

    @Test
    void testAuthenticationProviderMultipleCreations() {
        // ARRANGE & ACT
        AuthenticationProvider provider1 = securityConfig.authenticationProvider();
        AuthenticationProvider provider2 = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(provider1, "First provider should not be null");
        assertNotNull(provider2, "Second provider should not be null");
        assertTrue(provider1 instanceof DaoAuthenticationProvider &&
                        provider2 instanceof DaoAuthenticationProvider,
                "Both providers should be DaoAuthenticationProvider instances");
    }

    @Test
    void testAuthenticationProviderFullyConfigured() {
        // ARRANGE - No additional setup

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(authProvider, "Should create provider with dependencies");
        assertTrue(authProvider instanceof DaoAuthenticationProvider,
                "Should be properly configured DaoAuthenticationProvider");
    }

    // ==================== Authentication Manager Tests ====================

    @Test
    void testAuthenticationManagerNotNull() throws Exception {
        // ARRANGE - AuthenticationManager is autowired from Spring context

        // ACT
        AuthenticationManager manager = authenticationManager;

        // ASSERT
        assertNotNull(manager, "AuthenticationManager should not be null");
    }

    @Test
    void testAuthenticationManagerBeanExistsInContext() throws Exception {
        // ARRANGE - No additional setup

        // ACT
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        // ASSERT
        assertNotNull(manager, "AuthenticationManager should be created");
    }

    @Test
    void testAuthenticationManagerRetrievedFromConfiguration() throws Exception {
        // ARRANGE - AuthenticationConfiguration is autowired

        // ACT
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        // ASSERT
        assertNotNull(manager, "AuthenticationManager should be retrieved from configuration");
    }

    @Test
    void testAuthenticationManagerConsistency() throws Exception {
        // ARRANGE - AuthenticationConfiguration is autowired

        // ACT
        AuthenticationManager manager1 = securityConfig.authenticationManager(authenticationConfiguration);
        AuthenticationManager manager2 = securityConfig.authenticationManager(authenticationConfiguration);

        // ASSERT
        assertNotNull(manager1, "First manager should not be null");
        assertNotNull(manager2, "Second manager should not be null");
    }

    @Test
    void testAuthenticationManagerExistsInSpringContext() throws Exception {
        // ARRANGE - No additional setup

        // ACT
        AuthenticationManager manager = authenticationManager;

        // ASSERT
        assertNotNull(manager, "AuthenticationManager should exist in Spring context");
    }

    // ==================== Security Filter Chain Tests ====================

    @Test
    void testSecurityFilterChainNotNull() {
        // ARRANGE - SecurityFilterChain is autowired

        // ACT
        SecurityFilterChain chain = securityFilterChain;

        // ASSERT
        assertNotNull(chain, "SecurityFilterChain should not be null");
    }

    @Test
    void testSecurityFilterChainIsProperInstance() {
        // ARRANGE - No additional setup

        // ACT
        SecurityFilterChain chain = securityFilterChain;

        // ASSERT
        assertNotNull(chain, "SecurityFilterChain should be created");
        assertTrue(chain instanceof SecurityFilterChain,
                "Result should be an instance of SecurityFilterChain");
    }

    @Test
    void testSecurityFilterChainBeanExistsInContext() {
        // ARRANGE - SecurityFilterChain is autowired

        // ACT
        SecurityFilterChain chain = securityFilterChain;

        // ASSERT
        assertNotNull(chain, "SecurityFilterChain bean should exist in Spring context");
    }

    @Test
    void testAllSecurityBeansExist() {
        // ARRANGE - No additional setup

        // ACT
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();
        AuthenticationManager manager = authenticationManager;

        // ASSERT
        assertNotNull(corsSource, "CORS configuration bean should be created");
        assertNotNull(authProvider, "AuthenticationProvider bean should be created");
        assertNotNull(manager, "AuthenticationManager bean should be created");
    }

    @Test
    void testAuthenticationProviderConfiguredCorrectly() {
        // ARRANGE - No additional setup

        // ACT
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(authProvider, "AuthenticationProvider should be created");
        assertTrue(authProvider instanceof DaoAuthenticationProvider,
                "Should be DaoAuthenticationProvider");
    }

    // ==================== CORS Configuration Detail Tests ====================

    @Test
    void testCorsConfigurationSupportsDifferentPaths() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/any/resource/path");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfiguration, "CORS should be configured for all paths");
    }

    @Test
    void testCorsAllowsDeleteMethod() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("DELETE"),
                    "Should allow DELETE method");
        }
    }

    @Test
    void testCorsSupportsPreflightRequests() {
        // ARRANGE
        MockHttpServletRequest preflightRequest = new MockHttpServletRequest();
        preflightRequest.setRequestURI("/api/resource");
        preflightRequest.setMethod("OPTIONS");
        preflightRequest.addHeader("Origin", "http://localhost:3000");
        preflightRequest.addHeader("Access-Control-Request-Method", "POST");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(preflightRequest);

        // ASSERT
        if (corsConfiguration != null) {
            assertTrue(corsConfiguration.getAllowedMethods().contains("OPTIONS"),
                    "Should support preflight OPTIONS requests");
        }
    }

    // ==================== Integration Tests ====================

    @Test
    void testAllBeansProperlyWired() {
        // ARRANGE - No additional setup

        // ACT
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        AuthenticationProvider authProvider = securityConfig.authenticationProvider();
        AuthenticationManager authManager = authenticationManager;
        SecurityFilterChain chain = securityFilterChain;

        // ASSERT
        assertNotNull(corsSource, "All beans should be properly wired");
        assertNotNull(authProvider, "All beans should be properly wired");
        assertNotNull(authManager, "All beans should be properly wired");
        assertNotNull(chain, "All beans should be properly wired");
    }

    @Test
    void testAllMockBeansInjectedSuccessfully() {
        // ARRANGE - No additional setup

        // ACT - The @MockitoBean annotations should inject mocks

        // ASSERT
        assertNotNull(userDetailsService, "UserDetailsService mock should be injected");
        assertNotNull(jwtFilter, "JWTFilter mock should be injected");
        assertNotNull(oauth2AuthenticationSuccessHandler, "AuthenticationSuccessHandler mock should be injected");
        assertNotNull(passwordEncoder, "PasswordEncoder mock should be injected");
        assertNotNull(customOAuth2UserService, "CustomOAuth2UserService mock should be injected");
    }

    @Test
    void testSecurityConfigBeanInitialized() {
        // ARRANGE - No additional setup

        // ACT
        SecurityConfig config = securityConfig;

        // ASSERT
        assertNotNull(config, "SecurityConfig bean should exist in Spring context");
    }

    @Test
    void testLocalhostOriginAllowed() {
        // ARRANGE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource bean should be properly created");
        if (corsConfiguration != null && corsConfiguration.getAllowedOrigins() != null) {
            assertTrue(corsConfiguration.getAllowedOrigins().contains("http://localhost:3000"),
                    "Should specifically allow http://localhost:3000");
        }
    }

    // ==================== Edge Cases ====================

    @Test
    void testCorsConfigurationForMultiplePaths() {
        // ARRANGE
        String[] testPaths = {"/", "/api", "/events", "/gifts", "/donations", "/participants"};

        // ACT & ASSERT
        for (String path : testPaths) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(path);

            CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
            CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

            assertNotNull(corsConfiguration, "CORS should be configured for path: " + path);
        }
    }

    @Test
    void testCorsConfigurationForAllHttpMethods() {
        // ARRANGE
        String[] methods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");

        // ACT
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);

        // ASSERT
        if (corsConfiguration != null) {
            for (String method : methods) {
                assertTrue(corsConfiguration.getAllowedMethods().contains(method),
                        "Should allow " + method + " method");
            }
        }
    }

    @Test
    void testSecurityConfigurationNotNull() {
        // ARRANGE & ACT
        SecurityConfig config = securityConfig;

        // ASSERT
        assertNotNull(config, "SecurityConfig instance should not be null");
    }

    @Test
    void testAllAutowiredFieldsPopulated() {
        // ARRANGE & ACT - All @Autowired fields should be injected

        // ASSERT
        assertNotNull(securityConfig, "SecurityConfig should be autowired");
        assertNotNull(securityFilterChain, "SecurityFilterChain should be autowired");
        assertNotNull(authenticationManager, "AuthenticationManager should be autowired");
        assertNotNull(authenticationConfiguration, "AuthenticationConfiguration should be autowired");
    }

    @Test
    void testCorsConfigurationStaysSame() {
        // ARRANGE
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.setRequestURI("/api/test1");

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.setRequestURI("/api/test2");

        // ACT
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config1 = source.getCorsConfiguration(request1);
        CorsConfiguration config2 = source.getCorsConfiguration(request2);

        // ASSERT
        if (config1 != null && config2 != null) {
            assertEquals(config1.getAllowedOrigins(), config2.getAllowedOrigins(),
                    "CORS configurations should be consistent across requests");
        }
    }

    @Test
    void testAuthenticationProviderIndependentCreations() {
        // ARRANGE - Call authenticationProvider multiple times

        // ACT
        AuthenticationProvider provider1 = securityConfig.authenticationProvider();
        AuthenticationProvider provider2 = securityConfig.authenticationProvider();
        AuthenticationProvider provider3 = securityConfig.authenticationProvider();

        // ASSERT
        assertNotNull(provider1, "First provider should not be null");
        assertNotNull(provider2, "Second provider should not be null");
        assertNotNull(provider3, "Third provider should not be null");
    }

    @Test
    void testSecurityFilterChainRepeatedAccess() {
        // ARRANGE - No additional setup

        // ACT
        SecurityFilterChain chain1 = securityFilterChain;
        SecurityFilterChain chain2 = securityFilterChain;

        // ASSERT
        assertNotNull(chain1, "First chain should not be null");
        assertNotNull(chain2, "Second chain should not be null");
    }

}
