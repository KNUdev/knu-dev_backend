package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RolePromotionCondition implements Condition {

    PROJECT_AS_DEVELOPER("The number of projects done on campus is more than 0"),
    COMMITS_AS_DEVELOPER("The number of commits done in master branch on campus is more than 10"),
    COMMITS_AS_PREMASTER("The number of commits done in master branch on campus is more than 20"),
    PROJECT_AS_PREMASTER("The number of projects done on campus is more than 1"),
    COMMITS_AS_MASTER("The number of commits done in master branch on campus is more than 60"),
    WAS_A_SUPERVISOR("Was a supervisor"),
    WAS_AN_ARCHITECT("Was an architect");

    private final String body;

    @Override
    public String getDisplayBody() {
        return body;
    }
}

