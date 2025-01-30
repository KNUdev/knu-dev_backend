package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.assessmentmanagerapi.api.TaskAssignmentApi;
import ua.knu.knudev.assessmentmanagerapi.response.TaskAssignmentResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/assign")
public class TaskAssignmentController {

    private final TaskAssignmentApi taskAssignmentApi;

    @Operation(
            summary = "Assign task to a specific account",
            description = "This endpoint assigns a task to an account based on the provided email."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task successfully assigned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskAssignmentResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request, invalid email or other parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/to/{email}")
    public TaskAssignmentResponse assignTask(
            @Parameter(name = "Account email", description = "Email address of the account to assign the task to",
                    example = "email@knu.ua", in = ParameterIn.HEADER, required = true)
            @PathVariable(name = "email") String accountEmail) {
        return taskAssignmentApi.assignTaskToAccount(accountEmail);
    }
}
