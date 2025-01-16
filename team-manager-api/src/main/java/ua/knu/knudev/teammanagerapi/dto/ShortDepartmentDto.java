package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.UUID;

@Schema(description = "Object that contains short information about department")
public record ShortDepartmentDto(
        @Schema(description = "Department id", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Object with department name in English and in Ukrainian",
                requiredMode = Schema.RequiredMode.REQUIRED, implementation = MultiLanguageFieldDto.class)
        MultiLanguageFieldDto name
) {
}
