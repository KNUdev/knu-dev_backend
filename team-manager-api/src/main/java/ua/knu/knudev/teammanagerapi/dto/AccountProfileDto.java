package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;

@Schema(description = "DTO representing the account profile")
@Builder
public record AccountProfileDto(

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

        @Schema(description = "Avatar filename of the user", example = "avatar.png")
        String avatarFilename,

        @Schema(description = "Banner filename of the user", example = "banner.png")
        String bannerFilename
) {
}

