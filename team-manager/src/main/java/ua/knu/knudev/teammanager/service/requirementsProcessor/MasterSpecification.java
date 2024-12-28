package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.MasterDeveloperRequirements;

public class MasterSpecification implements Specification<MasterDeveloperRequirements> {

    @Value("${application.recruitment.requirements.master-developer.duration-in-months-as-pre-master}")
    private Integer durationInMonthsAsPreMaster;
    @Value("${application.recruitment.requirements.master-developer.managed-students-in-pre-campus-amount}")
    private Integer managedStudentsInPreCampusAmount;
    @Value("${application.recruitment.requirements.master-developer.pre-master-projects-amount}")
    private Integer preMasterProjectsAmount;
    @Value("${application.recruitment.requirements.master-developer.tech-lead-score}")
    private Integer techLeadScore;

    @Override
    public boolean isSatisfiedBy(MasterDeveloperRequirements masterDeveloperRequirements) {
        return masterDeveloperRequirements.getPreMasterDurationInMonths() > preMasterProjectsAmount
                && masterDeveloperRequirements.getManagedStudentsInPreCampus() > managedStudentsInPreCampusAmount
                && masterDeveloperRequirements.getPreMasterProjectsCount() >= durationInMonthsAsPreMaster
                && masterDeveloperRequirements.getTechLeadScore() >= techLeadScore;
    }
}
