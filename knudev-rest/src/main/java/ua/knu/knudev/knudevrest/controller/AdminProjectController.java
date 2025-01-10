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
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminProjectController {

    private final ProjectApi projectApi;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new project",
            description = "This endpoint allows creating a new project with specified parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public void createProject(
            @Valid @RequestBody @Parameter(
                    name = "Project creation request",
                    description = "Project creation data",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(description = "Project creation request payload")
            ) ProjectCreationRequest projectCreationRequest) {
        projectApi.createProject(projectCreationRequest);
    }
}
