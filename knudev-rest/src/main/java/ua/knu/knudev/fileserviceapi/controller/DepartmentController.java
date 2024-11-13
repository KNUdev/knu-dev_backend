package ua.knu.knudev.fileserviceapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentApi departmentApi;

    @PostMapping("/create")
    public void createDepartment(@RequestBody @Valid DepartmentCreationRequest departmentCreationRequest) {
        departmentApi.createDepartment(departmentCreationRequest);
    }
}
