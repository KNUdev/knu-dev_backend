package ua.knu.knudev.teammanagerapi.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;

import java.util.Set;
import java.util.UUID;

@Builder
public record DepartmentCreationRequest(
        UUID id,
        @NotNull(message = "Department name cannot be blank or empty")
        @NotBlank(message = "Department name cannot be blank or empty")
        String name,
        @NotEmpty(message = "Specialties collection cannot be empty")
        Set<@Valid SpecialtyCreationDto> specialties
) {
}
