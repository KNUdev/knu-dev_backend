package ua.knu.knudev.teammanagerapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record DepartmentDto(
        UUID id,
        @NotBlank(message = "Department name can`t be empty")
        @NotNull(message = "Department name cant be null")
        String name,
        @NotEmpty(message = "Specialties collection can`t be empty")
        Set<@Valid SpecialtyCreationDto> specialties
) {
}
