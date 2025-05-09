package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Request object for updating existing role promotion conditions")
public record RolePromotionConditionUpdateRequest(
        @Schema(description = "Unique identifier of the role promotion condition to update",
                example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Number of projects required to be promoted to Premaster", example = "2")
        Integer toPremasterProjectQuantity,

        @Schema(description = "Number of commits required to be promoted to Premaster", example = "50")
        Integer toPremasterCommitsQuantity,

        @Schema(description = "Number of projects required to be promoted to Master", example = "5")
        Integer toMasterProjectQuantity,

        @Schema(description = "Number of commits required to be promoted to Master", example = "100")
        Integer toMasterCommitsQuantity,

        @Schema(description = "Number of created campus tasks required to be promoted to Master", example = "10")
        Integer toMasterCreatedCampusTasksQuantity,

        @Schema(description = "Number of mentored sessions required to be promoted to Master", example = "5")
        Integer toMasterMentoredSessionsQuantity,

        @Schema(description = "Number of created campus tasks required to be promoted to Tech Lead", example = "20")
        Integer toTechLeadCreatedCampusTasksQuantity,

        @Schema(description = "Number of commits required to be promoted to Tech Lead", example = "200")
        Integer toTechLeadCommitsQuantity,

        @Schema(description = "Whether the user must have been a supervisor before promotion", example = "true")
        Boolean wasSupervisor,

        @Schema(description = "Whether the user must have been an architect before promotion", example = "true")
        Boolean wasArchitect
) {
}