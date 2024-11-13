package ua.knu.knudev.teammanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_NAME;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    private static final Double TEST_SPECIALTY_ID = DepartmentTestsConstants.TEST_SPECIALTY_ID;
    private static final UUID TEST_DEPARTMENT_ID = DepartmentTestsConstants.TEST_DEPARTMENT_ID;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    @DisplayName("Validate academic unit successfully when department and specialty exist")
    void should_ValidateAcademicUnit_When_DepartmentAndSpecialtyExist() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitExistence(academicUnitsIds));

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when on validation department does not exist")
    void should_ThrowDepartmentException_When_DepartmentDoesNotExistOnValidation() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.empty());
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        DepartmentException exception = assertThrows(
                DepartmentException.class,
                () -> departmentService.validateAcademicUnitExistence(academicUnitsIds)
        );
        assertEquals("Department with id " + TEST_DEPARTMENT_ID + " not found", exception.getMessage());

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when specialty does not exist in the department")
    void should_ThrowDepartmentException_When_SpecialtyDoesNotExistInDepartment() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(999.99)
                .build();

        DepartmentException exception = assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitExistence(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with id 999.99",
                exception.getMessage()
        );

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when department exists but has no specialties")
    void should_ThrowDepartmentException_When_DepartmentExistsButHasNoSpecialties() {
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
                departmentService.validateAcademicUnitExistence(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with id " + TEST_SPECIALTY_ID,
                exception.getMessage()
        );
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Validate academic unit successfully when specialties are loaded lazily")
    void should_ValidateAcademicUnit_When_SpecialtiesAreLoadedLazily() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .build();

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitExistence(academicUnitsIds));

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Return department when it exists")
    void should_ReturnDepartment_When_DepartmentExists() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));

        Department result = departmentService.getById(TEST_DEPARTMENT_ID);

        assertNotNull(result);
        assertEquals(TEST_DEPARTMENT_ID, result.getId());
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when on get by id department does not exist")
    void should_ThrowDepartmentException_When_DepartmentDoesNotExistOnGet() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.empty());

        DepartmentException exception = assertThrows(
                DepartmentException.class,
                () -> departmentService.getById(TEST_DEPARTMENT_ID)
        );

        assertEquals("Department with id " + TEST_DEPARTMENT_ID + " not found", exception.getMessage());

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

//    TODO improve
    @Test
    @DisplayName("Should create department when given valid data")
    public void Should_createDepartment_When_givenValidData() {
        // Arrange
        SpecialtyCreationDto specialtyCreationDto1 = buildSpecialtyCreationDto("Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = buildSpecialtyCreationDto("Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = buildSpecialtyCreationDto("History");
        Set<SpecialtyCreationDto> specialtyCreationDtos = Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3);
        List<Specialty> specialties = buildSpecialties(specialtyCreationDtos);

        List<String> specialtiesNames = List.of("Computer Engineering", "Radio technics", "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(specialtyCreationDtos,
                TEST_DEPARTMENT_NAME
        );
        when(specialtyRepository.findAll()).thenReturn(specialties);
        // Act
        departmentService.createDepartment(departmentCreationRequest);

        // Assert
        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository, times(1)).save(departmentCaptor.capture());

        Department savedDepartment = departmentCaptor.getValue();

        assertEquals(TEST_DEPARTMENT_NAME, savedDepartment.getName());
        assertEquals(3, savedDepartment.getSpecialties().size());

        specialtiesNames.forEach(specialtiesName -> {
            assertTrue(savedDepartment.getSpecialties().stream()
                    .map(Specialty::getName)
                    .collect(Collectors.toSet())
                    .containsAll(specialtiesNames));
        });
    }

    @Test
    @DisplayName("Should not create department when department exist")
    public void should_Not_Create_Department_When_DepartmentExists() {
        SpecialtyCreationDto specialtyCreationDto1 = buildSpecialtyCreationDto("Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = buildSpecialtyCreationDto("Radio technics");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2),
                TEST_DEPARTMENT_NAME
        );

        when(departmentRepository.existsByName(TEST_DEPARTMENT_NAME)).thenReturn(true);

        assertThrows(DepartmentException.class, () -> {
            departmentService.createDepartment(departmentCreationRequest);
        });
        verify(departmentRepository, times(1)).existsByName(TEST_DEPARTMENT_NAME);
        verifyNoMoreInteractions(departmentRepository);
    }

    private DepartmentCreationRequest buildDepartmentCreationRequest(Set<SpecialtyCreationDto> specialties, String departmentName) {
        return DepartmentCreationRequest.builder()
                .name(departmentName)
                .specialties(specialties)
                .build();
    }

    private List<Specialty> buildSpecialties(Set<SpecialtyCreationDto> specialtyCreationDtos) {
        return specialtyCreationDtos.stream().map(specialtyCreationDto -> {
            Specialty specialty = new Specialty();
            specialty.setCodeName(specialtyCreationDto.codeName());
            specialty.setName(specialtyCreationDto.name());
            return specialty;
        }).toList();

    }

    private SpecialtyCreationDto buildSpecialtyCreationDto(String name) {
        return new SpecialtyCreationDto(14.378, name);
    }

}
