package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountAdministrativeRole implements AccountRole {
    SITE_MANAGER("Site Manager"),
    HEAD_MANAGER("Head Manager");

    private final String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

