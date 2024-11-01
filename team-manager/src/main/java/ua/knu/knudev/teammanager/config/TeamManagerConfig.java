package ua.knu.knudev.teammanager.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan("ua.knu.knudev.teammanager")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.teammanager.repository")
@EntityScan("ua.knu.knudev.teammanager.domain")
@EnableJpaAuditing
public class TeamManagerConfig {
}
