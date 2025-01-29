package ua.knu.knudev.educationapi.dto;

import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

public record ProgramModuleDto(
    UUID id,
    MultiLanguageFieldDto name,
    MultiLanguageFieldDto description,
    String finalTaskUrl,
    List<ProgramTopicDto> topics
) {
}
