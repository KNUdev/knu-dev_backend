package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.AccountRoleEnhancementDto;

public interface PromotionManagerApi {

    boolean isReadyForPromotion(AccountRoleEnhancementDto accountRoleEnhancementDto);
}
