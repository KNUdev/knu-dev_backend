package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

@Builder
public record RolePromotionConditions(
        Integer projectsInCampusAmount,
        Integer commitsInCampusAmount,
//        todo add more fields
        Boolean wasASupervisor,
        Boolean wasAnArchitect
) {
}
