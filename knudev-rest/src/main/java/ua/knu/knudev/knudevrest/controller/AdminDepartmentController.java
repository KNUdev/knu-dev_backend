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
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/department")
public class AdminDepartmentController {

    private final DepartmentApi departmentApi;

    @Operation(
            summary = "Create a new department",
            description = """
                    This endpoint allows the admin to create a new department by providing 
                    necessary details such as the department name and description.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Department successfully created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "You do not have access to this endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDepartment(
            @Valid @RequestBody @Parameter(
                    name = "Department creation request",
                    description = "Department creation data",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = DepartmentCreationRequest.class)
            ) DepartmentCreationRequest departmentCreationRequest) {
        departmentApi.createDepartment(departmentCreationRequest);
    }
}
