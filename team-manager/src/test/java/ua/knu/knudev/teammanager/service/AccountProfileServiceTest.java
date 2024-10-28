//package ua.knu.knudev.teammanager.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
//import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;
//import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
//import ua.knu.knudev.knudevsecurityapi.response.AccountCreationResponse;
//import ua.knu.knudev.teammanager.domain.AccountProfile;
//import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
//import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
//import ua.knu.knudev.teammanagerapi.exception.AccountException;
//import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
////todo
//@ExtendWith(MockitoExtension.class)
//class AccountProfileServiceTest {
//
//    private static final String TEST_EMAIL = "testKnuDevEmail@knu.ua";
//    private static final String TEST_PASSWORD = "qwerty12345";
//    private static final Set<AccountRole> TEST_ROLES = Set.of(AccountRole.INTERN);
//
//    private static final AccountCreationRequest request = AccountCreationRequest.builder()
//            .email(TEST_EMAIL)
//            .password(TEST_PASSWORD)
//            .build();
//
//    private static final AccountCreationResponse response = AccountCreationResponse.builder()
//            .email(TEST_EMAIL)
//            .roles(TEST_ROLES)
//            .build();
//
//    private static final AccountProfile test = AccountProfile.builder()
//            .firstName(TEST_PASSWORD)
//            .middleName(TEST_PASSWORD)
//            .lastName(TEST_PASSWORD)
//            .build();
//
//    @Mock
//    private AccountAuthServiceApi accountAuthServiceApi;
//
//    @Mock
//    private AccountProfileRepository accountProfileRepository;
//
//    @Mock
//    private AccountProfileMapper accountProfileMapper;
//
//    @InjectMocks
//    private AccountProfileService accountProfileService;
//
//    @Test
//    void should_CreateAccount_When() {
//        //Arrange
//        when(accountAuthServiceApi.createAccount(request)).thenReturn(response);
//        when(accountProfileRepository.save(any())).thenReturn(test);
//
//        //Act
//        AccountRegistrationResponse createdAccount = accountProfileService.createAccount(request);
//
//        //Assert
//        verify(accountProfileRepository, times(1)).save(any(AccountProfile.class));
//        verify(accountProfileMapper, times(1)).toDto(any(AccountProfile.class));
//
//        assertEquals(createdAccount.accountProfile().email(), TEST_EMAIL);
//        assertEquals(createdAccount.accountProfile().roles(), TEST_ROLES);
//    }
//
//    @Test
//    void should_ThrowAccountProfileException_When_CreatingAccountWithExistingEmail() {
//        when(accountProfileRepository.existsByFirstName(TEST_EMAIL)).thenReturn(true);
//
//        assertThrows(AccountException.class, () -> accountProfileService.createAccount(request));
//    }
//}