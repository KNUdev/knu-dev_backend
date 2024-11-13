package ua.knu.knudev.teammanagerapi.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;

import java.util.Set;

@Builder
public record DepartmentCreationRequest(
        @NotEmpty(message = "Department name cannot be blank or empty.")
        @Size(min = 3, max = 150, message = "Too long or short department name.")
        @Pattern(regexp = "^[A-Za-zА-Яа-яЇїІіЄєҐґ\s-]+$", message = "Department name contains invalid characters.")
        String name,
        @NotEmpty(message = "Department must contain specialties")
        @Size(min = 1, max = 75, message = "Department must contain from 1 to 75 specialties")
        Set<@Valid SpecialtyCreationDto> specialties
) {
}
