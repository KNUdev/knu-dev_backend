package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreMasterDeveloperRequirements implements PromotionRequirements{

    private Integer commitsToMasterBranchAmount;

}
