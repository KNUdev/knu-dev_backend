package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;
import ua.knu.knudev.teammanagerapi.dto.*;
import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

import java.util.*;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class DevProfileTeamManagerService implements DevProfileTeamManagerApi {
    private static final Random RANDOM = new Random();
    private final DepartmentService departmentService;
    private final AccountProfileService accountProfileService;

    private static String generateRandomName(int minLength, int maxLength) {
        if (minLength < 1) {
            throw new IllegalArgumentException("Minimum length must be at least 1");
        }
        int nameLength = RANDOM.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder name = new StringBuilder(nameLength);

        char firstChar = (char) ('A' + RANDOM.nextInt(26));
        name.append(firstChar);

        for (int i = 1; i < nameLength; i++) {
            char nextChar = (char) ('a' + RANDOM.nextInt(26));
            name.append(nextChar);
        }
        return name.toString();
    }

    @Override
    public List<DepartmentWithSpecialtiesDto> createTestDepartments() {
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


        List<DepartmentWithSpecialtiesDto> out = new ArrayList<>();
        Set<ShortDepartmentDto> shortDepartments = departmentService.getShortDepartments();
        shortDepartments.forEach(d -> out.add(DepartmentWithSpecialtiesDto.builder()
                .id(d.id())
                .name(d.name())
                .specialties(departmentService.getSpecialtiesByDepartmentId(d.id()))
                .build()));
        return out;
    }

    @Override
    public List<ShortAccountProfileDto> createTestAccounts(Integer amount) {
        if (ObjectUtils.isEmpty(amount) || amount <= 0) {
            throw new RuntimeException("Invalid amount provided. Must be > 0");
        }
        Set<ShortDepartmentDto> shortDepartments = departmentService.getShortDepartments();
        if (shortDepartments.isEmpty()) {
            throw new RuntimeException("First create departments via /dev/departments/create");
        }
        List<DepartmentWithSpecialtiesDto> fullDepartments = departmentService.getFullDepartments().stream().toList();


        List<ShortAccountProfileDto> out = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int randomDepartmentIndex = getRandomIndex(fullDepartments);
            List<ShortSpecialtyDto> specialties = fullDepartments.get(randomDepartmentIndex).specialties().stream().toList();
            ShortSpecialtyDto randomSpecialty = specialties.get(getRandomIndex(specialties));

            AccountCreationRequest accountCreationRequest = AccountCreationRequest.builder()
                    .email("test" + i + "@knu.ua")
                    .expertise(getRandomExpertise())
                    .departmentId(fullDepartments.get(randomDepartmentIndex).id())
                    .specialtyCodename(randomSpecialty.codeName())
                    .githubAccountUsername("cumbainer")
                    .firstName(generateRandomName(5, 10))
                    .lastName(generateRandomName(4, 8))
                    .middleName(generateRandomName(8, 17))
                    .password("Vladvlad123!")
                    .yearOfStudy(getRandomValueInRange(1, 11))
                    .build();
            AccountProfileDto resp = accountProfileService.register(accountCreationRequest).accountProfile();

            ShortAccountProfileDto shortDto = ShortAccountProfileDto.builder()
                    .accountTechnicalRole(resp.technicalRole())
                    .name(resp.fullName())
                    .githubAccountUsername(resp.githubAccountUsername())
                    .build();
            out.add(shortDto);
        }
        return out;
    }

    private Expertise getRandomExpertise() {
        Expertise[] values = Expertise.values();
        return values[RANDOM.nextInt(values.length)];
    }

    private int getRandomIndex(Collection<?> collection) {
        return RANDOM.nextInt(collection.size());
    }

    public int getRandomValueInRange(int n, int k) {
        if (n > k) {
            throw new IllegalArgumentException("n must be less than or equal to k");
        }
        Random random = new Random();
        // nextInt(k - n + 1) generates a number between 0 and (k - n) inclusive.
        return n + random.nextInt(k - n + 1);
    }


}
