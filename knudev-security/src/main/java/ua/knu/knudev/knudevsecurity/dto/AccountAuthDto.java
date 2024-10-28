package ua.knu.knudev.knudevsecurity.dto;

import lombok.Builder;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.util.Set;
import java.util.UUID;

@Builder
public record AccountAuthDto(
        UUID id,
        String email,
        String password,
        Set<AccountRole> roles,
        boolean enabled,
        boolean nonLocked
) {
}
