package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.SubprojectDto;

import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Request DTO for updating project details")
public record ProjectUpdateRequest(
        @Schema(description = "Unique identifier of the project", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "Project`s id can not be blank")
        @NotNull(message = "Project`s id can not be null")
        UUID id,

        @Schema(description = "Name of the project", example = "AI Research Platform")
        String name,

        @Schema(description = "Multilingual description of the project")
        MultiLanguageFieldDto description,

        @Schema(description = "URL of the project's banner image", example = "https://example.com/banner.jpg")
        String banner,

        @Schema(description = "Current status of the project", example = "UNDER_DEVELOPMENT")
        ProjectStatus status,

        @Schema(description = "Set of tags associated with the project")
        Set<ProjectTag> tags,

        @Schema(description = "Profile of the project's architect")
        AccountProfileDto architect,

        @Schema(description = "Profile of the project's supervisor")
        AccountProfileDto supervisor,

        @Schema(description = "Set of subprojects related to the project")
        Set<SubprojectDto> subprojects
) {
}
