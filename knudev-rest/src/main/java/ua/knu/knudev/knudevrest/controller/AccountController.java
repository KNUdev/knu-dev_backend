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
import ua.knu.knudev.teammanagerapi.api.RolePromotionApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
import ua.knu.knudev.teammanagerapi.response.GetAccountByIdResponse;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountProfileApi accountProfileApi;
    private final RolePromotionApi rolePromotionApi;

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


    @Operation(
            summary = "Check if the account is eligible for role promotion",
            description = "Returns detailed promotion checklist and eligibility status for the given account",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Promotion eligibility checked successfully",
                            content = @Content(schema = @Schema(implementation = RolePromotionCheckResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{accountId}/promotion/check")
    public RolePromotionCheckResponse checkOnRolePromotionAbility(@PathVariable UUID accountId) {
        return rolePromotionApi.checkOnRolePromotionAbility(accountId);
    }

    @Operation(
            summary = "Promote the account to the next technical role",
            description = "Performs role promotion if the account meets the required conditions. " +
                    "Returns updated account profile.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account successfully promoted",
                            content = @Content(schema = @Schema(implementation = AccountProfileDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Promotion conditions not met"),
                    @ApiResponse(responseCode = "404", description = "Account not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PatchMapping("/{accountId}/promotion")
    public AccountProfileDto promote(@PathVariable UUID accountId) {
        return rolePromotionApi.promoteAccountProfileRole(accountId);
    }
}
