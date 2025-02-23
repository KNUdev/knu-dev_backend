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
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.SubprojectDto;
import ua.knu.knudev.teammanagerapi.request.ProjectUpdateRequest;
import ua.knu.knudev.teammanagerapi.request.SubprojectUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectApi projectApi;

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Update an existing project",
            description = "This endpoint allows to update existing project with specified parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project updated successfully",
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
    public FullProjectDto updateProject(
            @Valid @RequestBody @Parameter(
                    name = "Project update request",
                    description = "Project update data",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(description = "Project update request payload",
                            implementation = ProjectUpdateRequest.class)
            ) ProjectUpdateRequest projectUpdateRequest) {
        return projectApi.updateProject(projectUpdateRequest);
    }

    @Operation(
            summary = "Update subproject",
            description = "Allows to update some of subproject characteristics."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Subproject successfully updated",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subproject not found",
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
    @PatchMapping("/update/subproject")
    @ResponseStatus(HttpStatus.OK)
    public SubprojectDto updateSubproject(@Valid @RequestBody @Parameter(
            name = "Update subproject request",
            description = "The object of updating subproject",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(implementation = SubprojectUpdateRequest.class)
    ) SubprojectUpdateRequest subprojectUpdateRequest) {
        return projectApi.updateSubproject(subprojectUpdateRequest);
    }

}
