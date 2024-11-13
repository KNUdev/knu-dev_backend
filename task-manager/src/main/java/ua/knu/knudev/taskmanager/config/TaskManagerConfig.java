package ua.knu.knudev.taskmanager.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("ua.knu.knudev.taskmanager")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.taskmanager.repository")
@EntityScan("ua.knu.knudev.taskmanager.domain")
@EnableConfigurationProperties(TaskFileConfigProperties.class)
public class TaskManagerConfig {

}
