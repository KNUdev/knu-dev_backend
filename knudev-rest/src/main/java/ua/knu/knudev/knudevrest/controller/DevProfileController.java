package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.educationapi.api.DevProfileEducationApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/test")
@Profile("dev")
public class DevProfileController {
    private final DevProfileTeamManagerApi devProfileTeamManagerApi;
    private final DevProfileEducationApi devProfileEducationApi;

    @PostMapping("/departments/create")
    public void createTestDepartments() {
        devProfileTeamManagerApi.createTestDepartments();
    }

    @PostMapping("/program/create")
    public EducationProgramDto createProgram() {
        return devProfileEducationApi.createTestProgram();
    }

}
