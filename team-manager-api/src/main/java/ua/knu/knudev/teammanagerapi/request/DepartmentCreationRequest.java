package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;

import java.util.Set;

@Schema(description = "Request object containing department creation data")
@Builder
public record DepartmentCreationRequest(

        @Schema(description = "Department name in English", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Institute of high technology", minLength = 3, maxLength = 150)
        @NotEmpty(message = "Department name cannot be blank or empty.")
        @Size(min = 3, max = 150, message = "Too long or short department name.")
        @Pattern(regexp = "^[A-Za-z\\s-]+$",
                message = "Name in english can contains only english-alphabet letters"
        )
        String nameInEnglish,

        @Schema(description = "Department name in Ukrainian", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Інститут високих технологій", minLength = 3, maxLength = 150)
        @NotBlank(message = "Department name cannot be blank or empty.")
        @Size(min = 3, max = 150, message = "Too long or short department name.")
        @Pattern(regexp = "^[А-Яа-яЇїІіЄєҐґ\\s-]+$",
                message = "Name in ukrainian may contains only ukrainian-alphabet letters"
        )
        String nameInUkrainian,

        @Schema(description = "All specialties that department contains", requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = SpecialtyCreationDto.class, minLength = 1, maxLength = 75)
        @NotEmpty(message = "Department must contain specialties")
        @Size(min = 1, max = 75, message = "Department must contain from 1 to 75 specialties")
        Set<@Valid SpecialtyCreationDto> specialties
) {
}
