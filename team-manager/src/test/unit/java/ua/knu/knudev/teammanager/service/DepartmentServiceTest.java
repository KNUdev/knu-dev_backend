package ua.knu.knudev.teammanager.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.SpecialtyMapper;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    private static final Department TEST_DEPARTMENT = getTestDepartment();
    private static final Double TEST_SPECIALTY_ID = DepartmentTestsConstants.TEST_SPECIALTY_ID;
    private static final UUID TEST_DEPARTMENT_ID = DepartmentTestsConstants.TEST_DEPARTMENT_ID;
    private final UUID testDepartmentID = UUID.randomUUID();
    private final String departmentName = "Test Department";

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    @DisplayName("Validate academic unit successfully when department and specialty exist")
    void should_validateAcademicUnit_When_DepartmentAndSpecialtyExist() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(TEST_DEPARTMENT));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitByIds(academicUnitsIds));

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when on validation department does not exist")
    void should_throwDepartmentException_When_DepartmentDoesNotExistOnValidation() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.empty());
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        DepartmentException exception = assertThrows(
                DepartmentException.class,
                () -> departmentService.validateAcademicUnitByIds(academicUnitsIds)
        );
        assertEquals("Department with id " + TEST_DEPARTMENT_ID + " not found", exception.getMessage());

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when specialty does not exist in the department")
    void should_throwDepartmentException_When_SpecialtyDoesNotExistInDepartment() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(TEST_DEPARTMENT));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(999.99)
                .build();

        DepartmentException exception = assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitByIds(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with id 999.99",
                exception.getMessage()
        );

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when department exists but has no specialties")
    void should_throwDepartmentException_When_DepartmentExistsButHasNoSpecialties() {
        // Arrange
        Department emptyDepartment = new Department();
        emptyDepartment.setId(TEST_DEPARTMENT_ID);
        emptyDepartment.setName("Engineering");
        emptyDepartment.setSpecialties(new HashSet<>());

        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(emptyDepartment));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        // Act & Assert
        DepartmentException exception = assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitByIds(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with id " + TEST_SPECIALTY_ID,
                exception.getMessage()
        );
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Validate academic unit successfully when specialties are loaded lazily")
    void should_validateAcademicUnit_When_SpecialtiesAreLoadedLazily() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(TEST_DEPARTMENT));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitByIds(academicUnitsIds));

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Return department when it exists")
    void should_returnDepartment_When_DepartmentExists() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(TEST_DEPARTMENT));

        Department result = departmentService.getById(TEST_DEPARTMENT_ID);

        assertNotNull(result);
        assertEquals(TEST_DEPARTMENT_ID, result.getId());
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when on get by id department does not exist")
    void should_throwDepartmentException_When_DepartmentDoesNotExistOnGet() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.empty());

        DepartmentException exception = assertThrows(
                DepartmentException.class,
                () -> departmentService.getById(TEST_DEPARTMENT_ID)
        );

        assertEquals("Department with id " + TEST_DEPARTMENT_ID + " not found", exception.getMessage());

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Should create department when given valid data")
    public void should_create_department_when_given_valid_data() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, "CI");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(172.0, "RT");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), departmentName);
        Department department = buildDepartmentEntity(departmentName, specialtyCreationDto1, specialtyCreationDto2);

        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        departmentService.createDepartment(creationRequest);

        verify(specialtyMapper).toDomain(specialtyCreationDto1);
        verify(specialtyMapper).toDomain(specialtyCreationDto2);
        verify(departmentRepository).save(argThat(dep -> dep.getName().equals(departmentName)));

        assertEquals(1, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when not given department name")
    public void should_not_create_department_when_not_given_department_name() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, "CI");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(172.0, "RT");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), null);
        Department department = buildDepartmentEntity(null, specialtyCreationDto1, specialtyCreationDto2);
        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when department name is blank")
    public void should_not_create_department_when_department_name_is_blank() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, "CI");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(172.0, "RT");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), " ");
        Department department = buildDepartmentEntity(" ", specialtyCreationDto1, specialtyCreationDto2);
        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when specialties collection is empty")
    public void should_not_create_department_when_specialties_collection_is_empty() {
        Department department = new Department();
        department.setId(testDepartmentID);
        department.setName(departmentName);
        department.setSpecialties(null);

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(null, departmentName);
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has invalid code-name")
    public void should_not_create_department_when_at_least_one_specialty_has_invalid_code_name() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, "CI");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(0.0, "RT");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), departmentName);
        Department department = buildDepartmentEntity(departmentName, specialtyCreationDto1, specialtyCreationDto2);
        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has null name")
    public void should_not_create_department_when_at_least_one_specialty_has_invalid_name() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, null);
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(172.0, "RT");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), departmentName);
        Department department = buildDepartmentEntity(departmentName, specialtyCreationDto1, specialtyCreationDto2);
        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has blank name")
    public void should_not_create_department_when_at_least_one_specialty_has_blank_name() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(123.0, "CI");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(172.0, " ");

        DepartmentCreationRequest creationRequest = buildDepartmentCreationRequest(new HashSet<>(List.of(specialtyCreationDto1, specialtyCreationDto2)), departmentName);
        Department department = buildDepartmentEntity(departmentName, specialtyCreationDto1, specialtyCreationDto2);
        when(specialtyMapper.toDomain(specialtyCreationDto1)).thenReturn(department.getSpecialties().stream().findFirst().orElse(null));
        when(specialtyMapper.toDomain(specialtyCreationDto2)).thenReturn(department.getSpecialties().stream().skip(1).findFirst().orElse(null));
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));

        assertThrows(ConstraintViolationException.class, () -> {
            departmentService.createDepartment(creationRequest);
        });

        assertEquals(0, departmentRepository.findAll().size());
    }

    private DepartmentCreationRequest buildDepartmentCreationRequest(Set<SpecialtyCreationDto> specialties, String departmentName) {
        return DepartmentCreationRequest.builder()
                .id(testDepartmentID)
                .name(departmentName)
                .specialties(specialties)
                .build();
    }

    private Department buildDepartmentEntity(String departmentName, SpecialtyCreationDto specialtyCreationDto1, SpecialtyCreationDto specialtyCreationDto2) {
        Department department = new Department();
        Specialty specialty1 = new Specialty();
        Specialty specialty2 = new Specialty();

        department.setId(testDepartmentID);
        department.setName(departmentName);
        specialty1.setCodeName(specialtyCreationDto1.codeName());
        specialty1.setName(specialtyCreationDto1.name());
        specialty2.setCodeName(specialtyCreationDto2.codeName());
        specialty2.setName(specialtyCreationDto2.name());

        department.addSpecialty(specialty1);
        department.addSpecialty(specialty2);

        return department;
    }

}
