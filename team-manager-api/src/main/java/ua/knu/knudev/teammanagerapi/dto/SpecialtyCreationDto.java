package ua.knu.knudev.teammanagerapi.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SpecialtyCreationDto(
        @NotNull(message = "Specialty code-name cannot be null")
        @Positive(message = "Specialty code-name must be greater than 0")
        Double codeName,

        @NotBlank(message = "Specialty Ukrainian name cannot be blank or empty")
        @Size(min = 2, max = 200, message = "Specialty name must be between 2 and 200 characters")
        @Pattern(regexp = "^[А-Яа-яЇїІіЄєҐґ\\s-]+$",
                message = "Specialty name in ukrainian may contains only ukrainian-alphabet letters"
        )
        String ukName,

        @NotBlank(message = "Specialty English name cannot be blank or empty")
        @Size(min = 2, max = 200, message = "Specialty name must be between 2 and 200 characters")
        @Pattern(regexp = "^[A-Za-z\\s-]+$",
                message = "Specialty name in english may contains only english-alphabet letters"
        )
        String enName
) {
}
