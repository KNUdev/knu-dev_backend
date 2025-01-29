package ua.knu.knudev.educationapi.dto;

import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

public record ProgramTopicDto(
        UUID id,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<String> learningResources,
        String taskUrl
) {
}
