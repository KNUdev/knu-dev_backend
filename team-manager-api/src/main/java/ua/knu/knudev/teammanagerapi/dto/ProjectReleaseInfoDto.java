package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO for project release information")
public record ProjectReleaseInfoDto(

        @Schema(description = "Unique identifier of the release", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Release date of the project", example = "2023-12-31")
        LocalDate releaseDate,

        @Schema(description = "Domain name associated with the project", example = "example.com")
        String projectDomain,

        @Schema(description = "Unique identifier of the related project", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID projectId
) {
}
