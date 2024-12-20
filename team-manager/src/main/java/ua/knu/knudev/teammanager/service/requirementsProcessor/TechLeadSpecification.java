package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.teammanager.requirements.TechLeadRequirements;

public class TechLeadSpecification implements Specification<TechLeadRequirements> {

    @Override
    public boolean isSatisfiedBy(TechLeadRequirements techLeadRequirements) {
        return techLeadRequirements.getMasterDurationInMonths() > 6
                && techLeadRequirements.getTechLeadScore() >= 8
                && techLeadRequirements.getIsApproved();
    }
}
