package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.DepartmentWithSpecialtiesMapper;
import ua.knu.knudev.teammanager.mapper.ShortDepartmentMapper;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanagerapi.api.DepartmentApi;
import ua.knu.knudev.teammanagerapi.dto.DepartmentWithSpecialtiesDto;
import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ShortDepartmentMapper shortDepartmentMapper;
    private final DepartmentWithSpecialtiesMapper departmentWithSpecialtiesMapper;

    @Override
    @Transactional
    public void createDepartment(DepartmentCreationRequest request) {
        validateDepartmentCreationRequest(request.specialties());

        String nameEn = request.nameInEnglish();
        String nameUk = request.nameInUkrainian();
        Set<SpecialtyCreationDto> requestSpecialties = request.specialties();

        ensureDepartmentDoesNotExist(nameEn, nameUk);

        List<Double> specialtiesCodenames = requestSpecialties.stream()
                .map(SpecialtyCreationDto::codeName)
                .collect(Collectors.toList());

        List<Specialty> existingSpecialtiesByCodeName = specialtyRepository.findSpecialtiesByCodeNameIn(specialtiesCodenames);
        List<Specialty> existingSpecialtiesByName = specialtyRepository.findByNameUkInOrNameEnIn(
                requestSpecialties.stream().map(SpecialtyCreationDto::enName).collect(Collectors.toSet()),
                requestSpecialties.stream().map(SpecialtyCreationDto::ukName).collect(Collectors.toSet())
        );

        validateSpecialties(existingSpecialtiesByCodeName, existingSpecialtiesByName, requestSpecialties);

        Set<Specialty> allSpecialties = mergeExistingAndNewSpecialties(existingSpecialtiesByCodeName, requestSpecialties);
        Department department = Department.builder()
                .name(new MultiLanguageField(nameEn, nameUk))
                .specialties(allSpecialties)
                .build();

        departmentRepository.save(department);
    }

    @Override
    public Set<ShortDepartmentDto> getShortDepartments() {
        Set<Department> departments = new HashSet<>(departmentRepository.findAll());
        return shortDepartmentMapper.toDtos(departments);
    }

    public Set<DepartmentWithSpecialtiesDto> getFullDepartments() {
        Set<Department> departments = new HashSet<>(departmentRepository.findAll());
        return departmentWithSpecialtiesMapper.toDtos(departments);
    }

    @Override
    public Set<ShortSpecialtyDto> getSpecialtiesByDepartmentId(UUID departmentId) {
        return departmentRepository.findSpecialtiesByDepartmentId(departmentId).stream()
                .map(specialty -> ShortSpecialtyDto.builder()
                        .name(new MultiLanguageFieldDto(specialty.getName().getEn(), specialty.getName().getUk()))
                        .codeName(specialty.getCodeName())
                        .build())
                .collect(Collectors.toSet());
    }

    public Department getDepartmentByName(String nameEn, String nameUk) {
        String trimmedEnName = nameEn.trim();
        String trimmedUkName = nameUk.trim();
        return departmentRepository.getDepartmentByName_EnAndName_Uk(trimmedEnName, trimmedUkName)
                .orElseThrow(() -> new DepartmentException(String.format("Department with name in english: %s and uk name: %s not found",
                        trimmedEnName, trimmedUkName)));
    }

    public Department getById(UUID id) {
        if (ObjectUtils.isEmpty(id)) {
            log.error("Cannot get department by null id");
            throw new DepartmentException("Department id is null");
        }

        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.orElseThrow(() -> new DepartmentException(
                String.format("Department with id %s not found", id)
        ));
    }

    public void validateAcademicUnitExistence(UUID departmentId, Double specialtyCodeName) {
        Department department = getById(departmentId);
        ensureDepartmentContainsSpecialty(department, specialtyCodeName);
    }

    private void ensureDepartmentDoesNotExist(String nameEn, String nameUk) {
        boolean existsEn = departmentRepository.existsByName_En(nameEn);
        boolean existsUk = departmentRepository.existsByName_Uk(nameUk);

        if (existsEn || existsUk) {
            throw new DepartmentException(
                    String.format("Department with name '%s' or '%s' already exists", nameEn, nameUk),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Set<Specialty> mergeExistingAndNewSpecialties(List<Specialty> existingSpecialties,
                                                          Set<SpecialtyCreationDto> reqSpecialties) {
        Map<Double, Specialty> existingByCodeName = existingSpecialties.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, Function.identity()));

        Set<SpecialtyCreationDto> newSpecialtiesDto = reqSpecialties.stream()
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
                .name(new MultiLanguageField(dto.enName(), dto.ukName()))
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

    private Set<SpecialtyCreationDto> identifyCodeNameMismatches(List<Specialty> existingByCodeName,
                                                                 Set<SpecialtyCreationDto> requestSpecialties) {
        Map<Double, Specialty> existingMap = existingByCodeName.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, Function.identity()));

        return requestSpecialties.stream()
                .filter(dto -> existingMap.containsKey(dto.codeName()))
                .filter(dto -> {
                    Specialty existing = existingMap.get(dto.codeName());
                    MultiLanguageField existingName = existing.getName();
                    return !existingName.getEn().equals(dto.enName()) ||
                            !existingName.getUk().equals(dto.ukName());
                })
                .collect(Collectors.toSet());
    }

    private Set<SpecialtyCreationDto> identifyNameMismatches(List<Specialty> existingByName,
                                                             Set<SpecialtyCreationDto> requestSpecialties) {
        Map<String, Set<Double>> codeNamesByEnglishName = existingByName.stream()
                .collect(Collectors.groupingBy(
                        specialty -> specialty.getName().getEn(),
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        Map<String, Set<Double>> codeNamesByUkrainianName = existingByName.stream()
                .collect(Collectors.groupingBy(
                        specialty -> specialty.getName().getUk(),
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        return requestSpecialties.stream()
                .filter(dto -> hasNameMismatch(dto, codeNamesByEnglishName, codeNamesByUkrainianName))
                .collect(Collectors.toSet());
    }

    private void ensureDepartmentContainsSpecialty(Department department, Double specialtyCodeName) {
        if (ObjectUtils.isEmpty(specialtyCodeName)) {
            throw new DepartmentException("Specialty code-name cannot be empty");
        }

        boolean containsSpecialty = department.getSpecialties().stream()
                .anyMatch(specialty -> specialty.getCodeName().equals(specialtyCodeName));
        if (!containsSpecialty) {
            throw new DepartmentException(
                    String.format("Department '%s' does not contain specialty with code name '%s'.",
                            department.getId(), specialtyCodeName), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean hasNameMismatch(SpecialtyCreationDto reqSpecialty,
                                    Map<String, Set<Double>> codeNamesEn,
                                    Map<String, Set<Double>> codeNamesUk) {
        boolean mismatchEn = codeNamesEn.containsKey(reqSpecialty.enName()) &&
                !codeNamesEn.get(reqSpecialty.enName())
                        .contains(reqSpecialty.codeName());
        boolean mismatchUk = codeNamesUk.containsKey(reqSpecialty.ukName()) &&
                !codeNamesUk.get(reqSpecialty.ukName())
                        .contains(reqSpecialty.codeName());

        return mismatchEn || mismatchUk;
    }

    private void validateDepartmentCreationRequest(Set<SpecialtyCreationDto> specialtiesDto) {
        long expectedSize = specialtiesDto.size();

        List<Function<SpecialtyCreationDto, ?>> fieldExtractors = List.of(
                SpecialtyCreationDto::enName,
                SpecialtyCreationDto::ukName,
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

    private boolean areFieldsUnique(Set<SpecialtyCreationDto> specialtiesDto,
                                    Function<SpecialtyCreationDto, ?> extractor,
                                    long expectedSize) {
        return specialtiesDto.stream()
                .map(extractor)
                .distinct()
                .count() == expectedSize;
    }

}
