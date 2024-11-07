package ua.knu.knudev.knudevsecurityapi.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevcommon.constant.Expertise;

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

        @Valid
        FullName fullName,

        AcademicUnitsIds academicUnitsIds,
        MultipartFile avatarFile,
        Expertise expertise
) {
}
