package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Schema(description = "Enum that represent role`s types")
@RequiredArgsConstructor
@Getter
public enum AccountTechnicalRole implements AccountRole {
    //Todo add role, which represent that account can do nothing (not in campus\precampus)
    @Schema(description = "Role for users who is not in campus")
    INTERN("Intern", "INTERN ID", 1),

    @Schema(description = "Role for users who has passed pre-campus tests and can work in campus")
    DEVELOPER("Developer", "DEVELOPER ID", 2),

    @Schema(description = "Role for users who can lead developers")
    PREMASTER("Premaster", "PREMASTER ID", 3),

    @Schema(description = "Role for users who can lead projects and can be mentors in pre-campus")
    MASTER("Master", "MASTER ID", 4),

    @Schema(description = "Role for users who can lead projects and has almost full access to organization resources")
    TECHLEAD("Technical Lead", "TECHLEAD ID", 5);

    private final String displayName;
    private final String roleId;
    private final Integer index;

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

    public static Optional<AccountTechnicalRole> getNextRole(AccountTechnicalRole role) {
        if (role == null) {
            throw new IllegalArgumentException("AccountTechnicalRole is null");
        }
        for (AccountTechnicalRole accountTechnicalRole : values()) {
            if (accountTechnicalRole.index == role.index + 1) {
                return Optional.of(accountTechnicalRole);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
