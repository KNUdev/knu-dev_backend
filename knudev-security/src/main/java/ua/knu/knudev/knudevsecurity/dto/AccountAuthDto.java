package ua.knu.knudev.knudevsecurity.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountAuthDto(
        UUID id,
        String email,
        String password,
//        Set<AccountRole> roles,
        boolean enabled,
        boolean nonLocked
) {
}
