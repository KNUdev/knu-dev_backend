package ua.knu.knudev.teammanager.service.requirementsProcessor;

import org.springframework.beans.factory.annotation.Value;
import ua.knu.knudev.teammanagerapi.requirements.TechLeadRequirements;

public class TechLeadSpecification implements Specification<TechLeadRequirements> {

    @Value("${application.recruitment.requirements.tech-lead.projects-completed-versions-amount}")
    private Integer projectsCompletedVersionsAmount;
    @Value("${application.recruitment.requirements.tech-lead.mentored-students-at-precampus-amount}")
    private Integer mentoredStudentsAtPrecampusAmount;
    @Value("${application.recruitment.requirements.tech-lead.masterclasses-amount}")
    private Integer masterClassesAmount;

    @Override
    public boolean isSatisfiedForEnhancement(TechLeadRequirements techLeadRequirements) {
        return projectsCompletedVersionsAmount <= techLeadRequirements.getProjectsCompletedVersionsAmount()
                && mentoredStudentsAtPrecampusAmount <= techLeadRequirements.getMentoredStudentsAtPreCampusAmount()
                && masterClassesAmount <= techLeadRequirements.getMasterclassesAmount();
    }
}
