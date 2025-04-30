package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanagerapi.api.RolePromotionTaskApi;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.SubprojectAccountRepository;
import ua.knu.knudev.teammanager.service.promotionProcessor.DeveloperToPreMasterRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.MasterToTechLeadRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.PreMasterToMasterRequirement;
import ua.knu.knudev.teammanager.service.promotionProcessor.PromotionRequirement;
import ua.knu.knudev.teammanagerapi.api.RolePromotionApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.SubprojectAccountException;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

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
    private final AccountProfileMapper accountProfileMapper;
    private final RolePromotionTaskApi rolePromotionTaskApi;
    private final SessionApi sessionApi;

    private final Map<AccountTechnicalRole, PromotionRequirement> requirementMap = Map.of(
            AccountTechnicalRole.DEVELOPER, new DeveloperToPreMasterRequirement(),
            AccountTechnicalRole.PREMASTER, new PreMasterToMasterRequirement(),
            AccountTechnicalRole.MASTER, new MasterToTechLeadRequirement()
    );

    @Override
    public RolePromotionCheckResponse checkOnRolePromotionAbility(UUID accountId) {
        AccountProfile accountProfile = accountProfileService.getDomainById(accountId);
        RolePromotionConditions rolePromotionConditions = getRolePromotionConditions(accountId);
        PromotionRequirement promotionRequirement = requirementMap.get(accountProfile.getTechnicalRole());
        if (promotionRequirement != null) {
            Map<String, Boolean> checkList = promotionRequirement.getCheckListMap(rolePromotionConditions);
            boolean canPromote = promotionRequirement.canPromoteBy(rolePromotionConditions);

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

    private RolePromotionConditions getRolePromotionConditions(UUID accountId) {
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

        return RolePromotionConditions.builder()
                .projectsInCampusAmount(authorsProjects.isEmpty() ? 0 : authorsProjects.size())
                .commitsInCampusAmount(commitsAmountByAuthor)
                .createdTasksInCampusAmount(createdTasksInPreCampus)
                .mentoredSessionsAmount(mentoredSessionsAmount)
                .wasASupervisor(projectService.existsBySupervisorId(accountId))
                .wasAnArchitect(projectService.existsByArchitectId(accountId))
                .build();
    }

}
