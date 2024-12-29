package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/test")
@Profile("dev")
public class DevProfileController {
    private final DevProfileTeamManagerApi devProfileTeamManagerApi;

    @PostMapping("/departments/create")
    public void createTestDepartments() {
        devProfileTeamManagerApi.create3TestDepartments();
    }


}
