package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.RolePromotionCondition;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditions;

import java.util.Map;

@Component
public class PreMasterToMasterRequirement implements PromotionRequirement {

    @Value("${application.promotion.conditions.master.participation-in-projects}")
    private Integer projectsInCampusAmount;
    @Value("${application.promotion.conditions.master.commits-amount-in-master}")
    private Integer commitsInCampusAmount;

    @Override
    public Map<String, Boolean> getCheckListMap(RolePromotionConditions conditions) {
        return Map.of(
                RolePromotionCondition.PROJECT_AS_PREMASTER.getDisplayBody() + (projectsInCampusAmount -1),
                conditions.projectsInCampusAmount() >= projectsInCampusAmount,
                RolePromotionCondition.COMMITS_AS_PREMASTER.getDisplayBody() + commitsInCampusAmount,
                conditions.commitsInCampusAmount() >= commitsInCampusAmount
        );
    }
}
