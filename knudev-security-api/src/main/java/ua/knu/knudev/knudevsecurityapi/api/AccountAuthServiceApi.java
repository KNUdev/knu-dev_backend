package ua.knu.knudev.knudevsecurityapi.api;

import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.request.AccountAuthUpdateRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;

public interface AccountAuthServiceApi {
    AuthAccountCreationResponse createAccount(AccountCreationRequest creationRequest);

    boolean existsByEmail(String email);

    void update(AccountAuthUpdateRequest updateRequest);
}
