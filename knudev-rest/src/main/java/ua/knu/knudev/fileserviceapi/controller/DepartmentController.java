package ua.knu.knudev.fileserviceapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/department")
public class DepartmentController {

    private final DepartmentApi departmentApi;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDepartment(@Valid @RequestBody DepartmentCreationRequest departmentCreationRequest) {
        departmentApi.createDepartment(departmentCreationRequest);
    }
}
