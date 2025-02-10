package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
import ua.knu.knudev.teammanagerapi.response.GetAccountByIdResponse;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountProfileApi accountProfileApi;

    @Operation(
            summary = "Register a new account",
            description = """
                    This endpoint allows users to register a new account.
                    The request must include all required fields, such as username, email, and password.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account successfully registered.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountRegistrationResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountRegistrationResponse registerAccount(
            @Valid @ModelAttribute @Parameter(
                    name = "Account creation request",
                    description = "Account registration details",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = AccountCreationRequest.class)
            ) AccountCreationRequest registrationRequest) {
        return accountProfileApi.register(registrationRequest);
    }

    @GetMapping("/{accountId}")
    public GetAccountByIdResponse getById(@PathVariable String accountId) {
        return accountProfileApi.getById(accountId);
    }

    @PatchMapping(value = "/{accountId}/avatar/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateAvatar(@PathVariable UUID accountId, @RequestParam("newAvatar") MultipartFile newAvatar) {
        return accountProfileApi.updateAvatar(accountId, newAvatar);
    }

    @DeleteMapping("/{accountId}/avatar/remove")
    public void removeAvatar(@PathVariable UUID accountId) {
        accountProfileApi.removeAvatar(accountId);
    }

    @PatchMapping(value = "/{accountId}/banner/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateBanner(@PathVariable UUID accountId, @RequestParam("newBanner") MultipartFile newBanner) {
        return accountProfileApi.updateBanner(accountId, newBanner);
    }

    @DeleteMapping("/{accountId}/banner/remove")
    public void removeBanner(@PathVariable UUID accountId) {
        accountProfileApi.removeBanner(accountId);
    }
}
