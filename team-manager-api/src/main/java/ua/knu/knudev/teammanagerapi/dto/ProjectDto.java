package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record ProjectDto(

        UUID id,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        String avatarFilename,
        LocalDate startedAt,
        ProjectStatus status,
        Set<ProjectTag> tags,
        Set<String> githubRepoLinks,
        ProjectReleaseInfoDto releaseInfo,
        Set<ProjectAccountDto> projectAccounts

) {
}