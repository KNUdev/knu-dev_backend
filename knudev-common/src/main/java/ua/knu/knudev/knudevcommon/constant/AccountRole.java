package ua.knu.knudev.knudevcommon.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum AccountRole {
    INTERN,
    DEVELOPER,
    TEACHLEAD,
    SITE_MANAGER,
    HEAD_MANAGER;

    public static Set<AccountRole> buildFromStringsSet(Set<String> stringRoles) {
        return stringRoles.stream()
                .flatMap(stringRole -> Arrays.stream(AccountRole.values())
                        .filter(role -> StringUtils.equalsIgnoreCase(role.name(), stringRole)))
                .collect(Collectors.toSet());
    }

    public static AccountRole buildFromString(String accountRole) {
        return Arrays.stream(AccountRole.values()).
                filter(role -> StringUtils.equalsIgnoreCase(role.name(), accountRole))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountRole: " + accountRole));
    }
}
