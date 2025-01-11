package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectApi projectApi;

    @Operation(
            summary = "Retrieve project details by ID",
            description = "Fetches detailed information about a project based on the provided project ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project was successfully retrieved.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProject(@PathVariable @Parameter(
            name = "projectId",
            description = "Id of the project, information about which we want to see",
            in = ParameterIn.HEADER,
            schema = @Schema(implementation = ProjectDto.class),
            required = true,
            example = "f3b1c1b7d287b9f5acdb2f941517c7a9fcbf4bb2d9e8b3d3cfc622b1f67d34e8"
    ) UUID projectId) {
        return projectApi.getById(projectId);
    }

    @Operation(
            summary = "Retrieve a list of projects",
            description = "Fetches information about all projects in organization."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projects were successfully retrieved.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)
            ))
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Set<ProjectDto> getAllProjects() {
        return projectApi.getAll();
    }
}
