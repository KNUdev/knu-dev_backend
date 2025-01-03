package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.taskmanagerapi.api.TaskUploadAPI;

@RestController
@RequestMapping("/admin/task/upload")
@RequiredArgsConstructor
public class AdminTaskUploadController {

    private final TaskUploadAPI taskUploadAPI;

    @Operation(
            summary = "Upload task for role",
            description = """
                            This endpoint allows administrators to upload tasks associated with a specific role.
                            The role is passed as a path variable, and the task file is sent as a multipart file.
                            Example:
                            - Role: Intern
                            - File: task-details.pdf
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully uploaded."),
            @ApiResponse(responseCode = "400", description = "Invalid input provided."),
            @ApiResponse(responseCode = "403", description = "You are not have an access this endpoint.")
    })
    @PostMapping(value = "/campus/{role}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain;charset=UTF-8"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadTaskForRole(@PathVariable("role")
                                    @Parameter(description = "Current user`s account role", example = "Intern") String accountRole,
                                    @RequestParam("taskFile") @Valid @NotNull
                                    @Parameter(description = "File with task for user") MultipartFile taskFile) {
        return taskUploadAPI.uploadTaskForRole(accountRole, taskFile);
    }

}
