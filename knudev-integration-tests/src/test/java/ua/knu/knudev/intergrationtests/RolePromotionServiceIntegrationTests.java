package ua.knu.knudev.intergrationtests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanager.repository.SubprojectAccountRepository;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanager.service.ProjectService;
import ua.knu.knudev.teammanager.service.RolePromotionService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class RolePromotionServiceIntegrationTests {

    @Autowired
    private RolePromotionService rolePromotionService;
    @Autowired
    private AccountProfileService accountProfileService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SubprojectAccountRepository subprojectAccountRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private AccountProfileMapper accountProfileMapper;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Department testDepartment;
    private Specialty testSpecialty;
    private AccountProfile testAccountProfileDeveloper;
    private AccountProfile testAccountProfilePreMaster;
    private AccountProfile testAccountProfileMaster;
    private AccountProfile testAccountProfileTechLead;

    @BeforeEach
    public void setUp() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
        testAccountProfileDeveloper = createAndSaveTestAccountProfile(AccountTechnicalRole.DEVELOPER);
        testAccountProfilePreMaster = createAndSaveTestAccountProfile(AccountTechnicalRole.PREMASTER);
        testAccountProfileMaster = createAndSaveTestAccountProfile(AccountTechnicalRole.MASTER);
        testAccountProfileTechLead = createAndSaveTestAccountProfile(AccountTechnicalRole.TECHLEAD);
    }

    @AfterEach
    public void tearDown() {
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    private Department createTestDepartmentWithSpecialties() {
        Department department = new Department();
        department.setName(new MultiLanguageField("Test Department for projects", "Тестовий для проєктів"));

        Specialty s1 = new Specialty(122.1, "Computer Science for projects", "Науки для проєктів");
        Specialty s2 = new Specialty(123.1, "Computer Engineering for projects", "Інженерія для проєктів");
        Specialty s3 = new Specialty(125.1, "Cybernetics for projects", "Кібернетика для проєктів");

        department.addSpecialty(s1);
        department.addSpecialty(s2);
        department.addSpecialty(s3);

        return departmentRepository.save(department);
    }

    private AccountProfile createAndSaveTestAccountProfile(AccountTechnicalRole technicalRole) {
        String uniqueEmail = technicalRole.name().toLowerCase() + "@email.com";

        AccountProfile accountProfile = AccountProfile.builder()
                .id(UUID.randomUUID())
                .firstName("FirstName")
                .lastName("LastName")
                .middleName("MiddleName")
                .email(uniqueEmail)
                .avatarFilename(getMockMultipartFile().getName())
                .bannerFilename("bannerFilename")
                .technicalRole(technicalRole)
                .expertise(Expertise.BACKEND)
                .registrationDate(LocalDateTime.of(2021, 1, 1, 1, 1))
                .lastRoleUpdateDate(LocalDateTime.of(2022, 1, 1, 1, 2))
                .yearOfStudyOnRegistration(2)
                .unit(KNUdevUnit.CAMPUS)
                .githubAccountUsername("DenysLnk")
                .department(testDepartment)
                .specialty(testSpecialty)
                .build();

        return accountProfileRepository.save(accountProfile);
    }

    private MultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "dummy content".getBytes()
        );
    }

//todo write more logic

}
