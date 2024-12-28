package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanagerapi.requirements.PromotionRequirements;

public interface PromotionStrategy {

    <T extends PromotionRequirements> Specification<T> getSpecification(AccountTechnicalRole technicalRole);

}
