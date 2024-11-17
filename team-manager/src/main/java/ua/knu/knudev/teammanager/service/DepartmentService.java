package ua.knu.knudev.teammanager.service;

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
        Set<SpecialtyCreationDto> specialties = departmentCreationRequest.specialties();
        String departmentCreationEnglishName = departmentCreationRequest.nameInEnglish();
        String departmentCreationUkrainianName = departmentCreationRequest.nameInUkrainian();

        Set<SpecialtyCreationDto> specialtyCreationDtos = checkIfDepartmentHasValidSpecialties(specialties);
        assertDepartmentDoesNotExist(departmentCreationEnglishName, departmentCreationUkrainianName);

        List<Double> specialtiesCodeNames = specialtyCreationDtos.stream()
                .map(SpecialtyCreationDto::codeName)
                .toList();

        List<Specialty> allSpecialtiesByExistingCodeNames = specialtyRepository.findSpecialtiesByCodeNameIn(specialtiesCodeNames);
        Set<SpecialtyCreationDto> notExistInDbSpecialtyCreationDtos = chooseNotExistInDbSpecialties(allSpecialtiesByExistingCodeNames,
                specialtyCreationDtos);

        department.setNameInEnglish(departmentCreationEnglishName);
        department.setNameInUkrainian(departmentCreationUkrainianName);

        if (CollectionUtils.isNotEmpty(notExistInDbSpecialtyCreationDtos)) {
            notExistInDbSpecialtyCreationDtos.forEach(specialtyDto -> {
                Specialty specialty = new Specialty();
                specialty.setCodeName(specialtyDto.codeName());
                specialty.setNameInEnglish(specialtyDto.nameInEnglish());
                specialty.setNameInUkrainian(specialtyDto.nameInUkrainian());
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

    private boolean existByNameInEnglish(String name) {
        return departmentRepository.existsByNameInEnglish(name);
    }

    private boolean existByNameInUkrainian(String name) {
        return departmentRepository.existsByNameInUkrainian(name);
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

    private static Set<SpecialtyCreationDto> checkIfDepartmentHasValidSpecialties(Set<SpecialtyCreationDto> specialtyCreationDtos) {
        if (CollectionUtils.isEmpty(specialtyCreationDtos)) {
            return Collections.emptySet();
        }

        List<String> specialtiesEnglishNames = new ArrayList<>();
        List<String> specialtiesUkrainianNames = new ArrayList<>();
        List<Double> specialtiesCodeNames = new ArrayList<>();

        specialtyCreationDtos.forEach(specialty -> {
            String nameInEnglish = specialty.nameInEnglish();
            String nameInUkrainian = specialty.nameInUkrainian();
            Double codeName = specialty.codeName();

            if (specialtiesEnglishNames.contains(nameInEnglish) || specialtiesUkrainianNames.contains(nameInUkrainian)
                    || specialtiesCodeNames.contains(codeName)) {
                throw new DepartmentException("Specialty with name in english " + nameInEnglish + " and with name in ukrainian: "
                        + nameInUkrainian + " and with code name " + codeName + " are not unique!");
            }
            specialtiesEnglishNames.add(nameInEnglish);
            specialtiesUkrainianNames.add(nameInUkrainian);
            specialtiesCodeNames.add(codeName);
        });

        return specialtyCreationDtos;
    }

    private void assertDepartmentDoesNotExist(String departmentNameInEnglish, String departmentNameInUkrainian) {
        boolean existByNameInEnglish = existByNameInEnglish(departmentNameInEnglish);
        boolean existByNameInUkrainian = existByNameInUkrainian(departmentNameInUkrainian);

        if (existByNameInUkrainian && existByNameInEnglish) {
            throw new DepartmentException("Department with name in ukrainian: " + departmentNameInUkrainian + " and with name in english: "
                    + departmentNameInEnglish + "already exists!");
        }
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }
}
