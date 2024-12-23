package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.teammanagerapi.requirements.DeveloperRequirements;

public class DeveloperSpecification implements Specification<DeveloperRequirements> {

    @Override
    public boolean isSatisfiedBy(DeveloperRequirements developerRequirements) {
        return developerRequirements.getProjectsInPreCampus() > 0
                        && developerRequirements.getPreCampusDuration() > 6;
    }

}
