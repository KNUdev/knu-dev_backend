package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.PreMasterDeveloperRequirements;

public class PreMasterSpecification implements Specification<PreMasterDeveloperRequirements>{

    @Value("${application.recruitment.requirements.pre-master-developer.tech-lead-score}")
    private int techLeadScore;
    @Value("${application.recruitment.requirements.pre-master-developer.developing-time-in-campus}")
    private int developingTimeInCampus;

    @Override
    public boolean isSatisfiedBy(PreMasterDeveloperRequirements preMasterDeveloperRequirements) {
        return preMasterDeveloperRequirements.getTechLeadScore() >= techLeadScore
                        && preMasterDeveloperRequirements.getDevelopingTimeInCampus() > developingTimeInCampus;
    }
}
