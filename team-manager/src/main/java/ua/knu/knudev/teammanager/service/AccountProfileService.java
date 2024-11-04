package ua.knu.knudev.teammanager.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.exception.FileException;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountProfileService implements AccountProfileApi {

    private final AccountProfileRepository accountProfileRepository;
    private final AccountAuthServiceApi accountAuthServiceApi;
    private final ImageServiceApi imageServiceApi;
    private final DepartmentService departmentService;

    @Override
    @Transactional
    public AccountRegistrationResponse register(@Valid AccountCreationRequest request) {
        validateEmailNotExists(request.email());
        departmentService.validateAcademicUnitByIds(request.academicUnitsIds());

        AuthAccountCreationResponse createdAuthAccount = accountAuthServiceApi.createAccount(request);
        String uploadFilename = uploadAvatar(request.avatarFile());

        AccountProfile accountProfileToSave = buildAccountProfile(request, uploadFilename, createdAuthAccount);
        AccountProfile savedAccount = accountProfileRepository.save(accountProfileToSave);

        return buildRegistrationResponse(savedAccount, request.email(), createdAuthAccount);
    }

    private String uploadAvatar(MultipartFile file) {
        try {
            boolean fileIsPresent = ObjectUtils.isNotEmpty(file) && ArrayUtils.getLength(file.getBytes()) != 0;
            if (fileIsPresent) {
                return imageServiceApi.uploadFile(file, ImageSubfolder.ACCOUNT_PICTURES);
            }
        } catch (IOException e) {
            throw new FileException("Error while reading the file.");
        }
        return null;
    }

    private void validateEmailNotExists(String email) {
        boolean accountProfileExists = accountProfileRepository.existsByEmail(email);
        if (accountProfileExists) {
            throw new AccountException(
                    String.format("Account with email %s already exists", email)
            );
        }

        boolean accountAuthExists = accountAuthServiceApi.existsByEmail(email);
        if (accountAuthExists) {
            log.error("AccountProfile does not exists, but AccountAuth does exist by email {}", email);
            throw new AccountException("Registration error happened. Please contact support.");
        }
    }

    private AccountProfile buildAccountProfile(AccountCreationRequest request,
                                               String uploadFilename,
                                               AuthAccountCreationResponse authAccount) {
        FullName reqFullName = request.fullName();
        AcademicUnitsIds reqAcademicUnitsIds = request.academicUnitsIds();

        Department department = departmentService.getById(reqAcademicUnitsIds.departmentId());
        Specialty specialty = department.getSpecialties().stream()
                .filter(s -> s.getCodeName().equals(reqAcademicUnitsIds.specialtyCodename()))
                .findAny()
                .orElseThrow(() -> new AccountException(
                        String.format("Specialty with id %s not found in department %s",
                                reqAcademicUnitsIds.specialtyCodename(),
                                reqAcademicUnitsIds.departmentId()
                        )
                ));

        return AccountProfile.builder()
                .email(authAccount.email())
                .firstName(reqFullName.firstName())
                .lastName(reqFullName.lastName())
                .middleName(reqFullName.middleName())
                .avatarFilename(uploadFilename)
                .department(department)
                .specialty(specialty)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    private AccountRegistrationResponse buildRegistrationResponse(AccountProfile savedAccount,
                                                                  String requestEmail,
                                                                  AuthAccountCreationResponse createdAuthAccount) {
        AccountProfileDto account = buildAccountDto(savedAccount, createdAuthAccount);
        return AccountRegistrationResponse.builder()
                .accountProfile(account)
                .responseMessage("Verification email has been sent to: " + requestEmail)
                .build();
    }

    private AccountProfileDto buildAccountDto(AccountProfile savedAccount, AuthAccountCreationResponse authAccount) {
        return AccountProfileDto.builder()
                .roles(authAccount.roles())
                .fullName(FullName.builder()
                        .firstName(savedAccount.getFirstName())
                        .lastName(savedAccount.getLastName())
                        .middleName(savedAccount.getMiddleName())
                        .build())
                .avatarFilename(savedAccount.getAvatarFilename())
                .email(savedAccount.getEmail())
                .academicUnitsIds(AcademicUnitsIds.builder()
                        .departmentId(savedAccount.getDepartment().getId())
                        .specialtyCodename(savedAccount.getSpecialty().getCodeName())
                        .build())
                .build();
    }
}
