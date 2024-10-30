package ua.knu.knudev.knudevcommon.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AcademicUnitsIds(
        UUID departmentId,
        Double specialtyId
) {
}
