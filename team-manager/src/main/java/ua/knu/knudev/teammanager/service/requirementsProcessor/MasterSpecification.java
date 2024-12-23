package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.teammanagerapi.requirements.MasterDeveloperRequirements;

public class MasterSpecification implements Specification<MasterDeveloperRequirements> {

    @Override
    public boolean isSatisfiedBy(MasterDeveloperRequirements masterDeveloperRequirements) {
        return masterDeveloperRequirements.getPreMasterDurationInMonths() > 12
                && masterDeveloperRequirements.getManagedStudentsInPreCampus() > 2
                && masterDeveloperRequirements.getPreMasterProjectsCount() > 0
                && masterDeveloperRequirements.getTechLeadScore() >= 7;
    }
}
