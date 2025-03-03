package ua.knu.knudev.teammanager.github;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RetrieveGithubUserCommitsAmountRequest(
        LocalDate firstCommitDate,
        LocalDate lastCommitDate,
        String githubUsername,
        Boolean isUndated
) {
}
