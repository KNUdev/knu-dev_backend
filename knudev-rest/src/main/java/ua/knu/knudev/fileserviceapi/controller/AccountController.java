package ua.knu.knudev.fileserviceapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountProfileApi accountAuthServiceApi;

    @PostMapping("/register")
    public AccountRegistrationResponse registerAccount(@Valid @RequestBody AccountCreationRequest registrationRequest) {
        return accountAuthServiceApi.register(registrationRequest);
    }

}
