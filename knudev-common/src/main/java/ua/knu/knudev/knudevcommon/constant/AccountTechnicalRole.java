package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema(description = "Enum that represent role`s types")
@RequiredArgsConstructor
public enum AccountTechnicalRole implements AccountRole {
    //Todo add role, which represent that account can do nothing (not in campus\precampus)
    @Schema(description = "Role for users who is not in campus")
    INTERN("Intern", false),

    @Schema(description = "Role for users who has passed pre-campus tests and can work in campus")
    DEVELOPER("Developer", true),

    @Schema(description = "Role for users who can lead developers")
    PREMASTER("Premaster", true),

    @Schema(description = "Role for users who can lead projects and can be mentors in pre-campus")
    MASTER("Master", true),

    @Schema(description = "Role for users who can lead projects and has almost full access to organization resources")
    TECHLEAD("Technical Lead", true);

    private final String displayName;
    private final boolean isCampusRole;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public Boolean isCampusRole() {
        return isCampusRole;
    }

}
