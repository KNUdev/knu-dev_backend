package ua.knu.knudev.knudevsecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.security.AuthUserDetails;
import ua.knu.knudev.knudevsecurity.utils.JWTSigningKeyProvider;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTService {

    private final Integer accessTokenExpirationInMillis;
    private final Integer refreshTokenExpirationInMillis;
    private final String issuerName;
    private final JWTSigningKeyProvider signingKeyProvider;

    public JWTService(
            @Value("${application.jwt.expiration}") Integer accessTokenExpirationInMillis,
            @Value("${application.jwt.refresh-token.expiration}") Integer refreshTokenExpirationInMillis,
            @Value("${application.jwt.issuer}") String issuerName, JWTSigningKeyProvider signingKeyProvider) {
        this.accessTokenExpirationInMillis = accessTokenExpirationInMillis;
        this.refreshTokenExpirationInMillis = refreshTokenExpirationInMillis;
        this.issuerName = issuerName;
        this.signingKeyProvider = signingKeyProvider;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKeyProvider.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }

    public Set<String> extractAccountRoles(String token) {
        List<?> roles = parseClaims(token).get("roles", List.class);

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    public boolean isAccessToken(String token) {
        Boolean isAccessToken = parseClaims(token).get("isacct", Boolean.class);
        return isAccessToken != null && isAccessToken;
    }

    public boolean isTokenValid(String token, AuthUserDetails userDetails) {
        try {
            Date expiryDate = extractClaim(token, Claims::getExpiration);
            boolean isTokenExpired = expiryDate.before(new Date());
            String email = extractEmail(token);

            return StringUtils.equals(email, userDetails.getEmail()) && !isTokenExpired;
        } catch (SignatureException e) {
            return false;
        }
    }

    public Tokens generateTokens(AuthUserDetails userDetails) {
        return Tokens.builder()
                .accessToken(generateAccessToken(userDetails))
                .refreshToken(generateRefreshToken(userDetails))
                .build();
    }

    public String generateAccessToken(AuthUserDetails userDetails) {
        AccountAuth account = (AccountAuth) userDetails;
        return buildToken(buildExtraClaims(true, account),
                userDetails,
                accessTokenExpirationInMillis
        );
    }

    public String generateRefreshToken(AuthUserDetails userDetails) {
        return buildToken(buildExtraClaims(false, userDetails),
                userDetails,
                refreshTokenExpirationInMillis
        );
    }

    private String buildToken(Map<String, Object> extraClaims, AuthUserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getEmail())
                .issuer(issuerName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKeyProvider.getSigningKey())
                .compact();
    }

    private Map<String, Object> buildExtraClaims(boolean isAccessToken, AuthUserDetails userDetails) {
        Map<String, Object> extraClaimsMap = new HashMap<>();
        extraClaimsMap.put("isacct", isAccessToken);

        Set<String> accountRoles = userDetails.getRoles().stream()
                .filter(Objects::nonNull)
                .map(AccountRole::name)
                .collect(Collectors.toSet());
        extraClaimsMap.put("roles", accountRoles);
        extraClaimsMap.put("userid", userDetails.getId());
        return extraClaimsMap;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKeyProvider.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
