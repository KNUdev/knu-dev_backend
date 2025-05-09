package ua.knu.knudev.teammanager.service.promotionProcessor;

import ua.knu.knudev.teammanagerapi.dto.AccountRolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;

import java.util.Map;

public interface PromotionRequirement {

    Map<String, Boolean> getCheckListMap(AccountRolePromotionConditions accountConditions,
                                         RolePromotionConditionDto requiredConditions);

    default boolean canPromoteBy(AccountRolePromotionConditions accountConditions,
                                 RolePromotionConditionDto requiredConditions) {
        return getCheckListMap(accountConditions, requiredConditions).values().stream()
                .allMatch(Boolean::booleanValue);
    }

}
