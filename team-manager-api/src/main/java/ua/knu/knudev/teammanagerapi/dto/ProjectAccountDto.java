package ua.knu.knudev.teammanagerapi.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectAccountDto(
        UUID projectId,
        UUID accountId,
        LocalDate dateJoined
) {
}