package ua.knu.knudev.teammanager.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevsecurityapi.api.AccountAuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.request.AccountAuthUpdateRequest;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthAccountCreationResponse;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.MultiLanguageFieldMapper;
import ua.knu.knudev.teammanager.mapper.ShortDepartmentMapper;
import ua.knu.knudev.teammanager.mapper.SpecialtyMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.service.api.GithubManagementApi;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.constant.AccountsCriteriaFilterOption;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.AccountSearchCriteria;
import ua.knu.knudev.teammanagerapi.dto.ShortAccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.request.AccountUpdateRequest;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
import ua.knu.knudev.teammanagerapi.response.GetAccountByIdResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountProfileService implements AccountProfileApi {

    private final AccountProfileRepository accountProfileRepository;
    private final AccountAuthServiceApi accountAuthServiceApi;
    private final ImageServiceApi imageServiceApi;
    private final DepartmentService departmentService;
    private final SpecialtyService specialtyService;
    private final ShortDepartmentMapper shortDepartmentMapper;
    private final MultiLanguageFieldMapper multiLanguageFieldMapper;
    private final SpecialtyMapper specialtyMapper;
    private final GithubManagementApi gitHubManagementApi;
    private final Environment environment;

    @Override
    @Transactional
    public AccountRegistrationResponse register(@Valid AccountCreationRequest request) {
        BindException bindException = new BindException(request, "createUserRequest");

        validateEmailNotExists(bindException, request.email());
        departmentService.validateAcademicUnitExistence(request.departmentId(), request.specialtyCodename());
        validateGithubUsername(bindException, request);
        throwValidationExceptionIfExists(bindException);

        AuthAccountCreationResponse createdAuthAccount = accountAuthServiceApi.createAccount(request);
        String uploadedAvatarFilename = uploadAccountImage(request.avatarFile(), ImageSubfolder.ACCOUNT_AVATARS);
        String uploadedBannerFilename = uploadAccountImage(request.bannerFile(), ImageSubfolder.ACCOUNT_BANNERS);

        AccountProfile accountProfileToSave = buildAccountProfile(request,
                uploadedAvatarFilename,
                uploadedBannerFilename,
                createdAuthAccount
        );
        AccountProfile savedAccount = accountProfileRepository.save(accountProfileToSave);

        return buildRegistrationResponse(savedAccount, request.email(), createdAuthAccount);
    }

    @Override
    public AccountProfileDto getById(UUID id) {
        AccountProfile account = getDomainById(id);
        return mapAccountProfileToDto(account);
    }

    @SneakyThrows
    private void throwValidationExceptionIfExists(BindException bindException) {
        if (bindException.hasErrors()) {

            MethodParameter methodParam;
            try {
                methodParam = new MethodParameter(
                        this.getClass().getDeclaredMethod("register", AccountCreationRequest.class),
                        0
                );
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            throw new MethodArgumentNotValidException(methodParam, bindException);
        }
    }

    @Override
    public GetAccountByIdResponse getById(String accountProfileId) {
//        UUID accountId = UUID.fromString(accountProfileId);
        UUID accountId;
        //todo refactor
        try {
            accountId = UUID.fromString(accountProfileId);
        } catch (IllegalArgumentException e) {
            throw new AccountException("Invalid account profile id: " + accountProfileId, HttpStatus.NOT_FOUND);
        }

        AccountProfile accountProfile = getDomainById(accountId);
        String avatarPath = StringUtils.isNotEmpty(accountProfile.getAvatarFilename())
                ? (imageServiceApi.getPathByFilename(
                accountProfile.getAvatarFilename(),
                ImageSubfolder.ACCOUNT_AVATARS)
        ) : null;
        String bannerPath = StringUtils.isNotEmpty(accountProfile.getBannerFilename())
                ? (imageServiceApi.getPathByFilename(
                accountProfile.getBannerFilename(),
                ImageSubfolder.ACCOUNT_BANNERS)
        ) : null;

        String fullName = accountProfile.getFirstName() + " " + accountProfile.getLastName() +
                " " + accountProfile.getMiddleName();
        return GetAccountByIdResponse.builder()
                .fullName(fullName)
                .githubAccountUsername(accountProfile.getGithubAccountUsername())
                .email(accountProfile.getEmail())
                .technicalRole(accountProfile.getTechnicalRole())
                .expertise(accountProfile.getExpertise())
                .department(shortDepartmentMapper.toDto(accountProfile.getDepartment()))
                .specialty(specialtyMapper.toShortDto(accountProfile.getSpecialty()))
                .registeredAt(accountProfile.getRegistrationDate())
                .avatarImageUrl(avatarPath)
                .bannerImageUrl(bannerPath)
                .projects(List.of(
//                        ShortProjectDto.builder()
//                                .avatarFilename("")
////                                .tags()
//                                .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
//                                .name(multiLangFieldMapper.toDto(accountProfile.getSpecialty().getName()))
//                                .status(ProjectStatus.UNDER_DEVELOPMENT)
//                                .build(),
//                        ShortProjectDto.builder()
//                                .avatarFilename("")
////                                .tags()
//                                .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
//                                .name(multiLangFieldMapper.toDto(accountProfile.getSpecialty().getName()))
//                                .status(ProjectStatus.UNDER_DEVELOPMENT)
//                                .build(),
//                        ShortProjectDto.builder()
//                                .avatarFilename("")
////                                .tags()
//                                .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
//                                .name(multiLangFieldMapper.toDto(accountProfile.getSpecialty().getName()))
//                                .status(ProjectStatus.UNDER_DEVELOPMENT)
//                                .build()

                ))
                .completedEducationPrograms(List.of(
//                        EducationProgramSummaryDto.builder()
//                                .name(new MultiLanguageFieldDto("Giga Program", "Супер програма"))
//                                .programExpertise(Expertise.BACKEND)
//                                .durationInDays(365)
//                                .totalTasks(250)
//                                .totalTests(100)
//                                .build(),
//                        EducationProgramSummaryDto.builder()
//                                .name(new MultiLanguageFieldDto("Giga Program", "Супер програма"))
//                                .programExpertise(Expertise.BACKEND)
//                                .durationInDays(365)
//                                .totalTasks(250)
//                                .totalTests(100)
//                                .build()
                ))
                .build();
    }


    @Override
    public AccountProfileDto getByEmail(String email) {
        AccountProfile account = accountProfileRepository.findByEmail(email)
                .orElseThrow(() -> new AccountException(
                        String.format("Account with email %s does not exist", email)
                ));
        return mapAccountProfileToDto(account);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountProfileRepository.existsByEmail(email);
    }

    private boolean assertEmailExists(BindException bindException, String email) {
        boolean emailExists = existsByEmail(email);
        if (emailExists) {
            MultiLanguageFieldDto error = MultiLanguageFieldDto.builder()
                    .en(String.format("Account with email %s already exists", email))
                    .uk(String.format("Аккаунт з імейлом %s вже існує", email))
                    .build();
            addValidationError(bindException, "email", error);
            return true;
        }
        return false;
    }

    @Override
    //todo refactor two N + 1 problems (department id and specialty id)
    public Page<AccountProfileDto> findAllBySearchQuery(AccountSearchCriteria accountSearchCriteria, Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Map<AccountsCriteriaFilterOption, Object> filtersMap = buildAccountsFiltersMap(accountSearchCriteria);
        Page<AccountProfile> searchedAccountsPage = accountProfileRepository.findAllAccountsByFilters(filtersMap, paging);

        return searchedAccountsPage.map(accountProfile -> {
            String avatarFilename = accountProfile.getAvatarFilename();
            String bannerFilename = accountProfile.getBannerFilename();

            String avatarUrl = StringUtils.isNotEmpty(avatarFilename) ? imageServiceApi.getPathByFilename(
                    avatarFilename, ImageSubfolder.ACCOUNT_AVATARS
            ) : null;
            String bannerUrl = StringUtils.isNotEmpty(bannerFilename) ? imageServiceApi.getPathByFilename(
                    bannerFilename, ImageSubfolder.ACCOUNT_BANNERS
            ) : null;

            MultiLanguageFieldDto departmentName = multiLanguageFieldMapper.toDto(accountProfile.getDepartment().getName());
            MultiLanguageFieldDto specialtyName = multiLanguageFieldMapper.toDto(accountProfile.getSpecialty().getName());
            return AccountProfileDto.builder()
                    .id(accountProfile.getId())
                    .fullName(new FullName(
                            accountProfile.getFirstName(),
                            accountProfile.getLastName(),
                            accountProfile.getMiddleName())
                    )
                    .email(accountProfile.getEmail())
                    .technicalRole(accountProfile.getTechnicalRole())
                    .avatarFilename(avatarUrl)
                    .bannerFilename(bannerUrl)
                    .unit(accountProfile.getUnit())
                    .academicUnitsIds(AcademicUnitsIds.builder()
                            .departmentId(accountProfile.getDepartment().getId())
                            .specialtyCodename(accountProfile.getSpecialty().getCodeName())
                            .build())
                    .githubAccountUsername(accountProfile.getGithubAccountUsername())
                    .expertise(accountProfile.getExpertise())
                    .universityStudyYear(accountProfile.getCurrentYearOfStudy())
                    .lastRoleUpdateDate(accountProfile.getLastRoleUpdateDate())
                    .registeredAt(accountProfile.getRegistrationDate())
                    .departmentName(departmentName)
                    .specialtyName(specialtyName)
                    .build();
        });
    }

    @Override
    public Page<ShortAccountProfileDto> findAllTeamMembers(Integer pageNumber, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        Page<AccountProfile> teamMembersPage = accountProfileRepository.findAllByUnit(KNUdevUnit.CAMPUS, paging);

        return teamMembersPage.map(accountProfile ->
                ShortAccountProfileDto.builder()
                        .name(new FullName(
                                accountProfile.getFirstName(),
                                accountProfile.getLastName(),
                                accountProfile.getMiddleName())
                        )
                        .githubAccountUsername(accountProfile.getGithubAccountUsername())
                        .avatarFilename(accountProfile.getAvatarFilename())
                        .accountTechnicalRole(accountProfile.getTechnicalRole())
                        .build()
        );
    }

    @Override
    public String updateAvatar(UUID accountProfileId, MultipartFile newAvatar) {
        AccountProfile account = getDomainById(accountProfileId);

        String oldAvatarFilename = account.getAvatarFilename();
        String newAvatarFilename = imageServiceApi.updateByFilename(oldAvatarFilename, newAvatar,
                ImageSubfolder.ACCOUNT_AVATARS);

        account.setAvatarFilename(newAvatarFilename);
        accountProfileRepository.save(account);

        return imageServiceApi.getPathByFilename(newAvatarFilename, ImageSubfolder.ACCOUNT_AVATARS);
    }

    @Override
    @Transactional
    public void removeAvatar(UUID accountProfileId) {
        AccountProfile account = getDomainById(accountProfileId);
        String avatarFilename = account.getAvatarFilename();
        if (StringUtils.isEmpty(avatarFilename)) {
            throw new AccountException(
                    String.format("Account with id: %s does not have avatar", accountProfileId)
            );
        }
        imageServiceApi.removeByFilename(avatarFilename, ImageSubfolder.ACCOUNT_AVATARS);
        account.setAvatarFilename(null);
        accountProfileRepository.save(account);
    }

    @Override
    @Transactional
    public String updateBanner(UUID accountProfileId, MultipartFile newBanner) {
        AccountProfile account = getDomainById(accountProfileId);

        String oldBannerFilename = account.getBannerFilename();
        String newBannerFilename = imageServiceApi.updateByFilename(oldBannerFilename, newBanner,
                ImageSubfolder.ACCOUNT_BANNERS);

        account.setBannerFilename(newBannerFilename);
        accountProfileRepository.save(account);

        return imageServiceApi.getPathByFilename(newBannerFilename, ImageSubfolder.ACCOUNT_BANNERS);
    }

    @Override
    public void removeBanner(UUID accountProfileId) {
        AccountProfile account = getDomainById(accountProfileId);
        String bannerFilename = account.getBannerFilename();
        if (StringUtils.isEmpty(bannerFilename)) {
            throw new AccountException(
                    String.format("Account with id: %s does not have banner", accountProfileId)
            );
        }
        imageServiceApi.removeByFilename(bannerFilename, ImageSubfolder.ACCOUNT_BANNERS);
        account.setBannerFilename(null);
        accountProfileRepository.save(account);
    }

    @Override
    public AccountProfileDto getByGithubUsername(String githubUsername) {
        AccountProfile accountProfile = accountProfileRepository.findAccountProfileByGithubAccountUsername(githubUsername)
                .orElseThrow(() -> new AccountException("Account with githubUsername " + githubUsername + " not found!"));
        return mapAccountProfileToDto(accountProfile);
    }

    @Override
    @Transactional
    public AccountProfileDto update(AccountUpdateRequest request) {
        AccountProfile accountProfile = getDomainById(request.getAccountId());
        String email = request.getEmail();
        String gitHubAccountUsername = request.getGitHubAccountUsername();

        checkIfGithubUsernameIsInvalid(gitHubAccountUsername);
        checkIfEmailIsInvalid(email);

        if (request.getDeleteAvatar() != null && request.getDeleteAvatar()) {
            removeAvatar(accountProfile.getId());
        }
        if (request.getDeleteBanner() != null && request.getDeleteBanner()) {
            removeBanner(accountProfile.getId());
        }

        updateSpecialtyAndDepartmentIfItsValid(request.getSpecialtyCodename(), request.getDepartmentId(), accountProfile);
        updateField(request.getFirstName(), accountProfile::setFirstName);
        updateField(request.getLastName(), accountProfile::setLastName);
        updateField(request.getMiddleName(), accountProfile::setMiddleName);
        updateField(email, accountProfile::setEmail);
        updateField(request.getYearOfStudyOnRegistration(), accountProfile::setYearOfStudyOnRegistration);
        updateField(request.getUnit(), accountProfile::setUnit);
        updateField(gitHubAccountUsername, accountProfile::setGithubAccountUsername);
        updateField(request.getTechnicalRole(), accountProfile::setTechnicalRole);
        updateField(request.getExpertise(), accountProfile::setExpertise);

        if (request.getTechnicalRole() != null) {
            accountProfile.setLastRoleUpdateDate(LocalDateTime.now());
        }

        accountAuthServiceApi.update(new AccountAuthUpdateRequest(request.getAccountId(), email, request.getTechnicalRole()));

        AccountProfile updatedAccount = accountProfileRepository.save(accountProfile);
        return mapAccountProfileToDto(updatedAccount);
    }

    private <T> void updateField(T newValue, Consumer<T> setter) {
        Optional.ofNullable(newValue).ifPresent(setter);
    }

    private void checkIfGithubUsernameIsInvalid(String gitHubAccountUsername) {
        if (gitHubAccountUsername != null && !gitHubManagementApi.existsByUsername(gitHubAccountUsername)) {
            throw new AccountException("Invalid git username :" + gitHubAccountUsername);
        }
    }

    private void checkIfEmailIsInvalid(String email) {
        if (email != null && !email.matches("^[\\w.-]+@knu\\.ua$")) {
            throw new AccountException("Invalid email address:" + email);
        }
    }

    private void updateSpecialtyAndDepartmentIfItsValid(Double specialtyCodename, UUID departmentId, AccountProfile accountProfile) {
        boolean isSpecialtyUpdated = false;
        if (departmentId != null) {
            Department department = departmentService.getById(departmentId);
            departmentService.validateAcademicUnitExistence(departmentId,
                    specialtyCodename != null ? specialtyCodename : accountProfile.getSpecialty().getCodeName());

            if (specialtyCodename != null) {
                Specialty specialty = specialtyService.getByCodeName(specialtyCodename);
                accountProfile.setSpecialty(specialty);
                isSpecialtyUpdated = true;
            }

            accountProfile.setDepartment(department);
        }

        if (!isSpecialtyUpdated && specialtyCodename != null) {
            Specialty specialty = specialtyService.getByCodeName(specialtyCodename);
            departmentService.validateAcademicUnitExistence(accountProfile.getDepartment().getId(), specialtyCodename);
            accountProfile.setSpecialty(specialty);
        }

    }

    private AccountProfileDto mapAccountProfileToDto(AccountProfile accountProfile) {
        MultiLanguageFieldDto departmentName = multiLanguageFieldMapper.toDto(accountProfile.getDepartment().getName());
        MultiLanguageFieldDto specialtyName = multiLanguageFieldMapper.toDto(accountProfile.getSpecialty().getName());

        return AccountProfileDto.builder()
                .id(accountProfile.getId())
                .email(accountProfile.getEmail())
                .technicalRole(accountProfile.getTechnicalRole())
                .fullName(new FullName(
                        accountProfile.getFirstName(),
                        accountProfile.getLastName(),
                        accountProfile.getMiddleName()))
                .academicUnitsIds(new AcademicUnitsIds(
                        accountProfile.getDepartment().getId(),
                        accountProfile.getSpecialty().getCodeName()))
                .avatarFilename(accountProfile.getAvatarFilename())
                .bannerFilename(accountProfile.getBannerFilename())
                .githubAccountUsername(accountProfile.getGithubAccountUsername())
                .expertise(accountProfile.getExpertise())
                .registeredAt(accountProfile.getRegistrationDate())
                .universityStudyYear(accountProfile.getCurrentYearOfStudy())
                .lastRoleUpdateDate(accountProfile.getLastRoleUpdateDate())
                .departmentName(departmentName)
                .specialtyName(specialtyName)
                .unit(accountProfile.getUnit())
                .build();
    }

    public AccountProfile getDomainById(UUID id) {
        return accountProfileRepository.findById(id)
                .orElseThrow(() -> new AccountException(
                        String.format("Account with id %s does not exist", id),
                        HttpStatus.NOT_FOUND
                ));
    }

    @SneakyThrows
    private void validateEmailNotExists(BindException bindException, String email) {
        boolean accountProfileExists = assertEmailExists(bindException, email);
        if (accountProfileExists) {
            return;
        }

        boolean accountAuthExists = accountAuthServiceApi.existsByEmail(email);
        if (accountAuthExists) {
            log.error("AccountProfile does not exists, but AccountAuth does exist by email {}", email);
            throw new AccountException("Registration error happened. Please contact support.");
        }
    }

    private AccountProfile buildAccountProfile(AccountCreationRequest request,
                                               String uploadedAvatarFilename,
                                               String uploadedBannerFilename,
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
                .githubAccountUsername(request.githubAccountUsername())
                .avatarFilename(uploadedAvatarFilename)
                .bannerFilename(uploadedBannerFilename)
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
                .githubAccountUsername(savedAccount.getGithubAccountUsername())
                .avatarFilename(savedAccount.getAvatarFilename())
                .bannerFilename(savedAccount.getBannerFilename())
                .email(savedAccount.getEmail())
                .academicUnitsIds(AcademicUnitsIds.builder()
                        .departmentId(savedAccount.getDepartment().getId())
                        .specialtyCodename(savedAccount.getSpecialty().getCodeName())
                        .build())
                .build();
    }

    protected Map<AccountsCriteriaFilterOption, Object> buildAccountsFiltersMap(AccountSearchCriteria accountSearchCriteria) {

        Map<AccountsCriteriaFilterOption, Object> filters = new EnumMap<>(AccountsCriteriaFilterOption.class);
        addFilter2Map(filters, AccountsCriteriaFilterOption.USER_INITIALS_OR_GITHUB_OR_EMAIL, accountSearchCriteria.searchQuery());
        addFilter2Map(filters, AccountsCriteriaFilterOption.REGISTERED_AT, accountSearchCriteria.registeredAt());
        addFilter2Map(filters, AccountsCriteriaFilterOption.REGISTERED_BEFORE, accountSearchCriteria.registeredBefore());
        addFilter2Map(filters, AccountsCriteriaFilterOption.UNIT, accountSearchCriteria.unit());
        addFilter2Map(filters, AccountsCriteriaFilterOption.EXPERTISE, accountSearchCriteria.expertise());
        addFilter2Map(filters, AccountsCriteriaFilterOption.DEPARTMENT, accountSearchCriteria.departmentId());
        addFilter2Map(filters, AccountsCriteriaFilterOption.SPECIALTY, accountSearchCriteria.specialtyCodeName());
        addFilter2Map(filters, AccountsCriteriaFilterOption.UNIVERSITY_STUDY_YEARS, accountSearchCriteria.universityStudyYear());
        addFilter2Map(filters, AccountsCriteriaFilterOption.RECRUITMENT_ORIGIN, accountSearchCriteria.recruitmentId());
        addFilter2Map(filters, AccountsCriteriaFilterOption.TECHNICAL_ROLE, accountSearchCriteria.technicalRole());

        return filters;
    }

    private void addFilter2Map(Map<AccountsCriteriaFilterOption, Object> filters, AccountsCriteriaFilterOption option, Object value) {
        if (value != null) {
            filters.put(option, value);
        }
    }

    private String uploadAccountImage(MultipartFile avatarFile, ImageSubfolder subfolder) {
        if (ObjectUtils.isEmpty(avatarFile)) {
            return null;
        }
        return imageServiceApi.uploadFile(avatarFile, subfolder);
    }

    @SneakyThrows
    private void validateGithubUsername(BindException bindException, AccountCreationRequest request) {
        if (environment == null || Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        boolean validGithubUsername = gitHubManagementApi.existsByUsername(request.githubAccountUsername());
        if (!validGithubUsername) {
            MultiLanguageFieldDto multiLangError = MultiLanguageFieldDto.builder()
                    .en(String.format("There is no Github account with %s username", request.githubAccountUsername()))
                    .uk(String.format(
                            "Github профілю з іменем користувача %s не існує",
                            request.githubAccountUsername())
                    )
                    .build();
            addValidationError(bindException, "githubAccountUsername", multiLangError);
        }
    }

    private void addValidationError(BindException bindException,
                                    String fieldName,
                                    MultiLanguageFieldDto multiLangError) {

        bindException.addError(new FieldError(
                "createUserRequest",
                fieldName,
                multiLangError,
                false,
                null,
                null,
                multiLangError.getEn()
        ));
    }

    public List<AccountProfile> getAllAccountProfiles() {
        return accountProfileRepository.findAll();
    }

}
