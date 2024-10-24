package ua.knu.knudev.knudevsecurity.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.service.JWTService;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.io.IOException;

import static ua.knu.knudev.knudevsecurity.security.config.UrlRegistry.AUTH_EXCLUDED_URLS;
import static ua.knu.knudev.knudevsecurity.security.config.UrlRegistry.AUTH_URL;
import static ua.knu.knudev.knudevsecurity.security.filters.FiltersSharedLogicContainer.extractJWTHeader;
import static ua.knu.knudev.knudevsecurity.security.filters.FiltersSharedLogicContainer.writeMessageInResponse;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isPublicUrlRequest(request, response, filterChain)) return;

        String authHeader = extractJWTHeader(request);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String accountUsername = jwtService.extractUsername(jwt);
        if (accountUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtService.isAccessToken(jwt)) {
                String message = "Please enter an access token";
                writeMessageInResponse(response, 403, message);
                return;
            }

            //todo maybe this wont work
            AccountAuth userDetails = AccountAuth.builder()
                    .email(accountUsername)
                    .roles(AccountRole.buildFromSet(jwtService.extractAccountRole(jwt)))
                    .build();

            if (jwtService.isTokenValid(jwt, userDetails)) {
                setAuthentication(userDetails, request);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(AccountAuth userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private boolean isPublicUrlRequest(HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain) throws IOException, ServletException {

        String servletPath = request.getServletPath();
        if (servletPath.contains(AUTH_URL) && !AUTH_EXCLUDED_URLS.contains(servletPath)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }
}
