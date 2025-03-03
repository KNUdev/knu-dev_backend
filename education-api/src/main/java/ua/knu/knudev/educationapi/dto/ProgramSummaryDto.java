package ua.knu.knudev.educationapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgramSummaryDto(
        UUID id,
        MultiLanguageFieldDto name,
        int totalSections,
        int totalModules,
        int totalTopics,
        Expertise expertise,
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        LocalDateTime createdAt,
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        LocalDateTime lastUpdatedAt,
        boolean isPublished,
        int totalActiveSessions
) {
}
