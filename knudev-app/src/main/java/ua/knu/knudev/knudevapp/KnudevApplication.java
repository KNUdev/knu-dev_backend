package ua.knu.knudev.knudevapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ua.knu.knudev.assessmentmanager.config.AssessmentManagerModuleConfig;
import ua.knu.knudev.assessmentmanagerapi.config.AssessmentManagerAPIModuleConfig;
import ua.knu.knudev.education.config.EducationModuleConfig;
import ua.knu.knudev.educationapi.config.EducationAPIModuleConfig;
import ua.knu.knudev.fileservice.config.FileServiceModuleConfig;
import ua.knu.knudev.fileserviceapi.config.FileServiceAPIModuleConfig;
import ua.knu.knudev.knudevcommon.config.CommonConfig;
import ua.knu.knudev.knudevliquibase.config.LiquibaseConfig;
import ua.knu.knudev.knudevrest.config.RestModuleConfig;
import ua.knu.knudev.knudevsecurity.config.SecurityModuleConfig;
import ua.knu.knudev.knudevsecurityapi.config.SecurityApiConfig;
import ua.knu.knudev.teammanager.config.TeamManagerModuleConfig;
import ua.knu.knudev.teammanagerapi.config.TeamManagerAPIModuleConfig;

@SpringBootApplication
@Import({CommonConfig.class, LiquibaseConfig.class, RestModuleConfig.class, SecurityModuleConfig.class, SecurityApiConfig.class,
        AssessmentManagerModuleConfig.class, AssessmentManagerAPIModuleConfig.class, TeamManagerModuleConfig.class, TeamManagerAPIModuleConfig.class,
        FileServiceModuleConfig.class, FileServiceAPIModuleConfig.class, EducationModuleConfig.class, EducationAPIModuleConfig.class})
public class KnudevApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnudevApplication.class, args);
    }

}
