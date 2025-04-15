package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RolePromotionCondition implements Condition {

    PROJECT_AS_DEVELOPER("The number of projects done on campus is more than "),
    COMMITS_AS_DEVELOPER("The number of commits done in master branch on campus is more than "),
    COMMITS_AS_PREMASTER("The number of commits done in master branch on campus is more than "),
    PROJECT_AS_PREMASTER("The number of projects done on campus is more than "),
    COMMITS_AS_MASTER("The number of commits done in master branch on campus is more than "),
    WAS_A_SUPERVISOR("Was a supervisor"),
    WAS_AN_ARCHITECT("Was an architect");

    private final String body;

    @Override
    public String getDisplayBody() {
        return body;
    }
}

