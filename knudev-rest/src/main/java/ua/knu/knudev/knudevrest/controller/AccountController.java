package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Register a new account",
            description = """
        This endpoint allows users to register a new account.
        The request must include all required fields, such as username, email, and password.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account successfully registered."),
            @ApiResponse(responseCode = "400", description = "Invalid input provided.")
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountRegistrationResponse registerAccount(
            @Valid @ModelAttribute @Parameter(description = "Account registration details") AccountCreationRequest registrationRequest) {
        return accountAuthServiceApi.register(registrationRequest);
    }
}

