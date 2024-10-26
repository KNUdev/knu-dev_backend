package ua.knu.knudev.knudevsecurityapi.dto;

import lombok.Builder;

@Builder
public record Tokens(String accessToken, String refreshToken) {
}
