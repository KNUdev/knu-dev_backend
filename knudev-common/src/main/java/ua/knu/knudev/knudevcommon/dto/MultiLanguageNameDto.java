package ua.knu.knudev.knudevcommon.dto;

import lombok.Builder;

@Builder
public record MultiLanguageNameDto(String enName, String ukName) {
}
