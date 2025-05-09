package ua.knu.knudev.teammanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
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
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestSpecialty;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_NAME_IN_ENGLISH;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_NAME_IN_UKRAINIAN;

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

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitExistence(TEST_DEPARTMENT_ID, TEST_SPECIALTY_ID));

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when on validation department does not exist")
    void should_ThrowDepartmentException_When_DepartmentDoesNotExistOnValidation() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.empty());

        DepartmentException exception = assertThrows(
                DepartmentException.class,
                () -> departmentService.validateAcademicUnitExistence(TEST_DEPARTMENT_ID, TEST_SPECIALTY_ID)
        );
        assertEquals("Department with id " + TEST_DEPARTMENT_ID + " not found", exception.getMessage());

        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when specialty does not exist in the department")
    void should_ThrowDepartmentException_When_SpecialtyDoesNotExistInDepartment() {
        Specialty testSpecialty = getTestSpecialty("Тестова спеціальність", "Test specialty");
        testSpecialty.setCodeName(999.123);
        Department testDepartment = getTestDepartment();
        testDepartment.setSpecialties(Set.of(testSpecialty));

        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(testDepartment));

        assertThrows(
                DepartmentException.class,
                () -> departmentService.validateAcademicUnitExistence(TEST_DEPARTMENT_ID, TEST_SPECIALTY_ID)
        );
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Throw DepartmentException when department exists but has no specialties")
    void should_ThrowDepartmentException_When_DepartmentExistsButHasNoSpecialties() {
        // Arrange
        Department emptyDepartment = new Department();
        emptyDepartment.setId(TEST_DEPARTMENT_ID);
        emptyDepartment.setName(new MultiLanguageField("Engineering", "Інженерія"));
        emptyDepartment.setSpecialties(new HashSet<>());

        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(emptyDepartment));

        // Act & Assert
        assertThrows(DepartmentException.class, () ->
                departmentService.validateAcademicUnitExistence(TEST_DEPARTMENT_ID, TEST_SPECIALTY_ID)
        );
        verify(departmentRepository, times(1)).findById(TEST_DEPARTMENT_ID);
    }

    @Test
    @DisplayName("Validate academic unit successfully when specialties are loaded lazily")
    void should_ValidateAcademicUnit_When_SpecialtiesAreLoadedLazily() {
        when(departmentRepository.findById(TEST_DEPARTMENT_ID)).thenReturn(Optional.of(getTestDepartment()));

        assertDoesNotThrow(() -> departmentService.validateAcademicUnitExistence(TEST_DEPARTMENT_ID, TEST_SPECIALTY_ID));

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

    @Test
    @DisplayName("Should create department when given valid data")
    public void Should_createDepartment_When_givenValidData() {
        // Arrange
        SpecialtyCreationDto specialtyCreationDto1 = buildSpecialtyCreationDto("Computer Engineering",
                "Комп'ютерна інженерія", 14.377);
        SpecialtyCreationDto specialtyCreationDto2 = buildSpecialtyCreationDto("Radio technics",
                "Радіо техніка", 14.378);
        SpecialtyCreationDto specialtyCreationDto3 = buildSpecialtyCreationDto("History",
                "Історія", 14.379);

        List<String> specialtiesNamesInEnglish = List.of("Computer Engineering", "Radio technics", "History");
        List<String> specialtiesNamesInUkrainian = List.of("Комп'ютерна інженерія", "Радіо техніка", "Історія");

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2, specialtyCreationDto3),
                TEST_DEPARTMENT_NAME_IN_ENGLISH,
                TEST_DEPARTMENT_NAME_IN_UKRAINIAN
        );
        // Act
        departmentService.createDepartment(departmentCreationRequest);

        // Assert
        ArgumentCaptor<Department> departmentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository, times(1)).save(departmentCaptor.capture());

        Department savedDepartment = departmentCaptor.getValue();

        assertEquals(TEST_DEPARTMENT_NAME_IN_ENGLISH, savedDepartment.getName().getEn());
        assertEquals(3, savedDepartment.getSpecialties().size());

        specialtiesNamesInEnglish.forEach(specialtiesName -> assertTrue(savedDepartment.getSpecialties().stream()
                .map(specialty -> specialty.getName().getEn())
                .collect(Collectors.toSet())
                .containsAll(specialtiesNamesInEnglish)));

        specialtiesNamesInUkrainian.forEach(specialtiesName -> assertTrue(savedDepartment.getSpecialties().stream()
                .map(specialty -> specialty.getName().getUk())
                .collect(Collectors.toSet())
                .containsAll(specialtiesNamesInUkrainian)));
    }

    @Test
    @DisplayName("Should not create department when department exist")
    public void should_Not_Create_Department_When_DepartmentExists() {
        SpecialtyCreationDto specialtyCreationDto1 = buildSpecialtyCreationDto("Computer Engineering",
                "Комп'ютерна інженерія", 14.378);
        SpecialtyCreationDto specialtyCreationDto2 = buildSpecialtyCreationDto("Radio technics",
                "Радіо техніка", 14.379);

        DepartmentCreationRequest departmentCreationRequest = buildDepartmentCreationRequest(
                Set.of(specialtyCreationDto1, specialtyCreationDto2),
                TEST_DEPARTMENT_NAME_IN_ENGLISH,
                TEST_DEPARTMENT_NAME_IN_UKRAINIAN
        );

        when(departmentRepository.existsByName_En(TEST_DEPARTMENT_NAME_IN_ENGLISH)).thenReturn(true);
        when(departmentRepository.existsByName_Uk(TEST_DEPARTMENT_NAME_IN_UKRAINIAN)).thenReturn(true);

        assertThrows(DepartmentException.class, () -> departmentService.createDepartment(departmentCreationRequest));
        verify(departmentRepository, times(1)).existsByName_En(TEST_DEPARTMENT_NAME_IN_ENGLISH);
        verify(departmentRepository, times(1)).existsByName_Uk(TEST_DEPARTMENT_NAME_IN_UKRAINIAN);
        verifyNoMoreInteractions(departmentRepository);
    }

    private DepartmentCreationRequest buildDepartmentCreationRequest(Set<SpecialtyCreationDto> specialties,
                                                                     String nameInEnglish,
                                                                     String nameInUkrainian) {
        return DepartmentCreationRequest.builder()
                .nameInEnglish(nameInEnglish)
                .nameInUkrainian(nameInUkrainian)
                .specialties(specialties)
                .build();
    }

    private SpecialtyCreationDto buildSpecialtyCreationDto(String nameInEnglish, String nameInUkraine, Double codeName) {
        return new SpecialtyCreationDto(codeName, nameInUkraine, nameInEnglish);
    }

}
