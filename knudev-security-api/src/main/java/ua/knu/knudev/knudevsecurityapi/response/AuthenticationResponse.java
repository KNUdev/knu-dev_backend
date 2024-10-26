package ua.knu.knudev.knudevsecurityapi.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(String accessToken, String refreshToken) {
}
