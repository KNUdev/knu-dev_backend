package ua.knu.knudev.knudevsecurityapi.constant;

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

    public static Set<AccountRole> buildFromSet(Set<String> stringRoles) {
        return stringRoles.stream()
                .flatMap(stringRole -> Arrays.stream(AccountRole.values())
                        .filter(role -> StringUtils.equalsIgnoreCase(role.name(), stringRole)))
                .collect(Collectors.toSet());
    }
}
