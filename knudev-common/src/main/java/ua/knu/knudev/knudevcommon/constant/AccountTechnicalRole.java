package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "Enum that represent role`s types")
@RequiredArgsConstructor
@Getter
public enum AccountTechnicalRole implements AccountRole {
    //Todo add role, which represent that account can do nothing (not in campus\precampus)
    @Schema(description = "Role for users who is not in campus")
    INTERN("Intern", "INTERN ID"),

    @Schema(description = "Role for users who has passed pre-campus tests and can work in campus")
    DEVELOPER("Developer", "DEVELOPER ID"),

    @Schema(description = "Role for users who can lead developers")
    PREMASTER("Premaster", "PREMASTER ID"),

    @Schema(description = "Role for users who can lead projects and can be mentors in pre-campus")
    MASTER("Master", "MASTER ID"),

    @Schema(description = "Role for users who can lead projects and has almost full access to organization resources")
    TECHLEAD("Technical Lead", "TECHLEAD ID");

    private final String displayName;
    private final String roleId;

    public static AccountTechnicalRole getFromString(String stringAccountTechnicalRole) {
        //todo checks
        return AccountTechnicalRole.valueOf(stringAccountTechnicalRole);
    }

    public static AccountTechnicalRole getById(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Role id is null or empty");
        }
        for (AccountTechnicalRole role : values()) {
            if (role.roleId.equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No AccountTechnicalRole found for id: " + id);
    }

    public AccountTechnicalRole getNextRole() {
        return switch (this) {
            case INTERN -> DEVELOPER;
            case DEVELOPER -> PREMASTER;
            case PREMASTER -> MASTER;
            case MASTER -> TECHLEAD;
            case TECHLEAD -> null;
        };
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
