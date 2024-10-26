package ua.knu.knudev.teammanagerapi.dto;

import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;

import java.util.Set;

public record AccountProfileDto(
        String email,
        Set<AccountRole> roles
) {
}
