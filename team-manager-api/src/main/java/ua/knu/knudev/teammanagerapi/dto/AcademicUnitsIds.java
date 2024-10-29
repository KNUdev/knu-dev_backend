package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AcademicUnitsIds(
        UUID departmentId,
        Double specialtyId
) {
}
