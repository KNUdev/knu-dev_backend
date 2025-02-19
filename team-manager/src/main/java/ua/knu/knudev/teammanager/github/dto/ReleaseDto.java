package ua.knu.knudev.teammanager.github.dto;

import java.time.LocalDateTime;

public record ReleaseDto(
        LocalDateTime initializedAt,
        LocalDateTime releaseFinishDate,
        String version,
        String changesLogEn,
        Integer aggregatedGithubCommitCount
) {
}
