package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Schema(description = "DTO for linking a project with an account")
public record ProjectAccountDto(

        @Schema(description = "Unique identifier of the project", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID projectId,

        @Schema(description = "Unique identifier of the account", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID accountId,

        @Schema(description = "The date when the account joined the project", example = "2023-01-01")
        LocalDate dateJoined
) {
}
