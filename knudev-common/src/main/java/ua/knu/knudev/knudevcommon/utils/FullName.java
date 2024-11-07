package ua.knu.knudev.knudevcommon.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record FullName(
        @NotBlank(message = "First name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "First name must contain only English letters and valid symbols (- or ')")
        String firstName,

        @NotBlank(message = "Last name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "Last name must contain only English letters and valid symbols (- or ')")
        String lastName,

        @NotBlank(message = "Middle name cannot be null or blank.")
        @Pattern(regexp = "^[A-Za-z'-]+$", message = "Middle name must contain only English letters and valid symbols (- or ')")
        String middleName
) {
}
