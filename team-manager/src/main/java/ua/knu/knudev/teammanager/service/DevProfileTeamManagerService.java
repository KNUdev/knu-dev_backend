package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.Set;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class DevProfileTeamManagerService implements DevProfileTeamManagerApi {
    private final DepartmentService departmentService;

    @Override
    public void createTestDepartments() {
        SpecialtyCreationDto s1 = new SpecialtyCreationDto(123.0, "Computer Engineering", "Комп'ютерна інженерія");
        SpecialtyCreationDto s2 = new SpecialtyCreationDto(172.0, "Radiotechnics", "Радіотехніка");
        SpecialtyCreationDto s3 = new SpecialtyCreationDto(105.0, "Applied Physics", "Прикладна фізика");
        DepartmentCreationRequest dcr = new DepartmentCreationRequest(
                "Faculty of Radiophysics, Electronics and Computer Systems",
                "Факультет Радіофізики, Електроніки та Комп'юторних систем",
                Set.of(s1, s2, s3)
        );

        SpecialtyCreationDto s11 = new SpecialtyCreationDto(124.0, "System Analytics", "Системна Аналітика");
        SpecialtyCreationDto s22 = new SpecialtyCreationDto(125.0, "Cybersecurity", "Кібербезпека");
        SpecialtyCreationDto s33 = new SpecialtyCreationDto(122.0, "Computer Science", "Комп'ютерні науки");

        SpecialtyCreationDto s44 = new SpecialtyCreationDto(127.0, "Data Analytics", "Аналіз даних");
        SpecialtyCreationDto s55 = new SpecialtyCreationDto(128.0, "Artificial Intelligence", "Штучний інтелект");
        SpecialtyCreationDto s66 = new SpecialtyCreationDto(129.0, "Bioinformatics", "Біоінформатика");
        SpecialtyCreationDto s77 = new SpecialtyCreationDto(130.0, "Quantum Computing", "Квантові обчислення");
        SpecialtyCreationDto s88 = new SpecialtyCreationDto(131.0, "Network Administration", "Адміністрування мереж");
        SpecialtyCreationDto s99 = new SpecialtyCreationDto(132.0, "Cloud Computing", "Хмарні обчислення");
        SpecialtyCreationDto s100 = new SpecialtyCreationDto(133.0, "Project Management", "Управління проєктами");

        DepartmentCreationRequest dcr2 = new DepartmentCreationRequest(
                "Faculty of Informational Technologies",
                "Факультет Інформаційних Технологій",
                Set.of(
                        s11, s22, s33,
                        s44, s55, s66,
                        s77, s88, s99,
                        s100
                )
        );
        SpecialtyCreationDto s111 = new SpecialtyCreationDto(121.0, "Software Engineering", "Інженерія Програмного Забезпечення");
        SpecialtyCreationDto s222 = new SpecialtyCreationDto(126.0, "Information systems and technologies", "Інформаційні системи та технології");
        SpecialtyCreationDto s333 = new SpecialtyCreationDto(138.1127, "Super Omega Giga Faculty", "Гіпер мега бляха факультет");

        DepartmentCreationRequest dcr3 = new DepartmentCreationRequest(
                "Faculty of Cybernetics",
                "Факультет Кібернетики",
                Set.of(s111, s222, s333)
        );

        departmentService.createDepartment(dcr);
        departmentService.createDepartment(dcr2);
        departmentService.createDepartment(dcr3);
    }

}
