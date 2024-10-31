//package ua.knu.knudev.teammanager.service;
//
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
//import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
//import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
//import ua.knu.knudev.knudevsecurityapi.response.AccountCreationResponse;
//import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;
//import ua.knu.knudev.teammanager.config.TeamManagerConfig;
//import ua.knu.knudev.teammanager.config.TestConfig;
//import ua.knu.knudev.teammanager.domain.AccountProfile;
//import ua.knu.knudev.teammanager.domain.Department;
//import ua.knu.knudev.teammanager.domain.Specialty;
//import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
//import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
//import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
//import ua.knu.knudev.teammanagerapi.exception.AccountException;
//import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
//
//import java.util.Set;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
////@SpringBootTest
////@SpringBootTest(classes = TeamManagerConfig.class)
//@ActiveProfiles("test")
//@Transactional
//
//@ContextConfiguration(classes = TeamManagerConfig.class)
//@ExtendWith({MockitoExtension.class, SpringExtension.class})
//@Import(TestConfig.class)
////@ExtendWith({MockitoExtension.class})
//public class AccountProfileServiceIntegrationTest {
//
//    @Autowired
//    private AccountProfileService accountProfileService;
//
//    @Autowired
//    private AccountProfileRepository accountProfileRepository;
//
//    @MockBean
//    private AccountAuthServiceApi accountAuthServiceApi;
//
//    @MockBean
//    private FileServiceApi fileServiceApi;
//
//    @Autowired
//    private DepartmentService departmentService;
//
//    @Autowired
//    private SpecialtyService specialtyService;
//
//    @Autowired
//    private AccountProfileMapper accountProfileMapper;
//
//    @Test
//    public void test() {
//
//    }
////    @BeforeEach
////    public void setup() {
////        // Initialize test data: Departments and Specialties
////        Department department = Department.builder()
////                .id(UUID.randomUUID())
////                .name("Engineering")
////                .build();
////
////        Specialty specialty = Specialty.builder()
////                .codeName(1.0) // Assuming codeName is a Double as per domain
////                .name("Software Engineering")
////                .build();
////
////        department.addSpecialty(specialty);
////
////        departmentService.create(department);
////    }
////
////    /**
////     * Test Case: Successful Registration
////     */
////    @Test
////    public void testRegister_Successful() {
////        // Arrange
////        MockMultipartFile mockAvatar = new MockMultipartFile(
////                "avatar",
////                "avatar.jpg",
////                "image/jpeg",
////                "Fake Image Content".getBytes()
////        );
////
////        AccountCreationRequest request = AccountCreationRequest.builder()
////                .email("user@example.com")
////                .firstName("John")
////                .lastName("Doe")
////                .middleName("M")
////                .departmentId(1L) // Adjust if Department ID is UUID
////                .specialtyId("Software") // Assuming specialtyId corresponds to codeName
////                .avatarFile(mockAvatar)
////                .build();
////
////        AccountCreationResponse authResponse = AccountCreationResponse.builder()
////                .accountId(100L)
////                .email("user@example.com")
////                .build();
////
////        when(accountAuthServiceApi.createAccount(any(AccountCreationRequest.class)))
////                .thenReturn(authResponse);
////
////        when(fileServiceApi.uploadFile(any(), any()))
////                .thenReturn("avatar.jpg");
////
////        // Act
////        AccountRegistrationResponse response = accountProfileService.register(request);
////
////        // Assert
////        assertNotNull(response);
////        assertEquals("Verification email has been sent to: user@example.com", response.getResponseMessage());
////
////        AccountProfile savedProfile = accountProfileRepository.findByEmail("user@example.com").orElse(null);
////        assertNotNull(savedProfile);
////        assertEquals("John", savedProfile.getFirstName());
////        assertEquals("Doe", savedProfile.getLastName());
////        assertEquals("M", savedProfile.getMiddleName());
////        assertEquals("avatar.jpg", savedProfile.getAvatar());
////        assertEquals("Engineering", savedProfile.getDepartment().getName());
////        assertEquals("Software Engineering", savedProfile.getSpecialty().getName());
////    }
////
////    /**
////     * Test Case: Registration with Existing Email
////     */
////    @Test
////    public void testRegister_EmailAlreadyExists() {
////        // Arrange
////        String existingEmail = "existing@example.com";
////
////        // Pre-save an account with the existing email
////        AccountProfile existingProfile = AccountProfile.builder()
////                .id(1)
////                .email(existingEmail)
////                .firstName("Existing")
////                .lastName("User")
////                .middleName("E")
////                .avatar("existing.jpg")
////                .accountRole(AccountRole.INTERN)
////                .department(departmentService.getById(1L))
////                .specialty(specialtyService.getByCodeName(1.0)) // Adjust as per specialtyId type
////                .build();
////        accountProfileRepository.save(existingProfile);
////
////        MockMultipartFile mockAvatar = new MockMultipartFile(
////                "avatar",
////                "avatar.jpg",
////                "image/jpeg",
////                "Fake Image Content".getBytes()
////        );
////
////        AccountCreationRequest request = AccountCreationRequest.builder()
////                .email(existingEmail)
////                .firstName("John")
////                .lastName("Doe")
////                .middleName("M")
////                .departmentId(1L)
////                .specialtyId("Software")
////                .avatarFile(mockAvatar)
////                .build();
////
////        // Act & Assert
////        AccountException exception = assertThrows(AccountException.class, () -> {
////            accountProfileService.register(request);
////        });
////
////        assertEquals("Account with email existing@example.com already exists", exception.getMessage());
////
////        // Verify that external services were not called
////        verify(accountAuthServiceApi, never()).createAccount(any());
////        verify(fileServiceApi, never()).uploadFile(any(), any());
////    }
////
////    /**
////     * Test Case: Registration with Invalid Department ID
////     */
////    @Test
////    public void testRegister_InvalidDepartmentId() {
////        // Arrange
////        long invalidDepartmentId = 999L; // Assuming this ID doesn't exist
////        MockMultipartFile mockAvatar = new MockMultipartFile(
////                "avatar",
////                "avatar.jpg",
////                "image/jpeg",
////                "Fake Image Content".getBytes()
////        );
////
////        AccountCreationRequest request = AccountCreationRequest.builder()
////                .email("invaliddept@example.com")
////                .firstName("Jane")
////                .lastName("Doe")
////                .middleName("J")
////                .departmentId(invalidDepartmentId)
////                .specialtyId("Software")
////                .avatarFile(mockAvatar)
////                .build();
////
////        // Mocking DepartmentService to throw an exception
////        doThrow(new AccountException("Invalid department ID: " + invalidDepartmentId))
////                .when(departmentService)
////                .validateDepartmentAndSpecialty(any(AcademicUnitsIds.class));
////
////        // Act & Assert
////        AccountException exception = assertThrows(AccountException.class, () -> {
////            accountProfileService.register(request);
////        });
////    }
//}
//
////        ass
