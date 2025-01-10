package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ProjectDto(
        UUID id,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        String filename,
        LocalDate startedAt,
        ProjectStatus status) {
}
