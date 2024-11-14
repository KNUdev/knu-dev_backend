package ua.knu.knudev.knudevsecurityapi.response;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

@Builder
public record AuthAccountCreationResponse(
        UUID id,
        String email,
//        Set<AccountRole> roles
        AccountTechnicalRole technicalRole
) {
}
