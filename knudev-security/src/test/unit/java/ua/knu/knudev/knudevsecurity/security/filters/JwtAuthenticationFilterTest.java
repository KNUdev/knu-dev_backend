package ua.knu.knudev.knudevsecurity.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.knu.knudev.knudevsecurity.service.JWTService;

import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.knudevsecurity.util.TestsUtils.buildJWT;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    private static final String TEST_JWT_TOKEN = "jwtToken";
    private static final String TEST_USERNAME = "testUsername";
    private static final String SECURED_URL = "/secured-endpoint-path";

    @Mock
    private JWTService jwtService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FiltersSharedLogicContainer sharedLogicContainer;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Should bypass JWT validation and authenticate requests to public URLs")
    public void should_NotValidateJWTAndAuthenticate_When_RequestIsForPublicURL() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer, times(0)).extractJWTHeader(request);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should not authenticate requests without a JWT header")
    public void should_NotAuthenticate_When_RequestHasNoJWTHeader() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer).extractJWTHeader(request);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should not authenticate requests with JWT header missing 'Bearer' prefix")
    public void should_NotAuthenticate_When_RequestHasJWTHeaderButNoBearerPrefix() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn("SomeToken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer).extractJWTHeader(request);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should respond with 403 when the JWT header contains a refresh token instead of an access token")
    public void should_Throw403Exception_When_BearerHeaderIsRefreshToken() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(getJWT());
        when(jwtService.extractUsername(TEST_JWT_TOKEN)).thenReturn("user@example.com");
        when(jwtService.isAccessToken(TEST_JWT_TOKEN)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(sharedLogicContainer).writeMessageInResponse(
                response,
                403,
                "Please enter an access token"
        );
        verify(filterChain, never()).doFilter(request, response);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Should authenticate requests with a valid JWT access token")
    public void should_Authenticate_When_JWTHeaderIsPresentAndValid() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(buildJWT(TEST_JWT_TOKEN));

        when(jwtService.extractUsername(TEST_JWT_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtService.isAccessToken(TEST_JWT_TOKEN)).thenReturn(true);
        when(jwtService.extractAccountRole(TEST_JWT_TOKEN)).thenReturn(Set.of("DEVELOPER"));
        when(jwtService.isTokenValid(eq(TEST_JWT_TOKEN), any())).thenReturn(true);

        SecurityContext securityContext = mockSecurityContext();
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Should not authenticate requests with an invalid JWT token")
    public void should_NotAuthenticate_When_JWTHeaderIsInvalid() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(getJWT());

        when(jwtService.extractUsername(TEST_JWT_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtService.isAccessToken(TEST_JWT_TOKEN)).thenReturn(true);
        when(jwtService.isTokenValid(eq(TEST_JWT_TOKEN), any())).thenReturn(false);

        SecurityContext securityContext = mockSecurityContext();
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not re-authenticate when the user is already authenticated")
    public void should_NotAuthenticate_When_UserIsAlreadyAuthenticated() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn(SECURED_URL);
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(getJWT());
        when(jwtService.extractUsername(TEST_JWT_TOKEN)).thenReturn(TEST_USERNAME);

        SecurityContext securityContext = mockSecurityContext();
        Authentication existingAuth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, never()).isAccessToken(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(jwtService);
    }

    private SecurityContext mockSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        return securityContext;
    }

    private String getJWT() {
        return buildJWT(TEST_JWT_TOKEN);
    }

}
