package ua.knu.knudev.educationapi.dto;

import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.UUID;

public record EducationProgramDto(
        UUID id,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Expertise expertise,
        int version

) {

}
