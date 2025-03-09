package ua.knu.knudev.teammanager.github.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserCommitsDto(
        LocalDate lastCommitDate,
        Integer totalCommits
) {
}
