package ua.knu.knudev.teammanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SpecialtyCreationDto(
        @NotNull(message = "Specialty code-name cant be null or 0")
        Double codeName,
        @NotBlank(message = "Specialty name can`t be blank")
        @NotNull(message = "Specialty name can`t be null")
        String name
) {
}
