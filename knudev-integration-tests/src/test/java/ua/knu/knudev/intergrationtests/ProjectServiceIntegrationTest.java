package ua.knu.knudev.intergrationtests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.*;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.mapper.SubprojectAccountMapper;
import ua.knu.knudev.teammanager.mapper.SubprojectMapper;
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.ProjectService;
import ua.knu.knudev.teammanagerapi.dto.*;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;
import ua.knu.knudev.teammanagerapi.request.ProjectUpdateRequest;
import ua.knu.knudev.teammanagerapi.request.SubprojectUpdateRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class ProjectServiceIntegrationTest {

    private static final String TEST_PROJECT_NAME_IN_ENGLISH = "test-project";
    private static final String TEST_PROJECT_DESCRIPTION_IN_ENGLISH = "test-project-description";
    private static final String TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN = "тест-проєкта-опис";
    private static final UUID TEST_PROJECT_UUID = UUID.randomUUID();
    private static final String TEST_GITHUB_REPO_LINK_1 = "https://github.com/KNUdev/knu-dev_backend";
    private static final String TEST_GITHUB_REPO_LINK_2 = "https://github.com/KNUdev/knu-dev_frontend";

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private AccountProfileMapper accountProfileMapper;
    @Autowired
    private SubprojectMapper subprojectMapper;
    @Autowired
    private SubprojectAccountMapper subprojectAccountMapper;

    private Project testProject;
    private Department testDepartment;
    private Specialty testSpecialty;
    private AccountProfile testAccountProfile;
    @Autowired
    private SubprojectRepository subprojectRepository;

    @BeforeEach
    public void setUp() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
        testAccountProfile = createAndSaveTestAccountProfile();
        testProject = createTestProjectAndSave();
    }

    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
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

    private AccountProfile createAndSaveTestAccountProfile() {
        AccountProfile accountProfile = AccountProfile.builder()
                .id(UUID.randomUUID())
                .firstName("FirstName")
                .lastName("LastName")
                .middleName("MiddleName")
                .email("email@email.com")
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
                TEST_PROJECT_NAME_IN_ENGLISH,
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
                Set.of(createTestReleaseParticipation(testAccountProfile, release));
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

    private ProjectUpdateRequest createTestProjectUpdateRequest(UUID projectId) {
        Subproject testSubproject = createTestSubproject(testProject, "https://github.com/KNUdev/knu-dev_mobile", SubprojectType.MOBILE_APP);
        SubprojectDto subprojectDto = subprojectMapper.toDto(testSubproject);

        return ProjectUpdateRequest.builder()
                .id(projectId)
                .name("Updated name")
                .description(MultiLanguageFieldDto.builder()
                        .en("Updated description")
                        .uk("Оновлений опис")
                        .build())
                .banner("Updated banner")
                .status(ProjectStatus.MAINTENANCE)
                .tags(Set.of(ProjectTag.FINANCES))
                .architect(accountProfileMapper.toDto(testAccountProfile))
                .supervisor(accountProfileMapper.toDto(testAccountProfile))
                .subprojects(Set.of(subprojectDto))
                .build();
    }

    private SubprojectUpdateRequest createTestSubprojectUpdateRequest(UUID subprojectId) {
        Subproject testSubproject = createTestSubproject(testProject, "https://github.com/KNUdev/knu-dev_mobile", SubprojectType.MOBILE_APP);
        Subproject save = subprojectRepository.save(testSubproject);

        return SubprojectUpdateRequest.builder()
                .id(subprojectId)
                .subprojectAccountDtos(subprojectAccountMapper.toDtos(save.getAllDevelopers()))
                .build();
    }

    @Test
    @DisplayName("Should successfully get project by id when provided valid id")
    void should_SuccessfullyGetProjectById_When_ProvidedValidId() {
        FullProjectDto response = projectService.getById(testProject.getId());

        assertNotNull(response);
        assertEquals(testProject.getName(), response.getName());
        assertEquals(testProject.getTags(), response.getTags());
        assertEquals(testProject.getArchitect().getId(), response.getArchitect().id());
        assertEquals(testProject.getStatus(), response.getStatus());
        assertEquals(testProject.getSubprojects().size(), response.getSubprojects().size());
    }

    @Test
    @DisplayName("Should throw exception when provided not valid project id")
    void should_ThrowException_When_ProvidedNotValidProjectId() {
        assertThrows(ProjectException.class, () -> projectService.getById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should successfully get all projects when provided valid data")
    void should_SuccessfullyGetAllProjects_When_ProvidedValidData() {
        for (int i = 0; i < 9; i++) {
            createTestProjectAndSave();
        }
        Page<ShortProjectDto> response = projectService.getAll(0, 10);
        ShortProjectDto firstResponseShortDto = response.get().findFirst().get();

        assertNotNull(response);
        assertEquals(10, response.getTotalElements());
        assertEquals(testProject.getName(), firstResponseShortDto.getName());
        assertEquals(testProject.getTags(), firstResponseShortDto.getTags());
        assertEquals(testProject.getStatus(), firstResponseShortDto.getStatus());
        assertEquals(testProject.getLastUpdatedAt(), firstResponseShortDto.getLastUpdate());
        assertEquals(testProject.getBanner(), firstResponseShortDto.getBanner());
    }

    @Test
    @DisplayName("Should successfully update project when provided valid data")
    void should_SuccessfullyUpdateProject_When_ProvidedValidData() {
        ProjectUpdateRequest request = createTestProjectUpdateRequest(testProject.getId());
        FullProjectDto response = projectService.updateProject(request);

        assertNotNull(response);
        assertEquals("Updated name", response.getName());
        assertEquals("Updated banner", response.getBanner());
        assertEquals("Updated description", response.getDescription().getEn());
        assertEquals("Оновлений опис", response.getDescription().getUk());
        assertEquals(ProjectStatus.MAINTENANCE, response.getStatus());
        assertTrue(response.getTags().contains(ProjectTag.FINANCES));
        assertNotNull(response.getSubprojects());
    }

    @Test
    @DisplayName("Should throw exception when provided not valid project id in update request")
    void should_ThrowException_When_ProvidedNotValidProjectIdInUpdateRequest() {
        ProjectUpdateRequest request = createTestProjectUpdateRequest(UUID.randomUUID());

        assertThrows(ProjectException.class, () -> projectService.updateProject(request));
    }

    @Test
    @DisplayName("Should successfully update subproject when provided valid data")
    void should_SuccessfullyUpdateSubproject_When_ProvidedValidData() {
        SubprojectUpdateRequest request = createTestSubprojectUpdateRequest(testProject.getSubprojects().iterator().next().getId());

        SubprojectDto response = projectService.updateSubproject(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getAllDevelopers());

        List<UUID> accountIds = response.getAllDevelopers().stream()
                .map(SubprojectAccountDto::getSubprojectAccountIdDto)
                .map(SubprojectAccountIdDto::getAccountId)
                .toList();
        List<UUID> subprojectIds = response.getAllDevelopers().stream()
                .map(SubprojectAccountDto::getSubprojectAccountIdDto)
                .map(SubprojectAccountIdDto::getSubprojectId)
                .toList();

        assertTrue(accountIds.contains(request.subprojectAccountDtos().iterator().next().getSubprojectAccountIdDto().getAccountId()));
        assertTrue(subprojectIds.contains(request.subprojectAccountDtos().iterator().next().getSubprojectAccountIdDto().getSubprojectId()));
    }

    @Test
    @DisplayName("Should throw exception when provided not valid subproject id in update request")
    void should_ThrowException_When_ProvidedNotValidSubprojectIdInUpdateRequest() {
        SubprojectUpdateRequest request = createTestSubprojectUpdateRequest(UUID.randomUUID());

        assertThrows(ProjectException.class, () -> projectService.updateSubproject(request));
    }

    @Test
    @DisplayName("SShould throw exception when provided nullable account id")
    void should_ThrowException_When_ProvidedNullableAccountId() {
        assertThrows(ProjectException.class, () -> projectService.getAllByAccountId(null));
    }

}