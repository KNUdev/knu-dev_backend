package ua.knu.knudev.teammanagerapi.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record RolePromotionCheckResponse(
        Boolean canPromote,
        Map<String, Boolean> checkList
) {
}
