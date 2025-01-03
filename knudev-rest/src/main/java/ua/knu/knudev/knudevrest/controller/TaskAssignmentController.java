package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.taskmanagerapi.api.TaskAssignmentApi;
import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;

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
            @ApiResponse(responseCode = "200", description = "Task successfully assigned"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid email or other parameters"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/to/{email}")
    public TaskAssignmentResponse assignTask(
            @Parameter(description = "Email address of the account to assign the task to", example = "email@knu.ua")
            @PathVariable(name = "email") String accountEmail) {
        return taskAssignmentApi.assignTaskToAccount(accountEmail);
    }
}
