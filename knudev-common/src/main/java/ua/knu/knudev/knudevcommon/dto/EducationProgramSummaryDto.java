package ua.knu.knudev.knudevcommon.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;

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
