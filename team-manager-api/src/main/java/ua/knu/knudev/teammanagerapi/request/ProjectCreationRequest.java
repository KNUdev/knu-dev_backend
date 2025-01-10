package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;

@Schema(description = "Request for creating a new project.")
@Builder
public record ProjectCreationRequest(

        @Schema(
                description = "Project name in multiple languages.",
                example = "{ 'en': 'KNUdev main site', 'uk': 'КНУдев головний сайт' }",
                implementation = MultiLanguageFieldDto.class
        )
        @Valid @NotNull
        MultiLanguageFieldDto name,

        @Schema(
                description = "Project description in multiple languages.",
                example = "{ 'en': 'Main KNUdev organization site', 'uk': 'Головний сайт організації КНУдев' }",
                implementation = MultiLanguageFieldDto.class
        )
        @Valid @NotNull
        MultiLanguageFieldDto description,

        @Schema(description = "File for the project's avatar.", example = "avatar.png")
        MultipartFile avatarFile,

        @Schema(
                description = "Links to the project's GitHub repositories.",
                example = "[ 'https://github.com/knudev/backend', 'https://github.com/knudev/frontend' ]"
        )
        @NotEmpty(message = "At least one GitHub repository URL must be provided.")
        Set<String> githubRepoUrls,

        @Schema(description = "Tags associated with the project.", example = "[ 'WEB', 'MOBILE' ]",
                implementation = ProjectTag.class)
        @NotEmpty(message = "At least one project tag must be specified.")
        Set<ProjectTag> tags
) {
}
