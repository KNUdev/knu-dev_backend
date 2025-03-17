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

        @Schema(description = "User`s email address", example = "john@knu.ua", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "registration.validation.email.notBlank")
        @Email(message = "registration.validation.email.format")
        @Pattern(
                regexp = "^[\\w.-]+@knu\\.ua$",
                message = "registration.validation.email.domain"
        )
        String email,

        @Schema(description = "User`s password", example = "QwerTy123!", requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8, maxLength = 64)
        @NotBlank(message = "registration.validation.password.notBlank")
        @Size(
                min = 8,
                max = 64,
                message = "registration.validation.password.size"
        )
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$",
                message = "registration.validation.password.letterAndDigit"
        )
        String password,

        @Schema(description = "User`s first name", example = "John", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "registration.validation.firstName.notBlank")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "registration.validation.firstName.validLang"
        )
        @Size(
                max = 50,
                message = "registration.validation.firstName.maxSize"
        )
        String firstName,

        @Schema(description = "User`s last name", example = "Lenon", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "registration.validation.lastName.notBlank")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "registration.validation.lastName.validLang"
        )
        @Size(
                max = 50,
                message = "registration.validation.lastName.maxSize"
        )
        String lastName,

        @Schema(description = "User`s maddle name", example = "Petrovich", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "registration.validation.middleName.notBlank")
        @Pattern(
                regexp = "^[A-Za-z'-]+$",
                message = "registration.validation.middleName.validLang"
        )
        @Size(
                max = 50,
                message = "registration.validation.middleName.maxSize"
        )
        String middleName,

        @Schema(description = "The user's department ID, which uniquely identifies the department within the organization.",
                example = "d8f1c78a-b7d8-4b9f-975f-0104d6deab82", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "registration.validation.department.present")
        UUID departmentId,

        @Schema(description = "The specialty code name associated with the user's academic specialization.",
                example = "109.1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "registration.validation.specialty.present")
        @DecimalMin(value = "1.0", message = "Specialty code name must be at least 1.0")
        Double specialtyCodename,

        @Schema(description = "Avatar file of the user", implementation = MultipartFile.class)
        MultipartFile avatarFile,

        @Schema(description = "Banner file of the user", implementation = MultipartFile.class)
        MultipartFile bannerFile,

        @Schema(description = "Defines the expertise areas of an employee", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "registration.validation.expertise.notBlank")
        Expertise expertise,

        @Schema(description = "University study year on registration", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1, message = "registration.validation.yearOfStudy.min")
        @Max(value = 11, message = "registration.validation.yearOfStudy.max")
        @NotNull(message = "registration.validation.yearOfStudy.notBlank")
        Integer yearOfStudy,

        @Schema(description = "Github account username", example = "DenysLnk", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "registration.validation.githubUsername.notBlank")
        String githubAccountUsername
) {
}
