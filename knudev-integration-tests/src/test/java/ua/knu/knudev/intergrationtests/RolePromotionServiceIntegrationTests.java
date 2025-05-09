package ua.knu.knudev.intergrationtests;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanager.domain.RolePromotionTask;
import ua.knu.knudev.assessmentmanager.repository.RolePromotionTaskRepository;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.repository.*;
import ua.knu.knudev.educationapi.enums.SessionStatus;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.*;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.RolePromotionService;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.RolePromotionConditionException;
import ua.knu.knudev.teammanagerapi.exception.SubprojectAccountException;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionCreationRequest;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionUpdateRequest;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class RolePromotionServiceIntegrationTests {

    private static final String TEST_PROJECT_NAME_IN_ENGLISH = "test-project";
    private static final String TEST_PROJECT_DESCRIPTION_IN_ENGLISH = "test-project-description";
    private static final String TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN = "тест-проєкта-опис";
    private static final UUID TEST_PROJECT_UUID = UUID.randomUUID();
    private static final UUID TEST_ROLE_PROMOTION_CONDITION_ID = UUID.randomUUID();
    private static final String TEST_GITHUB_REPO_LINK_1 = "https://github.com/KNUdev/knu-dev_backend";
    private static final String TEST_GITHUB_REPO_LINK_2 = "https://github.com/KNUdev/knu-dev_frontend";

    @Autowired
    private RolePromotionService rolePromotionService;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SubprojectAccountRepository subprojectAccountRepository;
    @Autowired
    private RolePromotionConditionRepository rolePromotionConditionRepository;
    @Autowired
    private RolePromotionTaskRepository rolePromotionTaskRepository;
    @Autowired
    private EducationSessionRepository educationSessionRepository;
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private TopicRepository programTopicRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private EducationProgramRepository educationProgramRepository;

    private AccountProfile testAccountProfile;
    private Department testDepartment;
    private Specialty testSpecialty;
    private Project testProject;
    private RolePromotionConditions testRolePromotionConditions;

    @BeforeEach
    public void setUp() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
        testAccountProfile = createAndSaveTestAccountProfile();
        testProject = createTestProjectAndSave();
        testRolePromotionConditions = createTestRolePromotionConditionAndSave();
    }

    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
        rolePromotionConditionRepository.deleteAll();
    }

    private Department createTestDepartmentWithSpecialties() {
        Department department = new Department();
        department.setName(new MultiLanguageField("Test Department", "Тестовий"));

        Specialty s1 = new Specialty(122.0, "Computer Science", "Науки");
        Specialty s2 = new Specialty(123.0, "Computer Engineering", "Інженерія");
        Specialty s3 = new Specialty(125.0, "Cybernetics", "Кібернетика");

        department.addSpecialty(s1);
        department.addSpecialty(s2);
        department.addSpecialty(s3);

        return departmentRepository.save(department);
    }

    private AccountProfile createAndSaveTestAccountProfile() {
        UUID randomConstant = UUID.randomUUID();
        AccountProfile accountProfile = AccountProfile.builder()
                .id(UUID.randomUUID())
                .firstName("FirstName" + randomConstant)
                .lastName("LastName" + randomConstant)
                .middleName("MiddleName" + randomConstant)
                .email("email" + randomConstant + "@email.com")
                .avatarFilename(getMockMultipartFile().getName())
                .bannerFilename("bannerFilename")
                .technicalRole(AccountTechnicalRole.DEVELOPER)
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

    private Project createTestProjectAndSave() {
        Project project = new Project();

        project.setId(TEST_PROJECT_UUID);
        project.setName(TEST_PROJECT_NAME_IN_ENGLISH);
        project.setDescription(new MultiLanguageField(TEST_PROJECT_DESCRIPTION_IN_ENGLISH, TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN));
        project.setBanner(getMockMultipartFile().getName());
        project.setStartedAt(LocalDate.of(2023, 1, 1));
        project.setLastUpdatedAt(LocalDateTime.of(2024, 1, 1, 1, 1));
        project.setStatus(ProjectStatus.UNDER_DEVELOPMENT);
        project.setTags(Set.of(ProjectTag.DEPARTMENTS, ProjectTag.FINANCES));
        project.setArchitect(testAccountProfile);
        project.setSupervisor(testAccountProfile);

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
                createRolePromotionConditionsTestRelease("v1.0.0", 1, subproject),
                createRolePromotionConditionsTestRelease("v1.0.1", 2, subproject),
                createRolePromotionConditionsTestRelease("v2.0.0", 3, subproject)
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

    private Release createRolePromotionConditionsTestRelease(String version, int index, Subproject subproject) {
        Release release = new Release();

        release.setId(UUID.randomUUID());
        release.setInitializedAt(LocalDateTime.of(2000 + index, 2, 1, 1, 1));
        release.setReleaseFinishDate(LocalDateTime.of(2000 + index, 5, 1, 1, 1));
        release.setVersion(version);
        release.setSubproject(subproject);
        release.setAggregatedGithubCommitCount(1);
        release.setChangesLogEn("Changes log in English");

        Set<ReleaseParticipation> testReleaseParticipation =
                Set.of(createRolePromotionConditionsTestReleaseParticipation(testAccountProfile, release));
        release.setReleaseDevelopers(testReleaseParticipation);

        return release;
    }

    private ReleaseParticipation createRolePromotionConditionsTestReleaseParticipation(AccountProfile accountProfile, Release release) {
        ReleaseParticipation releaseParticipation = new ReleaseParticipation();

        releaseParticipation.setId(UUID.randomUUID());
        releaseParticipation.setAccountProfile(accountProfile);
        releaseParticipation.setRoleSnapshot(AccountTechnicalRole.DEVELOPER);
        releaseParticipation.setCommitCount(1);
        releaseParticipation.setRelease(release);

        return releaseParticipation;
    }

    private RolePromotionConditions createTestRolePromotionConditionAndSave() {
        RolePromotionConditions rolePromotionConditions = RolePromotionConditions.builder()
                .id(TEST_ROLE_PROMOTION_CONDITION_ID)
                .toPremasterProjectQuantity(2)
                .toPremasterCommitsQuantity(20)
                .toMasterProjectQuantity(4)
                .toMasterCommitsQuantity(40)
                .toMasterCreatedCampusTasksQuantity(2)
                .toMasterMentoredSessionsQuantity(1)
                .toTechLeadCommitsQuantity(60)
                .toTechLeadCreatedCampusTasksQuantity(4)
                .wasArchitect(true)
                .wasSupervisor(true)
                .createdAt(LocalDateTime.of(2022, 2, 2, 2, 2))
                .singleton(true)
                .build();

        return rolePromotionConditionRepository.save(rolePromotionConditions);
    }

    public RolePromotionConditionCreationRequest createTestRolePromotionCreationRequest() {
        return RolePromotionConditionCreationRequest.builder()
                .toPremasterProjectQuantity(2)
                .toPremasterCommitsQuantity(20)
                .toMasterProjectQuantity(4)
                .toMasterCommitsQuantity(40)
                .toMasterCreatedCampusTasksQuantity(2)
                .toMasterMentoredSessionsQuantity(1)
                .toTechLeadCreatedCampusTasksQuantity(4)
                .toTechLeadCommitsQuantity(60)
                .wasArchitect(true)
                .wasSupervisor(true)
                .build();
    }

    public RolePromotionConditionUpdateRequest createTestRolePromotionConditionUpdateRequest() {
        return RolePromotionConditionUpdateRequest.builder()
                .id(testRolePromotionConditions.getId())
                .toPremasterProjectQuantity(2)
                .toPremasterCommitsQuantity(30)
                .toMasterProjectQuantity(5)
                .toMasterCommitsQuantity(50)
                .toMasterCreatedCampusTasksQuantity(3)
                .toMasterMentoredSessionsQuantity(2)
                .toTechLeadCreatedCampusTasksQuantity(5)
                .toTechLeadCommitsQuantity(70)
                .wasSupervisor(false)
                .build();
    }

    public void createRolePromotionTaskAndSave() {
        RolePromotionTask rolePromotionTask = RolePromotionTask.builder()
                .id(UUID.randomUUID())
                .additionDate(LocalDateTime.of(2022, 2, 2, 2, 2))
                .targetTechnicalRole(AccountTechnicalRole.MASTER)
                .creatorAccountEmail(testAccountProfile.getEmail())
                .taskFilename("taskFilename")
                .build();

        rolePromotionTaskRepository.save(rolePromotionTask);
    }

    public void createAndSaveTestEducationSession() {
        EducationSession educationSession = EducationSession.builder()
                .id(UUID.randomUUID())
                .educationProgram(createAndSaveTestEducationProgram())
                .startDate(LocalDateTime.now().minusDays(2))
                .status(SessionStatus.ONGOING)
                .sprints(new ArrayList<>())
                .participantIds(Set.of(testAccountProfile.getId()))
                .mentorIds(Set.of(testAccountProfile.getId()))
                .build();

        educationSession = educationSessionRepository.save(educationSession);

        for (int i = 1; i < 5; i++) {
            Sprint sprint = createAndSaveTestSprint(i, educationSession, educationSession.getEducationProgram());
            educationSession.addSprint(sprint);
        }

        educationSessionRepository.save(educationSession);
    }

    public Sprint createAndSaveTestSprint(int orderIndex, EducationSession session, EducationProgram educationProgram) {
        Sprint sprint = Sprint.builder()
                .id(UUID.randomUUID())
                .session(session)
                .orderIndex(orderIndex)
                .type(SprintType.TOPIC)
                .startDate(LocalDateTime.now().minusDays(2))
                .durationInDays(10)
                .programTopic(createAndSaveTestProgramTopic())
                .programModule(createAndSaveTestProgramModule())
                .programSection(createAndSaveTestProgramSection())
                .program(educationProgram)
                .status(SprintStatus.ACTIVE)
                .build();

        return sprintRepository.save(sprint);
    }

    public ProgramTopic createAndSaveTestProgramTopic() {
        ProgramTopic programTopic = ProgramTopic.builder()
                .id(UUID.randomUUID())
                .finalTaskFilename("finalTaskFilename")
                .name(new ua.knu.knudev.education.domain.MultiLanguageField("Test Topic", "Тестова тема"))
                .description(new ua.knu.knudev.education.domain.MultiLanguageField("Test Topic Description", "Опис тестової теми"))
                .createdDate(LocalDateTime.now().minusDays(2))
                .learningResources(Set.of("https://example.com/resource1", "https://example.com/resource2"))
                .difficulty(1)
                .testId(UUID.randomUUID())
                .build();

        return programTopicRepository.save(programTopic);
    }

    public ProgramModule createAndSaveTestProgramModule() {
        ProgramModule programModule = ProgramModule.builder()
                .id(UUID.randomUUID())
                .finalTaskFilename("finalTaskFilename")
                .name(new ua.knu.knudev.education.domain.MultiLanguageField("Test Module", "Тестовий модуль"))
                .description(new ua.knu.knudev.education.domain.MultiLanguageField("Test Module Description", "Опис тестового модуля"))
                .createdDate(LocalDateTime.now().minusDays(2))
                .build();

        return moduleRepository.save(programModule);
    }

    public ProgramSection createAndSaveTestProgramSection() {
        ProgramSection programSection = ProgramSection.builder()
                .id(UUID.randomUUID())
                .name(new ua.knu.knudev.education.domain.MultiLanguageField("Test Section", "Тестовий розділ"))
                .description(new ua.knu.knudev.education.domain.MultiLanguageField("Test Section Description", "Опис тестового розділу"))
                .createdDate(LocalDateTime.now().minusDays(2))
                .finalTaskFilename("finalTaskFilename")
                .build();

        return sectionRepository.save(programSection);
    }

    public EducationProgram createAndSaveTestEducationProgram() {
        EducationProgram educationProgram = EducationProgram.builder()
                .id(UUID.randomUUID())
                .finalTaskFilename("finalTaskFilename")
                .name(new ua.knu.knudev.education.domain.MultiLanguageField("test program", "тестова програма"))
                .description(new ua.knu.knudev.education.domain.MultiLanguageField("Test Program Description", "Опис тестової програми"))
                .createdDate(LocalDateTime.now().minusDays(2))
                .expertise(Expertise.BACKEND)
                .isPublished(false)
                .build();

        return educationProgramRepository.save(educationProgram);
    }

    @Nested
    @DisplayName("Check on role promotion conditions create tests")
    public class CheckOnRolePromotionConditionsCreateTests {
        @Test
        @DisplayName("Should create role promotion condition when provided valid data")
        public void should_CorrectlyCreateRolePromotionCondition_When_ProvidedValidData() {
            RolePromotionConditionCreationRequest request = createTestRolePromotionCreationRequest();

            rolePromotionConditionRepository.deleteAll();
            RolePromotionConditionDto response = rolePromotionService.createRolePromotionConditions(request);

            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals(request.toPremasterProjectQuantity(), response.toPremasterProjectQuantity());
            assertEquals(request.toPremasterCommitsQuantity(), response.toPremasterCommitsQuantity());
            assertEquals(request.toMasterProjectQuantity(), response.toMasterProjectQuantity());
            assertEquals(request.toMasterCommitsQuantity(), response.toMasterCommitsQuantity());
            assertEquals(request.toMasterCreatedCampusTasksQuantity(), response.toMasterCreatedCampusTasksQuantity());
            assertEquals(request.toMasterMentoredSessionsQuantity(), response.toMasterMentoredSessionsQuantity());
            assertEquals(request.toTechLeadCreatedCampusTasksQuantity(), response.toTechLeadCreatedCampusTasksQuantity());
            assertEquals(request.toTechLeadCommitsQuantity(), response.toTechLeadCommitsQuantity());
            assertEquals(request.wasArchitect(), response.wasArchitect());
            assertEquals(request.wasSupervisor(), response.wasSupervisor());
        }

        @Test
        @DisplayName("Should throw exception when trying to create role promotion condition when it already exists")
        public void should_ThrowException_When_RolePromotionConditionAlreadyExists() {
            RolePromotionConditionCreationRequest request = createTestRolePromotionCreationRequest();
            assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.createRolePromotionConditions(request));
        }

        @Test
        @DisplayName("Should throw exception when trying to create role promotion condition with invalid data")
        public void should_ThrowException_When_RolePromotionConditionWithInvalidData() {
            assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.createRolePromotionConditions(null));
        }
    }

    @Nested
    @DisplayName("Check on role promotion conditions update tests")
    public class CheckOnRolePromotionConditionsUpdateTests {
        @Test
        @DisplayName("Should update role promotion condition when provided valid data")
        public void should_CorrectlyUpdateRolePromotionCondition_When_ProvidedValidData() {
            RolePromotionConditionUpdateRequest request = createTestRolePromotionConditionUpdateRequest();

            RolePromotionConditionDto response = rolePromotionService.updateRolePromotionConditions(request);

            assertNotNull(response);
            assertEquals(request.id(), response.id());
            assertEquals(request.toPremasterProjectQuantity(), response.toPremasterProjectQuantity());
            assertEquals(request.toPremasterCommitsQuantity(), response.toPremasterCommitsQuantity());
            assertEquals(request.toMasterProjectQuantity(), response.toMasterProjectQuantity());
            assertEquals(request.toMasterCommitsQuantity(), response.toMasterCommitsQuantity());
            assertEquals(request.toMasterCreatedCampusTasksQuantity(), response.toMasterCreatedCampusTasksQuantity());
            assertEquals(request.toMasterMentoredSessionsQuantity(), response.toMasterMentoredSessionsQuantity());
            assertEquals(request.toTechLeadCreatedCampusTasksQuantity(), response.toTechLeadCreatedCampusTasksQuantity());
            assertEquals(request.toTechLeadCommitsQuantity(), response.toTechLeadCommitsQuantity());
            assertEquals(testRolePromotionConditions.getWasArchitect(), response.wasArchitect());
            assertEquals(request.wasSupervisor(), response.wasSupervisor());
        }

        @Test
        @DisplayName("Should throw exception when trying to update role promotion condition with invalid data")
        public void should_ThrowException_When_RolePromotionConditionWithInvalidData() {
            assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.updateRolePromotionConditions(null));
        }

        @Test
        @DisplayName("Should throw exception when trying to update role promotion condition that doesn't exist")
        public void should_ThrowException_When_RolePromotionConditionDoesNotExist() {
            RolePromotionConditionUpdateRequest request = RolePromotionConditionUpdateRequest.builder()
                    .id(UUID.randomUUID())
                    .toPremasterProjectQuantity(2)
                    .toPremasterCommitsQuantity(20)
                    .toMasterProjectQuantity(4)
                    .toMasterCommitsQuantity(40)
                    .toMasterCreatedCampusTasksQuantity(2)
                    .toMasterMentoredSessionsQuantity(1)
                    .toTechLeadCreatedCampusTasksQuantity(4)
                    .toTechLeadCommitsQuantity(60)
                    .wasArchitect(true)
                    .wasSupervisor(true)
                    .build();

            assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.updateRolePromotionConditions(request));
        }
    }

    @Test
    @DisplayName("Should successfully delete role promotion condition when provided valid data")
    public void should_SuccessfullyDeleteRolePromotionCondition_When_ProvidedValidData() {
        rolePromotionService.deleteRolePromotionConditions(testRolePromotionConditions.getId());
        assertEquals(0, rolePromotionConditionRepository.findAll().size());
    }

    @Test
    @DisplayName("Should return RolePromotionCondition when it exists")
    public void should_ReturnRolePromotionCondition_When_ItExists() {
        RolePromotionConditionDto response = rolePromotionService.getRolePromotionConditions();

        assertNotNull(response);
        assertEquals(testRolePromotionConditions.getId(), response.id());
        assertEquals(testRolePromotionConditions.getToPremasterProjectQuantity(), response.toPremasterProjectQuantity());
        assertEquals(testRolePromotionConditions.getToPremasterCommitsQuantity(), response.toPremasterCommitsQuantity());
        assertEquals(testRolePromotionConditions.getToMasterProjectQuantity(), response.toMasterProjectQuantity());
        assertEquals(testRolePromotionConditions.getToMasterCommitsQuantity(), response.toMasterCommitsQuantity());
        assertEquals(testRolePromotionConditions.getToMasterCreatedCampusTasksQuantity(), response.toMasterCreatedCampusTasksQuantity());
        assertEquals(testRolePromotionConditions.getToMasterMentoredSessionsQuantity(), response.toMasterMentoredSessionsQuantity());
        assertEquals(testRolePromotionConditions.getToTechLeadCreatedCampusTasksQuantity(), response.toTechLeadCreatedCampusTasksQuantity());
        assertEquals(testRolePromotionConditions.getToTechLeadCommitsQuantity(), response.toTechLeadCommitsQuantity());
        assertEquals(testRolePromotionConditions.getWasArchitect(), response.wasArchitect());
        assertEquals(testRolePromotionConditions.getWasSupervisor(), response.wasSupervisor());
    }

    @Test
    @DisplayName("Should throw exception when trying to get role promotion condition that doesn't exist")
    public void should_ThrowException_When_TryingToGetRolePromotionConditionThatDoesNotExist() {
        rolePromotionConditionRepository.deleteAll();
        assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.getRolePromotionConditions());
    }

    @Nested
    @DisplayName("Check on succesfull role promotion tests")
    @Transactional
    public class CheckOnSuccesfullRolePromotionTests {
        @Test
        @DisplayName("Should return true when account has correct data for promotion to premaster")
        public void should_ReturnTrue_When_AccountHasCorrectDataForPromotionToPremaster() {
            UUID testAccountId = testAccountProfile.getId();
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(21);
            subprojectAccountRepository.save(subprojectAccount);

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(testAccountId);

            assertNotNull(response);
            assertTrue(response.canPromote());
        }

        @Test
        @DisplayName("Should return true when account has correct data for promotion to master")
        public void should_ReturnTrue_When_AccountHasCorrectDataForPromotionToMaster() {
            UUID testAccountId = testAccountProfile.getId();
            createTestProjectAndSave();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.PREMASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(40);

            for (int i = 0; i < 3; i++) {
                createRolePromotionTaskAndSave();
            }

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);
            createAndSaveTestEducationSession();

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(accountProfile.getId());

            assertNotNull(response);
            assertTrue(response.canPromote());
        }

        @Test
        @DisplayName("Should return true when account has correct data for promotion to tech lead")
        public void should_ReturnTrue_When_AccountHasCorrectDataForPromotionToTechLead() {
            UUID testAccountId = testAccountProfile.getId();
            createTestProjectAndSave();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.MASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();

            subprojectAccount.setTotalCommits(62);
            testProject.setArchitect(testAccountProfile);
            testProject.setSupervisor(testAccountProfile);

            for (int i = 0; i < 4; i++) {
                createRolePromotionTaskAndSave();
            }

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(accountProfile.getId());

            assertNotNull(response);
            assertTrue(response.canPromote());
        }
    }

    @Nested
    @DisplayName("Check on failed role promotion check tests")
    public class CheckOnFailedRolePromotionCheckTests {
        @Test
        @DisplayName("Should return false when account does not have correct data for promotion to premaster")
        public void should_ReturnFalse_When_AccountDoesNotHaveCorrectDataForPromotionToPremaster() {
            UUID testAccountId = testAccountProfile.getId();
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(10);
            subprojectAccountRepository.save(subprojectAccount);

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(testAccountId);

            assertNotNull(response);
            assertFalse(response.canPromote());
        }

        @Test
        @DisplayName("Should return false when account does not have correct data for promotion to master")
        public void should_ReturnFalse_When_AccountDoesNotHaveCorrectDataForPromotionToMaster() {
            UUID testAccountId = testAccountProfile.getId();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.PREMASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(38);

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(accountProfile.getId());

            assertNotNull(response);
            assertFalse(response.canPromote());
            response.checkList().forEach((key, value) -> assertFalse(value));
        }

        @Test
        @DisplayName("Should return false when account does not have correct data for promotion to tech lead")
        public void should_ReturnFalse_When_AccountDoesNotHaveCorrectDataForPromotionToTechLead() {
            testAccountProfile.setTechnicalRole(AccountTechnicalRole.MASTER);

            testProject.setArchitect(null);
            testProject.setSupervisor(null);

            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            RolePromotionCheckResponse response = rolePromotionService.checkOnRolePromotionAbility(accountProfile.getId());

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should throw AccountException when provided not valid account id")
        public void should_ThrowException_When_ProvidedNotValidAccountId() {
            assertThrows(AccountException.class, () -> rolePromotionService.checkOnRolePromotionAbility(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should throw exception when not provided role promotion conditions")
        public void should_ThrowException_When_RolePromotionConditionsNotProvided() {
            rolePromotionConditionRepository.deleteAll();
            assertThrows(RolePromotionConditionException.class, () -> rolePromotionService.checkOnRolePromotionAbility(testAccountProfile.getId()));
        }
    }

    @Nested
    @Transactional
    @DisplayName("Check on successful account role promotion")
    class CheckOnSuccessfulRolePromotionTests {
        @Test
        @DisplayName("Should successfully promote account to premaster when provided valid data")
        public void should_SuccessfullyPromoteAccountToPremaster_When_AccountHasCorrectDataForPromotionToPremaster() {
            UUID testAccountId = testAccountProfile.getId();
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(21);
            subprojectAccountRepository.save(subprojectAccount);

            AccountProfileDto response = rolePromotionService.promoteAccountProfileRole(testAccountId);

            assertEquals(AccountTechnicalRole.PREMASTER, response.technicalRole());
        }

        @Test
        @DisplayName("Should successfully promote account to master when provided valid data")
        public void should_SuccessfullyPromoteAccountToMaster_When_AccountHasCorrectDataForPromotionToMaster() {
            UUID testAccountId = testAccountProfile.getId();
            createTestProjectAndSave();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.PREMASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(40);

            for (int i = 0; i < 3; i++) {
                createRolePromotionTaskAndSave();
            }

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);
            createAndSaveTestEducationSession();

            AccountProfileDto response = rolePromotionService.promoteAccountProfileRole(accountProfile.getId());

            assertEquals(AccountTechnicalRole.MASTER, response.technicalRole());
        }

        @Test
        @DisplayName("Should successfully promote account to tech lead when provided valid data")
        public void should_SuccessfullyPromoteAccountToTechLead_When_AccountHasCorrectDataForPromotionToTechLead() {
            UUID testAccountId = testAccountProfile.getId();
            createTestProjectAndSave();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.MASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();

            subprojectAccount.setTotalCommits(62);
            testProject.setArchitect(testAccountProfile);
            testProject.setSupervisor(testAccountProfile);

            for (int i = 0; i < 4; i++) {
                createRolePromotionTaskAndSave();
            }

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            AccountProfileDto response = rolePromotionService.promoteAccountProfileRole(accountProfile.getId());

            assertEquals(AccountTechnicalRole.TECHLEAD, response.technicalRole());
        }
    }

    @Nested
    @DisplayName("Check on failed account role promotion")
    class CheckOnFailedRolePromotionTests {
        @Test
        @DisplayName("Should not promote account to premaster when provided invalid data")
        public void should_NotPromoteAccountToPremaster_When_AccountDoesNotHaveCorrectDataForPromotionToPremaster() {
            UUID testAccountId = testAccountProfile.getId();
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(10);
            subprojectAccountRepository.save(subprojectAccount);

            assertThrows(SubprojectAccountException.class, () -> rolePromotionService.promoteAccountProfileRole(testAccountId));
        }

        @Test
        @DisplayName("Should not promote account to master when provided invalid data")
        public void should_NotPromoteAccountToMaster_When_AccountDoesNotHaveCorrectDataForPromotionToMaster() {
            UUID testAccountId = testAccountProfile.getId();

            testAccountProfile.setTechnicalRole(AccountTechnicalRole.PREMASTER);
            SubprojectAccount subprojectAccount = subprojectAccountRepository.findAllById_AccountId(testAccountId).get()
                    .iterator()
                    .next();
            subprojectAccount.setTotalCommits(38);

            subprojectAccountRepository.save(subprojectAccount);
            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            assertThrows(SubprojectAccountException.class, () -> rolePromotionService.promoteAccountProfileRole(accountProfile.getId()));
        }

        @Test
        @DisplayName("Should not promote account to tech lead when provided invalid data")
        public void should_NotPromoteAccountToTechLead_When_AccountDoesNotHaveCorrectDataForPromotionToTechLead() {
            testAccountProfile.setTechnicalRole(AccountTechnicalRole.MASTER);

            testProject.setArchitect(null);
            testProject.setSupervisor(null);

            AccountProfile accountProfile = accountProfileRepository.save(testAccountProfile);

            assertThrows(SubprojectAccountException.class, () -> rolePromotionService.promoteAccountProfileRole(accountProfile.getId()));
        }
    }
}
