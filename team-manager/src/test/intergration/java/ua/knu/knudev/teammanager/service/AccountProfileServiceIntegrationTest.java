package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.config.TeamManagerConfig;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static ua.knu.knudev.teammanager.utils.AccountTestUtils.getTestAccountProfile;

@SpringBootTest(classes = TeamManagerConfig.class)
@ImportAutoConfiguration(classes = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        ValidationAutoConfiguration.class
})
@ActiveProfiles("test")
@Transactional
public class AccountProfileServiceIntegrationTest {

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @MockBean
    private AccountAuthServiceApi accountAuthServiceApi;

    @MockBean
    private FileServiceApi fileServiceApi;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Specialty testSpecialty;

    @BeforeEach
    public void setup() {
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
//
//        testSpecialty = getTestSpecialty("Test Specialty");
//        testDepartment = getTestDepartment();
//        departmentRepository.save(testDepartment);

        Department d1 = new Department();
        d1.setName("d1");

        Specialty s1 = new Specialty();
        s1.setCodeName(1.0);
        s1.setName("Specialty 1");

        Specialty s2 = new Specialty();
        s2.setCodeName(2.0);
        s2.setName("Specialty 2");

        Specialty s3 = new Specialty();
        s3.setCodeName(3.0);
        s3.setName("Specialty 3");

        d1.addSpecialty(s1);
        d1.addSpecialty(s2);
        d1.addSpecialty(s3);

        departmentRepository.save(d1);
        testDepartment = d1;
        testSpecialty = s1;
    }

    @Test
    public void testSuccessfulRegistration() {
        String testEmail = "test@knu.ua";
        String testPassword = "password123";
        FullName fullName = new FullName("John", "Doe", "Middle");
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                testDepartment.getId(),
                testSpecialty.getCodeName()
        );

        MultipartFile mockAvatarFile = Mockito.mock(MultipartFile.class);

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .fullName(fullName)
                .academicUnitsIds(academicUnitsIds)
                .avatarFile(mockAvatarFile)
                .build();

        AuthAccountCreationResponse authResponse = AuthAccountCreationResponse.builder()
                .email(testEmail)
                .build();

        Mockito.when(accountAuthServiceApi.createAccount(any()))
                .thenReturn(authResponse);

        String uploadedFilename = "avatar.png";
        Mockito.when(fileServiceApi.uploadAccountPicture(any()))
                .thenReturn(uploadedFilename);

        // Perform registration
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert response
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.accountProfile());
        Assertions.assertEquals(testEmail, response.accountProfile().email());
        Assertions.assertEquals("Verification email has been sent to: " + testEmail,
                response.responseMessage());

        // Assert that the account was saved in the repository
        Optional<AccountProfile> savedAccountOpt = accountProfileRepository.findByEmail(testEmail);
        Assertions.assertTrue(savedAccountOpt.isPresent());
        AccountProfile savedAccount = savedAccountOpt.get();
        Assertions.assertEquals(fullName.firstName(), savedAccount.getFirstName());
        Assertions.assertEquals(fullName.lastName(), savedAccount.getLastName());
        Assertions.assertEquals(fullName.middleName(), savedAccount.getMiddleName());
        Assertions.assertEquals(uploadedFilename, savedAccount.getAvatar());
        Assertions.assertEquals(testDepartment.getId(), savedAccount.getDepartment().getId());
        Assertions.assertEquals(testSpecialty.getCodeName(), savedAccount.getSpecialty().getCodeName());
    }

    @Test
    public void testRegistrationWithExistingEmail() {
        // Prepare existing account
        String testEmail = "existing@knu.ua";
        AccountProfile existingAccount = getTestAccountProfile();
        existingAccount.setDepartment(testDepartment);
        existingAccount.setSpecialty(testSpecialty);
        existingAccount.setEmail(testEmail);
        accountProfileRepository.save(existingAccount);

        // Prepare request
        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(testEmail)
                .password("password123")
                .fullName(FullName.builder()
                        .firstName(existingAccount.getFirstName())
                        .lastName(existingAccount.getLastName())
                        .middleName(existingAccount.getMiddleName())
                        .build())
                .academicUnitsIds(AcademicUnitsIds.builder()
                        .departmentId(testDepartment.getId())
                        .specialtyId(testSpecialty.getCodeName())
                        .build())
                .build();

        // Perform registration and expect exception
        assertThrows(AccountException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithInvalidDepartment() {
        // Prepare request with non-existing department ID
        UUID invalidDepartmentId = UUID.randomUUID();
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                invalidDepartmentId,
                testSpecialty.getCodeName()
        );

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email("test@knu.ua")
                .password("password123")
                .academicUnitsIds(academicUnitsIds)
                .build();

        // Perform registration and expect exception
        assertThrows(DepartmentException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithInvalidSpecialty() {
        // Prepare request with invalid specialty code name
        Double invalidSpecialtyCodeName = 999.0;
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                testDepartment.getId(),
                invalidSpecialtyCodeName
        );

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email("test@knu.ua")
                .password("password123")
                .academicUnitsIds(academicUnitsIds)
                .build();

        // Perform registration and expect exception
        assertThrows(DepartmentException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWhenAuthServiceFails() {
        // Mock dependencies
        String testEmail = "test@knu.ua";
        String testPassword = "password123";
        FullName fullName = new FullName("John", "Doe", "Middle");
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                testDepartment.getId(),
                testSpecialty.getCodeName()
        );

        MultipartFile mockAvatarFile = Mockito.mock(MultipartFile.class);

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .fullName(fullName)
                .academicUnitsIds(academicUnitsIds)
                .avatarFile(mockAvatarFile)
                .build();

        // Simulate auth service failure
        Mockito.when(accountAuthServiceApi.createAccount(any()))
                .thenThrow(new RuntimeException("Auth service failed"));

        // Perform registration and expect an exception
        assertThrows(RuntimeException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWhenFileServiceFails() {
        // Mock dependencies
        String testEmail = "test@knu.ua";
        String testPassword = "password123";
        FullName fullName = new FullName("John", "Doe", "Middle");
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                testDepartment.getId(),
                testSpecialty.getCodeName()
        );

        MultipartFile mockAvatarFile = Mockito.mock(MultipartFile.class);

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .fullName(fullName)
                .academicUnitsIds(academicUnitsIds)
                .avatarFile(mockAvatarFile)
                .build();

        AuthAccountCreationResponse authResponse = AuthAccountCreationResponse.builder()
                .email(testEmail)
                .build();

        Mockito.when(accountAuthServiceApi.createAccount(any()))
                .thenReturn(authResponse);

        // Simulate file upload failure
        Mockito.when(fileServiceApi.uploadAccountPicture(any()))
                .thenThrow(new RuntimeException("File upload failed"));

        // Perform registration and expect an exception
        assertThrows(RuntimeException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithInvalidPassword() {
        String testEmail = "test";
        String testPassword = "validpassword12345";
        FullName fullName = new FullName("John", "Doe", "Middle");
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                testDepartment.getId(),
                testSpecialty.getCodeName()
        );

        MultipartFile mockAvatarFile = Mockito.mock(MultipartFile.class);

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .fullName(fullName)
                .academicUnitsIds(academicUnitsIds)
                .avatarFile(mockAvatarFile)
                .build();

        AuthAccountCreationResponse authResponse = AuthAccountCreationResponse.builder()
                .email(testEmail)
                .build();

        Mockito.when(accountAuthServiceApi.createAccount(any()))
                .thenReturn(authResponse);

        String uploadedFilename = "avatar.png";
        Mockito.when(fileServiceApi.uploadAccountPicture(any()))
                .thenReturn(uploadedFilename);

        // Perform registration and expect a validation exception
        assertThrows(ConstraintViolationException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithDepartmentHavingNoSpecialties() {
        Department departmentWithoutSpecialties = Department.builder()
                .id(UUID.randomUUID())
                .name("Empty Department")
                .build();
        departmentRepository.save(departmentWithoutSpecialties);

        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                departmentWithoutSpecialties.getId(),
                999.0
        );

        AccountCreationRequest request = AccountCreationRequest.builder()
                .email("test@knu.ua")
                .password("password123")
                .academicUnitsIds(academicUnitsIds)
                .build();

        // Perform registration and expect an AccountException
        assertThrows(DepartmentException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithInvalidEmailFormat() {
        // Prepare request with invalid email format
        AccountCreationRequest request = AccountCreationRequest.builder()
                .email("invalid-email")
                .password("password123")
                .build();

        // Perform registration and expect a validation exception
        assertThrows(AccountException.class, () -> {
            accountProfileService.register(request);
        });
    }

    @Test
    public void testRegistrationWithMissingRequiredFields() {
        // Prepare request missing password
        AccountCreationRequest request = AccountCreationRequest.builder()
                .email("test@knu.ua")
                .build();

        // Perform registration and expect a validation exception
        assertThrows(AccountException.class, () -> {
            accountProfileService.register(request);
        });
    }

}
