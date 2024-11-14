package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.intergrationtests.repository.SpecialtyRepository;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.taskmanager.repository.TaskAssignmentRepository;
import ua.knu.knudev.taskmanager.service.TaskAssignmentService;
import ua.knu.knudev.taskmanager.service.TaskUploadService;
import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import static ua.knu.knudev.intergrationtests.utils.constants.AccountTestsConstants.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class TaskAssignmentServiceIntegrationTest {

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
    private TaskUploadService taskUploadService;
    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    private Department TEST_DEPARTMENT;
    private Specialty TEST_SPECIALTY;
    private String ACCOUNT_EMAIL;

    @BeforeEach
    public void setup() {
        Department d1 = new Department();
        d1.setName("d1");

        Specialty s1 = new Specialty(122.0, "Computer Science");
        Specialty s2 = new Specialty(123.0, "Computer Engineering");
        Specialty s3 = new Specialty(125.0, "Cybernetics");

        d1.addSpecialty(s1);
        d1.addSpecialty(s2);
        d1.addSpecialty(s3);

        departmentRepository.save(d1);
        TEST_DEPARTMENT = d1;
        TEST_SPECIALTY = s1;

        AccountCreationRequest validAccountCreationReq = getValidAccountCreationReq();
        AccountRegistrationResponse register = accountProfileService.register(validAccountCreationReq);

        ACCOUNT_EMAIL = register.accountProfile().email();
        taskUploadService.uploadTaskForRole(AccountTechnicalRole.DEVELOPER.name(), getPdfFile());
    }

    @AfterEach
    void tearDown() {
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
        accountAuthRepository.deleteAll();
        taskAssignmentRepository.deleteAll();
    }

    @Test
    public void should_CreateTaskAssignment_When_GivenValidData() {
        TaskAssignmentResponse taskAssignmentResponse = taskAssignmentService.assignTaskToStudent(ACCOUNT_EMAIL);

        Assertions.assertNotNull(taskAssignmentResponse.verificationCode());
        //todo
    }

    public void should_ThrowTaskException_When_ThereIsAvailable() {

    }

    public void should_ThrowAccountException_When_AccountDoesNotExist() {

    }

    public void should_ThrowTaskAssignmentException_When_AssignmentAlreadyExists() {

    }

    private AccountCreationRequest getValidAccountCreationReq() {
        return AccountCreationRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .fullName(TEST_FULLNAME)
                .academicUnitsIds(getAcademicUnitsIds())
                .avatarFile(getMockMultipartFile())
                .expertise(Expertise.BACKEND)
                .build();
    }

    private MultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "avatarFile",
                "avatar.png",
                "image/png",
                "dummy content".getBytes()
        );
    }

    private MultipartFile getPdfFile() {
        return new MockMultipartFile(
                "Task_Developer_MoonlightWalk",
                "Task_Developer_MoonlightWalk.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );
    }

    private AcademicUnitsIds getAcademicUnitsIds() {
        return new AcademicUnitsIds(
                TEST_DEPARTMENT.getId(),
                TEST_SPECIALTY.getCodeName()
        );
    }

//    private AccountProfile getTestAccountProfile() {
//        return AccountProfile.builder()
//                .email(TEST_EMAIL)
//                .firstName(PROFILE_FIRST_NAME)
//                .lastName(PROFILE_LAST_NAME)
//                .middleName(PROFILE_MIDDLE_NAME)
//                .avatarFilename(TEST_FILE_NAME)
//                .department(TEST_DEPARTMENT)
//                .specialty(TEST_SPECIALTY)
//                .registrationDate(LocalDateTime.now())
//                .build();
//    }


}
