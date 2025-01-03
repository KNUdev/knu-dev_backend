package ua.knu.knudev.teammanagerapi.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

@Schema(description = "Response object containing account registration data")
@Builder
public record AccountRegistrationResponse(

        @Schema(description = "Profile data of the registered account", implementation = AccountProfileDto.class,
                requiredMode = Schema.RequiredMode.REQUIRED)
        AccountProfileDto accountProfile,

        @Schema(description = "Message providing additional information about the registration process",
                example = "Account successfully created", requiredMode = Schema.RequiredMode.REQUIRED)
        String responseMessage
) {
}

