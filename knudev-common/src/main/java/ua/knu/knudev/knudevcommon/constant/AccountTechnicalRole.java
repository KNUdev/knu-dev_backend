package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountTechnicalRole implements AccountRole {
    //Todo add role, which represent that account can do nothing (not in campus\precampus)
    INTERN("Intern"),
    DEVELOPER("Developer"),
    PREMASTER("Premaster"),
    MASTER("Master"),
    TECHLEAD("Technical Lead");

    private final String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }


}
