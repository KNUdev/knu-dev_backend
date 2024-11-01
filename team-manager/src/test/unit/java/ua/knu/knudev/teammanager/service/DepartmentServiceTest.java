package ua.knu.knudev.teammanager.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    private static final Double TEST_SPECIALTY_ID = DepartmentTestsConstants.TEST_SPECIALTY_ID;
    private static final UUID TEST_DEPARTMENT_ID = DepartmentTestsConstants.TEST_DEPARTMENT_ID;
    private final UUID testDepartmentID = UUID.randomUUID();
    private static final String TEST_DEPARTMENT_NAME = "Test Department";

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    @DisplayName("Validate academic unit successfully when department and specialty exist")
    void should_validateAcademicUnit_When_DepartmentAndSpecialtyExist() {
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
    void should_throwDepartmentException_When_DepartmentDoesNotExistOnValidation() {
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
    void should_throwDepartmentException_When_SpecialtyDoesNotExistInDepartment() {
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
    void should_validateAcademicUnit_When_SpecialtiesAreLoadedLazily() {
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
    void should_returnDepartment_When_DepartmentExists() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));

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
    public void should_createDepartment_When_givenValidData() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, "Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(14.378, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                TEST_DEPARTMENT_NAME
        );

        departmentService.createDepartment(departmentCreationRequest);

        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository, times(1)).save(departmentCaptor.capture());

        Department savedDepartment = departmentCaptor.getValue();

        assertEquals(TEST_DEPARTMENT_NAME, savedDepartment.getName());
        assertEquals(3, savedDepartment.getSpecialties().size());

        assertTrue(savedDepartment.getSpecialties().stream()
                .anyMatch(specialty -> "Computer Engineering".equals(specialty.getName())));
        assertTrue(savedDepartment.getSpecialties().stream()
                .anyMatch(specialty -> "Radio technics".equals(specialty.getName())));
        assertTrue(savedDepartment.getSpecialties().stream()
                .anyMatch(specialty -> "History".equals(specialty.getName())));
    }


    @Test
    @DisplayName("Should not create department when not given department name")
    public void should_notCreateDepartment_When_notGivenDepartmentName() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, "Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(14.378, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                null
        );

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                });

        assertEquals("Department name cannot be blank or empty", constraintViolationException.getMessage());
    }

    @Test
    @DisplayName("Should not create department when department name is blank")
    public void should_notCreateDepartment_When_departmentNameIsBlank() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, "Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(14.378, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                " "
        );

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                });

        assertEquals("Department name cannot be blank or empty", constraintViolationException.getMessage());

    }

    @Test
    @DisplayName("Should not create department when specialties collection is empty")
    public void should_notCreateDepartment_When_specialtiesCollectionIsEmpty() {
        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(null, TEST_DEPARTMENT_NAME);

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                });

        assertEquals("Specialties collection cannot be empty", constraintViolationException.getMessage());

    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has invalid code-name")
    public void should_notCreateDepartment_When_atLeastOneSpecialtyHasInvalidCodeName() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, "Computer Engineering");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(null, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                TEST_DEPARTMENT_NAME
        );

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                });

        assertEquals("Specialty code-name cant be null or 0", constraintViolationException.getMessage());

    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has null name")
    public void should_notCreateDepartment_When_atLeastOneSpecialtyHasInvalidName() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, null);
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(14.378, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                TEST_DEPARTMENT_NAME
        );

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                });

        assertEquals("Specialty name can`t be null", constraintViolationException.getMessage());

    }

    @Test
    @DisplayName("Should not create department when at least 1 specialty has blank name")
    public void should_notCreateDepartment_When_atLeastOneSpecialtyHasBlankName() {
        SpecialtyCreationDto specialtyCreationDto1 = new SpecialtyCreationDto(14.378, "  ");
        SpecialtyCreationDto specialtyCreationDto2 = new SpecialtyCreationDto(14.378, "Radio technics");
        SpecialtyCreationDto specialtyCreationDto3 = new SpecialtyCreationDto(14.378, "History");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                TEST_DEPARTMENT_NAME
        );

        ConstraintViolationException constraintViolationException = assertThrows(
                ConstraintViolationException.class, () -> {
                    departmentService.createDepartment(departmentCreationRequest);
                }
        );

        assertEquals("Specialty name can`t be blank", constraintViolationException.getMessage());

    }

    private DepartmentCreationRequest buildDepartmentCreationRequest(Set<SpecialtyCreationDto> specialties, String departmentName) {
        return DepartmentCreationRequest.builder()
                .id(testDepartmentID)
                .name(departmentName)
                .specialties(specialties)
                .build();
    }

}
