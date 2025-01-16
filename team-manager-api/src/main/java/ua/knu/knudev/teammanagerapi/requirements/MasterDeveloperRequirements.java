package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterDeveloperRequirements implements PromotionRequirements{

    private Integer preMasterProjectsCount;
    private Double techLeadScore;
    private Integer managedStudentsInPreCampus;
    private Integer preMasterDurationInMonths;
}


