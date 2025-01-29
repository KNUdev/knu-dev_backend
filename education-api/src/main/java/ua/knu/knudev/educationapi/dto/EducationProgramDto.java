package ua.knu.knudev.educationapi.dto;

import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

//todo tests
public record EducationProgramDto(
        UUID id,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Expertise expertise,
        boolean isPublished,
        int version,
        String finalTaskUrl,
        List<ProgramSectionDto> sections
) {

}
