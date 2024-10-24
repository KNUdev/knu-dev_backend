package ua.knu.knudev.knudevsecurityapi.api;

import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

public interface AuthServiceApi {
    AuthenticationResponse authenticate(AuthenticationRequest authReq);
}
