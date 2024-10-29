package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

@Builder
public record FullName(
        String firstName,
        String lastName,
        String middleName
) {
}
