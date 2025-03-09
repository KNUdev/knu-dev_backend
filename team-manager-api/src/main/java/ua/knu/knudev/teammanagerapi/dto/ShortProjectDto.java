package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for a short description of a project")
public class ShortProjectDto {

    @Schema(description = "Project name (multi-language field)", implementation = MultiLanguageFieldDto.class)
    private String name;

    @Schema(description = "Project description (multi-language field)", implementation = MultiLanguageFieldDto.class)
    private MultiLanguageFieldDto description;

    @Schema(description = "Status of the project", example = "PLANNED", implementation = ProjectStatus.class)
    private ProjectStatus status;

    @Schema(description = "Filename of the project's avatar", example = "project-avatar.png")
    private String banner;

    @Schema(description = "Tags associated with the project", implementation = ProjectTag.class)
    private Set<ProjectTag> tags;

    @Schema(description = "Project`s last update date")
    private LocalDateTime lastUpdate;
}
