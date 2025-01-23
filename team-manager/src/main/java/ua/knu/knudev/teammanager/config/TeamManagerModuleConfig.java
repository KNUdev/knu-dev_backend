package ua.knu.knudev.teammanager.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan("ua.knu.knudev.teammanager")
@EnableJpaRepositories(basePackages = "ua.knu.knudev.teammanager.repository")
@EntityScan("ua.knu.knudev.teammanager.domain")
@EnableScheduling
public class TeamManagerModuleConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("CloseRecruitmentTask-");
        scheduler.initialize();
        return scheduler;
    }
}
