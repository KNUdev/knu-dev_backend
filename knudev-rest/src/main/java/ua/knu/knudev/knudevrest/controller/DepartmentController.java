package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentApi departmentApi;

    @GetMapping("/departments")
    public Set<ShortDepartmentDto> getShortDepartments() {
        return departmentApi.getShortDepartments();
    }

    @GetMapping("/departments/{departmentId}/specialties")
    public Set<ShortSpecialtyDto> getSpecialtiesByDepartment(@PathVariable UUID departmentId) {
        return departmentApi.getSpecialtiesByDepartmentId(departmentId);
    }

}
