package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.service.ImageService;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.*;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.ProjectService;
import ua.knu.knudev.teammanagerapi.dto.ProjectAccountDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class ProjectServiceIntegrationTest {

    private static final String TEST_PROJECT_NAME_IN_ENGLISH = "test-project";
    private static final String TEST_PROJECT_NAME_IN_UKRAINIAN = "тест-проєкта";
    private static final String TEST_PROJECT_DESCRIPTION_IN_ENGLISH = "test-project-description";
    private static final String TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN = "тест-проєкта-опис";
    private static final UUID TEST_PROJECT_UUID = UUID.randomUUID();
    private static final String TEST_GITHUB_REPO_LINK_1 = "https://github.com/KNUdev/knu-dev/issues";
    private static final String TEST_GITHUB_REPO_LINK_2 = "https://github.com/KNUdev/knu-dev/pulls";
    private static final String TEST_PROJECT_DOMAIN = "knudev";

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private ProjectReleaseInfoRepository projectReleaseInfoRepository;

    private Project testProject;
    private Department testDepartment;
    private Specialty testSpecialty;
    private AccountProfile testAccountProfile;

    @BeforeEach
    public void setUp() {
        testProject = createTestProjectAndSave();
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
        testAccountProfile = createAndSaveTestAccountProfile();
    }

    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    private Project createTestProjectAndSave() {
        MultiLanguageField name = new MultiLanguageField(TEST_PROJECT_NAME_IN_ENGLISH, TEST_PROJECT_NAME_IN_UKRAINIAN);
        MultiLanguageField description = new MultiLanguageField(TEST_PROJECT_DESCRIPTION_IN_ENGLISH, TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN);
        Set<ProjectTag> tags = Set.of(ProjectTag.MANAGEMENT, ProjectTag.FINANCES);
        Set<String> repoLinks = Set.of(TEST_GITHUB_REPO_LINK_1, TEST_GITHUB_REPO_LINK_2);

        Project project = Project.builder()
                .id(TEST_PROJECT_UUID)
                .name(name)
                .description(description)
                .avatarFilename(TEST_PROJECT_NAME_IN_ENGLISH)
                .startedAt(null)
                .status(ProjectStatus.PLANNED)
                .tags(tags)
                .githubRepoLinks(repoLinks)
                .build();

        return projectRepository.save(project);
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
                .avatarFilename("avatarFilename")
                .technicalRole(AccountTechnicalRole.DEVELOPER)
                .expertise(Expertise.BACKEND)
                .registrationDate(LocalDateTime.of(2021, 1, 1, 1, 1))
                .lastRoleUpdateDate(LocalDateTime.of(2022, 1, 1, 1, 2))
                .yearOfStudyOnRegistration(2)
                .unit(KNUdevUnit.CAMPUS)
                .department(testDepartment)
                .specialty(testSpecialty)
                .build();
        return accountProfileRepository.save(accountProfile);
    }

    private ProjectCreationRequest getValidProjectCreationRequest() {
        MultiLanguageFieldDto name = new MultiLanguageFieldDto(TEST_PROJECT_NAME_IN_ENGLISH, TEST_PROJECT_NAME_IN_UKRAINIAN);
        MultiLanguageFieldDto description = new MultiLanguageFieldDto(TEST_PROJECT_DESCRIPTION_IN_ENGLISH, TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN);
        Set<ProjectTag> tags = Set.of(ProjectTag.MANAGEMENT, ProjectTag.FINANCES);
        Set<String> repoLinks = Set.of(TEST_GITHUB_REPO_LINK_1, TEST_GITHUB_REPO_LINK_2);

        return ProjectCreationRequest.builder()
                .name(name)
                .description(description)
                .avatarFile(getMockMultipartFile())
                .githubRepoUrls(repoLinks)
                .tags(tags)
                .build();
    }

    private AddProjectDeveloperRequest getValidAddProjectDeveloperRequest() {
        return AddProjectDeveloperRequest.builder()
                .accountProfileId(testAccountProfile.getId())
                .projectId(testProject.getId())
                .build();
    }

    private MultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                TEST_PROJECT_NAME_IN_ENGLISH,
                "avatar.png",
                "image/png",
                "dummy content".getBytes()
        );
    }

    @Test
    @DisplayName("Should create project successfully when provided valid creation request")
    public void should_CreateProjectSuccessfully_When_ProvidedValidCreationRequest() {
        //Arrange
        ProjectCreationRequest request = getValidProjectCreationRequest();
        Set<ProjectTag> tags = Set.of(ProjectTag.MANAGEMENT, ProjectTag.FINANCES);
        Set<String> repoLinks = Set.of(TEST_GITHUB_REPO_LINK_1, TEST_GITHUB_REPO_LINK_2);

        //Act
        ProjectDto response = projectService.create(request);

        //Assert
        assertNotNull(response, "Response should not be null");
        assertNull(response.startedAt());

        List<Project> projectByName = projectRepository.findProjectByName(new MultiLanguageField(TEST_PROJECT_NAME_IN_ENGLISH,
                TEST_PROJECT_NAME_IN_UKRAINIAN));

        assertEquals(2, projectByName.size());
        assertEquals(TEST_PROJECT_NAME_IN_ENGLISH, response.name().getEn());
        assertEquals(TEST_PROJECT_NAME_IN_UKRAINIAN, response.name().getUk());
        assertEquals(TEST_PROJECT_DESCRIPTION_IN_ENGLISH, response.description().getEn());
        assertEquals(TEST_PROJECT_DESCRIPTION_IN_UKRAINIAN, response.description().getUk());
        assertEquals(TEST_PROJECT_NAME_IN_ENGLISH, response.avatarFilename());
        assertEquals(ProjectStatus.PLANNED, response.status());
        assertEquals(tags, response.tags());
        assertEquals(repoLinks, response.githubRepoLinks());
        assertTrue(projectRepository.existsProjectByAvatarFilename(TEST_PROJECT_NAME_IN_ENGLISH), "Project should exist");
        assertTrue(imageService.existsByFilename(TEST_PROJECT_NAME_IN_ENGLISH, ImageSubfolder.PROJECTS_AVATARS));
    }

    @Nested
    @DisplayName("Add developer to project tests")
    class AddDeveloperToProjectTests {
        @Test
        @DisplayName("Should add developer to project when given valid data")
        public void should_AddDeveloperToProjectSuccessfully_When_ProvidedValidData() {
            //Arrange
            AddProjectDeveloperRequest request = getValidAddProjectDeveloperRequest();

            //Act
            ProjectDto response = projectService.addDeveloper(request);
            List<UUID> profileAccountIds = response.projectAccounts().stream()
                    .map(ProjectAccountDto::accountId)
                    .toList();

            //Assert
            assertNotNull(response, "Response should not be null");
            assertNotNull(response.projectAccounts(), "Project accounts should not be null");
            assertEquals(testProject.getId(), response.id());
            assertTrue(accountProfileRepository.existsById(testAccountProfile.getId()), "Account profile should exist");
            assertTrue(projectRepository.existsProjectById(testProject.getId()), "Project should exist");
            assertTrue(profileAccountIds.contains(testAccountProfile.getId()), "Profile accounts should contain account id");
        }

        @Test
        @DisplayName("Should throw ProjectException when projectAccount already exist in project")
        public void should_ThrowProjectException_When_ProjectAccountAlreadyExist() {
            //Arrange
            projectService.addDeveloper(getValidAddProjectDeveloperRequest());

            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.addDeveloper(getValidAddProjectDeveloperRequest())
            );

            assertEquals("Account with ID: " + testAccountProfile.getId() +
                    " is already assigned to project: " + testProject.getId(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when given not valid projectId")
        public void should_ThrowException_When_ProjectIdIsNotValid() {
            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.addDeveloper(new AddProjectDeveloperRequest(
                            testAccountProfile.getId(), testAccountProfile.getId()))
            );

            assertEquals("Project with id " + testAccountProfile.getId() + " not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Project`s status update tests")
    class UpdateProjectStatusTests {
        @Test
        @DisplayName("Should update status when given valid data")
        public void should_UpdateProjectStatusSuccessfully_When_ProvidedValidData() {
            //Act
            ProjectDto response = projectService.updateStatus(testProject.getId(), ProjectStatus.FINISHED);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(ProjectStatus.FINISHED, response.status());
            assertEquals(testProject.getId(), response.id());
            assertTrue(projectRepository.existsById(testProject.getId()), "Project should exist");
            assertTrue(projectRepository.existsByStatus(ProjectStatus.FINISHED));
        }

        @Test
        @DisplayName("Should set startedAt on now when status was PLANNED and new status is UNDER_DEVELOPMENT")
        public void should_SetStartedAtOnNow_When_ProjectStatusWasPLANNED_AndNewProjectStatusIsUNDER_DEVELOPMENT() {
            //Act
            ProjectDto response = projectService.updateStatus(testProject.getId(), ProjectStatus.UNDER_DEVELOPMENT);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(ProjectStatus.UNDER_DEVELOPMENT, response.status());
            assertEquals(LocalDate.now(), response.startedAt());
            assertTrue(projectRepository.existsByStatus(ProjectStatus.UNDER_DEVELOPMENT));
        }

        @Test
        @DisplayName("Should throw ProjectException when newProjectStatus = null")
        public void should_ThrowProjectException_When_newProjectStatusIsNull() {
            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.updateStatus(testProject.getId(), null)
            );

            assertEquals("Project status can't be null!", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw ProjectException when given not valid projectId")
        public void should_ThrowProjectException_When_ProjectIdIsNotValid() {
            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.updateStatus(testAccountProfile.getId(), ProjectStatus.PLANNED)
            );

            assertEquals("Project with id " + testAccountProfile.getId() + " not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get project by id tests")
    class GetProjectByIdTests {
        @Test
        @DisplayName("Should return project successfully when given valid id")
        public void should_ReturnProjectSuccessfully_When_ProvidedValidId() {
            //Act
            ProjectDto response = projectService.getById(testProject.getId());

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(testProject.getId(), response.id());
            assertEquals(testProject.getName().getEn(), response.name().getEn());
            assertEquals(testProject.getName().getUk(), response.name().getUk());
            assertEquals(testProject.getDescription().getEn(), response.description().getEn());
            assertEquals(testProject.getDescription().getUk(), response.description().getUk());
            assertEquals(testProject.getTags(), response.tags());
            assertEquals(testProject.getGithubRepoLinks(), response.githubRepoLinks());
            assertTrue(projectRepository.existsById(testProject.getId()), "Project should exist");
        }

        @Test
        @DisplayName("Should throw ProjectException when given not valid id")
        public void should_ThrowProjectException_When_ProvidedInvalidId() {
            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.getById(testAccountProfile.getId())
            );

            assertEquals("Project with id " + testAccountProfile.getId() + " not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get all projects tests")
    class GetAllProjectsTests {
        @Test
        @DisplayName("Should return all projects when it provided in db")
        public void should_ReturnAllProjects_When_ProvidedInDb() {
            //Act
            Set<ProjectDto> response = projectService.getAll();
            UUID projectId = response.stream().map(ProjectDto::id).toList().get(0);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(1, response.size(), "Response should contain 1 project");
            assertEquals(testProject.getId(), projectId);
        }

        @Test
        @DisplayName("Should throw ProjectException when database has noone project")
        public void should_ThrowProjectException_When_DatabaseHasNoOneProject() {
            //Arrange
            projectRepository.deleteAll();

            //Act & Assert
            ProjectException exception = assertThrows(
                    ProjectException.class,
                    () -> projectService.getAll()
            );

            assertEquals("No projects found!", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Project`s release tests")
    class ReleaseProjectTests {
        @Test
        @DisplayName("Should successfully save project with project release when given valid data")
        public void should_ReturnProjectSuccessfully_When_ProvidedValidData() {
            //Act
            ProjectDto response = projectService.release(testProject.getId(), TEST_PROJECT_DOMAIN);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertNotNull(response.releaseInfo(), "Release info should not be null");
            assertEquals(testProject.getId(), response.id());
            assertEquals(LocalDate.now(), response.releaseInfo().releaseDate());
            assertEquals(TEST_PROJECT_DOMAIN, response.releaseInfo().projectDomain());
            assertEquals(testProject.getTags(), response.tags());
            assertTrue(projectReleaseInfoRepository.existsById(response.id()),
                    "Project release info should exist");
        }

        @Test
        @DisplayName("Should throw ProjectException when project`s release info is not null")
        public void should_ThrowProjectException_When_ProjectReleaseInfoIsNotNull() {
            //Arrange
            projectService.release(testProject.getId(), TEST_PROJECT_DOMAIN);

            //Act & Assert
            ProjectException exception = assertThrows(ProjectException.class,
                    () -> projectService.release(testProject.getId(), TEST_PROJECT_DOMAIN)
            );
            assertEquals("Project already has a release!", exception.getMessage());
            assertNotNull(projectRepository.findById(testProject.getId()).get().getReleaseInfo(),
                    "Release info should not be null");
        }
    }

}
