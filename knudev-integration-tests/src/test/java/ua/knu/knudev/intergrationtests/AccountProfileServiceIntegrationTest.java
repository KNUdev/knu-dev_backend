package ua.knu.knudev.intergrationtests;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.service.ImageService;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.intergrationtests.repository.SpecialtyRepository;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.service.AccountProfileService;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ua.knu.knudev.intergrationtests.utils.constants.AccountTestsConstants.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class AccountProfileServiceIntegrationTest {

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private AccountAuthRepository accountAuthRepository;

    private Department TEST_DEPARTMENT;
    private Specialty TEST_SPECIALTY;

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
    }

    @AfterEach
    void tearDown() {
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
        accountAuthRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register account successfully when provided valid account creation request")
    public void should_RegisterAccount_When_ProvidedValidAccountCreationRequest() {
        // Arrange
        AccountCreationRequest request = getValidAccountCreationReq();

        // Act
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.accountProfile());
        assertEquals(TEST_EMAIL, response.accountProfile().email());

        Optional<AccountProfile> savedAccountOpt = accountProfileRepository.findByEmail(TEST_EMAIL);
        assertTrue(savedAccountOpt.isPresent());

        AccountProfile savedAccount = savedAccountOpt.get();
        assertEquals(TEST_FULLNAME.firstName(), savedAccount.getFirstName());
        assertEquals(TEST_FULLNAME.lastName(), savedAccount.getLastName());
        assertEquals(TEST_FULLNAME.middleName(), savedAccount.getMiddleName());
        assertEquals(TEST_DEPARTMENT.getId(), savedAccount.getDepartment().getId());
        assertEquals(TEST_SPECIALTY.getCodeName(), savedAccount.getSpecialty().getCodeName());

        assertTrue(accountAuthRepository.existsByEmail(TEST_EMAIL));
        assertTrue(imageService.existsByFilename(savedAccount.getAvatarFilename(), ImageSubfolder.ACCOUNT_PICTURES));
    }

    @Test
    @DisplayName("Should throw AccountException when registering with existing email")
    public void should_ThrowAccountException_When_RegisteringWithExistingEmail() {
        AccountCreationRequest request = getValidAccountCreationReq();

        AccountProfile existingAccount = getTestAccountProfile();
        accountProfileRepository.save(existingAccount);

        assertThrows(AccountException.class, () -> accountProfileService.register(request));
    }

    @Test
    @DisplayName("Should throw DepartmentException when registering with invalid department")
    public void should_ThrowDepartmentException_When_RegisteringWithInvalidDepartment() {
        UUID invalidDepartmentId = UUID.randomUUID();
        AcademicUnitsIds academicUnitsIds = new AcademicUnitsIds(
                invalidDepartmentId,
                TEST_SPECIALTY.getCodeName()
        );
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .academicUnitsIds(academicUnitsIds)
                .build();

        assertThrows(DepartmentException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw DepartmentException when registering with invalid specialty")
    public void should_ThrowDepartmentException_When_RegisteringWithInvalidSpecialty() {
        Double invalidSpecialtyCodeName = 999.0;
        AcademicUnitsIds invalidSpecialtyAcademicUnitIds = new AcademicUnitsIds(
                TEST_DEPARTMENT.getId(),
                invalidSpecialtyCodeName
        );
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .academicUnitsIds(invalidSpecialtyAcademicUnitIds)
                .build();

        assertThrows(DepartmentException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should register account when given avatar file is null")
    public void should_RegisterAccount_When_GivenAvatarFileIsNull() {
        // Arrange
        MultipartFile corruptedFile = new MockMultipartFile(
                "avatarFile",
                "avatar.png",
                "image/png",
                (byte[]) null
        );
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .avatarFile(corruptedFile)
                .build();

        // Act
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert
        assertTrue(accountProfileRepository.existsByEmail(TEST_EMAIL));
        assertTrue(accountAuthRepository.existsByEmail(TEST_EMAIL));
        assertNull(response.accountProfile().avatarFilename());
    }

    @Test
    @DisplayName("Should fail registration when avatar file is too large")
    public void should_FailRegistration_When_AvatarFileIsTooLarge() {
        byte[] largeContent = new byte[10 * 1024 * 1024];
        MultipartFile largeFile = new MockMultipartFile(
                "avatarFile",
                "avatar.png",
                "image/png",
                largeContent
        );
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .avatarFile(largeFile)
                .build();

        assertThrows(FileException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw FileException when avatar file is not an image")
    public void should_ThrowFileException_When_AvatarFileIsNotAnImage() {
        MultipartFile svgFile = new MockMultipartFile(
                "svgFile",
                "avatar.svg",
                "image/png",
                "dummy content".getBytes()
        );

        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .avatarFile(svgFile)
                .build();

        assertThrows(FileException.class, () -> accountProfileService.register(request));
        assertAccountDoesNotExist();
    }

    @Test
    @DisplayName("Should throw AccountException when auth service fails during registration")
    public void should_ThrowAccountException_When_AuthServiceFailsDuringRegistration() {
        AccountCreationRequest request = getValidAccountCreationReq();

        AccountAuth existingAuthAccount = new AccountAuth();
        existingAuthAccount.setEmail(TEST_EMAIL);
        existingAuthAccount.setPassword(TEST_PASSWORD);
        accountAuthRepository.save(existingAuthAccount);

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        List<AccountProfile> profiles = accountProfileRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(0, profiles.size());
    }

    @Test
    @DisplayName("Should allow only one account registration when concurrent registrations with same email occur")
    //todo fix
    public void should_AllowOnlyOneAccountRegistration_When_ConcurrentRegistrationsWithSameEmailOccur() throws InterruptedException {
        // Arrange
        AccountCreationRequest request = getValidAccountCreationReq();

        Runnable registrationTask = () -> accountProfileService.register(request);

        Thread thread1 = new Thread(registrationTask);
        Thread thread2 = new Thread(registrationTask);

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert
        List<AccountProfile> profileAccounts = accountProfileRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(1, profileAccounts.size());

        List<AccountAuth> authAccounts = accountAuthRepository.findAllByEmail(TEST_EMAIL);
        assertEquals(1, authAccounts.size());
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when registering with invalid password")
    public void should_ThrowConstraintViolationException_When_RegisteringWithInvalidPassword() {
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .password("123")
                .build();

        assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when registering with invalid email format")
    public void should_ThrowConstraintViolationException_When_RegisteringWithInvalidEmailFormat() {
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .email("invalidEmail")
                .build();

        assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when required fields are missing")
    public void should_ThrowConstraintViolationException_When_RequiredFieldsAreMissingDuringRegistration() {
        AccountCreationRequest request = AccountCreationRequest.builder()
                .email(TEST_EMAIL)
                .build();

        assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when given invalid email domain")
    public void should_ThrowConstraintViolationException_When_GivenInvalidEmailDomain() {
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .email("testValidEmail@gmail.com")
                .build();

        assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when names are not in English")
    public void should_ThrowConstraintViolationException_When_NamesAreNotInEnglish() {
        AccountCreationRequest request = getValidAccountCreationReq().toBuilder()
                .fullName(FullName.builder()
                        .firstName("Владислав")
                        .lastName("Петренко")
                        .middleName("Григорович")
                        .build())
                .email(TEST_EMAIL)
                .build();

        assertThrows(ConstraintViolationException.class, () -> accountProfileService.register(request));
    }

    private AccountCreationRequest getValidAccountCreationReq() {
        return AccountCreationRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .fullName(TEST_FULLNAME)
                .academicUnitsIds(getAcademicUnitsIds())
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

    private AcademicUnitsIds getAcademicUnitsIds() {
        return new AcademicUnitsIds(
                TEST_DEPARTMENT.getId(),
                TEST_SPECIALTY.getCodeName()
        );
    }

    private AccountProfile getTestAccountProfile() {
        return AccountProfile.builder()
                .id(1)
                .email(TEST_EMAIL)
                .firstName(PROFILE_FIRST_NAME)
                .lastName(PROFILE_LAST_NAME)
                .middleName(PROFILE_MIDDLE_NAME)
                .avatarFilename(TEST_FILE_NAME)
                .department(TEST_DEPARTMENT)
                .specialty(TEST_SPECIALTY)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    private void assertAccountDoesNotExist() {
        assertFalse(accountProfileRepository.existsByEmail(TEST_EMAIL));
        assertFalse(accountAuthRepository.existsByEmail(TEST_EMAIL));
    }
}
