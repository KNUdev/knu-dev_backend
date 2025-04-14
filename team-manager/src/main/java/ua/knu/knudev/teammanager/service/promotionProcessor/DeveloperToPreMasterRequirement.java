package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.knudevcommon.constant.RolePromotionCondition;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditions;

import java.util.Map;

public class DeveloperToPreMasterRequirement implements PromotionRequirement {

    @Value("${application.promotion.conditions.pre-master.participation-in-projects}")
    private Integer projectsInCampusAmount;
    @Value("${application.promotion.conditions.pre-master.commits-amount-in-master}")
    private Integer commitsInCampusAmount;

    @Override
    public Map<String, Boolean> getCheckListMap(RolePromotionConditions conditions) {
        return Map.of(
                RolePromotionCondition.PROJECT_AS_DEVELOPER.getDisplayBody(),
                conditions.projectsInCampusAmount() >= projectsInCampusAmount,
                RolePromotionCondition.COMMITS_AS_DEVELOPER.getDisplayBody(),
                conditions.commitsInCampusAmount() >= commitsInCampusAmount
        );
    }
}
