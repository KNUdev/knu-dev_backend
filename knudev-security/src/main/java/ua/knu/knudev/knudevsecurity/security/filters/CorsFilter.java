package ua.knu.knudev.knudevsecurity.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class CorsFilter extends OncePerRequestFilter {

    // Allow multiple trusted origins for flexibility in development/production
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList("http://localhost:3000", "https://example.com");

    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, PATCH, OPTIONS";
    private static final String ALLOWED_HEADERS = "Authorization,authorization,Content-Type,content-type";
    private static final String ALLOW_CREDENTIALS = "true";
    private static final long MAX_AGE = 3600; // 1 hour in seconds

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        // Check if origin is allowed
        if (isAllowedOrigin(origin)) {
            setCorsHeaders(response, origin);

            // Handle preflight OPTIONS request
            if (isPreflightRequest(request)) {
                handlePreflightRequest(request, response);
                return;  // Short-circuit after responding to preflight
            }
        } else {
            // Reject requests from disallowed origins
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CORS origin denied");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedOrigin(String origin) {
        // Allow requests with no Origin header (e.g., same-origin requests)
        if (origin == null) {
            return true;
        }
        // Check if the origin is one of the allowed ones
        //todo return back to return ALLOWED_ORIGINS.contains(origin);
        return true;
//        return ALLOWED_ORIGINS.contains(origin);
    }

    private void setCorsHeaders(HttpServletResponse response, String origin) {
        //todo change later to specific domains
//        response.setHeader("Access-Control-Allow-Origin", origin);  // Reflect the valid origin

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        response.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        response.setHeader("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
        response.setHeader("Access-Control-Max-Age", String.valueOf(MAX_AGE));  // Cache preflight response
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) &&
                request.getHeader("Access-Control-Request-Method") != null;
    }

    private void handlePreflightRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestedMethod = request.getHeader("Access-Control-Request-Method");
        String requestedHeaders = request.getHeader("Access-Control-Request-Headers");

        // Validate the preflight method and headers against allowed values
        if (ALLOWED_METHODS.contains(requestedMethod) && areAllowedHeaders(requestedHeaders)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CORS preflight request");
        }
    }

    private boolean areAllowedHeaders(String headers) {
        if (headers == null || headers.isEmpty()) {
            return true; // No headers to validate
        }
        List<String> requestedHeaders = Arrays.asList(headers.split(","));
        List<String> allowedHeaders = Arrays.asList(ALLOWED_HEADERS.split(","));

        // Ensure all requested headers are allowed
        return new HashSet<>(allowedHeaders).containsAll(requestedHeaders);
    }
}
