package ua.knu.knudev.intergrationtests.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ua.knu.knudev.teammanager",
        "ua.knu.knudev.knudevsecurity",
        "ua.knu.knudev.fileservice",
        "ua.knu.knudev.taskmanager",
        "ua.knu.knudev.intergrationtests"
})
@EnableJpaRepositories(basePackages = {"ua.knu.knudev.intergrationtests"})
public class IntegrationTestsConfig {

}
