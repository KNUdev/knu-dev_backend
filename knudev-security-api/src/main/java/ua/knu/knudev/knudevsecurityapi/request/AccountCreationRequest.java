package ua.knu.knudev.knudevsecurityapi.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

//todo refactor
@Builder
public record AccountCreationRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Pattern(
                regexp = "^[\\w.-]+@knu\\.ua$",
                message = "Email must be in the @knu.ua domain"
        )
        String email,

        @NotBlank(message = "Password cannot be blank")
//        @Size(
//                min = 8,
//                max = 64,
//                message = "Password must be between 8 and 64 characters"
//        )
        String password,

        String firstName,
        String lastName,
        String middleName,

        MultipartFile avatarFile,

        UUID departmentId,
        Double specialtyId


) {
}
