package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

@Service
@RequiredArgsConstructor
public class AccountProfileService implements AccountProfileApi {
    private final AccountProfileRepository accountProfileRepository;
    private final AccountProfileMapper accountProfileMapper;
    private final AccountAuthServiceApi accountAuthServiceApi;

    @Override
    public AccountProfileDto getByEmail(String email) {
        return null;
    }

    @Override
    public AccountRegistrationResponse createAccount(AccountCreationRequest accountCreationRequest) {
        if (accountExists(accountCreationRequest.email())) {
            throw new AccountException("Account already exists");
        }

        //todo
        AccountCreationResponse createdAuthAccount = accountAuthServiceApi.createAccount(accountCreationRequest);
        AccountProfile accountProfileToSave = AccountProfile.builder()
                .firstName(accountCreationRequest.email())
                .middleName(accountCreationRequest.email())
                .lastName(accountCreationRequest.email())
                .build();
        AccountProfile savedAccount = accountProfileRepository.save(accountProfileToSave);
        return AccountRegistrationResponse.builder()
                .accountProfile(accountProfileMapper.toDto(savedAccount))
                .responseMessage("Verification email has been sent to: " + accountCreationRequest.email())
                .build();
    }

    private boolean accountExists(String email) {
        return accountProfileRepository.existsByFirstName(email);
    }


}
