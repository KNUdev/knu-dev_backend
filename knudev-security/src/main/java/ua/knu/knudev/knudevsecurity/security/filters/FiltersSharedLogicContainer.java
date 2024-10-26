package ua.knu.knudev.knudevsecurity.security.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class FiltersSharedLogicContainer {

    public static void writeMessageInResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setHeader("Content-Type", "application/json");
        response.getWriter().write(message);
    }

    public static String extractJWTHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }
}
