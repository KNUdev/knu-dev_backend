package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

@Builder
public record AccountRoleEnhancementDto(
        UUID id,
        AccountTechnicalRole technicalRole,
        String githubUsername
) {
}
