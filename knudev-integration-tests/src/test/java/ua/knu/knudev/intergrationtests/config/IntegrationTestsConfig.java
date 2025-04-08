package ua.knu.knudev.intergrationtests.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ua.knu.knudev.teammanager",
        "ua.knu.knudev.education",
        "ua.knu.knudev.knudevsecurity",
        "ua.knu.knudev.fileservice",
        "ua.knu.knudev.assessmentmanager",
        "ua.knu.knudev.intergrationtests"
})
public class IntegrationTestsConfig {

}
