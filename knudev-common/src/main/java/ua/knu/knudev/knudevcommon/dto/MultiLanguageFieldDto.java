package ua.knu.knudev.knudevcommon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Represents a fields in multiple languages: English and Ukrainian.")
@Builder
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MultiLanguageFieldDto {
    @Schema(description = "The field in English.", example = "Historical")
    private String en;

    @Schema(description = "The field in Ukrainian.", example = "Історичний")
    private String uk;
}
