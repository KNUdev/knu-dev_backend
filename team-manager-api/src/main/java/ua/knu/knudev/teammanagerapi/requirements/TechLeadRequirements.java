package ua.knu.knudev.teammanagerapi.requirements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechLeadRequirements implements PromotionRequirements{

    private Integer masterDurationInMonths;
    private Double techLeadScore;
    private Boolean isApproved;
}
