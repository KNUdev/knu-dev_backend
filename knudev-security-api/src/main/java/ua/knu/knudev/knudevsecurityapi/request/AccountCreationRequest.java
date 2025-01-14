package ua.knu.knudev.knudevsecurityapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.util.UUID;

@Schema(description = "Request object containing account creation data")
@Builder(toBuilder = true)
public record AccountCreationRequest(

        @Schema(description = "User`s email address", example = "john@knu.ua",requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Pattern(
                regexp = "^[\\w.-]+@knu\\.ua$",
                message = "Email must be in the @knu.ua domain"
        )
        String email,

        @Schema(description = "User`s password", example = "QwerTy123!", requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8, maxLength = 64)
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

        @Schema(description = "User`s first name", example = "John", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
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

        @Schema(description = "User`s last name", example = "Lenon", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
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

        @Schema(description = "User`s maddle name", example = "Petrovich", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
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

        @Schema(description = "The user's department ID, which uniquely identifies the department within the organization.",
                example = "d8f1c78a-b7d8-4b9f-975f-0104d6deab82", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Department ID must be present")
        UUID departmentId,

        @Schema(description = "The specialty code name associated with the user's academic specialization.",
                example = "109.1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Specialty code name must be present")
        @DecimalMin(value = "1.0", message = "Specialty code name must be at least 1.0")
        Double specialtyCodename,

        @Schema(description = "Avatar file of the user", implementation = MultipartFile.class)
        MultipartFile avatarFile,

        @Schema(description = "Defines the expertise areas of an employee", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Expertise must not be null")
        Expertise expertise,

        @Schema(description = "University study year on registration", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(1) @Max(11)
        @NotNull
        Integer yearOfStudy
) {
}
