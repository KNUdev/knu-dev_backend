package ua.knu.knudev.teammanagerapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO representing the account profile")
@Builder
public record AccountProfileDto(

        @Schema(description = "UUID format account id", example = "fda846b1-948c-473c-bc61-7a31c3937aed",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "User's email address", example = "ivan@knu.ua",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "User's role in the system", example = "MASTER", implementation = AccountTechnicalRole.class,
                requiredMode = Schema.RequiredMode.REQUIRED)
        AccountTechnicalRole technicalRole,

        @Schema(description = "User's full name", implementation = FullName.class, requiredMode = Schema.RequiredMode.REQUIRED)
        FullName fullName,

        @Schema(description = "IDs of academic units", implementation = AcademicUnitsIds.class,
                requiredMode = Schema.RequiredMode.REQUIRED)
        AcademicUnitsIds academicUnitsIds,

        @Schema(description = "Department`s name in which user are", implementation = MultiLanguageFieldDto.class)
        MultiLanguageFieldDto departmentName,

        @Schema(description = "Specialty`s name in which user are", implementation = MultiLanguageFieldDto.class)
        MultiLanguageFieldDto specialtyName,

        @Schema(description = "Avatar filename of the user", example = "avatar.png")
        String avatarFilename,

        @Schema(description = "Banner filename of the user", example = "banner.png")
        String bannerFilename,

        @Schema(description = "GitHub account username", example = "JohnDoe")
        String githubAccountUsername,

        @Schema(description = "Account expertise", example = "BACKEND", implementation = Expertise.class)
        Expertise expertise,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        @Schema(description = "Registration date", example = "2025-15-03T11:50:23.223Z", implementation = LocalDateTime.class)
        LocalDateTime registeredAt,

        @Schema(description = "Account(student) university course when registered", example = "2")
        int yearOfStudyOnRegistration,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        @Schema(description = "Date, when technical role was last updated", example = "2025-15-03T11:50:23.223Z",
                implementation = LocalDateTime.class)
        LocalDateTime lastRoleUpdateDate,

        @Schema(description = "KNUdev unit", example = "CAMPUS")
        KNUdevUnit unit
) {
}

