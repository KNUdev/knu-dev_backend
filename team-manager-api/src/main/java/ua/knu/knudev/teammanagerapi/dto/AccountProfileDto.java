package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;

@Builder
public record AccountProfileDto(
        String email,
        AccountTechnicalRole technicalRole,
        FullName fullName,
        AcademicUnitsIds academicUnitsIds,
        String avatarFilename
) {
}
