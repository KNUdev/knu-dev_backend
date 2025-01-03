package ua.knu.knudev.knudevapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ua.knu.knudev.fileservice.config.FileServiceConfig;
import ua.knu.knudev.fileserviceapi.config.FileServiceApiConfig;
import ua.knu.knudev.knudevrest.config.OpenApiConfiguration;
import ua.knu.knudev.knudevrest.config.RestConfig;
import ua.knu.knudev.knudevcommon.config.CommonConfig;
import ua.knu.knudev.knudevliquibase.config.LiquibaseConfig;
import ua.knu.knudev.knudevsecurity.config.SecurityModuleConfig;
import ua.knu.knudev.knudevsecurityapi.config.SecurityApiConfig;
import ua.knu.knudev.taskmanager.config.TaskManagerConfig;
import ua.knu.knudev.taskmanagerapi.config.TaskManagerApiConfig;
import ua.knu.knudev.teammanager.config.TeamManagerConfig;
import ua.knu.knudev.teammanagerapi.config.TeamManagerApiConfig;

@SpringBootApplication
@Import({CommonConfig.class, LiquibaseConfig.class, RestConfig.class, SecurityModuleConfig.class, SecurityApiConfig.class,
        TaskManagerConfig.class, TaskManagerApiConfig.class, TeamManagerConfig.class, TeamManagerApiConfig.class,
        FileServiceConfig.class, FileServiceApiConfig.class, OpenApiConfiguration.class})
public class KnudevApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnudevApplication.class, args);
    }

}
