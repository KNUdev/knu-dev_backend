package ua.knu.knudev.knudevcommon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Represents a fields in multiple languages: English and Ukrainian.")
@Builder
@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class MultiLanguageFieldDto {
    @Schema(description = "The field in English.", example = "Historical")
    @NotEmpty(message = "Field 'en' cannot be blank or empty.")
    @Pattern(regexp = "^[A-Za-z\\s-]+$",
            message = "Field 'en' can contains only english-alphabet letters"
    )
    private String en;

    @Schema(description = "The field in Ukrainian.", example = "Історичний")
    @NotBlank(message = "Field 'uk' cannot be blank or empty.")
    @Pattern(regexp = "^[А-Яа-яЇїІіЄєҐґ\\s-]+$",
            message = "Field 'uk' may contains only ukrainian-alphabet letters"
    )
    private String uk;
}
