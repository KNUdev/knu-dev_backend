package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionCreationRequest;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionUpdateRequest;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.util.UUID;

public interface RolePromotionApi {

    RolePromotionConditionDto createRolePromotionConditions(RolePromotionConditionCreationRequest request);

    RolePromotionConditionDto updateRolePromotionConditions(RolePromotionConditionUpdateRequest request);

    void deleteRolePromotionConditions(UUID conditionId);

    RolePromotionConditionDto getRolePromotionConditions();

    RolePromotionCheckResponse checkOnRolePromotionAbility(UUID accountId);

    AccountProfileDto promoteAccountProfileRole(UUID accountId);

}
