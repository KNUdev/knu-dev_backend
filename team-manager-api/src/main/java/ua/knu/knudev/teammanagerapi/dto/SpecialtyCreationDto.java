package ua.knu.knudev.teammanagerapi.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SpecialtyCreationDto(
        @NotNull(message = "Specialty code-name cannot be null.")
        @Positive(message = "Specialty code must be greater than 0.")
        Double codeName,
        @NotNull(message = "Specialty name cannot be blank.")
        @Size(min = 2, max = 200, message = "Specialty name must be between 2 and 200 characters.")
        @Pattern(regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ\s-]+$", message = "Specialty name contains invalid characters.")
        String name
) {
}
