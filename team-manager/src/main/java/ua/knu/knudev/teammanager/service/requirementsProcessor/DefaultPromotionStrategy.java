package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.stereotype.Component;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanagerapi.requirements.PromotionRequirements;

import java.util.Map;

@Component
public class DefaultPromotionStrategy implements PromotionStrategy {

    private final Map<AccountTechnicalRole, Specification<? extends PromotionRequirements>> promotionMap;

    public DefaultPromotionStrategy() {
        promotionMap = Map.of(
                AccountTechnicalRole.INTERN, new DeveloperSpecification(),
                AccountTechnicalRole.DEVELOPER, new PreMasterSpecification(),
                AccountTechnicalRole.PREMASTER, new MasterSpecification(),
                AccountTechnicalRole.MASTER, new TechLeadSpecification()
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends PromotionRequirements> Specification<T> getSpecification(AccountTechnicalRole technicalRole) {
        Specification<T> specification = (Specification<T>) promotionMap.get(technicalRole);
        if (specification == null) {
            throw new IllegalArgumentException("No specification found for role " + technicalRole);
        }
        return specification;
    }
}
