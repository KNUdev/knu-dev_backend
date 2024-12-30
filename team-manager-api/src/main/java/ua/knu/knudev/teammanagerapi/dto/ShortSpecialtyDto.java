package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageNameDto;

@Builder
public record ShortSpecialtyDto(double codeName, MultiLanguageNameDto name) {
}
