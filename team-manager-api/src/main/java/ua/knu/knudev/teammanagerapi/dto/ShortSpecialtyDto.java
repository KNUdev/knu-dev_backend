package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageNameDto;

@Schema(description = "Object that contains short information about specialty")
@Builder
public record ShortSpecialtyDto(
        @Schema(description = "Specialty code name", requiredMode = Schema.RequiredMode.REQUIRED, example = "109.1")
        double codeName,

        @Schema(description = "Specialty name in English and Ukrainian", implementation = MultiLanguageNameDto.class,
                requiredMode = Schema.RequiredMode.REQUIRED)
        MultiLanguageNameDto name
) {
}
