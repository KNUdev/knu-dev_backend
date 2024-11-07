package ua.knu.knudev.teammanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    private static final Department TEST_DEPARTMENT = getTestDepartment();
    private static final Double TEST_SPECIALTY_ID = DepartmentTestsConstants.TEST_SPECIALTY_ID;
    private static final UUID TEST_DEPARTMENT_ID = DepartmentTestsConstants.TEST_DEPARTMENT_ID;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    @DisplayName("Validate academic unit successfully when department and specialty exist")
    void should_validateAcademicUnit_When_DepartmentAndSpecialtyExist() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(TEST_DEPARTMENT));
        AcademicUnitsIds academicUnitsIds = AcademicUnitsIds.builder()
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyCodename(TEST_SPECIALTY_ID)
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
                .specialtyCodename(TEST_SPECIALTY_ID)
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
                .specialtyCodename(999.99)
                .build();

        DepartmentException exception = assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitByIds(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with code name: 999.99",
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
                .specialtyCodename(TEST_SPECIALTY_ID)
                .build();

        // Act & Assert
        DepartmentException exception = assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitByIds(academicUnitsIds)
        );
        assertEquals(
                "Department with id " + TEST_DEPARTMENT_ID + " does not contain specialty with code name: " + TEST_SPECIALTY_ID,
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
                .specialtyCodename(TEST_SPECIALTY_ID)
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
}
