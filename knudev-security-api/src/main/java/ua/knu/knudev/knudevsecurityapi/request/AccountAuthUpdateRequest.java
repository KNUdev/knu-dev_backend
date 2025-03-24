package ua.knu.knudev.knudevsecurityapi.request;

import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

public record AccountAuthUpdateRequest(
        UUID accountId,
        String email,
        AccountTechnicalRole technicalRole
) {
}
