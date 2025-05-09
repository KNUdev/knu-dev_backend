package ua.knu.knudev.teammanager.service.promotionProcessor;

import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.RolePromotionConditionSignature;
import ua.knu.knudev.teammanagerapi.dto.AccountRolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;

import java.util.Map;

@Component
public class PreMasterToMasterRequirement implements PromotionRequirement {

    @Override
    public Map<String, Boolean> getCheckListMap(AccountRolePromotionConditions accountConditions,
                                                RolePromotionConditionDto requiredConditions) {

        Integer requiredMentoredSessionsQuantity = requiredConditions.toMasterMentoredSessionsQuantity();
        Integer requiredCreatedTasksQuantity = requiredConditions.toMasterCreatedCampusTasksQuantity();
        Integer requiredCommitsQuantity = requiredConditions.toMasterCommitsQuantity();
        Integer requiredProjectsQuantity = requiredConditions.toMasterProjectQuantity();

        return Map.of(
                RolePromotionConditionSignature.PROJECTS_COUNT_KEY_CONSTANT + " " + (requiredProjectsQuantity - 1),
                accountConditions.projectsInCampusAmount() >= requiredProjectsQuantity,
                RolePromotionConditionSignature.COMMITS_COUNT_KEY_CONSTANT + " " + requiredCommitsQuantity,
                accountConditions.commitsInCampusAmount() >= requiredCommitsQuantity,
                RolePromotionConditionSignature.CREATED_TASKS_IN_CAMPUS_AMOUNT_KEY_CONSTANT + " " + requiredCreatedTasksQuantity,
                accountConditions.createdTasksInCampusAmount() >= requiredCreatedTasksQuantity,
                RolePromotionConditionSignature.MENTORED_SESSIONS_AMOUNT_KEY_CONSTANT + " " + requiredMentoredSessionsQuantity,
                accountConditions.mentoredSessionsAmount() >= requiredMentoredSessionsQuantity
        );
    }
}
