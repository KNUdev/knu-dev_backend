package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterDeveloperRequirements implements PromotionRequirements{

    private Integer commitsToMasterBranchAmount;
    private Integer projectsCompletedVersionsAmount;

}


