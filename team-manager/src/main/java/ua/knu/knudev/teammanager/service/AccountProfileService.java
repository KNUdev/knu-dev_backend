package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

@Service
@RequiredArgsConstructor
public class AccountProfileService implements AccountProfileApi {

    private final AccountProfileRepository accountProfileRepository;
    private final AccountProfileMapper accountProfileMapper;
    private final AccountAuthServiceApi accountAuthServiceApi;
    private final FileServiceApi fileServiceApi;
    private final DepartmentService departmentService;

    @Override
    @Transactional
    public AccountRegistrationResponse register(AccountCreationRequest request) {
        validateEmailNotExists(request.email());
        departmentService.validateAcademicUnitExistence(request.academicUnitsIds());

        AuthAccountCreationResponse createdAuthAccount = accountAuthServiceApi.createAccount(request);
        String uploadFilename = fileServiceApi.uploadAccountPicture(request.avatarFile());

        AccountProfile accountProfileToSave = buildAccountProfile(request, uploadFilename, createdAuthAccount);
        AccountProfile savedAccount = accountProfileRepository.save(accountProfileToSave);

        return buildRegistrationResponse(savedAccount, request.email());
    }

    private void validateEmailNotExists(String email) {
        if (accountProfileRepository.existsByEmail(email)) {
            throw new AccountException(
                    String.format("Account with email %s already exists", email)
            );
        }
    }

    private AccountProfile buildAccountProfile(AccountCreationRequest request,
                                               String uploadFilename,
                                               AuthAccountCreationResponse authAccount) {
        FullName reqFullName = request.fullName();
        AcademicUnitsIds reqAcademicUnitsIds = request.academicUnitsIds();

        Department department = departmentService.getById(reqAcademicUnitsIds.departmentId());
        Specialty specialty = department.getSpecialties().stream()
                .filter(s -> s.getCodeName().equals(reqAcademicUnitsIds.specialtyId()))
                .findAny()
                .orElseThrow(() -> new AccountException(
                        String.format("Specialty with id %s not found in department %s",
                                reqAcademicUnitsIds.specialtyId(),
                                reqAcademicUnitsIds.departmentId()
                        )
                ));

        return AccountProfile.builder()
                .email(authAccount.email())
                .firstName(reqFullName.firstName())
                .lastName(reqFullName.lastName())
                .middleName(reqFullName.middleName())
                .avatar(uploadFilename)
                .department(department)
                .specialty(specialty)
                .build();
    }

    private AccountRegistrationResponse buildRegistrationResponse(AccountProfile savedAccount,
                                                                  String requestEmail) {
        AccountProfileDto account = accountProfileMapper.toDto(savedAccount);
        return AccountRegistrationResponse.builder()
                .accountProfile(account)
                .responseMessage("Verification email has been sent to: " + requestEmail)
                .build();
    }
}
