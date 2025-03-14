package ua.knu.knudev.teammanagerapi.request;

import ua.knu.knudev.knudevcommon.constant.Expertise;

public record RecruitmentReceivingRequest(
        String name,
        Expertise expertise
) {
}
