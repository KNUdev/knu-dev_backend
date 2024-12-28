package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeveloperRequirements implements PromotionRequirements{

    private Integer preCampusDuration;
    private Integer projectsInPreCampus;
}
