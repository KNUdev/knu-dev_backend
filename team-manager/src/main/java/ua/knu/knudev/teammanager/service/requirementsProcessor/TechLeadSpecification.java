package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.TechLeadRequirements;

public class TechLeadSpecification implements Specification<TechLeadRequirements> {

    @Value("${application.recruitment.requirements.tech-lead.duration-in-months-as-master}")
    private int durationInMonthsAsMaster;
    @Value("${application.recruitment.requirements.tech-lead.tech-lead-score}")
    private int techLeadScore;

    @Override
    public boolean isSatisfiedBy(TechLeadRequirements techLeadRequirements) {
        return techLeadRequirements.getMasterDurationInMonths() > durationInMonthsAsMaster
                && techLeadRequirements.getTechLeadScore() >= techLeadScore
                && techLeadRequirements.getIsApproved();
    }
}
