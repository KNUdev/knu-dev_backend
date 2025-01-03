package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.taskmanagerapi.api.TaskAPI;
import ua.knu.knudev.taskmanagerapi.dto.TaskDto;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskAPI taskAPI;

    @Operation(summary = "Get Task by ID", description = "Fetches a task by its unique task ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the task"),
            @ApiResponse(responseCode = "400", description = "Invalid task ID supplied"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/gdjghdsjg")
    public TaskDto getTaskById(
            @Parameter(description = "The ID of the task to be retrieved", required = true)
            @RequestParam String taskId) {
        return taskAPI.getById(taskId);
    }

}
