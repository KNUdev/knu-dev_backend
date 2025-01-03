package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
            @ApiResponse(responseCode = "200", description = "Task successfully uploaded."),
            @ApiResponse(responseCode = "400", description = "Invalid input provided."),
            @ApiResponse(responseCode = "403", description = "You are not have an access this endpoint.")
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDepartment(@Valid @RequestBody @Parameter(description = "Department creation data")
                                 DepartmentCreationRequest departmentCreationRequest) {
        departmentApi.createDepartment(departmentCreationRequest);
    }

}
