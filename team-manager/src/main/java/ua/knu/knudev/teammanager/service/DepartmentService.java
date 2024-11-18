package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    @Transactional
    public void createDepartment(DepartmentCreationRequest request) {
        validateDepartmentCreationRequest(request.specialties());

        String nameEn = request.nameInEnglish();
        String nameUk = request.nameInUkrainian();
        Set<SpecialtyCreationDto> specialtiesDto = request.specialties();

        ensureDepartmentDoesNotExist(nameEn, nameUk);

        List<Double> codeNames = specialtiesDto.stream()
                .map(SpecialtyCreationDto::codeName)
                .collect(Collectors.toList());
        List<Specialty> existingSpecialtiesByCodeName = specialtyRepository.findSpecialtiesByCodeNameIn(codeNames);
        List<Specialty> existingSpecialtiesByName = specialtyRepository.findSpecialtiesByNameInEnglishInOrNameInUkrainianIn(
                specialtiesDto.stream().map(SpecialtyCreationDto::nameInEnglish).collect(Collectors.toSet()),
                specialtiesDto.stream().map(SpecialtyCreationDto::nameInUkrainian).collect(Collectors.toSet())
        );

        validateSpecialties(existingSpecialtiesByCodeName, existingSpecialtiesByName, specialtiesDto);

        Set<Specialty> allSpecialties = mergeExistingAndNewSpecialties(existingSpecialtiesByCodeName, specialtiesDto);
        Department department = Department.builder()
                .nameInEnglish(nameEn)
                .nameInUkrainian(nameUk)
                .specialties(allSpecialties)
                .build();

        departmentRepository.save(department);
    }

    public Department getById(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() ->
                new DepartmentException(String.format("Department with id '%s' not found.", id), HttpStatus.BAD_REQUEST)
        );
    }

    public void validateAcademicUnitExistence(AcademicUnitsIds academicUnitsIds) {
        Department department = getById(academicUnitsIds.departmentId());
        ensureSpecialtyInDepartment(department, academicUnitsIds.specialtyCodename());
    }

    private void ensureDepartmentDoesNotExist(String nameEn, String nameUk) {
        boolean existsEn = departmentRepository.existsByNameInEnglish(nameEn);
        boolean existsUk = departmentRepository.existsByNameInUkrainian(nameUk);

        if (existsEn || existsUk) {
            throw new DepartmentException(
                    String.format("Department with name '%s' or '%s' already exists", nameEn, nameUk),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Set<Specialty> mergeExistingAndNewSpecialties(List<Specialty> existingSpecialties, Set<SpecialtyCreationDto> specialtiesDto) {
        Map<Double, Specialty> existingByCodeName = existingSpecialties.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, Function.identity()));

        Set<SpecialtyCreationDto> newSpecialtiesDto = specialtiesDto.stream()
                .filter(dto -> !existingByCodeName.containsKey(dto.codeName()))
                .collect(Collectors.toSet());

        Set<Specialty> newSpecialties = newSpecialtiesDto.stream()
                .map(this::convertToSpecialty)
                .collect(Collectors.toSet());

        Set<Specialty> allSpecialties = new HashSet<>(existingSpecialties);
        allSpecialties.addAll(newSpecialties);

        return allSpecialties;
    }

    private Specialty convertToSpecialty(SpecialtyCreationDto dto) {
        return Specialty.builder()
                .codeName(dto.codeName())
                .nameInEnglish(dto.nameInEnglish())
                .nameInUkrainian(dto.nameInUkrainian())
                .build();
    }

    private void validateSpecialties(
            List<Specialty> existingByCodeName,
            List<Specialty> existingByName,
            Set<SpecialtyCreationDto> requestSpecialties
    ) {
        Set<SpecialtyCreationDto> codeNameMismatches = identifyCodeNameMismatches(existingByCodeName, requestSpecialties);
        Set<SpecialtyCreationDto> nameMismatches = identifyNameMismatches(existingByName, requestSpecialties);

        List<String> errorMessages = new ArrayList<>();
        if (!codeNameMismatches.isEmpty()) {
            errorMessages.add("Conflicting code names with existing specialties");
        }
        if (!nameMismatches.isEmpty()) {
            errorMessages.add("Conflicting names with existing specialties");
        }
        if (!errorMessages.isEmpty()) {
            throw new DepartmentException(String.join(", ", errorMessages), HttpStatus.BAD_REQUEST);
        }
    }

    private Set<SpecialtyCreationDto> identifyCodeNameMismatches(List<Specialty> existingByCodeName, Set<SpecialtyCreationDto> requestSpecialties) {
        Map<Double, Specialty> existingMap = existingByCodeName.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, Function.identity()));

        return requestSpecialties.stream()
                .filter(dto -> existingMap.containsKey(dto.codeName()))
                .filter(dto -> {
                    Specialty existing = existingMap.get(dto.codeName());
                    return !existing.getNameInEnglish().equals(dto.nameInEnglish()) ||
                            !existing.getNameInUkrainian().equals(dto.nameInUkrainian());
                })
                .collect(Collectors.toSet());
    }

    private Set<SpecialtyCreationDto> identifyNameMismatches(List<Specialty> existingByName, Set<SpecialtyCreationDto> requestSpecialties) {
        Map<String, Set<Double>> codeNamesByEnglishName = existingByName.stream()
                .collect(Collectors.groupingBy(
                        Specialty::getNameInEnglish,
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        Map<String, Set<Double>> codeNamesByUkrainianName = existingByName.stream()
                .collect(Collectors.groupingBy(
                        Specialty::getNameInUkrainian,
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        return requestSpecialties.stream()
                .filter(dto -> hasNameMismatch(dto, codeNamesByEnglishName, codeNamesByUkrainianName))
                .collect(Collectors.toSet());
    }

    private boolean hasNameMismatch(SpecialtyCreationDto dto, Map<String, Set<Double>> codeNamesEn, Map<String, Set<Double>> codeNamesUk) {
        boolean mismatchEn = codeNamesEn.containsKey(dto.nameInEnglish()) &&
                !codeNamesEn.get(dto.nameInEnglish()).contains(dto.codeName());
        boolean mismatchUk = codeNamesUk.containsKey(dto.nameInUkrainian()) &&
                !codeNamesUk.get(dto.nameInUkrainian()).contains(dto.codeName());
        return mismatchEn || mismatchUk;
    }

    private void ensureSpecialtyInDepartment(Department department, Double specialtyCodeName) {
        boolean containsSpecialty = department.getSpecialties().stream()
                .anyMatch(specialty -> specialty.getCodeName().equals(specialtyCodeName));

        if (!containsSpecialty) {
            throw new DepartmentException(
                    String.format("Department '%s' does not contain specialty with code name '%s'.",
                            department.getId(), specialtyCodeName),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateDepartmentCreationRequest(Set<SpecialtyCreationDto> specialtiesDto) {
        long expectedSize = specialtiesDto.size();

        List<Function<SpecialtyCreationDto, ?>> fieldExtractors = List.of(
                SpecialtyCreationDto::nameInEnglish,
                SpecialtyCreationDto::nameInUkrainian,
                SpecialtyCreationDto::codeName
        );

        boolean allFieldsUnique = fieldExtractors.stream()
                .allMatch(extractor -> areFieldsUnique(specialtiesDto, extractor, expectedSize));

        if (!allFieldsUnique) {
            throw new DepartmentException(
                    "Specialties in the request must have unique names and code names.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private boolean areFieldsUnique(Set<SpecialtyCreationDto> specialtiesDto, Function<SpecialtyCreationDto, ?> extractor, long expectedSize) {
        return specialtiesDto.stream()
                .map(extractor)
                .distinct()
                .count() == expectedSize;
    }

}
