package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.Set;
import java.util.UUID;

public interface DepartmentApi {

    void createDepartment(DepartmentCreationRequest departmentCreationRequest);

    Set<ShortDepartmentDto> getShortDepartments();

    Set<ShortSpecialtyDto> getSpecialtiesByDepartmentId(UUID departmentId);
}
