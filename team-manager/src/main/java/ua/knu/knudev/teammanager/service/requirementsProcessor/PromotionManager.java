package ua.knu.knudev.teammanager.service.requirementsProcessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanagerapi.api.PromotionManagerApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.requirements.*;

@Service
@RequiredArgsConstructor
public class PromotionManager implements PromotionManagerApi {

    private final PromotionStrategy promotionStrategy;

    @Override
//    TODO also we need to create service where we will compute requirements for different developers
    public boolean isReadyToPromotion(AccountProfileDto accountProfileDto) {
        AccountTechnicalRole accountTechnicalRole = accountProfileDto.technicalRole();
        PromotionRequirements requirements = createPromotionRequirements(accountTechnicalRole);
        Specification<PromotionRequirements> specification = promotionStrategy.getSpecification(accountTechnicalRole);
        return specification.isSatisfiedBy(requirements);
    }

    private PromotionRequirements createPromotionRequirements(AccountTechnicalRole technicalRole) {
        return switch (technicalRole) {
            case INTERN -> new DeveloperRequirements();
            case DEVELOPER -> new PreMasterDeveloperRequirements();
            case PREMASTER -> new MasterDeveloperRequirements();
            case MASTER -> new TechLeadRequirements();
            default -> throw new IllegalArgumentException("No requirements class for role: " + technicalRole);
        };
    }
}