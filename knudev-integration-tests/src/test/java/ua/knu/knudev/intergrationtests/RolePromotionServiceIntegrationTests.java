package ua.knu.knudev.intergrationtests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.*;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanager.service.ProjectService;
import ua.knu.knudev.teammanager.service.RolePromotionService;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private AccountProfileMapper accountProfileMapper;
    @Autowired
    private SubprojectAccountRepository subprojectAccountRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private ProjectRepository projectRepository;

    private static final String TEST_PROJECT_NAME_IN_ENGLISH = "test-project";
    private static final String TEST_PROJECT_DESCRIPTION_IN_ENGLISH = "test-project-description";
    private static final String TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN = "тест-проєкта-опис";
    private static final UUID TEST_PROJECT_UUID = UUID.randomUUID();
    private static final String TEST_GITHUB_REPO_LINK_1 = "https://github.com/KNUdev/knu-dev_backend";
    private static final String TEST_GITHUB_REPO_LINK_2 = "https://github.com/KNUdev/knu-dev_frontend";

    private Department testDepartment;
    private Specialty testSpecialty;
    private Project testProject;
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
        testProject = createTestProjectAndSave(testAccountProfileMaster, testAccountProfileMaster);
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

    private Project createTestProjectAndSave(AccountProfile firstAccountProfile, AccountProfile secondAccountProfile) {
        Project project = new Project();

        project.setId(TEST_PROJECT_UUID);
        project.setName(TEST_PROJECT_NAME_IN_ENGLISH);
        project.setDescription(new MultiLanguageField(TEST_PROJECT_DESCRIPTION_IN_ENGLISH, TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN));
        project.setBanner(getMockMultipartFile().getName());
        project.setStartedAt(LocalDate.of(2023, 1, 1));
        project.setLastUpdatedAt(LocalDateTime.of(2024, 1, 1, 1, 1));
        project.setStatus(ProjectStatus.UNDER_DEVELOPMENT);
        project.setTags(Set.of(ProjectTag.DEPARTMENTS, ProjectTag.FINANCES));
        project.setArchitect(firstAccountProfile);
        project.setSupervisor(secondAccountProfile);

        Set<Subproject> testSubprojects = Set.of(
                createTestSubproject(project, TEST_GITHUB_REPO_LINK_1, SubprojectType.BACKEND),
                createTestSubproject(project, TEST_GITHUB_REPO_LINK_2, SubprojectType.FRONTEND)
        );
        project.setSubprojects(testSubprojects);

        return projectRepository.save(project);
    }

    private Subproject createTestSubproject(Project project, String repoLink, SubprojectType type) {
        Subproject subproject = new Subproject();
        subproject.setId(UUID.randomUUID());
        subproject.setProject(project);
        subproject.setType(type);
        subproject.setResourceUrl(repoLink);
        subproject.setReleases(Set.of());

        Set<Release> testRelease = Set.of(
                createTestRelease("v1.0.0", 1, subproject),
                createTestRelease("v1.0.1", 2, subproject),
                createTestRelease("v2.0.0", 3, subproject)
        );
        subproject.setReleases(testRelease);

        Set<AccountProfile> allDevelopers = testRelease.stream()
                .map(release -> release.getReleaseDevelopers()
                        .stream()
                        .map(ReleaseParticipation::getAccountProfile)
                        .toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<SubprojectAccount> subprojectAccounts = allDevelopers.stream()
                .map(developer -> createSubprojectAccount(subproject, developer))
                .collect(Collectors.toSet());

        subproject.setAllDevelopers(subprojectAccounts);

        return subproject;
    }

    private SubprojectAccount createSubprojectAccount(Subproject subproject, AccountProfile accountProfile) {
        SubprojectAccount subprojectAccount = new SubprojectAccount();
        subprojectAccount.setId(SubprojectAccountId.builder()
                .subprojectId(subproject.getId())
                .accountId(null)
                .build());
        subprojectAccount.setSubproject(subproject);
        subprojectAccount.setAccountProfile(accountProfile);
        subprojectAccount.setDateJoined(LocalDate.of(2023, 1, 1));
        subprojectAccount.setLastCommitDate(LocalDate.of(2025, 2, 1));
        subprojectAccount.setTotalCommits(1);
        subprojectAccount.setTotalLinesOfCodeWritten(100);
        return subprojectAccount;
    }

    private Release createTestRelease(String version, int index, Subproject subproject) {
        Release release = new Release();

        release.setId(UUID.randomUUID());
        release.setInitializedAt(LocalDateTime.of(2000 + index, 2, 1, 1, 1));
        release.setReleaseFinishDate(LocalDateTime.of(2000 + index, 5, 1, 1, 1));
        release.setVersion(version);
        release.setSubproject(subproject);
        release.setAggregatedGithubCommitCount(1);
        release.setChangesLogEn("Changes log in English");

        Set<ReleaseParticipation> testReleaseParticipation =
                Set.of(createTestReleaseParticipation(testAccountProfileDeveloper, release));
        release.setReleaseDevelopers(testReleaseParticipation);

        return release;
    }

    private ReleaseParticipation createTestReleaseParticipation(AccountProfile accountProfile, Release release) {
        ReleaseParticipation releaseParticipation = new ReleaseParticipation();

        releaseParticipation.setId(UUID.randomUUID());
        releaseParticipation.setAccountProfile(accountProfile);
        releaseParticipation.setRoleSnapshot(AccountTechnicalRole.DEVELOPER);
        releaseParticipation.setCommitCount(1);
        releaseParticipation.setRelease(release);

        return releaseParticipation;
    }

    @Test
    @DisplayName("Should return positive result on valid promotion request to pre-master.")
    public void should_ReturnPositiveResultOnPromotionToPreMaster_When_ProvidedValidData() {
        
    }

}
