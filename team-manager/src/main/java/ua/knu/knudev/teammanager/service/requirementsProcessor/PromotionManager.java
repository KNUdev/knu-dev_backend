package ua.knu.knudev.teammanager.service.requirementsProcessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.teammanagerapi.api.PromotionManagerApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.AccountRoleEnhancementDto;
import ua.knu.knudev.teammanagerapi.requirements.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionManager implements PromotionManagerApi {

    private final PromotionStrategy promotionStrategy;

    @Override
    public boolean isReadyForPromotion(AccountRoleEnhancementDto accountRoleEnhancementDto) {
        AccountTechnicalRole accountTechnicalRole = accountRoleEnhancementDto.technicalRole();
        PromotionRequirements requirements = createPromotionRequirements(accountTechnicalRole, accountRoleEnhancementDto);
        Specification<PromotionRequirements> specification = promotionStrategy.getSpecification(accountTechnicalRole);

        return specification.isSatisfiedForEnhancement(requirements);
    }

    //    TODO IF WE HAVE POSSIBILITY TO TAKE ALL DATA FROM ACCOUNT WE CAN PUT THAT DATA HERE AS CONSTRUCTOR PARAMS
    private PromotionRequirements createPromotionRequirements(AccountTechnicalRole technicalRole,
                                                              AccountRoleEnhancementDto accountRoleEnhancementDto) {

        return switch (technicalRole) {
            case DEVELOPER -> new PreMasterDeveloperRequirements();
            case PREMASTER -> new MasterDeveloperRequirements();
            case MASTER -> new TechLeadRequirements();
            default -> throw new IllegalArgumentException("No requirements class for role: " + technicalRole);
        };
    }
}