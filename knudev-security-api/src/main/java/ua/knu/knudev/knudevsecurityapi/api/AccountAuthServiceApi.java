package ua.knu.knudev.knudevsecurityapi.api;

import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AccountCreationResponse;

public interface AccountAuthServiceApi {
    AccountCreationResponse createAccount(AccountCreationRequest creationRequest);
}
