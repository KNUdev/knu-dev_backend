package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.DeveloperRequirements;

public class DeveloperSpecification implements Specification<DeveloperRequirements> {

    @Value("${application.recruitment.requirements.developer.pre-campus-projects-amount}")
    private int preCampusProjectsAmount;
    @Value("${application.recruitment.requirements.developer.pre-campus-duration}")
    private int preCampusDuration;

    @Override
    public boolean isSatisfiedBy(DeveloperRequirements developerRequirements) {
        return developerRequirements.getProjectsInPreCampus() >= preCampusProjectsAmount
                        && developerRequirements.getPreCampusDuration() > preCampusDuration;
    }

}
