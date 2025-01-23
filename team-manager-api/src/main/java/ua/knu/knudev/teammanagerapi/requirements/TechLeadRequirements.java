package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechLeadRequirements implements PromotionRequirements {

    private Integer projectsCompletedVersionsAmount;
    private Integer mentoredStudentsAtPreCampusAmount;
    private Integer masterclassesAmount;

}
