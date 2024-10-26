package ua.knu.knudev.knudevsecurity.dto;

import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.util.Set;
import java.util.UUID;

public record AccountAuthDto(
        UUID id,
        String email,
        String password,
        Set<AccountRole> roles,
        boolean enabled,
        boolean nonLocked
) {
}
