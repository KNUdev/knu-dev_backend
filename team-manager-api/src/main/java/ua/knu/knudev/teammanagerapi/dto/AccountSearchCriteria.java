package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountSearchCriteria(
        String searchQuery,
        LocalDateTime registeredAt,
        LocalDateTime registeredBefore,
        KNUdevUnit unit,
        Expertise expertise,
        String departmentId,
        String specialtyCodeName,
        Integer universityStudyYear,
        UUID recruitmentId,
        AccountTechnicalRole technicalRole
) {

}
