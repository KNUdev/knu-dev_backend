package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanagerapi.api.RolePromotionTaskApi;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.RolePromotionConditions;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.mapper.RolePromotionConditionMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.RolePromotionConditionRepository;
import ua.knu.knudev.teammanager.repository.SubprojectAccountRepository;
import ua.knu.knudev.teammanager.service.promotionProcessor.DeveloperToPreMasterRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.MasterToTechLeadRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.PreMasterToMasterRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.PromotionRequirement;
import ua.knu.knudev.teammanagerapi.api.RolePromotionApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.AccountRolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.RolePromotionConditionException;
import ua.knu.knudev.teammanagerapi.exception.SubprojectAccountException;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionCreationRequest;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionUpdateRequest;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePromotionService implements RolePromotionApi {

    private final AccountProfileService accountProfileService;
    private final ProjectService projectService;
    private final SubprojectAccountRepository subprojectAccountRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final RolePromotionConditionRepository rolePromotionConditionRepository;
    private final AccountProfileMapper accountProfileMapper;
    private final RolePromotionConditionMapper rolePromotionConditionMapper;
    private final RolePromotionTaskApi rolePromotionTaskApi;
    private final SessionApi sessionApi;

    private final Map<AccountTechnicalRole, PromotionRequirement> requirementMap = Map.of(
            AccountTechnicalRole.DEVELOPER, new DeveloperToPreMasterRequirement(),
            AccountTechnicalRole.PREMASTER, new PreMasterToMasterRequirement(),
            AccountTechnicalRole.MASTER, new MasterToTechLeadRequirement()
    );

    @Override
    public RolePromotionConditionDto createRolePromotionConditions(RolePromotionConditionCreationRequest request) {
        if (request == null) {
            throw new RolePromotionConditionException("Role promotion request cannot be null!");
        }
        if (!rolePromotionConditionRepository.findAll().isEmpty()) {
            throw new RolePromotionConditionException("Role promotion condition already exists!");
        }

        RolePromotionConditions rolePromotionConditions = RolePromotionConditions.builder()
                .toPremasterProjectQuantity(request.toPremasterProjectQuantity())
                .toPremasterCommitsQuantity(request.toPremasterCommitsQuantity())
                .toMasterProjectQuantity(request.toMasterProjectQuantity())
                .toMasterCommitsQuantity(request.toMasterCommitsQuantity())
                .toMasterCreatedCampusTasksQuantity(request.toMasterCreatedCampusTasksQuantity())
                .toMasterMentoredSessionsQuantity(request.toMasterMentoredSessionsQuantity())
                .toTechLeadCreatedCampusTasksQuantity(request.toTechLeadCreatedCampusTasksQuantity())
                .toTechLeadCommitsQuantity(request.toTechLeadCommitsQuantity())
                .wasSupervisor(request.wasSupervisor())
                .wasArchitect(request.wasArchitect())
                .createdAt(LocalDateTime.now())
                .singleton(true)
                .build();

        rolePromotionConditions = rolePromotionConditionRepository.save(rolePromotionConditions);

        return rolePromotionConditionMapper.toDto(rolePromotionConditions);
    }

    @Override
    public RolePromotionConditionDto updateRolePromotionConditions(RolePromotionConditionUpdateRequest request) {
        if (request == null) {
            throw new RolePromotionConditionException("Role promotion request cannot be null!");
        }
        RolePromotionConditions rolePromotionConditions = rolePromotionConditionRepository.findById(request.id())
                .orElseThrow(() -> new RolePromotionConditionException("Role promotion condition with id: " + request.id() + " not found!"));

        rolePromotionConditions.setToPremasterProjectQuantity(getOrDefault(request.toPremasterProjectQuantity(),
                rolePromotionConditions.getToPremasterProjectQuantity()));
        rolePromotionConditions.setToPremasterCommitsQuantity(getOrDefault(request.toPremasterCommitsQuantity(),
                rolePromotionConditions.getToPremasterCommitsQuantity()));
        rolePromotionConditions.setToMasterProjectQuantity(getOrDefault(request.toMasterProjectQuantity(),
                rolePromotionConditions.getToMasterProjectQuantity()));
        rolePromotionConditions.setToMasterCommitsQuantity(getOrDefault(request.toMasterCommitsQuantity(),
                rolePromotionConditions.getToMasterCommitsQuantity()));
        rolePromotionConditions.setToMasterCreatedCampusTasksQuantity(getOrDefault(request.toMasterCreatedCampusTasksQuantity(),
                rolePromotionConditions.getToMasterCreatedCampusTasksQuantity()));
        rolePromotionConditions.setToMasterMentoredSessionsQuantity(getOrDefault(request.toMasterMentoredSessionsQuantity(),
                rolePromotionConditions.getToMasterMentoredSessionsQuantity()));
        rolePromotionConditions.setToTechLeadCreatedCampusTasksQuantity(getOrDefault(request.toTechLeadCreatedCampusTasksQuantity(),
                rolePromotionConditions.getToTechLeadCreatedCampusTasksQuantity()));
        rolePromotionConditions.setToTechLeadCommitsQuantity(getOrDefault(request.toTechLeadCommitsQuantity(),
                rolePromotionConditions.getToTechLeadCommitsQuantity()));
        rolePromotionConditions.setWasSupervisor(getOrDefault(request.wasSupervisor(),
                rolePromotionConditions.getWasSupervisor()));
        rolePromotionConditions.setWasArchitect(getOrDefault(request.wasArchitect(),
                rolePromotionConditions.getWasArchitect()));
        rolePromotionConditions.setUpdatedAt(LocalDateTime.now());

        rolePromotionConditions = rolePromotionConditionRepository.save(rolePromotionConditions);
        return rolePromotionConditionMapper.toDto(rolePromotionConditions);
    }

    @Override
    public void deleteRolePromotionConditions(UUID conditionId) {
        rolePromotionConditionRepository.deleteById(conditionId);
        log.info("Deleted role promotion condition with id: {}", conditionId);
    }

    @Override
    public RolePromotionConditionDto getRolePromotionConditions() {
        RolePromotionConditions rolePromotionConditions = rolePromotionConditionRepository.findAll().stream().findFirst().orElseThrow(
                () -> new RolePromotionConditionException("Role promotion condition not found!")
        );

        return rolePromotionConditionMapper.toDto(rolePromotionConditions);
    }

    @Override
    public RolePromotionCheckResponse checkOnRolePromotionAbility(UUID accountId) {
        AccountProfile accountProfile = accountProfileService.getDomainById(accountId);
        AccountRolePromotionConditions accountConditions = getRolePromotionConditions(accountId);
        PromotionRequirement promotionRequirement = requirementMap.get(accountProfile.getTechnicalRole());

        if (promotionRequirement != null) {
            RolePromotionConditions rolePromotionConditions = rolePromotionConditionRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(
                            () -> new RolePromotionConditionException("Role promotion condition not found!")
                    );

            RolePromotionConditionDto requiredConditions = rolePromotionConditionMapper.toDto(rolePromotionConditions);
            Map<String, Boolean> checkList = promotionRequirement.getCheckListMap(accountConditions, requiredConditions);
            boolean canPromote = promotionRequirement.canPromoteBy(accountConditions, requiredConditions);

            return RolePromotionCheckResponse.builder()
                    .checkList(checkList)
                    .canPromote(canPromote)
                    .build();
        }
        return RolePromotionCheckResponse.builder()
                .checkList(Map.of())
                .canPromote(false)
                .build();
    }

    @Override
    public AccountProfileDto promoteAccountProfileRole(UUID accountId) {
        RolePromotionCheckResponse rolePromotionCheckResponse = checkOnRolePromotionAbility(accountId);
        if (rolePromotionCheckResponse.canPromote()) {
            AccountProfile accountProfile = accountProfileService.getDomainById(accountId);
            AccountTechnicalRole originalTechnicalRole = accountProfile.getTechnicalRole();

            AccountTechnicalRole newTechnicalRole = AccountTechnicalRole.getNextRole(originalTechnicalRole)
                    .orElseThrow(() -> new AccountException("Account technical role can not be promoted!"));

            accountProfile.setTechnicalRole(newTechnicalRole);
            accountProfileRepository.save(accountProfile);

            log.info("Promoting user {} from {} to {}", accountId, originalTechnicalRole, newTechnicalRole);
            return accountProfileMapper.toDto(accountProfile);
        } else {
            throw new SubprojectAccountException("You are not eligible for promotion!");
        }
    }

    private AccountRolePromotionConditions getRolePromotionConditions(UUID accountId) {
        if (rolePromotionConditionRepository.findAll().isEmpty()) {
            throw new RolePromotionConditionException("Role promotion condition not found!");
        }

        List<ShortProjectDto> authorsProjects = projectService.getAllByAccountId(accountId);

        AccountProfile accountProfile = accountProfileRepository.findById(accountId).orElseThrow(
                () -> new AccountException("Account with id: " + accountId + " not found!")
        );

        Set<SubprojectAccount> subprojectAccounts = subprojectAccountRepository.findAllById_AccountId(accountId)
                .orElseThrow(() -> new SubprojectAccountException(
                        "Subproject account with id: " + accountId + " not found!")
                );

        int commitsAmountByAuthor = subprojectAccounts.stream()
                .mapToInt(SubprojectAccount::getTotalCommits)
                .sum();

        int createdTasksInPreCampus = rolePromotionTaskApi.getAllTasksByAccountEmail(accountProfile.getEmail()).size();
        int mentoredSessionsAmount = sessionApi.getAllSessionsByMentorId(accountId).size();

        return AccountRolePromotionConditions.builder()
                .projectsInCampusAmount(authorsProjects.isEmpty() ? 0 : authorsProjects.size())
                .commitsInCampusAmount(commitsAmountByAuthor)
                .createdTasksInCampusAmount(createdTasksInPreCampus)
                .mentoredSessionsAmount(mentoredSessionsAmount)
                .wasASupervisor(projectService.existsBySupervisorId(accountId))
                .wasAnArchitect(projectService.existsByArchitectId(accountId))
                .build();
    }

    private <T> T getOrDefault(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }

}
