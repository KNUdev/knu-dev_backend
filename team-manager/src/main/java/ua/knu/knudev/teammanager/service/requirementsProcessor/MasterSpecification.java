package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.MasterDeveloperRequirements;

public class MasterSpecification implements Specification<MasterDeveloperRequirements> {

    @Value("${application.recruitment.requirements.master-developer.commits-to-master-branch-amount}")
    private Integer commitsToMasterBranchAmount;
    @Value("${application.recruitment.requirements.master-developer.projects-completed-versions-amount}")
    private Integer projectsCompleteVersionsAmount;

    @Override
    public boolean isSatisfiedForEnhancement(MasterDeveloperRequirements masterDeveloperRequirements) {
        return commitsToMasterBranchAmount <= masterDeveloperRequirements.getCommitsToMasterBranchAmount()
                    && projectsCompleteVersionsAmount <= masterDeveloperRequirements.getProjectsCompletedVersionsAmount();
    }
}
