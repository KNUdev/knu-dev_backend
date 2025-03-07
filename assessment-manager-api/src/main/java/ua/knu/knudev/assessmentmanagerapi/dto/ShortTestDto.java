package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record ShortTestDto(
        UUID id,
        String enName
) {
}
