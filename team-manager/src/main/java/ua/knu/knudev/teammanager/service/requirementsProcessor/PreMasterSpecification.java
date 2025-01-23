package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.PreMasterDeveloperRequirements;

public class PreMasterSpecification implements Specification<PreMasterDeveloperRequirements> {

    @Value("${application.recruitment.requirements.pre-master-developer.commits-to-master-branch-amount}")
    private Integer commitsToMasterAmount;

    @Override
    public boolean isSatisfiedForEnhancement(PreMasterDeveloperRequirements preMasterDeveloperRequirements) {
        return commitsToMasterAmount <= preMasterDeveloperRequirements.getCommitsToMasterBranchAmount();
    }
}
