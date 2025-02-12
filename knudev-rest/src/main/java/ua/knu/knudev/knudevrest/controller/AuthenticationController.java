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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.api.AuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;

import java.io.IOException;

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

    @Operation(
            summary = "Refresh access token",
            description = "Refresh the access token using a valid refresh token provided in the Authorization header. "
                    + "The header must be in the format 'Bearer <refreshToken>'. On success, returns a new access token along with the provided refresh token.",
            parameters = {
                    @Parameter(
                            name = HttpHeaders.AUTHORIZATION,
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer token containing the refresh token",
                            schema = @Schema(
                                    type = "string",
                                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
                            )
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - either an access token was provided instead of a refresh token, or the token is invalid.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid Authorization header, or the provided token has expired.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authServiceApi.refreshToken(request, response);
    }

}
