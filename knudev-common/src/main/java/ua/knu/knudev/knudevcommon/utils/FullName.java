package ua.knu.knudev.knudevcommon.utils;

import lombok.Builder;

@Builder
public record FullName(
        String firstName,
        String lastName,
        String middleName
) {
}
