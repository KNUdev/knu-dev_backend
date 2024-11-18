package ua.knu.knudev.intergrationtests.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ua.knu.knudev.fileservice.config.ImageFileConfigProperties;
import ua.knu.knudev.taskmanager.config.TaskFileConfigProperties;

@SpringBootApplication(scanBasePackages = {
        "ua.knu.knudev.teammanager",
        "ua.knu.knudev.knudevsecurity",
        "ua.knu.knudev.fileservice",
        "ua.knu.knudev.taskmanager",
        "ua.knu.knudev.intergrationtests"
})
//@EnableConfigurationProperties({ImageFileConfigProperties.class, TaskFileConfigProperties.class})
public class IntegrationTestsConfig {

}
