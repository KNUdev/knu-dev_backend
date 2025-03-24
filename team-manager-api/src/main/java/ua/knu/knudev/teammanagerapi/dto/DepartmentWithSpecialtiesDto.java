package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder
public record DepartmentWithSpecialtiesDto(
        UUID id,
        MultiLanguageFieldDto name,
        Set<ShortSpecialtyDto> specialties
) {
}
