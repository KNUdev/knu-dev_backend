package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Request object for creating role promotion conditions with quantitative requirements")
public record RolePromotionConditionCreationRequest(
        @Schema(description = "Number of projects required to be promoted to Premaster", example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Projects in campus amount cannot be null")
        @Min(value = 0, message = "Projects in campus amount cannot be negative")
        Integer toPremasterProjectQuantity,

        @Schema(description = "Number of commits required to be promoted to Premaster", example = "50",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Commits in campus amount cannot be null")
        @Min(value = 0, message = "Commits in campus amount cannot be negative")
        Integer toPremasterCommitsQuantity,

        @Schema(description = "Number of projects required to be promoted to Master", example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Created tasks in campus amount cannot be null")
        @Min(value = 0, message = "Created tasks in campus amount cannot be negative")
        Integer toMasterProjectQuantity,

        @Schema(description = "Number of commits required to be promoted to Master", example = "100",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Commits in campus amount cannot be null")
        @Min(value = 0, message = "Commits in campus amount cannot be negative")
        Integer toMasterCommitsQuantity,

        @Schema(description = "Number of created campus tasks required to be promoted to Master", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Created tasks in campus amount cannot be null")
        @Min(value = 0, message = "Created tasks in campus amount cannot be negative")
        Integer toMasterCreatedCampusTasksQuantity,

        @Schema(description = "Number of mentored sessions required to be promoted to Master", example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Mentored sessions amount cannot be null")
        @Min(value = 0, message = "Mentored sessions amount cannot be negative")
        Integer toMasterMentoredSessionsQuantity,

        @Schema(description = "Number of created campus tasks required to be promoted to Tech Lead", example = "20",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Created tasks in campus amount cannot be null")
        @Min(value = 0, message = "Created tasks in campus amount cannot be negative")
        Integer toTechLeadCreatedCampusTasksQuantity,

        @Schema(description = "Number of commits required to be promoted to Tech Lead", example = "200",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Commits in campus amount cannot be null")
        @Min(value = 0, message = "Commits in campus amount cannot be negative")
        Integer toTechLeadCommitsQuantity,

        @Schema(description = "Whether the user must have been a supervisor before promotion", example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Was supervisor cannot be null")
        Boolean wasSupervisor,

        @Schema(description = "Whether the user must have been an architect before promotion", example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Was architect cannot be null")
        Boolean wasArchitect
) {
}