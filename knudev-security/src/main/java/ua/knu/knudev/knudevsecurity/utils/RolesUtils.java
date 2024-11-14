package ua.knu.knudev.knudevsecurity.utils;

import ua.knu.knudev.knudevcommon.constant.AccountAdministrativeRole;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.Arrays;

public class RolesUtils {

//    public static <T extends Enum<T> & AccRole> T getRoleFromString(Class<T> roleClass, String roleName) {
//        return Arrays.stream(roleClass.getEnumConstants())
//                .filter(role -> role.name().equalsIgnoreCase(roleName))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException(
//                        String.format("No %s found for role: %S", roleClass.getSimpleName(), roleName)
//                ));
//    }
//
//
//    public static AccountTechnicalRole getTechnicalRoleFromString(String roleName) {
//        return getRoleFromString(AccountTechnicalRole.class, roleName);
//    }
//
//    public static AccountAdministrativeRole getAdministrativeRoleFromString(String roleName) {
//        return getRoleFromString(AccountAdministrativeRole.class, roleName);
//    }

    public static AccountTechnicalRole getTechnicalRoleFromString(String roleName) {
        return Arrays.stream(AccountTechnicalRole.values())
                .filter(role -> role.name().equalsIgnoreCase(roleName))
                .findFirst()
                .orElse(null);
    }

    public static AccountAdministrativeRole getAdministrativeRoleFromString(String roleName) {
        return Arrays.stream(AccountAdministrativeRole.values())
                .filter(role -> role.name().equalsIgnoreCase(roleName))
                .findFirst()
                .orElse(null);
    }
}
