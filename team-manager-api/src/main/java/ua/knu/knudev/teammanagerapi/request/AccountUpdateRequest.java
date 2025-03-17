package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.util.UUID;

@Data
@Schema(description = "Request for updating an account")
public class AccountUpdateRequest {
        @Schema(description = "Account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID accountId;

        @Schema(description = "User's first name", example = "John")
        private String firstName;

        @Schema(description = "User's last name", example = "Doe")
        private String lastName;

        @Schema(description = "User's middle name", example = "Michael")
        private String middleName;

        @Schema(description = "User's email", example = "john.doe@knu.ua")
        private String email;

        @Schema(description = "User's technical role")
        private AccountTechnicalRole technicalRole;

        @Schema(description = "Year of study at the time of registration", example = "3")
        private Integer yearOfStudyOnRegistration;

        @Schema(description = "KNUdev unit")
        private KNUdevUnit unit;

        @Schema(description = "GitHub account username", example = "johndoe")
        private String gitHubAccountUsername;

        @Schema(description = "Specialty code", example = "121.0")
        private Double specialtyCodeName;

        @Schema(description = "Department identifier", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        private UUID departmentId;
}
