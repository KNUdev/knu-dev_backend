package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectApi projectApi;

    @PostMapping("/create")
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
                    schema = @Schema(description = "Project creation request payload",
                            implementation = ProjectCreationRequest.class)
            ) ProjectCreationRequest projectCreationRequest) {
        projectApi.create(projectCreationRequest);
    }

    @Operation(
            summary = "Add developer to a project",
            description = "Adds a developer to the specified project by associating the developer's account profile with the project."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Developer successfully added to the project",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or account profile not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PatchMapping("/{projectId}/developers/add")
    @ResponseStatus(HttpStatus.OK)
    public void addDevelopers(@Valid @RequestBody @Parameter(
            name = "Add project developer request",
            description = "The object of adding a developer to the project",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(implementation = AddProjectDeveloperRequest.class)
    ) AddProjectDeveloperRequest addProjectDeveloperRequest) {
        projectApi.addDeveloper(addProjectDeveloperRequest);
    }

    @Operation(
            summary = "Upgrade project's status",
            description = "Updates the status of the project with the given project ID to the provided status. " +
                    "Only authorized users with appropriate permissions can update the project status."
    )
    @Parameters(value = {
            @Parameter(
                    name = "projectID",
                    description = "The id of project which status we want to update",
                    example = "f3b1c1b7d287b9f5acdb2f941517c7a9fcbf4bb2d9e8b3d3cfc622b1f67d34e8",
                    required = true,
                    in = ParameterIn.HEADER
            ),
            @Parameter(
                    name = "status",
                    description = "New status which we want set to that project",
                    example = "FINISHED",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(implementation = ProjectStatus.class))
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status was successfully changed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PatchMapping("/{projectId}/update/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateStatus(@PathVariable UUID projectId, @RequestBody ProjectStatus status) {
        projectApi.updateStatus(projectId, status);
    }

    @Operation(
            summary = "Release a project",
            description = "This endpoint releases a project by associating it with a domain and saving the release info."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project successfully released",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict, the project already has a release",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "projectId",
                    description = "The id of project which we want to release",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true,
                    in = ParameterIn.HEADER
            ),
            @Parameter(
                    name = "projectDomain",
                    description = "Domain which was assigned to that project",
                    required = true,
                    in = ParameterIn.HEADER
            )
    })
    @PatchMapping("/{projectId}/release")
    @ResponseStatus(HttpStatus.OK)
    public void releaseProject(@PathVariable UUID projectId, @RequestBody String projectDomain) {
        projectApi.release(projectId, projectDomain);
    }


}
