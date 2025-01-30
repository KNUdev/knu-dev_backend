package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a project")
public class FullProjectDto {

    @Schema(description = "Unique identifier of the project", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Project name (multi-language field)", implementation = MultiLanguageFieldDto.class)
    protected MultiLanguageFieldDto name;

    @Schema(description = "Project description (multi-language field)", implementation = MultiLanguageFieldDto.class)
    private MultiLanguageFieldDto description;

    @Schema(description = "Status of the project", example = "PLANNED", implementation = ProjectStatus.class)
    private ProjectStatus status;

    @Schema(description = "Filename of the project's avatar", example = "project-avatar.png")
    private String avatarFilename;

    @Schema(description = "Tags associated with the project", implementation = ProjectTag.class)
    private Set<ProjectTag> tags;

    @Schema(description = "Start date of the project", example = "2023-01-01")
    private LocalDate startedAt;

    @Schema(description = "Links to the project's GitHub repositories")
    private Set<String> githubRepoLinks;

    @Schema(description = "Release information about the project", implementation = ProjectReleaseInfoDto.class)
    private ProjectReleaseInfoDto releaseInfo;

    @Schema(description = "Accounts associated with the project", implementation = ProjectAccountDto.class)
    private Set<ProjectAccountDto> projectAccounts;
}
