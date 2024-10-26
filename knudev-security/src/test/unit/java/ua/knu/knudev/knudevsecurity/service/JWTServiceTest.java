package ua.knu.knudev.knudevsecurity.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {

    private JWTService jwtService;
    private static final Integer accessTokenExpirationInMillis = 600000; // 1 minute
    private static final Integer refreshTokenExpirationInMillis = 1200000; // 2 minutes
    private static final String issuerName = "testIssuer";

    private AccountAuth account;

    @BeforeEach
    public void setUp() {
        jwtService = new JWTService(
                accessTokenExpirationInMillis,
                refreshTokenExpirationInMillis,
                issuerName
        );

        account = AccountAuth.builder()
                .email("test@knu.ua")
                .roles(Set.of(AccountRole.INTERN))
                .build();
    }

    @Test
    public void testGenerateTokensAndValidate() {
        Tokens tokens = jwtService.generateTokens(account);

        assertNotNull(tokens);
        assertNotNull(tokens.accessToken());
        assertNotNull(tokens.refreshToken());

        assertTrue(jwtService.isTokenValid(tokens.accessToken(), account));
        assertTrue(jwtService.isTokenValid(tokens.refreshToken(), account));
    }

    @Test
    public void testExtractUsername() {
        Tokens tokens = jwtService.generateTokens(account);

        String usernameFromAccessToken = jwtService.extractUsername(tokens.accessToken());
        assertEquals("test@knu.ua", usernameFromAccessToken);

        String usernameFromRefreshToken = jwtService.extractUsername(tokens.refreshToken());
        assertEquals("test@knu.ua", usernameFromRefreshToken);
    }

    @Test
    public void testExtractAccountRole() {
        Tokens tokens = jwtService.generateTokens(account);

        Set<String> rolesFromAccessToken = jwtService.extractAccountRole(tokens.accessToken());
        assertTrue(rolesFromAccessToken.contains("INTERN"));
    }

    @Test
    public void testIsAccessToken() {
        Tokens tokens = jwtService.generateTokens(account);

        assertTrue(jwtService.isAccessToken(tokens.accessToken()));
        assertFalse(jwtService.isAccessToken(tokens.refreshToken()));
    }

    @Test
    public void testExpiredToken() {
        JWTService shortLivedJwtService = new JWTService(
                100,
                100,
                issuerName
        );

        Tokens tokens = shortLivedJwtService.generateTokens(account);


        assertThrows(ExpiredJwtException.class, () ->
                shortLivedJwtService.isTokenValid(tokens.accessToken(), account));

        assertThrows(ExpiredJwtException.class, () ->
                shortLivedJwtService.isTokenValid(tokens.accessToken(), account));
    }

    @Test
    public void testInvalidSignature() {
        Tokens tokens = jwtService.generateTokens(account);

        String tamperedToken = tokens.accessToken() + "tampered";

        assertFalse(jwtService.isTokenValid(tamperedToken, account));
    }

    @Test
    public void testExtractClaimWithInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    public void testIsTokenValidWithDifferentUserDetails() {
        Tokens tokens = jwtService.generateTokens(account);

        AccountAuth differentUser = AccountAuth.builder()
                .email("anotherTest@knu.ua")
                .roles(Set.of(AccountRole.DEVELOPER, AccountRole.HEAD_MANAGER))
                .build();

        assertFalse(jwtService.isTokenValid(tokens.accessToken(), differentUser));
    }
}
