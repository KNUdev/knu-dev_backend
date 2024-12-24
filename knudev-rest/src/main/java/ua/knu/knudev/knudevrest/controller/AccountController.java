package ua.knu.knudev.knudevrest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountProfileApi accountAuthServiceApi;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountRegistrationResponse registerAccount(@Valid @ModelAttribute AccountCreationRequest registrationRequest) {
        return accountAuthServiceApi.register(registrationRequest);
    }


}
