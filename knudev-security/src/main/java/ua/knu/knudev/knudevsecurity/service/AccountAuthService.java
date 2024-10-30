package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.dto.AccountAuthDto;
import ua.knu.knudev.knudevsecurity.mapper.AccountAuthMapper;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.constant.AccountRole;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountAuthService implements AccountAuthServiceApi {

    private final AccountAuthRepository accountAuthRepository;
    private final AccountAuthMapper accountAuthMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthAccountCreationResponse createAccount(AccountCreationRequest creationRequest) {
        AccountAuth accountAuth = AccountAuth.builder()
                .email(creationRequest.email())
                .password(passwordEncoder.encode(creationRequest.password()))
                .build();
        setAccountAuthDefaults(accountAuth);

        AccountAuth savedAccount = accountAuthRepository.save(accountAuth);
        return AuthAccountCreationResponse.builder()
                .email(savedAccount.getEmail())
                .roles(savedAccount.getRoles())
                .build();
    }

    public AccountAuthDto getByEmail(String email) {
        String errorMessage = String.format("Account with email %s does not exist", email);
        AccountAuth account = Optional.ofNullable(accountAuthRepository.findAccountAuthByEmail(email))
                .orElseThrow(() -> new AccountAuthException(errorMessage));
        return accountAuthMapper.toDto(account);
    }

    private void setAccountAuthDefaults(AccountAuth accountAuth) {
        accountAuth.setRoles(Set.of(AccountRole.INTERN));
        accountAuth.setNonLocked(true);
        accountAuth.setEnabled(true);
    }

}
