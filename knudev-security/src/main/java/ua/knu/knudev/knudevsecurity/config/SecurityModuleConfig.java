package ua.knu.knudev.knudevsecurity.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("ua.knu.knudev.knudevsecurity")
@EntityScan("ua.knu.knudev.knudevsecurity.domain")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.knudevsecurity.repository")
public class SecurityModuleConfig {
}
