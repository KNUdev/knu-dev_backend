package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.api.AuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthServiceApi authServiceApi;

    @Operation(
            summary = "Authenticate user",
            description = "This endpoint allows the user to authenticate by providing their credentials (username and password)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid authentication request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Parameter(
                    name = "Authentication request",
                    description = "Authentication request containing the user's credentials",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = AuthenticationRequest.class)
            ) AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authServiceApi.authenticate(authenticationRequest));
    }

//    @PostMapping("/refresh-token")
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        authApi.refreshToken(request, response);
//    }
//
//    @PostMapping("/tfa/code/verify")
//    public AuthenticationResponse verifyTfaCode(@Valid TfaVerificationRequest request) {
//        return authApi.verifyTfaCode(request);
//    }
}
