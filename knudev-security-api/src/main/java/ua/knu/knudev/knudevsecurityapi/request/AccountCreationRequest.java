package ua.knu.knudev.knudevsecurityapi.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.util.UUID;

@Builder(toBuilder = true)
public record AccountCreationRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Pattern(
                regexp = "^[\\w.-]+@knu\\.ua$",
                message = "Email must be in the @knu.ua domain"
        )
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(
                min = 8,
                max = 64,
                message = "Password must be between 8 and 64 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$",
                message = "Password must contain at least one letter and one digit"
        )
        String password,

        @NotBlank(message = "First name cannot be null or blank.")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "First name must contain only English letters and valid symbols (- or ')"
        )
        @Size(
                max = 50,
                message = "First name cannot exceed 50 characters"
        )
        String firstName,

        @NotBlank(message = "Last name cannot be null or blank.")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "Last name must contain only English letters and valid symbols (- or ')"
        )
        @Size(
                max = 50,
                message = "Last name cannot exceed 50 characters"
        )
        String lastName,

        @NotBlank(message = "Middle name cannot be null or blank.")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "Middle name must contain only English letters and valid symbols (- or ')"
        )
        @Size(
                max = 50,
                message = "Middle name cannot exceed 50 characters"
        )
        String middleName,

        @NotNull(message = "Department ID must be present")
        UUID departmentId,

        @NotNull(message = "Specialty code name must be present")
        @DecimalMin(value = "1.0", message = "Specialty code name must be at least 1.0")
        Double specialtyCodename,

        MultipartFile avatarFile,

        @NotNull(message = "Expertise must not be null")
        Expertise expertise
) {
}
