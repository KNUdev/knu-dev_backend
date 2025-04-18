package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RolePromotionTaskDto(
        UUID id,
        String taskFilename,
        LocalDateTime additionDate,
        LocalDateTime lastUpdateDate,
        AccountTechnicalRole targetTechnicalRole,
        String creatorAccountEmail
) {
}
