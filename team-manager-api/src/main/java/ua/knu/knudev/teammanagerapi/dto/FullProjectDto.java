package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Schema(description = "Project name in english", example = "KNUdev")
    protected String name;

    @Schema(description = "Project description (multi-language field)", implementation = MultiLanguageFieldDto.class)
    private MultiLanguageFieldDto description;

    @Schema(description = "Filename of the project's banner", example = "project-banner.png")
    private String banner;

    @Schema(description = "Status of the project", example = "PLANNED", implementation = ProjectStatus.class)
    private ProjectStatus status;

    @Schema(description = "Tags associated with the project", implementation = ProjectTag.class)
    private Set<ProjectTag> tags;

    @Schema(description = "Start date of the project", example = "2023-01-01")
    private LocalDate startedAt;

    @Schema(description = "Project`s last update date", example = "2024-01-01")
    private LocalDateTime lastUpdatedAt;

    @Schema(description = "Project`s architect", implementation = AccountProfileDto.class)
    private AccountProfileDto architect;

    @Schema(description = "Project`s supervisor", implementation = AccountProfileDto.class)
    private AccountProfileDto supervisor;

    @Schema(description = "Project`s parts, like frontend and backend", implementation = SubprojectDto.class)
    private Set<SubprojectDto> subprojects;
}
