package ua.knu.knudev.knudevsecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTService {

    private final Integer accessTokenExpirationInMillis;
    private final Integer refreshTokenExpirationInMillis;
    private final String issuerName;

    public JWTService(
            @Value("${application.jwt.expiration}") Integer accessTokenExpirationInMillis,
            @Value("${application.jwt.refresh-token.expiration}") Integer refreshTokenExpirationInMillis,
            @Value("${application.jwt.issuer}") String issuerName) {
        this.accessTokenExpirationInMillis = accessTokenExpirationInMillis;
        this.refreshTokenExpirationInMillis = refreshTokenExpirationInMillis;
        this.issuerName = issuerName;
    }

    public static SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode("OblBr1u8BlwbqA61vt0yk4TC/mPlvT2O+t2m9SCci6g=");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }

    public Set<String> extractAccountRole(String token) {
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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Date expiryDate = extractClaim(token, Claims::getExpiration);
            boolean isTokenExpired = expiryDate.before(new Date());
            String username = extractUsername(token);

            return StringUtils.equals(username, userDetails.getUsername()) && !isTokenExpired;
        } catch (SignatureException e) {
            return false;
        }
    }

    public Tokens generateTokens(UserDetails userDetails) {
        return Tokens.builder()
                .accessToken(generateAccessToken(userDetails))
                .refreshToken(generateRefreshToken(userDetails))
                .build();
    }

    private String generateAccessToken(UserDetails userDetails) {
        AccountAuth account = (AccountAuth) userDetails;
        return buildToken(buildExtraClaims(true, account.getRoles()),
                userDetails,
                accessTokenExpirationInMillis
        );
    }

    private String generateRefreshToken(UserDetails userDetails) {
        AccountAuth account = (AccountAuth) userDetails;
        return buildToken(buildExtraClaims(false, account.getRoles()),
                userDetails,
                refreshTokenExpirationInMillis
        );
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuer(issuerName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private Map<String, Object> buildExtraClaims(boolean isAccessToken, Set<AccountRole> roles) {
        Map<String, Object> extraClaimsMap = new HashMap<>();
        extraClaimsMap.put("isacct", isAccessToken);
        extraClaimsMap.put("roles", roles.stream().map(Enum::name).collect(Collectors.toSet()));
        return extraClaimsMap;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
