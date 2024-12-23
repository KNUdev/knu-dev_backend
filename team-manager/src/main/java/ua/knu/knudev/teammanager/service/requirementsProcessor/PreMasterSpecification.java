package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.teammanagerapi.requirements.PreMasterDeveloperRequirements;

public class PreMasterSpecification implements Specification<PreMasterDeveloperRequirements>{

    @Override
    public boolean isSatisfiedBy(PreMasterDeveloperRequirements preMasterDeveloperRequirements) {
        return preMasterDeveloperRequirements.getTechLeadScore() >= 7
                        && preMasterDeveloperRequirements.getDevelopingTimeInCampus() > 9;
    }
}
