package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema(description = "Enum that represent role`s types")
@RequiredArgsConstructor
public enum AccountTechnicalRole implements AccountRole {
    //Todo add role, which represent that account can do nothing (not in campus\precampus)
    @Schema(description = "Role for users who is not in campus")
    INTERN("Intern"),

    @Schema(description = "Role for users who has passed pre-campus tests and can work in campus")
    DEVELOPER("Developer"),

    @Schema(description = "Role for users who can lead developers")
    PREMASTER("Premaster"),

    @Schema(description = "Role for users who can lead projects and can be mentors in pre-campus")
    MASTER("Master"),

    @Schema(description = "Role for users who can lead projects and has almost full access to organization resources")
    TECHLEAD("Technical Lead");

    private final String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static AccountTechnicalRole getFromString(String stringAccountTechnicalRole) {
        //todo checks
        return AccountTechnicalRole.valueOf(stringAccountTechnicalRole);
    }

}
