package ua.knu.knudev.teammanager.service;

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
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.utils.constants.AccountTestsConstants;
import ua.knu.knudev.teammanagerapi.dto.AcademicUnitsIds;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

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
    private FileServiceApi fileServiceApi;

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
                .password(AccountTestsConstants.TEST_PASSWORD)
                .departmentId(TEST_DEPARTMENT_ID)
                .specialtyId(TEST_SPECIALTY_ID)
                .avatarFile(mockAvatarFile)
                .firstName(AccountTestsConstants.PROFILE_FIRST_NAME)
                .lastName(AccountTestsConstants.PROFILE_LAST_NAME)
                .middleName(AccountTestsConstants.PROFILE_MIDDLE_NAME)
                .build();
    }

    @Test
    @DisplayName("Should register successfully when all inputs are valid")
    void should_RegisterSuccessfully_When_AllInputsAreValid() {
        // Arrange
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(fileServiceApi.uploadAccountPicture(eq(mockAvatarFile))).thenReturn(TEST_FILE_NAME);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(accountProfileRepository.save(any(AccountProfile.class))).thenReturn(testProfile);
        when(accountProfileMapper.toDto(any(AccountProfile.class))).thenReturn(testProfileDto);

        // Act
        AccountRegistrationResponse response = accountProfileService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(testProfileDto, response.accountProfile());

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verify(fileServiceApi, times(1)).uploadAccountPicture(eq(mockAvatarFile));
        verify(departmentService, times(1)).getById(TEST_DEPARTMENT_ID);
        verify(accountProfileRepository, times(1)).save(accountProfileCaptor.capture());
        verify(accountProfileMapper, times(1)).toDto(eq(testProfile));

        AccountProfile capturedProfile = accountProfileCaptor.getValue();
        assertEquals(TEST_EMAIL, capturedProfile.getEmail());
        assertEquals(PROFILE_FIRST_NAME, capturedProfile.getFirstName());
        assertEquals(PROFILE_LAST_NAME, capturedProfile.getLastName());
        assertEquals(PROFILE_MIDDLE_NAME, capturedProfile.getMiddleName());
        assertEquals(TEST_FILE_NAME, capturedProfile.getAvatar());
        assertEquals(testDepartment, capturedProfile.getDepartment());
        assertEquals(testSpecialty.getCodeName(), capturedProfile.getSpecialty().getCodeName());
    }

    @Test
    @DisplayName("Should throw AccountException when email already exists")
    void should_ThrowAccountException_When_AccountWithSuchEmailExists() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, never()).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verifyNoMoreInteractions(accountAuthServiceApi, fileServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
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
        verifyNoMoreInteractions(accountAuthServiceApi, fileServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
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
        verifyNoMoreInteractions(fileServiceApi, departmentService, accountProfileRepository, accountProfileMapper);
    }

    @Test
    @DisplayName("Should throw AccountException when file upload fails")
    void should_ThrowAccountExceptionWhen_AvatarUploadFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(fileServiceApi.uploadAccountPicture(mockAvatarFile))
                .thenThrow(new AccountException("File upload failed"));

        assertThrows(AccountException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verify(fileServiceApi, times(1)).uploadAccountPicture(eq(mockAvatarFile));
        verifyNoMoreInteractions(departmentService, accountAuthServiceApi, fileServiceApi, accountProfileRepository, accountProfileMapper);
    }

    @Test
    @DisplayName("Should throw RuntimeException when saving account profile fails")
    void should_ThrowRuntimeException_When_SavingProfileFails() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(accountProfileRepository.save(any(AccountProfile.class)))
                .thenThrow(new RuntimeException("Database save failed"));

        assertThrows(RuntimeException.class, () -> accountProfileService.register(request));

        verify(accountProfileRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(departmentService, times(1)).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        verify(accountAuthServiceApi, times(1)).createAccount(eq(request));
        verify(fileServiceApi, times(1)).uploadAccountPicture(eq(mockAvatarFile));
        verify(departmentService, times(1)).getById(TEST_DEPARTMENT_ID);
        verify(accountProfileRepository, times(1)).save(any(AccountProfile.class));
        verifyNoMoreInteractions(accountProfileMapper);
    }

    @Test
    @DisplayName("Should correctly build registration response")
    void should_BuildRegistrationResponseCorrectly_When_InputDataIsValid() {
        when(accountProfileRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        doNothing().when(departmentService).validateAcademicUnitByIds(any(AcademicUnitsIds.class));
        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class))).thenReturn(AUTH_RESPONSE);
        when(departmentService.getById(TEST_DEPARTMENT_ID)).thenReturn(testDepartment);
        when(accountProfileRepository.save(any(AccountProfile.class))).thenReturn(testProfile);
        when(accountProfileMapper.toDto(any(AccountProfile.class))).thenReturn(testProfileDto);

        AccountRegistrationResponse response = accountProfileService.register(request);

        assertNotNull(response);
        assertEquals(testProfileDto, response.accountProfile());
        assertEquals("Verification email has been sent to: " + TEST_EMAIL, response.responseMessage());
    }
}
