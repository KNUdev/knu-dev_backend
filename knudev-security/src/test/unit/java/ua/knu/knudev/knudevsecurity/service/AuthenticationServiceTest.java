package ua.knu.knudev.knudevsecurity.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.dto.AccountAuthDto;
import ua.knu.knudev.knudevsecurity.mapper.AccountAuthMapper;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private static final String TEST_ACCESS_TOKEN = "testAccessToken123";
    private static final String TEST_REFRESH_TOKEN = "testRefreshToken123";
    private static final String TEST_EMAIL = "testUser@knu.ua";
    private static final String TEST_PASSWORD = "testPassword123";

    @Mock
    private AccountAuthService accountService;

    @Mock
    private AccountAuthMapper accountAuthMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    public void should_Authenticate_When_GivenValidCredentials() {
        // Arrange
        AuthenticationRequest authReq = getAuthRequest();

        AccountAuth accountAuth = AccountAuth.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(true)
                .nonLocked(true)
                .build();
        AccountAuthDto accountAuthDto = buildAccountDto(true, true);

        Tokens tokens = new Tokens(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        when(accountService.findByEmail(TEST_EMAIL)).thenReturn(accountAuthDto);
        when(accountAuthMapper.toDomain(accountAuthDto)).thenReturn(accountAuth);

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

        verify(accountService).findByEmail(TEST_EMAIL);
        verify(accountAuthMapper).toDomain(accountAuthDto);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateTokens(accountAuth);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when credentials are invalid")
    public void should_ThrowUsernameNotFoundException_When_GivenInvalidCredentials() {
        AuthenticationRequest authReq = getAuthRequest();

        when(accountService.findByEmail(TEST_EMAIL)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(authReq));

        verify(accountService).findByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(accountAuthMapper);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    @DisplayName("Should throw LockedException when user account is locked")
    public void should_ThrowLockedException_When_TryToAuthenticateWithLockedUser() {
        AuthenticationRequest authReq = new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);

        AccountAuthDto accountAuthDto = buildAccountDto(true, false);

        when(accountService.findByEmail(TEST_EMAIL)).thenReturn(accountAuthDto);

        assertThrows(LockedException.class, () -> authenticationService.authenticate(authReq));

        verify(accountService).findByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(accountAuthMapper);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    @DisplayName("Should throw DisabledException when user account is disabled")
    public void should_ThrowDisabledException_When_TryToAuthenticateWithDisabledUser() {
        AuthenticationRequest authReq = getAuthRequest();

        AccountAuthDto accountAuthDto = buildAccountDto(false, true);
        when(accountService.findByEmail(TEST_EMAIL)).thenReturn(accountAuthDto);

        assertThrows(DisabledException.class, () -> authenticationService.authenticate(authReq));

        verify(accountService).findByEmail(TEST_EMAIL);
        verifyNoMoreInteractions(accountAuthMapper);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(authenticationManager);
    }

    private AuthenticationRequest getAuthRequest() {
        return new AuthenticationRequest(TEST_EMAIL, TEST_PASSWORD);
    }

    private AccountAuthDto buildAccountDto(boolean isEnabled, boolean isNonLocked) {
        return AccountAuthDto.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .enabled(isEnabled)
                .nonLocked(isNonLocked)
                .build();
    }

}
