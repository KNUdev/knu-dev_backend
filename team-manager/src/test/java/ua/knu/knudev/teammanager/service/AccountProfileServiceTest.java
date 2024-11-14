package ua.knu.knudev.teammanager.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestSpecialty;
import static ua.knu.knudev.teammanager.utils.AccountTestUtils.getTestAccountProfile;
import static ua.knu.knudev.teammanager.utils.AccountTestUtils.getTestAccountProfileDto;
import static ua.knu.knudev.teammanager.utils.constants.AccountTestsConstants.*;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_ID;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_SPECIALTY_ID;

@ExtendWith(MockitoExtension.class)
class AccountProfileServiceTest {

    private static final AuthAccountCreationResponse AUTH_RESPONSE = AuthAccountCreationResponse.builder()
            .email(TEST_EMAIL)
            .technicalRole(TEST_TECHNICAL_ROLE)
            .build();

    private static final Department testDepartment = getTestDepartment();
    private static final Specialty testSpecialty = getTestSpecialty("Test Specialty");
    private static final AccountProfile testProfile = getTestAccountProfile();
    private static final AccountProfileDto testProfileDto = getTestAccountProfileDto();

    @Mock
    private AccountAuthServiceApi accountAuthServiceApi;

    @Mock
    private AccountProfileRepository accountProfileRepository;

    @Mock
    private AccountProfileMapper accountProfileMapper;

    @Mock
    private ImageServiceApi imageServiceApi;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private MultipartFile mockAvatarFile;

    @InjectMocks
    private AccountProfileService accountProfileService;

    @Captor
    private ArgumentCaptor<AccountProfile> accountProfileCaptor;

    private AccountCreationRequest request;

    @BeforeEach
    void setUp() {
        request = AccountCreationRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .academicUnitsIds(AcademicUnitsIds.builder()
                        .departmentId(TEST_DEPARTMENT_ID)
                        .specialtyCodename(TEST_SPECIALTY_ID)
                        .build())
                .avatarFile(mockAvatarFile)
                .fullName(FullName.builder()
                        .firstName(PROFILE_FIRST_NAME)
                        .lastName(PROFILE_LAST_NAME)
                        .middleName(PROFILE_MIDDLE_NAME)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Should register successfully when all inputs are valid")
    void should_RegisterSuccessfully_When_AllInputsAreValid() {
        // Arrange
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(uploadAvatar()).thenReturn(TEST_FILE_NAME);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(accountProfileRepository.save(any(AccountProfile.class))).thenReturn(testProfile);
        try {
            when(mockAvatarFile.getBytes()).thenReturn(new byte[]{1, 2, 3, 4, 5});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Act
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(testProfileDto, response.accountProfile());

        verify(accountProfileRepository, times(1)).save(accountProfileCaptor.capture());
        AccountProfile capturedProfile = accountProfileCaptor.getValue();

        assertEquals(TEST_EMAIL, capturedProfile.getEmail());
        assertEquals(PROFILE_FIRST_NAME, capturedProfile.getFirstName());
        assertEquals(PROFILE_LAST_NAME, capturedProfile.getLastName());
        assertEquals(PROFILE_MIDDLE_NAME, capturedProfile.getMiddleName());
        assertEquals(TEST_FILE_NAME, capturedProfile.getAvatarFilename());
        assertEquals(testDepartment, capturedProfile.getDepartment());
        assertEquals(testSpecialty.getCodeName(), capturedProfile.getSpecialty().getCodeName());

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(departmentService, times(1)).getById(TEST_DEPARTMENT_ID);
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verifyUploadAvatar();
    }

    @Test
    @DisplayName("Should throw AccountException when email already exists")
    void should_ThrowAccountException_When_AccountWithSuchEmailExists() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, never()).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verifyNoMoreInteractions(accountAuthServiceApi, imageServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
    }

    @Test
    @DisplayName("Should throw DepartmentException when department validation fails")
    void should_ThrowDepartmentException_When_ValidationFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doThrow(new DepartmentException("Invalid department or specialty"))
                .when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));

        assertThrows(DepartmentException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verifyNoMoreInteractions(imageServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
    }

    @Test
    @DisplayName("Should throw AccountException when account auth creation fails")
    void should_ThrowAccountException_When_AccountAuthCreationFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class)))
                .thenThrow(new AccountException("Auth service failure"));

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verifyNoMoreInteractions(imageServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
    }

    @Test
    @DisplayName("Should throw AccountException when file upload fails")
    @SneakyThrows
    void should_ThrowAccountExceptionWhen_AvatarUploadFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(uploadAvatar()).thenThrow(new AccountException("File upload failed"));
        when(mockAvatarFile.getBytes()).thenReturn(new byte[]{1, 2, 3, 4, 5});

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verifyNoMoreInteractions(departmentService, imageServiceApi, accountProfileRepository, accountProfileMapper);
        verifyUploadAvatar();
    }

    @Test
    @DisplayName("Should throw RuntimeException when saving account profile fails")
    @SneakyThrows
    void should_ThrowRuntimeException_When_SavingProfileFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(mockAvatarFile.getBytes()).thenReturn(new byte[]{1, 2, 3, 4, 5});
        when(accountProfileRepository.save(any(AccountProfile.class)))
                .thenThrow(new RuntimeException("Database save failed"));

        assertThrows(RuntimeException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verify(departmentService, times(1)).getById(TEST_DEPARTMENT_ID);
        verify(accountProfileRepository, times(1)).save(any(AccountProfile.class));
        verifyNoMoreInteractions(accountProfileMapper);
        verifyUploadAvatar();
    }

    @Test
    @DisplayName("Should correctly build registration response")
    void should_BuildRegistrationResponseCorrectly_When_InputDataIsValid() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(accountProfileRepository.save(any(AccountProfile.class))).thenReturn(testProfile);

        AccountRegistrationResponse response = accountProfileService.register(request);

        assertNotNull(response);
        assertEquals(testProfileDto, response.accountProfile());
        assertEquals("Verification email has been sent to: " + TEST_EMAIL, response.responseMessage());
    }

    @Test
    @DisplayName("Should throw AccountException when given department does not contain given specialty")
    void should_ThrowAccountException_When_ValidDepartmentDoesNotContainGivenSpecialty() {
        Department testDepartment = getTestDepartment();
        testDepartment.getSpecialties().clear();

        when(departmentService.getById(testDepartment.getId())).thenReturn(testDepartment);

        AccountException accountException = assertThrows(
                AccountException.class,
                () -> accountProfileService.register(request)
        );

        String errorMessage = String.format("Specialty with id %s not found in department %s",
                TEST_SPECIALTY_ID, TEST_DEPARTMENT_ID);
        assertEquals(errorMessage, accountException.getMessage());
    }

    @Test
    @DisplayName("Should throw AccountException when account auth exists (Unexpected error)")
    void should_ThrowAccountException_When_AccountAuthExists() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(accountAuthServiceApi.existsByEmail(TEST_EMAIL)).thenReturn(true);

        AccountException accountException = assertThrows(
                AccountException.class,
                () -> accountProfileService.register(request)
        );

        String errorMessage = "Registration error happened. Please contact support.";
        assertEquals(errorMessage, accountException.getMessage());

        verifyNoMoreInteractions(departmentService, imageServiceApi, accountProfileRepository, accountProfileMapper);
    }

    private Object uploadAvatar() {
        return imageServiceApi.uploadFile(mockAvatarFile, ImageSubfolder.ACCOUNT_PICTURES);
    }

    private void verifyUploadAvatar() {
        verify(imageServiceApi, times(1))
                .uploadFile(mockAvatarFile, ImageSubfolder.ACCOUNT_PICTURES);
    }

}
