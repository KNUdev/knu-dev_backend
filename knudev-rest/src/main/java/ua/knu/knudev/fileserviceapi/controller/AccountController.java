package ua.knu.knudev.fileserviceapi.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountAuthServiceApi accountAuthServiceApi;

    @PostMapping("/register")
    public void registerAccount(@Valid @RequestBody AccountCreationRequest registrationRequest) {
        accountAuthServiceApi.createAccount(registrationRequest);
    }

}
