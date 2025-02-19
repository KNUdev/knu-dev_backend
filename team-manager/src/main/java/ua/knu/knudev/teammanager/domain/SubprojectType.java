package ua.knu.knudev.teammanager.domain;

import lombok.RequiredArgsConstructor;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;

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
            throw new ProjectException("Subproject type is null");
        }

        return Arrays.stream(SubprojectType.values())
                .filter(type -> type.subprojectType.equals(subprojectType.toLowerCase().trim()))
                .findFirst()
                .orElseThrow(() -> new ProjectException("Invalid subproject type: " + subprojectType));
    }
}
