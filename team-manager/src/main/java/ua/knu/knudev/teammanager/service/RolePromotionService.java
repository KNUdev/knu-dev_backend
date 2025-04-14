package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
                    .message(canPromote ? "You are eligible for promotion to " +
                            accountProfile.getTechnicalRole().getNextRole() + "!"
                            : "You are not eligible for promotion!")
                    .build();
        }
        return RolePromotionCheckResponse.builder()
                .checkList(Map.of())
                .canPromote(false)
                .message("You are not eligible for promotion!")
                .build();
    }

    @Override
    public AccountProfileDto promoteAccountProfileRole(UUID accountId) {
        RolePromotionCheckResponse rolePromotionCheckResponse = checkOnRolePromotionAbility(accountId);
        if (rolePromotionCheckResponse.canPromote()) {
            AccountProfile accountProfile = accountProfileService.getDomainById(accountId);
            AccountTechnicalRole originalTechnicalRole = accountProfile.getTechnicalRole();
            AccountTechnicalRole newTechnicalRole = originalTechnicalRole.getNextRole();
            if (newTechnicalRole == null) {
                throw new SubprojectAccountException("No higher role available for promotion!");
            }
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

        Set<SubprojectAccount> subprojectAccounts = subprojectAccountRepository.findAllById_AccountId(accountId)
                .orElseThrow(() -> new SubprojectAccountException(
                        "Subproject account with id: " + accountId + " not found!")
                );

        int commitsAmountByAuthor = subprojectAccounts.stream()
                .mapToInt(SubprojectAccount::getTotalCommits)
                .sum();

        return RolePromotionConditions.builder()
                .projectsInCampusAmount(authorsProjects.isEmpty() ? 0 : authorsProjects.size())
                .commitsInCampusAmount(commitsAmountByAuthor)
                .wasASupervisor(projectService.existsBySupervisorId(accountId))
                .wasAnArchitect(projectService.existsByArchitectId(accountId))
                .build();
    }

}
