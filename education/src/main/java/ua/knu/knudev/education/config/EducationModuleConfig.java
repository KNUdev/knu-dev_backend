package ua.knu.knudev.education.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("ua.knu.knudev.education")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.education.repository")
@EntityScan("ua.knu.knudev.education.domain")
public class EducationModuleConfig {

}
