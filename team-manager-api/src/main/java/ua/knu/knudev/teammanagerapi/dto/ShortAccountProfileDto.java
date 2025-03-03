package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.utils.FullName;

@Builder
@Schema(description = "A DTO representing a short version of the account profile, including full name, technical role, and avatar filename.")
public record ShortAccountProfileDto(

        @Schema(description = "Full name of the team member", requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = FullName.class)
        FullName name,

        @Schema(description = "Github account username", example = "DenysLnk", requiredMode = Schema.RequiredMode.REQUIRED)
        String githubAccountUsername,

        @Schema(description = "Technical role of the team member", requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = AccountTechnicalRole.class)
        AccountTechnicalRole accountTechnicalRole,

        @Schema(description = "Filename of the team member's avatar image", requiredMode = Schema.RequiredMode.REQUIRED)
        String avatarFilename
) {
}
