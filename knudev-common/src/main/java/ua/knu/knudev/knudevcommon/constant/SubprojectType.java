package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum SubprojectType {
    BACKEND("backend"),
    FRONTEND("frontend"),
    MOBILE_APP("mobile"),
    EXTERNAL_LIBRARY("library"),
    UI_UX_DESIGN("design");

    private final String subprojectType;

    public static SubprojectType detectSubprojectType(String subprojectType) {
        if (subprojectType == null) {
            throw new IllegalArgumentException("Subproject type is null");
        }

        return Arrays.stream(SubprojectType.values())
                .filter(type -> type.subprojectType.equals(subprojectType.toLowerCase().trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid subproject type: " + subprojectType));
    }
}
