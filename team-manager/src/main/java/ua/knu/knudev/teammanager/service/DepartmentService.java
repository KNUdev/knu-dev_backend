package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public void createDepartment(DepartmentCreationRequest departmentCreationRequest) {
        Department department = new Department();
        String departmentCreationName = departmentCreationRequest.name();

        boolean departmentAlreadyInDb = existByName(departmentCreationName);
        if (departmentAlreadyInDb) {
            throw new DepartmentException("Department with name" + departmentCreationName + "already exists");
        }


        department.setName(departmentCreationName);
        departmentCreationRequest.specialties()
                .forEach(specialtyDto -> {
                    Specialty specialty = new Specialty();
                    specialty.setCodeName(specialtyDto.codeName());
                    specialty.setName(specialtyDto.name());
                    department.addSpecialty(specialty);
                });

        departmentRepository.save(department);
    }

    public Department getById(UUID id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.orElseThrow(() -> new DepartmentException(
                String.format("Department with id %s not found", id)
        ));
    }

    public void validateAcademicUnitByIds(AcademicUnitsIds academicUnitsIds) {
        Department department = getById(academicUnitsIds.departmentId());
        validateSpecialtyInDepartment(department, academicUnitsIds.specialtyId());
    }

    private void validateSpecialtyInDepartment(Department department, Double specialtyId) {
        boolean containsSpecialty = department.getSpecialties()
                .stream()
                .anyMatch(specialty -> specialty.getCodeName().equals(specialtyId));
        if (!containsSpecialty) {
            throw new DepartmentException(
                    String.format("Department with id %s does not contain specialty with id %s",
                            department.getId(),
                            specialtyId)
            );
        }
    }

    private boolean existByName(String name) {
        return departmentRepository.existsByName(name);
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }
}
