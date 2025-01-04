package ua.knu.knudev.teammanagerapi.devprofile;

import org.springframework.context.annotation.Profile;

@Profile("dev")
public interface DevProfileTeamManagerApi {
    void createTestDepartments();
}
