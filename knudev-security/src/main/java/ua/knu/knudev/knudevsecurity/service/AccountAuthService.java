package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.dto.AccountAuthDto;
import ua.knu.knudev.knudevsecurity.mapper.AccountAuthMapper;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;
import ua.knu.knudev.knudevsecurityapi.request.AccountAuthUpdateRequest;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

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
                .id(savedAccount.getId())
                .email(savedAccount.getEmail())
                .technicalRole(savedAccount.getTechnicalRole())
                .build();
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountAuthRepository.existsByEmail(email);
    }

    @Override
    public void update(AccountAuthUpdateRequest request) {
        AccountAuth account = getDomainById(request.accountId());

        updateField(request.email(), account::setEmail);
        updateField(request.technicalRole(), account::setTechnicalRole);

        accountAuthRepository.save(account);
    }

    private <T> void updateField(T newValue, Consumer<T> setter) {
        Optional.ofNullable(newValue).ifPresent(setter);
    }

    private AccountAuth getDomainById(UUID id) {
        return accountAuthRepository.findById(id)
                .orElseThrow(() -> new AccountAuthException("Account with id " + id + " not found"));
    }

    public AccountAuthDto getByEmail(String email) {
        String errorMessage = String.format("Account with email %s does not exist", email);
        AccountAuth account = getDomainByEmail(email)
                .orElseThrow(() -> new AccountAuthException(errorMessage));
        return accountAuthMapper.toDto(account);
    }

    public Optional<AccountAuth> getDomainByEmail(String email) {
        return Optional.ofNullable(accountAuthRepository.findAccountAuthByEmail(email));
    }

    private void setAccountAuthDefaults(AccountAuth accountAuth) {
        accountAuth.setTechnicalRole(AccountTechnicalRole.INTERN);
        accountAuth.setNonLocked(true);
        accountAuth.setEnabled(true);
    }

}
