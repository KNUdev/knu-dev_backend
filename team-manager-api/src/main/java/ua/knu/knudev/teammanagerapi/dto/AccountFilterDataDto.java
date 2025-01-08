package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountFilterDataDto(
        String searchQuery,
        LocalDateTime registrationDate,
        LocalDateTime registrationEndDate,
        KNUdevUnit knuDevUnit,
        Expertise expertise,
        String departmentName,
        String specialtyName,
        Integer universityStudyYear,
        UUID recruitmentId,
        AccountTechnicalRole technicalRole
) {

}
