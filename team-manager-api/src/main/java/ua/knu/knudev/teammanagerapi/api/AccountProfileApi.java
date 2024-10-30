package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

public interface AccountProfileApi {
    AccountRegistrationResponse register(AccountCreationRequest registrationRequest);
}
