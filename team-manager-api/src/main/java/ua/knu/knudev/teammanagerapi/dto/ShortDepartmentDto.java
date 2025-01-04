package ua.knu.knudev.teammanagerapi.dto;

import ua.knu.knudev.knudevcommon.dto.MultiLanguageNameDto;

import java.util.Set;
import java.util.UUID;

public record ShortDepartmentDto(
        UUID id,
        MultiLanguageNameDto name
) {
}
