package ua.knu.knudev.knudevsecurityapi.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

import java.io.IOException;

public interface AuthServiceApi {
    AuthenticationResponse authenticate(AuthenticationRequest authReq);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
