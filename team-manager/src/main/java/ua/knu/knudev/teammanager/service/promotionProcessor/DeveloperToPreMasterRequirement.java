package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.RolePromotionConditionSignature;
import ua.knu.knudev.teammanagerapi.dto.AccountRolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;

import java.util.Map;

@Component
public class DeveloperToPreMasterRequirement implements PromotionRequirement {

    @Override
    public Map<String, Boolean> getCheckListMap(AccountRolePromotionConditions accountConditions,
                                                RolePromotionConditionDto requiredConditions) {

        Integer requiredCommitsQuantity = requiredConditions.toPremasterCommitsQuantity();
        Integer requiredProjectsQuantity = requiredConditions.toPremasterProjectQuantity();

        return Map.of(
                RolePromotionConditionSignature.PROJECTS_COUNT_KEY_CONSTANT + " " + (requiredProjectsQuantity - 1),
                accountConditions.projectsInCampusAmount() >= requiredProjectsQuantity,
                RolePromotionConditionSignature.COMMITS_COUNT_KEY_CONSTANT + " " + requiredCommitsQuantity,
                accountConditions.commitsInCampusAmount() >= requiredCommitsQuantity
        );
    }
}
