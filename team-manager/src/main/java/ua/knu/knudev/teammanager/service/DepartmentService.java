package ua.knu.knudev.teammanager.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.SpecialtyMapper;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

@Service
@RequiredArgsConstructor
@Validated
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public void createDepartment(@Valid DepartmentCreationRequest departmentCreationRequest) {
        Department department = new Department();

        department.setName(departmentCreationRequest.name());
        departmentCreationRequest.specialties().forEach(specialtyDto -> {
            Specialty specialty = specialtyMapper.toDomain(specialtyDto);
            department.addSpecialty(specialty);
        });

        departmentRepository.save(department);
    }
}
