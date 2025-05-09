package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

@Builder
public record AccountRolePromotionConditions(
        Integer projectsInCampusAmount,
        Integer commitsInCampusAmount,
        Integer createdTasksInCampusAmount,
        Integer mentoredSessionsAmount,
        Boolean wasASupervisor,
        Boolean wasAnArchitect
) {
}
