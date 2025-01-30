//package ua.knu.knudev.knudevrest.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.enums.ParameterIn;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
//import ua.knu.knudev.assessmentmanagerapi.api.TaskAPI;
//import ua.knu.knudev.assessmentmanagerapi.dto.TaskDto;
//
//@RestController
//@RequiredArgsConstructor
//public class TaskController {
//
//    private final TaskAPI taskAPI;
//
//    @Operation(summary = "Get Task by ID", description = "Fetches a task by its unique task ID.")
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Successfully retrieved the task",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = TaskDto.class)
//                    )),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid task ID supplied",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class)
//                    )),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Task not found",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class)
//                    ))
//    })
//    @GetMapping("/gdjghdsjg")
//    public TaskDto getTaskById(
//            @Parameter(name = "Task id", description = "The ID of the task to be retrieved", required = true,
//                    in = ParameterIn.HEADER)
//            @RequestParam String taskId) {
//        return taskAPI.getById(taskId);
//    }
//
//}
