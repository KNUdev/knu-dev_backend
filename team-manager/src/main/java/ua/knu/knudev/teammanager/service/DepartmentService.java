package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DepartmentService implements DepartmentApi {

    private final DepartmentRepository departmentRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    @Transactional
    public void createDepartment(DepartmentCreationRequest departmentCreationRequest) {
        validateDepartmentCreationRequest(departmentCreationRequest.specialties());

        String departmentCreationEnglishName = departmentCreationRequest.nameInEnglish();
        String departmentCreationUkrainianName = departmentCreationRequest.nameInUkrainian();
        Set<SpecialtyCreationDto> specialtiesDto = departmentCreationRequest.specialties();

        assertDepartmentDoesNotExist(departmentCreationEnglishName, departmentCreationUkrainianName);

        List<Double> specialtiesCodeNames = specialtiesDto.stream()
                .map(SpecialtyCreationDto::codeName)
                .collect(Collectors.toList());

        List<Specialty> existingSpecialtiesByCodename = specialtyRepository.findSpecialtiesByCodeNameIn(specialtiesCodeNames);
        List<Specialty> existingSpecialtiesByNames = specialtyRepository.findSpecialtiesByNameInEnglishInOrNameInUkrainianIn(
                specialtiesDto.stream().map(SpecialtyCreationDto::nameInEnglish).collect(Collectors.toSet()),
                specialtiesDto.stream().map(SpecialtyCreationDto::nameInUkrainian).collect(Collectors.toSet())
        );

        filterNotValidRequestSpecialties(existingSpecialtiesByCodename, existingSpecialtiesByNames, departmentCreationRequest.specialties());

        Map<Double, Specialty> existingSpecialtiesCodeNameSpecialtyMap = existingSpecialtiesByCodename.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, specialty -> specialty));
        Set<SpecialtyCreationDto> newSpecialtiesToCreate = specialtiesDto.stream()
                .filter(dto -> !existingSpecialtiesCodeNameSpecialtyMap.containsKey(dto.codeName()))
                .collect(Collectors.toSet());

        Set<Specialty> newSpecialties = newSpecialtiesToCreate.stream()
                .map(dto -> Specialty.builder()
                        .codeName(dto.codeName())
                        .nameInEnglish(dto.nameInEnglish())
                        .nameInUkrainian(dto.nameInUkrainian())
                        .build())
                .collect(Collectors.toSet());

        Set<Specialty> allSpecialties = new HashSet<>(existingSpecialtiesByCodename);
        allSpecialties.addAll(newSpecialties);

        Department department = Department.builder()
                .nameInEnglish(departmentCreationEnglishName)
                .nameInUkrainian(departmentCreationUkrainianName)
                .specialties(allSpecialties)
                .build();

        departmentRepository.save(department);
    }

    private void filterNotValidRequestSpecialties(
            List<Specialty> existingSpecialtiesByCodename,
            List<Specialty> existingSpecialtiesByName,
            Set<SpecialtyCreationDto> requestSpecialties
    ) {

        Set<SpecialtyCreationDto> codeNameMismatches = new HashSet<>();
        Set<SpecialtyCreationDto> nameMismatches = new HashSet<>();

        //Create Maps for Efficient Lookup
        Map<Double, Specialty> existingByCodeName = existingSpecialtiesByCodename.stream()
                .collect(Collectors.toMap(Specialty::getCodeName, Function.identity()));

        //Map names to their corresponding codeNames for name mismatch checks
        Map<String, Set<Double>> existingCodeNamesByEnglishName = existingSpecialtiesByName.stream()
                .collect(Collectors.groupingBy(
                        Specialty::getNameInEnglish,
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        Map<String, Set<Double>> existingCodeNamesByUkrainianName = existingSpecialtiesByName.stream()
                .collect(Collectors.groupingBy(
                        Specialty::getNameInUkrainian,
                        Collectors.mapping(Specialty::getCodeName, Collectors.toSet())
                ));

        //Identify Code Name Mismatches
        requestSpecialties.stream()
                .filter(dto -> existingByCodeName.containsKey(dto.codeName()))
                .filter(dto -> {
                    Specialty existing = existingByCodeName.get(dto.codeName());
                    return !existing.getNameInEnglish().equals(dto.nameInEnglish()) ||
                            !existing.getNameInUkrainian().equals(dto.nameInUkrainian());
                })
                .forEach(codeNameMismatches::add);

        //Identify Name Mismatches Based on nameInEnglish
        requestSpecialties.stream()
                .filter(dto -> existingCodeNamesByEnglishName.containsKey(dto.nameInEnglish()))
                .filter(dto -> !existingCodeNamesByEnglishName.get(dto.nameInEnglish()).contains(dto.codeName()))
                .forEach(nameMismatches::add);

        //Identify Name Mismatches Based on nameInUkrainian
        requestSpecialties.stream()
                .filter(dto -> existingCodeNamesByUkrainianName.containsKey(dto.nameInUkrainian()))
                .filter(dto -> !existingCodeNamesByUkrainianName.get(dto.nameInUkrainian()).contains(dto.codeName()))
                .forEach(nameMismatches::add);

        List<String> errorMessages = new ArrayList<>();
        //todo better exception messages
        if (!codeNameMismatches.isEmpty()) {
            errorMessages.add("Code name mismatch");
        }
        if (!nameMismatches.isEmpty()) {
            errorMessages.add("Name mismatch");
        }
        if(!errorMessages.isEmpty()) {
            throw new DepartmentException(
                    String.join("; ", errorMessages)
            );
        }
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

    private boolean existsByNameInEnglish(String name) {
        return departmentRepository.existsByNameInEnglish(name);
    }

    private boolean existsByNameInUkrainian(String name) {
        return departmentRepository.existsByNameInUkrainian(name);
    }

    private void validateDepartmentCreationRequest(Set<SpecialtyCreationDto> specialtyCreationDtos) {
        long inputSpecialtiesSize = specialtyCreationDtos.size();

        List<Function<SpecialtyCreationDto, ?>> fieldExtractors = List.of(
                SpecialtyCreationDto::nameInEnglish,
                SpecialtyCreationDto::nameInUkrainian,
                SpecialtyCreationDto::codeName
        );

        boolean allFieldsUnique = fieldExtractors.stream()
                .allMatch(extractor -> isStreamUnique(
                        specialtyCreationDtos.stream().map(extractor),
                        inputSpecialtiesSize
                ));

        if (!allFieldsUnique) {
            throw new DepartmentException("Specialties in the request are not unique");
        }
    }

    private void assertDepartmentDoesNotExist(String departmentNameInEnglish, String departmentNameInUkrainian) {
        boolean existByNameInEnglish = existsByNameInEnglish(departmentNameInEnglish);
        boolean existByNameInUkrainian = existsByNameInUkrainian(departmentNameInUkrainian);

        if (existByNameInUkrainian || existByNameInEnglish) {
            throw new DepartmentException(
                    String.format("Department with name %s already exists", departmentNameInEnglish)
            );
        }
    }

    private boolean isStreamUnique(Stream<?> stream, long size) {
        return stream.distinct().count() == size;
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }
}