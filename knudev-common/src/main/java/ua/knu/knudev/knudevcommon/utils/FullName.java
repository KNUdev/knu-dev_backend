package ua.knu.knudev.knudevcommon.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Schema(description = "Class representing a user's full name including first, middle, and last names.")
@Builder
public record FullName(

        @Schema(description = "The user's first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "First name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "First name must contain only English letters and valid symbols (- or ')")
        String firstName,

        @Schema(description = "The user's last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Last name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "Last name must contain only English letters and valid symbols (- or ')")
        String lastName,

        @Schema(description = "The user's middle name", example = "Vadimovich", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Middle name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "Middle name must contain only English letters and valid symbols (- or ')")
        String middleName
) {
}
