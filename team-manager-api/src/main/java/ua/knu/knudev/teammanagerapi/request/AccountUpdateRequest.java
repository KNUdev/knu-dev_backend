package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.util.UUID;

@Builder
@Schema(description = "Request for updating an account")
public record AccountUpdateRequest(
        @NotBlank(message = "Account id can not be blank or empty!")
        @Schema(description = "Account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID accountId,

        @Schema(description = "User's first name", example = "John")
        String firstName,

        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Schema(description = "User's middle name", example = "Michael")
        String middleName,

        @Schema(description = "User's email", example = "john.doe@knu.ua")
        String email,

        @Schema(description = "User's technical role")
        AccountTechnicalRole technicalRole,

        @Schema(description = "Year of study at the time of registration", example = "3")
        Integer yearOfStudyOnRegistration,

        @Schema(description = "KNUdev unit")
        KNUdevUnit unit,

        @Schema(description = "GitHub account username", example = "johndoe")
        String gitHubAccountUsername,

        @Schema(description = "Specialty code", example = "121.0")
        Double specialtyCodeName,

        @Schema(description = "Department identifier", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID departmentId
) {
}
