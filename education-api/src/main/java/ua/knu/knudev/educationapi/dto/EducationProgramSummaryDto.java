package ua.knu.knudev.educationapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

@Builder
public record EducationProgramSummaryDto(
        String banner,
        MultiLanguageFieldDto name,
        int totalTasks,
        int totalTests,
        int durationInDays,
        Expertise programExpertise
) {
}
