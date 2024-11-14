package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountTechnicalRole implements AccountRole {
    INTERN("Intern"),
    DEVELOPER("Developer"),
    TECHLEAD("Technical Lead");

    private final String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }


}
