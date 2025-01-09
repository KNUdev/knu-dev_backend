package ua.knu.knudev.knudevcommon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Represents a name in multiple languages: English and Ukrainian.")
@Builder
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MultiLanguageNameDto {
    @Schema(description = "The name in English.", example = "Historical")
    private String enName;

    @Schema(description = "The name in Ukrainian.", example = "Історичний")
    private String ukName;
}
