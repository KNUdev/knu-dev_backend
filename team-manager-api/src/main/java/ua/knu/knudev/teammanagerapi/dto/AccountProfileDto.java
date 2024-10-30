package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

@Builder
public record AccountProfileDto(
        String email,
        AccountRole role,
        FullName fullName,
        AcademicUnitsIds academicUnitsIds,
        String avatarFilename
) {
}
