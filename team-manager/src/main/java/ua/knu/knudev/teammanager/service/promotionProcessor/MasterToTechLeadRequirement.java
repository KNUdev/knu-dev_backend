package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.RolePromotionCondition;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditions;

import java.util.Map;

@Component
public class MasterToTechLeadRequirement implements PromotionRequirement {

    @Value("${application.promotion.conditions.tech-lead.was-a-supervisor}")
    private Boolean wasASupervisor;
    @Value("${application.promotion.conditions.tech-lead.was-an-architect}")
    private Boolean wasAnArchitect;
    @Value("${application.promotion.conditions.tech-lead.commits-amount-in-master}")
    private Integer commitsInCampusAmount;
    @Value("${application.promotion.conditions.tech-lead.created-tasks-in-campus-amount}")
    private Integer createdTasksInCampusAmount;

    @Override
    public Map<String, Boolean> getCheckListMap(RolePromotionConditions conditions) {
        return Map.of(
                RolePromotionCondition.COMMITS_COUNT_KEY_CONSTANT + " " + commitsInCampusAmount,
                conditions.commitsInCampusAmount() >= commitsInCampusAmount,
                RolePromotionCondition.WAS_A_SUPERVISOR_KEY_CONSTANT.toString(),
                conditions.wasASupervisor() == wasASupervisor,
                RolePromotionCondition.WAS_AN_ARCHITECT_KEY_CONSTANT.toString(),
                conditions.wasAnArchitect() == wasAnArchitect,
                RolePromotionCondition.CREATED_TASKS_IN_CAMPUS_AMOUNT_KEY_CONSTANT + " " + createdTasksInCampusAmount,
                conditions.createdTasksInCampusAmount() >= createdTasksInCampusAmount
        );
    }
}
