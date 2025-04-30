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
    @Value("${application.promotion.conditions.master.created-tasks-in-campus-amount}")
    private Integer createdTasksInCampusAmount;
    @Value("${application.promotion.conditions.master.mentored-sessions-amount}")
    private Integer mentoredSessionsAmount;

    @Override
    public Map<String, Boolean> getCheckListMap(RolePromotionConditions conditions) {
        return Map.of(
                RolePromotionCondition.PROJECTS_COUNT_KEY_CONSTANT + " " + (projectsInCampusAmount -1),
                conditions.projectsInCampusAmount() >= projectsInCampusAmount,
                RolePromotionCondition.COMMITS_COUNT_KEY_CONSTANT + " " + commitsInCampusAmount,
                conditions.commitsInCampusAmount() >= commitsInCampusAmount,
                RolePromotionCondition.CREATED_TASKS_IN_CAMPUS_AMOUNT_KEY_CONSTANT + " " + createdTasksInCampusAmount,
                conditions.createdTasksInCampusAmount() >= createdTasksInCampusAmount,
                RolePromotionCondition.MENTORED_SESSIONS_AMOUNT_KEY_CONSTANT + " " + mentoredSessionsAmount,
                conditions.mentoredSessionsAmount() >= mentoredSessionsAmount
        );
    }
}
