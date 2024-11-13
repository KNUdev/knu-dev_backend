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
import ua.knu.knudev.knudevcommon.constant.AccountRole;

import java.io.IOException;

import static ua.knu.knudev.knudevsecurity.security.config.UrlRegistry.AUTH_EXCLUDED_URLS;
import static ua.knu.knudev.knudevsecurity.security.config.UrlRegistry.AUTH_URL;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final JWTFiltersHelper jwtFiltersHelper;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isPublicUrlRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = jwtFiltersHelper.extractJWTHeader(request);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String accountUsername = jwtService.extractUsername(jwt);
        if (accountUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtService.isAccessToken(jwt)) {
                jwtFiltersHelper.writeMessageInResponse(
                        response,
                        403,
                        "Please enter an access token"
                );
                return;
            }

            //todo maybe this wont work
            AccountAuth userDetails = AccountAuth.builder()
                    .email(accountUsername)
                    .roles(AccountRole.buildFromStringsSet(jwtService.extractAccountRole(jwt)))
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

    private boolean isPublicUrlRequest(HttpServletRequest request)  {
        String servletPath = request.getServletPath();
        return servletPath.contains(AUTH_URL) && !AUTH_EXCLUDED_URLS.contains(servletPath);
    }

}
