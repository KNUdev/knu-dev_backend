package ua.knu.knudev.intergrationtests;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.service.ImageService;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class AccountProfileServiceIntegrationTest {

    private static final String TEST_EMAIL = "test@knu.ua";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_FILE_NAME = "avatar.png";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_MIDDLE_NAME = "Middle";
    @Autowired
    private AccountProfileService accountProfileService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    private Department testDepartment;
    private Specialty testSpecialty;

    @BeforeEach
    public void setup() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
    }

    @AfterEach
    public void tearDown() {
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
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .middleName(TEST_MIDDLE_NAME)
                .expertise(Expertise.BACKEND)
                .departmentId(testDepartment.getId())
                .yearOfStudy(2)
                .specialtyCodename(testSpecialty.getCodeName())
                .avatarFile(getMockMultipartFile())
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

    private AccountCreationRequest createInvalidAccountCreationReq(
            Consumer<AccountCreationRequest.AccountCreationRequestBuilder> modifier
    ) {
        AccountCreationRequest.AccountCreationRequestBuilder builder = getValidAccountCreationReq().toBuilder();
        modifier.accept(builder);
        return builder.build();
    }

    private void assertAccountDoesNotExist() {
        assertFalse(accountProfileRepository.existsByEmail(TEST_EMAIL), "Account profile should not exist");
        assertFalse(accountAuthRepository.existsByEmail(TEST_EMAIL), "Account auth should not exist");
    }

    @Test
    @DisplayName("Should register account successfully when provided valid account creation request")
    public void should_RegisterAccountSuccessfully_When_GivenValidInputData() {
        // Arrange
        AccountCreationRequest request = getValidAccountCreationReq();

        // Act
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.accountProfile(), "Account profile should not be null");
        assertEquals(TEST_EMAIL, response.accountProfile().email(), "Emails should match");

        Optional<AccountProfile> savedAccountOpt = accountProfileRepository.findByEmail(TEST_EMAIL);
        assertTrue(savedAccountOpt.isPresent(), "Account should be saved in repository");

        AccountProfile savedAccount = savedAccountOpt.get();
        assertEquals(TEST_FIRST_NAME, savedAccount.getFirstName(), "First names should match");
        assertEquals(TEST_LAST_NAME, savedAccount.getLastName(), "Last names should match");
        assertEquals(TEST_MIDDLE_NAME, savedAccount.getMiddleName(), "Middle names should match");
        assertEquals(testDepartment.getId(), savedAccount.getDepartment().getId(), "Department IDs should match");
        assertEquals(testSpecialty.getCodeName(), savedAccount.getSpecialty().getCodeName(), "Specialty code names should match");

        assertTrue(accountAuthRepository.existsByEmail(TEST_EMAIL), "Auth account should exist");
        assertTrue(imageService.existsByFilename(savedAccount.getAvatarFilename(), ImageSubfolder.ACCOUNT_AVATARS), "Avatar file should exist");
    }

    @Test
    @DisplayName("Should throw AccountException when registering with existing email")
    public void should_ThrowAccountException_When_AccountWithSuchEmailAlreadyExists() {
        // Arrange
        AccountCreationRequest request = getValidAccountCreationReq();

        AccountProfile existingAccount = AccountProfile.builder()
                .id(UUID.randomUUID())
                .email(TEST_EMAIL)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .middleName(TEST_MIDDLE_NAME)
                .avatarFilename(TEST_FILE_NAME)
                .department(testDepartment)
                .specialty(testSpecialty)
                .expertise(Expertise.BACKEND)
                .technicalRole(AccountTechnicalRole.INTERN)
                .registrationDate(LocalDateTime.now())
                .yearOfStudyOnRegistration(2)
                .build();

        accountProfileRepository.save(existingAccount);

        // Act & Assert
        AccountException exception = assertThrows(AccountException.class, () -> accountProfileService.register(request));
        assertEquals(String.format("Account with email %s already exists", TEST_EMAIL), exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DepartmentException when registering with invalid department")
    public void should_ThrowDepartmentException_When_GivenInvalidDepartment() {
        UUID invalidDepartmentId = UUID.randomUUID();
        AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder
                .departmentId(invalidDepartmentId)
        );

        assertThrows(DepartmentException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw DepartmentException when registering with invalid specialty")
    public void should_ThrowDepartmentException_When_GivenInvalidSpecialty() {
        Double invalidSpecialtyCodeName = 999.0;
        AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder
                .specialtyCodename(invalidSpecialtyCodeName)
        );

        assertThrows(DepartmentException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should register account when given avatar file is null")
    public void should_RegisterAccount_When_AvatarFileIsNull() {
        AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.avatarFile(null));

        AccountRegistrationResponse response = accountProfileService.register(request);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.accountProfile(), "Account profile should not be null");
        assertNull(response.accountProfile().avatarFilename(), "Avatar filename should be null");

        assertTrue(accountProfileRepository.existsByEmail(TEST_EMAIL), "Account profile should exist");
        assertTrue(accountAuthRepository.existsByEmail(TEST_EMAIL), "Auth account should exist");
    }

    @Test
    @DisplayName("Should fail registration when avatar file is too large")
    public void should_FailRegistration_When_AvatarFileIsTooLarge() {
        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
        MultipartFile largeFile = new MockMultipartFile(
                "avatarFile",
                "avatar.png",
                "image/png",
                largeContent
        );

        AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.avatarFile(largeFile));

        assertThrows(FileException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw FileException when avatar file is not an image")
    public void should_ThrowFileException_When_AvatarFileIsNotAnImage() {
        MultipartFile invalidFile = new MockMultipartFile(
                "avatarFile",
                "avatar.svg",
                "image/svg+xml",
                "dummy content".getBytes()
        );

        AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.avatarFile(invalidFile));

        assertThrows(FileException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw AccountException when auth service fails during registration")
    public void should__ThrowAccountException_When_AuthServiceFails() {
        AccountCreationRequest request = getValidAccountCreationReq();

        AccountAuth existingAuthAccount = new AccountAuth();
        existingAuthAccount.setEmail(TEST_EMAIL);
        existingAuthAccount.setPassword(TEST_PASSWORD);
        existingAuthAccount.setTechnicalRole(AccountTechnicalRole.DEVELOPER);
        accountAuthRepository.save(existingAuthAccount);

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        List<AccountProfile> profiles = accountProfileRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(0, profiles.size(), "No account profiles should be saved");
    }

    @Test
    @DisplayName("Should allow only one account registration when concurrent registrations with same email occur")
    public void should_AllowOnlyOneAccountRegistration_When_ConcurrentRegistrationsOccur() throws InterruptedException {
        AccountCreationRequest request = getValidAccountCreationReq();

        Runnable registrationTask = () -> {
            try {
                accountProfileService.register(request);
            } catch (Exception ignored) {
            }
        };

        Thread thread1 = new Thread(registrationTask);
        Thread thread2 = new Thread(registrationTask);

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        List<AccountProfile> profileAccounts = accountProfileRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(1, profileAccounts.size(), "Only one account profile should be saved");

        List<AccountAuth> authAccounts = accountAuthRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(1, authAccounts.size(), "Only one auth account should be saved");
    }

    @Nested
    @DisplayName("Invalid Account Creation Tests")
    class InvalidAccountCreationTests {

        @Test
        @DisplayName("Should throw ConstraintViolationException when registering with invalid password")
        void should_ThrowConstraintViolationException_When_GivenInvalidPasswordFormat() {
            AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.password("123"));

            assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
            assertAccountDoesNotExist();
        }

        @Test
        @DisplayName("Should throw ConstraintViolationException when registering with invalid email format")
        void should_ThrowConstraintViolationException_When_GivenInvalidEmailFormat() {
            AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.email("invalidEmail"));

            assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
            assertAccountDoesNotExist();
        }

        @Test
        @DisplayName("Should throw ConstraintViolationException when required fields are missing")
        void should_ThrowConstraintViolationException_When_RequiredFieldsAreMissing() {
            AccountCreationRequest request = AccountCreationRequest.builder().email(TEST_EMAIL).build();

            assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
            assertAccountDoesNotExist();
        }

        @Test
        @DisplayName("Should throw ConstraintViolationException when given invalid email domain")
        void should_ThrowConstraintViolationException_When_GivenInvalidEmailDomain() {
            AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder.email("testValidEmail@gmail.com"));

            assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
            assertAccountDoesNotExist();
        }

        @Test
        @DisplayName("Should throw ConstraintViolationException when names are not in English")
        void should_ThrowConstraintViolationException_When_InputNameIsNotEnglish() {
            AccountCreationRequest request = createInvalidAccountCreationReq(builder -> builder
                    .firstName("Владислав")
                    .lastName("Петренко")
                    .middleName("Григорович")
            );

            assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
            assertAccountDoesNotExist();
        }
    }
}
