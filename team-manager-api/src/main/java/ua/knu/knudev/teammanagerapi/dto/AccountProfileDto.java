package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.util.Set;

@Builder
public record AccountProfileDto(
        String email,
        Set<AccountRole> roles,
        FullName fullName,
        AcademicUnitsIds academicUnitsIds,
        String avatarFilename
) {
}
