package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.response.RolePromotionCheckResponse;

import java.util.UUID;

public interface RolePromotionApi {

    RolePromotionCheckResponse checkOnRolePromotionAbility(UUID accountId);

    AccountProfileDto promoteAccountProfileRole(UUID accountId);

}
