package ua.knu.knudev.knudevsecurity.security.filters;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.knudevsecurity.utils.JWTSigningKeyProvider;

import javax.crypto.SecretKey;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.knudevsecurity.util.TestsUtils.buildJWT;

@ExtendWith(MockitoExtension.class)
public class JWTValidityFilterTest {

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FiltersSharedLogicContainer sharedLogicContainer;

    @Mock
    private JWTSigningKeyProvider signingKeyProvider;

    @InjectMocks
    private JWTValidityFilter jwtValidityFilter;

    @Test
    @DisplayName("Should bypass JWT validation when JWT header is not present")
    public void should_NotValidateJWTHeader_When_JWTHeaderIsNotPresent() throws ServletException, IOException {
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(null);

        jwtValidityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer).extractJWTHeader(request);
    }

    @Test
    @DisplayName("Should bypass JWT validation when JWT header lacks 'Bearer' prefix")
    public void should_NotValidateJWT_When_JWTHeaderHasNoBearerPrefix() throws ServletException, IOException {
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn("noBearerJWT");

        jwtValidityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer).extractJWTHeader(request);
    }

    @Test
    @DisplayName("Should validate JWT header when JWT is present and valid")
    public void should_ValidateJWTHeader_When_JWTHeaderIsPresentAndValid() throws ServletException, IOException {
        // Arrange
        String validJwt = "validJwtToken";

        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(buildJWT(validJwt));

            mockJWTSecretKey();
            JwtParser jwtParser = mockJWTParser(jwtsMockedStatic);

            Jws<Claims> mockJws = mock(Jws.class);
            when(jwtParser.parseSignedClaims(validJwt)).thenReturn(mockJws);

            // Act
            jwtValidityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("Should respond with 401 when JWT token is expired")
    public void should_Throw401Exception_When_JWTTokenIsExpired() throws ServletException, IOException {
        // Arrange
        String expiredJwt = "expiredJwtToken";

        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(buildJWT(expiredJwt));

            mockJWTSecretKey();

            JwtParser jwtParser = mockJWTParser(jwtsMockedStatic);
            when(jwtParser.parseSignedClaims(expiredJwt))
                    .thenThrow(new ExpiredJwtException(null, null, "Token expired"));

            // Act
            jwtValidityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(sharedLogicContainer).writeMessageInResponse(
                    eq(response),
                    eq(401),
                    eq("Your token has expired")
            );
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("Should respond with 401 when JWT token is malformed or invalid")
    public void should_Throw401Exception_When_JWTHeaderIsInvalid() throws ServletException, IOException {
        // Arrange
        String invalidJwt = "invalidJwtToken";

        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(buildJWT(invalidJwt));

            mockJWTSecretKey();

            JwtParser jwtParser = mockJWTParser(jwtsMockedStatic);
            when(jwtParser.parseSignedClaims(invalidJwt)).thenThrow(new MalformedJwtException("Invalid token"));

            // Act
            jwtValidityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(sharedLogicContainer).writeMessageInResponse(
                    eq(response),
                    eq(401),
                    eq("Your JWT token is invalid")
            );
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("Should bypass JWT validation for refresh token endpoint")
    public void should_NotValidateJWTHeader_When_RequestHasRefreshTokenURL() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/refresh-token");
        when(sharedLogicContainer.extractJWTHeader(request)).thenReturn(buildJWT("refreshToken"));

        jwtValidityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(sharedLogicContainer).extractJWTHeader(request);
        verify(request).getServletPath();
    }

    private JwtParser mockJWTParser(MockedStatic<Jwts> jwtsMockedStatic) {
        JwtParserBuilder jwtParserBuilder = mock(JwtParserBuilder.class);
        JwtParser jwtParser = mock(JwtParser.class);

        jwtsMockedStatic.when(Jwts::parser).thenReturn(jwtParserBuilder);
        when(jwtParserBuilder.verifyWith(any(SecretKey.class))).thenReturn(jwtParserBuilder);
        when(jwtParserBuilder.build()).thenReturn(jwtParser);
        return jwtParser;
    }

    private void mockJWTSecretKey() {
        SecretKey mockKey = mock(SecretKey.class);
        when(signingKeyProvider.getSigningKey()).thenReturn(mockKey);
    }

}
