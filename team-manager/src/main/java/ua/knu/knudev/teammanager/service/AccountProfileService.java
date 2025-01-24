package ua.knu.knudev.teammanager.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
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
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.constant.AccountsCriteriaFilterOption;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.AccountSearchCriteria;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountProfileService implements AccountProfileApi {

    private final AccountProfileRepository accountProfileRepository;
    private final AccountAuthServiceApi accountAuthServiceApi;
    private final ImageServiceApi imageServiceApi;
    private final DepartmentService departmentService;
    private final AccountProfileMapper accountProfileMapper;

    @Override
    @Transactional
    public AccountRegistrationResponse register(@Valid AccountCreationRequest request) {
        validateEmailNotExists(request.email());
        departmentService.validateAcademicUnitExistence(request.departmentId(), request.specialtyCodename());

        AuthAccountCreationResponse createdAuthAccount = accountAuthServiceApi.createAccount(request);
        String uploadFilename = uploadImageAvatar(request.avatarFile());

        AccountProfile accountProfileToSave = buildAccountProfile(request, uploadFilename, createdAuthAccount);
        AccountProfile savedAccount = accountProfileRepository.save(accountProfileToSave);

        return buildRegistrationResponse(savedAccount, request.email(), createdAuthAccount);
    }

    @Override
    public AccountProfileDto getById(UUID id) {
        AccountProfile account = getDomainById(id);
        return accountProfileMapper.toDto(account);
    }

    @Override
    public AccountProfileDto getByEmail(String email) {
        AccountProfile account = accountProfileRepository.findByEmail(email)
                .orElseThrow(() -> new AccountException(
                        String.format("Account with email %s does not exist", email)
                ));
        return accountProfileMapper.toDto(account);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountProfileRepository.existsByEmail(email);
    }

    @Override
    public void assertEmailExists(String email) throws AccountException {
        boolean emailExists = existsByEmail(email);
        if (emailExists) {
            throw new AccountException(
                    String.format("Account with email %s already exists", email)
            );
        }
    }

    @Override
    public Page<AccountProfileDto> findAllBySearchQuery(AccountSearchCriteria accountSearchCriteria, Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Map<AccountsCriteriaFilterOption, Object> filtersMap = buildAccountsFiltersMap(accountSearchCriteria);
        Page<AccountProfile> searchedAccountsPage = accountProfileRepository.findAllAccountsByFilters(filtersMap, paging);
        return searchedAccountsPage.map(accountProfileMapper::toDto);
    }

    public AccountProfile getDomainById(UUID id) {
        return accountProfileRepository.findById(id)
                .orElseThrow(() -> new AccountException(
                        String.format("Account with id %s does not exist", id)
                ));
    }

    private void validateEmailNotExists(String email) {
        assertEmailExists(email);

        boolean accountAuthExists = accountAuthServiceApi.existsByEmail(email);
        if (accountAuthExists) {
            log.error("AccountProfile does not exists, but AccountAuth does exist by email {}", email);
            throw new AccountException("Registration error happened. Please contact support.");
        }
    }

    private AccountProfile buildAccountProfile(AccountCreationRequest request,
                                               String uploadFilename,
                                               AuthAccountCreationResponse authAccount) {
        UUID departmentId = request.departmentId();
        Double specialtyCodename = request.specialtyCodename();

        Department department = departmentService.getById(departmentId);
        Specialty specialty = department.getSpecialties().stream()
                .filter(s -> s.getCodeName().equals(specialtyCodename))
                .findAny()
                .orElseThrow(() -> new AccountException(
                        String.format("Specialty with id %s not found in department %s",
                                specialtyCodename, departmentId
                        )
                ));

        return AccountProfile.builder()
                .id(authAccount.id())
                .email(authAccount.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .avatarFilename(uploadFilename)
                .department(department)
                .specialty(specialty)
                .registrationDate(LocalDateTime.now())
                .yearOfStudyOnRegistration(request.yearOfStudy())
                .expertise(request.expertise())
                .technicalRole(authAccount.technicalRole())
                .unit(KNUdevUnit.PRECAMPUS)
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
                .technicalRole(authAccount.technicalRole())
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

    protected Map<AccountsCriteriaFilterOption, Object> buildAccountsFiltersMap(AccountSearchCriteria accountSearchCriteria) {

        Map<AccountsCriteriaFilterOption, Object> filters = new EnumMap<>(AccountsCriteriaFilterOption.class);
        addFilter2Map(filters, AccountsCriteriaFilterOption.USER_INITIALS_OR_EMAIL, accountSearchCriteria.searchQuery());
        addFilter2Map(filters, AccountsCriteriaFilterOption.REGISTERED_AT, accountSearchCriteria.registeredAt());
        addFilter2Map(filters, AccountsCriteriaFilterOption.REGISTERED_BEFORE, accountSearchCriteria.registeredBefore());
        addFilter2Map(filters, AccountsCriteriaFilterOption.UNIT, accountSearchCriteria.unit());
        addFilter2Map(filters, AccountsCriteriaFilterOption.EXPERTISE, accountSearchCriteria.expertise());
        addFilter2Map(filters, AccountsCriteriaFilterOption.DEPARTMENT, accountSearchCriteria.departmentId());
        addFilter2Map(filters, AccountsCriteriaFilterOption.SPECIALTY, accountSearchCriteria.specialtyCodeName());
        addFilter2Map(filters, AccountsCriteriaFilterOption.UNIVERSITY_STUDY_YEAR, accountSearchCriteria.universityStudyYear());
        addFilter2Map(filters, AccountsCriteriaFilterOption.RECRUITMENT_ORIGIN, accountSearchCriteria.recruitmentId());
        addFilter2Map(filters, AccountsCriteriaFilterOption.TECHNICAL_ROLE, accountSearchCriteria.technicalRole());

        return filters;
    }

    private void addFilter2Map(Map<AccountsCriteriaFilterOption, Object> filters, AccountsCriteriaFilterOption option, Object value) {
        if (value != null) {
            filters.put(option, value);
        }
    }

    private String uploadImageAvatar(MultipartFile avatarFile) {
        if (ObjectUtils.isEmpty(avatarFile)) {
            return null;
        }
        return imageServiceApi.uploadFile(avatarFile, ImageSubfolder.ACCOUNT_PICTURES);
    }
}
