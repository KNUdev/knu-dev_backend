package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.RolePromotionConditionSignature;
import ua.knu.knudev.teammanagerapi.dto.AccountRolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;

import java.util.Map;

@Component
public class MasterToTechLeadRequirement implements PromotionRequirement {

    @Override
    public Map<String, Boolean> getCheckListMap(AccountRolePromotionConditions accountConditions,
                                                RolePromotionConditionDto requiredConditions) {

        Integer requiredCommitsQuantity = requiredConditions.toTechLeadCommitsQuantity();
        Boolean wasSupervisor = requiredConditions.wasSupervisor();
        Boolean wasArchitect = requiredConditions.wasArchitect();
        Integer requiredCreatedTasksQuantity = requiredConditions.toTechLeadCreatedCampusTasksQuantity();

        return Map.of(
                RolePromotionConditionSignature.COMMITS_COUNT_KEY_CONSTANT + " " + requiredCommitsQuantity,
                accountConditions.commitsInCampusAmount() >= requiredCommitsQuantity,
                RolePromotionConditionSignature.WAS_A_SUPERVISOR_KEY_CONSTANT.toString(),
                accountConditions.wasASupervisor() == wasSupervisor,
                RolePromotionConditionSignature.WAS_AN_ARCHITECT_KEY_CONSTANT.toString(),
                accountConditions.wasAnArchitect() == wasArchitect,
                RolePromotionConditionSignature.CREATED_TASKS_IN_CAMPUS_AMOUNT_KEY_CONSTANT + " " + requiredCreatedTasksQuantity,
                accountConditions.createdTasksInCampusAmount() >= requiredCreatedTasksQuantity
        );
    }
}
