package ua.knu.knudev.teammanager.service.promotionProcessor;

import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditions;

import java.util.Map;

public interface PromotionRequirement {

    Map<String, Boolean> getCheckListMap(RolePromotionConditions conditions);

    default boolean canPromoteBy(RolePromotionConditions conditions) {
        return getCheckListMap(conditions).values().stream().allMatch(Boolean::booleanValue);
    }

}
