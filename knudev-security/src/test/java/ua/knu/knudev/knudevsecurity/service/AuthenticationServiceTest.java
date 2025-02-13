package ua.knu.knudev.knudevsecurity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;
import ua.knu.knudev.knudevsecurityapi.exception.TokenException;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private static final String TEST_ACCESS_TOKEN = "testAccessToken123";
    private static final String TEST_REFRESH_TOKEN = "testRefreshToken123";
    private static final String NEW_ACCESS_TOKEN = "newAccessToken456";
    private static final String TEST_EMAIL = "testUser@knu.ua";
    private static final String TEST_PASSWORD = "testPassword123";

    @Mock
    private AccountAuthService accountService;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    public void should_Authenticate_When_GivenValidCredentials() {
        // Arrange
        AuthenticationRequest authReq = new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);

        AccountAuth accountAuth = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(true)
                .nonLocked(true)
                .build();

        Tokens tokens = new Tokens(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.of(accountAuth));
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateTokens(accountAuth)).thenReturn(tokens);

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authReq);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());

        verify(accountService).getDomainByEmail(TEST_EMAIL);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateTokens(accountAuth);
    }

    @Test
    @DisplayName("Should throw AccountAuthException when user not found")
    public void should_ThrowAccountAuthException_When_GivenNonExistentUser() {
        // Arrange
        AuthenticationRequest authReq = new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);
        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        AccountAuthException ex = assertThrows(AccountAuthException.class,
                () -> authenticationService.authenticate(authReq));
        assertEquals("Invalid email or password", ex.getMessage());
        verify(accountService).getDomainByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(authenticationManager, jwtService);
    }

    @Test
    @DisplayName("Should throw LockedException when user account is locked")
    public void should_ThrowLockedException_When_TryToAuthenticateWithLockedUser() {
        // Arrange
        AuthenticationRequest authReq = new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);
        AccountAuth lockedAccount = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(true)
                .nonLocked(false)
                .build();

        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.of(lockedAccount));

        // Act & Assert
        assertThrows(LockedException.class, () -> authenticationService.authenticate(authReq));
        verify(accountService).getDomainByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(authenticationManager, jwtService);
    }

    @Test
    @DisplayName("Should throw DisabledException when user account is disabled")
    public void should_ThrowDisabledException_When_TryToAuthenticateWithDisabledUser() {
        // Arrange
        AuthenticationRequest authReq = new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);
        AccountAuth disabledAccount = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(false)
                .nonLocked(true)
                .build();

        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.of(disabledAccount));

        // Act & Assert
        assertThrows(DisabledException.class, () -> authenticationService.authenticate(authReq));
        verify(accountService).getDomainByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(authenticationManager, jwtService);
    }


    @Test
    @DisplayName("Should send error when Authorization header is missing")
    public void should_SendError_When_AuthorizationHeaderIsMissing() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is invalid");
    }

    @Test
    @DisplayName("Should send error when Authorization header does not start with 'Bearer '")
    public void should_SendError_When_AuthorizationHeaderDoesNotStartWithBearer() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidTokenFormat");

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is invalid");
    }

    @Test
    @DisplayName("Should throw TokenException when provided token is an access token")
    public void should_ThrowTokenException_When_ProvidedTokenIsAccessToken() {
        // Arrange
        String token = "accessToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenReturn(true);

        // Act & Assert
        TokenException ex = assertThrows(TokenException.class,
                () -> authenticationService.refreshToken(request, response));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw TokenException when token is expired")
    public void should_ThrowTokenException_When_TokenIsExpired() {
        // Arrange
        String token = "expiredRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        when(jwtService.isAccessToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // Act & Assert
        TokenException ex = assertThrows(TokenException.class,
                () -> authenticationService.refreshToken(request, response));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw TokenException when token signature is invalid")
    public void should_ThrowTokenException_When_TokenSignatureIsInvalid() {
        // Arrange
        String token = "invalidSignatureToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenThrow(new SignatureException("Invalid signature"));

        // Act & Assert
        TokenException ex = assertThrows(TokenException.class,
                () -> authenticationService.refreshToken(request, response));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw TokenException when account not found for extracted email")
    public void should_ThrowTokenException_When_AccountNotFoundForEmail() {
        // Arrange
        String token = "validRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenReturn(false);
        when(jwtService.extractEmail(token)).thenReturn(TEST_EMAIL);
        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        TokenException ex = assertThrows(TokenException.class,
                () -> authenticationService.refreshToken(request, response));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should do nothing when email extraction returns null")
    public void should_DoNothing_When_EmailExtractionReturnsNull() throws IOException {
        // Arrange
        String token = "validRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenReturn(false);
        when(jwtService.extractEmail(token)).thenReturn(null);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(response, never()).getOutputStream();
    }

    @Test
    @DisplayName("Should do nothing when token is not valid")
    public void should_DoNothing_When_TokenIsNotValid() throws IOException {
        // Arrange
        String token = "validRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenReturn(false);
        when(jwtService.extractEmail(token)).thenReturn(TEST_EMAIL);

        AccountAuth accountAuth = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(true)
                .nonLocked(true)
                .build();
        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.of(accountAuth));
        when(jwtService.isTokenValid(token, accountAuth)).thenReturn(false);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(response, never()).getOutputStream();
    }

    @Test
    @DisplayName("Should write AuthenticationResponse when token is valid")
    public void should_WriteAuthenticationResponse_When_TokenIsValid() throws IOException {
        // Arrange
        String token = "validRefreshToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.isAccessToken(token)).thenReturn(false);
        when(jwtService.extractEmail(token)).thenReturn(TEST_EMAIL);

        AccountAuth accountAuth = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(true)
                .nonLocked(true)
                .build();
        when(accountService.getDomainByEmail(TEST_EMAIL)).thenReturn(Optional.of(accountAuth));
        when(jwtService.isTokenValid(token, accountAuth)).thenReturn(true);
        when(jwtService.generateAccessToken(accountAuth)).thenReturn(NEW_ACCESS_TOKEN);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                baos.write(b);
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        ObjectMapper mapper = new ObjectMapper();
        AuthenticationResponse authResponse = mapper.readValue(baos.toByteArray(), AuthenticationResponse.class);
        assertEquals(NEW_ACCESS_TOKEN, authResponse.accessToken());
        assertEquals(token, authResponse.refreshToken());
    }
}
