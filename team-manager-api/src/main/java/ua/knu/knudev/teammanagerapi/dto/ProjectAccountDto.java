package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ProjectAccountDto(
        UUID projectId,
        UUID accountId,
        LocalDate dateJoined
) {
}