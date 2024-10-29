package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

public interface AccountProfileApi {
    AccountProfileDto getByEmail(String email);
    AccountRegistrationResponse register(AccountCreationRequest registrationRequest);
}
