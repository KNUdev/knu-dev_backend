package ua.knu.knudev.teammanagerapi.response;

import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

@Builder
public record AccountRegistrationResponse(
        AccountProfileDto accountProfile,
        String responseMessage
) {
}
