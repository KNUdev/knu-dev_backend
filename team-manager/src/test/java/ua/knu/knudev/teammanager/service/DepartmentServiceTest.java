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
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    private final UUID testDepartmentID = UUID.randomUUID();
    private final String departmentName = "Test Department";

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private DepartmentService departmentService;

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
