package ua.knu.knudev.knudevsecurity.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

@Builder
public record AccountAuthDto(
        UUID id,
        String email,
        String password,
        AccountTechnicalRole technicalRole,
        boolean enabled,
        boolean nonLocked
) {
}
