package ua.knu.knudev.knudevsecurity.security.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.knu.knudev.knudevsecurity.utils.JWTSigningKeyProvider;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTValidityFilter extends OncePerRequestFilter {

    private final FiltersSharedLogicContainer sharedLogicContainer;
    private final JWTSigningKeyProvider jwtSigningKeyProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = sharedLogicContainer.extractJWTHeader(request);
        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")
                && !StringUtils.containsIgnoreCase(request.getServletPath(), "refresh-token")) {
            String jwt = jwtHeader.substring(7);

            try {
                Jwts.parser().verifyWith(jwtSigningKeyProvider.getSigningKey()).build().parseSignedClaims(jwt);
            } catch (JwtException ex) {
                String message;
                if (ex instanceof ExpiredJwtException) {
                    message = "Your token has expired";
                    sharedLogicContainer.writeMessageInResponse(response, 401, message);
                    return;
                }
                message = "Your JWT token is invalid";
                sharedLogicContainer.writeMessageInResponse(response, 401, message);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
