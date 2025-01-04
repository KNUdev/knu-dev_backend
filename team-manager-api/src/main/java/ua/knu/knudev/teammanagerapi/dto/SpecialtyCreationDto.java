package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Schema(description = "Specialty creation object")
@Builder
public record SpecialtyCreationDto(

        @Schema(description = "Specialty codename", example = "109.2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Specialty code-name cannot be null")
        @Positive(message = "Specialty code-name must be greater than 0")
        Double codeName,

        @Schema(description = "Specialty name in Ukrainian", example = "Комп'ютерна інженерія", minLength = 2,
                maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Specialty Ukrainian name cannot be blank or empty")
        @Size(min = 2, max = 200, message = "Specialty name must be between 2 and 200 characters")
        @Pattern(regexp = "^[А-Яа-яЇїІіЄєҐґ\\s-]+$",
                message = "Specialty name in ukrainian may contains only ukrainian-alphabet letters"
        )
        String ukName,

        @Schema(description = "Specialty name in English", example = "Computer engineering", minLength = 2,
                maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Specialty English name cannot be blank or empty")
        @Size(min = 2, max = 200, message = "Specialty name must be between 2 and 200 characters")
        @Pattern(regexp = "^[A-Za-z\\s-]+$",
                message = "Specialty name in english may contains only english-alphabet letters"
        )
        String enName
) {
}
