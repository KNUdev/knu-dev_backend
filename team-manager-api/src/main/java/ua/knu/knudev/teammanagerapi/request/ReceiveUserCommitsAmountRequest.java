package ua.knu.knudev.teammanagerapi.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReceiveUserCommitsAmountRequest(
        LocalDate firstCommitDate,
        LocalDate lastCommitDate,
        String gitHubUsername,
        Boolean isUndated
) {
}
