package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import ua.knu.knudev.knudevsecurityapi.response.AccountCreationResponse;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountAuthService implements AccountAuthServiceApi {
    private final AccountAuthRepository accountAuthRepository;
    private final AccountAuthMapper accountAuthMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountCreationResponse createAccount(AccountCreationRequest creationRequest) {
        AccountAuth accountAuth = AccountAuth.builder()
                .email(creationRequest.email())
                .password(passwordEncoder.encode(creationRequest.password()))
                .roles(Set.of(AccountRole.INTERN))
                .nonLocked(true)
                .enabled(true)
                .build();

        AccountAuth savedAccount = accountAuthRepository.save(accountAuth);
        return AccountCreationResponse.builder()
                .email(savedAccount.getEmail())
                .roles(savedAccount.getRoles())
                .build();
    }

    public AccountAuthDto findByEmail(String email) {
        String errorMessage = String.format("Account with email %s does not exist", email);
        AccountAuth account = Optional.ofNullable(accountAuthRepository.findAccountAuthByEmail(email))
                .orElseThrow(() -> new AccountAuthException(errorMessage));
        return accountAuthMapper.toDto(account);
    }

    public void checkAccountValidity(AccountAuth account, String email) throws AuthenticationException {
        if (account == null) {
            String accDoesNotExistErrorMsg = String.format("Account with email %s does not exist", email);
            throw new UsernameNotFoundException(accDoesNotExistErrorMsg);
        }

        if (!account.isEnabled()) {
            String disabledAccMsg = "Your account is disabled. Please activate it via link on email: " + account.getEmail();
            throw new DisabledException(disabledAccMsg);
        }

        if (!account.isNonLocked()) {
            String disabledAccMsg = "Your account is locked. Please please contact support at EMAIL";
            throw new LockedException(disabledAccMsg);
        }
    }


}
