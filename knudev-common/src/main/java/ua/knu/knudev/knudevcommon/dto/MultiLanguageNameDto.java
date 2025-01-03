package ua.knu.knudev.knudevcommon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Represents a name in multiple languages: English and Ukrainian.")
@Builder
public record MultiLanguageNameDto(

        @Schema(description = "The name in English.", example = "Historical")
        String enName,

        @Schema(description = "The name in Ukrainian.", example = "Історичний")
        String ukName
) {
}
