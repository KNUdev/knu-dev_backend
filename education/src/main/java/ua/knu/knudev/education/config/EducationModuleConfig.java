package ua.knu.knudev.education.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan("ua.knu.knudev.education")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.education.repository")
@EntityScan("ua.knu.knudev.education.domain")
@EnableScheduling
public class EducationModuleConfig {

    @Bean(name = "educationTaskScheduler")
    public TaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(15);
        scheduler.setThreadNamePrefix("SessionStartTask-");
        scheduler.initialize();
        return scheduler;
    }

}
