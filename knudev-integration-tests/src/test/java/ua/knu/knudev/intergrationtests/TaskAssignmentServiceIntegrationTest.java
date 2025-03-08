package ua.knu.knudev.intergrationtests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanager.domain.TaskAssignment;
import ua.knu.knudev.assessmentmanager.domain.TaskAssignmentStatus;
import ua.knu.knudev.assessmentmanager.repository.RolePromotionTaskRepository;
import ua.knu.knudev.assessmentmanager.repository.TaskAssignmentRepository;
import ua.knu.knudev.assessmentmanager.service.RolePromotionTaskService;
import ua.knu.knudev.assessmentmanager.service.TaskAssignmentService;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;
import ua.knu.knudev.assessmentmanagerapi.response.TaskAssignmentResponse;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class TaskAssignmentServiceIntegrationTest {

    private static final String TEST_EMAIL = "student@knu.ua";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_MIDDLE_NAME = "Middle";
    private static final String TEST_FILE_NAME = "avatar.png";
    private static final String TASK_FILE_NAME = "Task_DEVELOPER_MoonlightWalk.pdf";
    @Autowired
    private TaskAssignmentService taskAssignmentService;
    @Autowired
    private AccountProfileService accountProfileService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private RolePromotionTaskService taskUploadService;
    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;
    @Autowired
    private RolePromotionTaskRepository taskRepository;
    @Value("${application.assignments.activation-expiry-in-days}")
    private Integer assignmentActivationExpiryInDays;
    private Department testDepartment;
    private Specialty testSpecialty;
    private String accountEmail;

    @BeforeEach
    public void setup() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();

        AccountCreationRequest validAccountCreationReq = getValidAccountCreationReq();
        AccountRegistrationResponse register = accountProfileService.register(validAccountCreationReq);
        accountEmail = register.accountProfile().email();

        taskUploadService.uploadTaskForRole(AccountTechnicalRole.INTERN.name(), getPdfFile());
    }

    @AfterEach
    void tearDown() {
        taskAssignmentRepository.deleteAll();
        taskRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountAuthRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
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

    private AccountCreationRequest getValidAccountCreationReq() {
        return AccountCreationRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .expertise(Expertise.BACKEND)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .middleName(TEST_MIDDLE_NAME)
                .departmentId(testDepartment.getId())
                .specialtyCodename(testSpecialty.getCodeName())
                .avatarFile(getMockMultipartFile())
                .yearOfStudy(2)
                .githubAccountUsername("test")
                .build();
    }

    private MultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "avatarFile",
                TEST_FILE_NAME,
                "image/png",
                "dummy content".getBytes()
        );
    }

    private MultipartFile getPdfFile() {
        return new MockMultipartFile(
                "taskFile",
                TASK_FILE_NAME,
                "application/pdf",
                "dummy content".getBytes()
        );
    }

    @Nested
    @DisplayName("Assign TaskDomain Tests")
    class AssignTaskDomainTests {

        @Test
        @DisplayName("Should create task assignment when given valid data")
        public void should_CreateTaskAssignment_When_GivenValidData() {
            //Arrange

            // Act
            TaskAssignmentResponse response = taskAssignmentService.assignTaskToAccount(accountEmail);

            // Assert
            assertNotNull(response, "Response should not be null");
            assertNotNull(response.verificationCode(), "Verification code should not be null");

            Optional<TaskAssignment> assignmentOpt = taskAssignmentRepository.findByAssignedAccountEmail(accountEmail);
            assertTrue(assignmentOpt.isPresent(), "TaskDomain assignment should be saved in repository");

            TaskAssignment assignment = assignmentOpt.get();
            assertEquals(accountEmail, assignment.getAssignedAccountEmail(), "Assigned account email should match");
            assertNotNull(assignment.getVerificationCode(), "Verification code should not be null");
            assertNotNull(assignment.getTask(), "TaskDomain should not be null");
            assertEquals(TaskAssignmentStatus.PENDING, assignment.getStatus(), "Status should be PENDING");
            assertNotNull(assignment.getCreationDate(), "Creation date should not be null");
            assertNotNull(assignment.getActivationExpiryDate(), "Activation expiry date should not be null");

            LocalDateTime expectedExpiryDate = assignment.getCreationDate().plusDays(assignmentActivationExpiryInDays);
            assertEquals(expectedExpiryDate, assignment.getActivationExpiryDate());
        }

        @Test
        @DisplayName("Should throw TaskAssignmentException when assignment already exists")
        public void should_ThrowTaskAssignmentException_When_AssignmentAlreadyExists() {
            taskAssignmentService.assignTaskToAccount(accountEmail);

            TaskAssignmentException exception = assertThrows(
                    TaskAssignmentException.class,
                    () -> taskAssignmentService.assignTaskToAccount(accountEmail)
            );

            assertEquals("TaskDomain for this account is already assigned", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw TaskException when no tasks are available")
        public void should_ThrowTaskException_When_NoTasksAvailable() {
            taskRepository.deleteAll();

            assertThrows(
                    TaskException.class,
                    () -> taskAssignmentService.assignTaskToAccount(accountEmail)
            );
        }

        @Test
        @DisplayName("Should throw AccountException when account does not exist")
        public void should_ThrowAccountException_When_AccountDoesNotExist() {
            String nonExistentEmail = "nonexistent@knu.ua";

            AccountException exception = assertThrows(
                    AccountException.class,
                    () -> taskAssignmentService.assignTaskToAccount(nonExistentEmail)
            );

            assertEquals(String.format("Account with email %s does not exist", nonExistentEmail), exception.getMessage());
        }

        @Test
        @DisplayName("Should handle concurrent task assignments gracefully")
        @SneakyThrows
        public void should_HandleConcurrentTaskAssignmentsGracefully_When_ConcurrentAssignmentOccurs() {
            // Arrange
            Runnable assignmentTask = () -> {
                try {
                    taskAssignmentService.assignTaskToAccount(accountEmail);
                } catch (Exception ignored) {
                }
            };

            Thread thread1 = new Thread(assignmentTask);
            Thread thread2 = new Thread(assignmentTask);
            Thread thread3 = new Thread(assignmentTask);

            // Act
            thread1.start();
            thread2.start();
            thread3.start();
            thread1.join();
            thread2.join();
            thread3.join();

            // Assert
            long assignmentCount = taskAssignmentRepository.countByAssignedAccountEmail(accountEmail);
            assertEquals(1, assignmentCount, "Only one task assignment should exist for the account");
        }

        @Test
        @DisplayName("Should not assign task if account technical role does not match any task")
        public void should_NotAssignTask_When_ThereIsNoMatchingTaskForTechnicalRole() {
            taskRepository.deleteAll();
            taskUploadService.uploadTaskForRole(AccountTechnicalRole.DEVELOPER.name(), getPdfFile());

            assertThrows(
                    TaskException.class,
                    () -> taskAssignmentService.assignTaskToAccount(accountEmail)
            );
        }
    }
}
