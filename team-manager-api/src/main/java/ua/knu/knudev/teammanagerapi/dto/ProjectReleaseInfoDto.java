package ua.knu.knudev.teammanagerapi.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectReleaseInfoDto(
        UUID id,
        LocalDate releaseDate,
        String projectDomain,
        UUID projectId
) {
}