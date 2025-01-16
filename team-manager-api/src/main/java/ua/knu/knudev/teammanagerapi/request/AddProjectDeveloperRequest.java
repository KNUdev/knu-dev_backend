package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Schema(description = "Request object for adding a developer to a project")
@Builder
public record AddProjectDeveloperRequest(
        @Schema(description = "Unique identifier of the account profile",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Account profile id can not be null")
        UUID accountProfileId,

        @Schema(description = "Unique identifier of the project",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Project id can not be null")
        UUID projectId
) {
}

