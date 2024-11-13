package ua.knu.knudev.teammanager.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public void createDepartment(DepartmentCreationRequest departmentCreationRequest) {
        Department department = new Department();
        String departmentCreationName = departmentCreationRequest.name();
        Set<@Valid SpecialtyCreationDto> specialties = departmentCreationRequest.specialties();

        boolean departmentAlreadyInDb = existByName(departmentCreationName);
        if (departmentAlreadyInDb) {
            throw new DepartmentException("Department with name" + departmentCreationName + "already exists");
        }

        List<Specialty> allSpecialtiesInDb = specialtyRepository.findAll();
        Set<SpecialtyCreationDto> specialtyCreationDtos = rejectSpecialtiesWithSameParameter(specialties);
        Set<SpecialtyCreationDto> notExistInDbSpecialtyCreationDtos = chooseNotExistInDbSpecialties(allSpecialtiesInDb,
                specialtyCreationDtos);

        department.setName(departmentCreationName);
        if (CollectionUtils.isNotEmpty(notExistInDbSpecialtyCreationDtos)) {
            notExistInDbSpecialtyCreationDtos.forEach(specialtyDto -> {
                Specialty specialty = new Specialty();
                specialty.setCodeName(specialtyDto.codeName());
                specialty.setName(specialtyDto.name());
                department.addSpecialty(specialty);
            });
        }

        departmentRepository.save(department);
    }

    public Department getById(UUID id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.orElseThrow(() -> new DepartmentException(
                String.format("Department with id %s not found", id)
        ));
    }

    public void validateAcademicUnitExistence(AcademicUnitsIds academicUnitsIds) {
        Department department = getById(academicUnitsIds.departmentId());
        validateSpecialtyInDepartment(department, academicUnitsIds.specialtyCodename());
    }

    private void validateSpecialtyInDepartment(Department department, Double specialtyId) {
        boolean containsSpecialty = department.getSpecialties()
                .stream()
                .anyMatch(specialty -> specialty.getCodeName().equals(specialtyId));
        if (!containsSpecialty) {
            throw new DepartmentException(
                    String.format("Department with id %s does not contain specialty with code name: %s",
                            department.getId(),
                            specialtyId)
            );
        }
    }

    private boolean existByName(String name) {
        return departmentRepository.existsByName(name);
    }

    private static Set<SpecialtyCreationDto> chooseNotExistInDbSpecialties(List<Specialty> specialties, Set<SpecialtyCreationDto> specialtyCreationDtos) {
        if (CollectionUtils.isEmpty(specialties)) {
            return specialtyCreationDtos;
        }
        if (CollectionUtils.isEmpty(specialtyCreationDtos)) {
            return Collections.emptySet();
        }

        Set<Double> existingCodeNames = specialties.stream()
                .map(Specialty::getCodeName)
                .collect(Collectors.toSet());

        return specialtyCreationDtos.stream()
                .filter(specialtyCreationDto -> !existingCodeNames.contains(specialtyCreationDto.codeName()))
                .collect(Collectors.toSet());
    }

    private static Set<SpecialtyCreationDto> rejectSpecialtiesWithSameParameter(Set<SpecialtyCreationDto> specialtyCreationDtos) {
        if (CollectionUtils.isEmpty(specialtyCreationDtos)) {
            return Collections.emptySet();
        }

        Set<String> uniqueNames = new HashSet<>();
        Set<Double> uniqueCodeNames = new HashSet<>();
        Set<SpecialtyCreationDto> uniqueSpecialties = new HashSet<>();

        specialtyCreationDtos.forEach(specialty -> {
            boolean isUniqueName = uniqueNames.add(specialty.name());
            boolean isUniqueCodeName = uniqueCodeNames.add(specialty.codeName());

            if (isUniqueName && isUniqueCodeName) {
                uniqueSpecialties.add(specialty);
            }
        });
        return uniqueSpecialties;
    }


    public Department create(Department department) {
        return departmentRepository.save(department);
    }
}
