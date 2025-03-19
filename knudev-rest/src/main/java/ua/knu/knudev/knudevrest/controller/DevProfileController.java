package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.educationapi.api.DevProfileEducationApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.DepartmentWithSpecialtiesDto;
import ua.knu.knudev.teammanagerapi.dto.ShortAccountProfileDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
@Profile("dev")
public class DevProfileController {
    private final DevProfileTeamManagerApi devProfileTeamManagerApi;
    private final DevProfileEducationApi devProfileEducationApi;

    @Operation(summary = "Create Test Departments",
            description = "Creates test departments with specialties for development purposes.")
    @PostMapping("/departments/create")
    public List<DepartmentWithSpecialtiesDto> createTestDepartments() {
        return devProfileTeamManagerApi.createTestDepartments();
    }

    @Operation(summary = "Create Test Accounts",
            description = "Creates a specified number of test accounts and returns their short profiles.")
    @PostMapping("/accounts/create")
    public List<ShortAccountProfileDto> createTestAccounts(
            @Parameter(description = "Number of test accounts to create", required = true)
            @RequestBody Integer amount) {
        return devProfileTeamManagerApi.createTestAccounts(amount);
    }

    @Operation(summary = "Create Active Recruitments",
            description = "Creates active recruitment records for testing purposes and returns a list of active recruitment DTOs.")
    @PostMapping("/recruitments/active/create")
    public List<ActiveRecruitmentDto> createActiveRecruitments() {
        return devProfileTeamManagerApi.createActiveRecruitments();
    }

    @Operation(summary = "Join Active Recruitment",
            description = "Joins an active recruitment; useful for testing the recruitment join process.")
    @PostMapping("/recruitments/active/join")
    public void joinActiveRecruitment() {
        devProfileTeamManagerApi.joinActiveRecruitment();
    }

    @Operation(summary = "Create Closed Recruitments",
            description = "Creates closed recruitment records for testing purposes and returns a list of closed recruitment DTOs.")
    @PostMapping("/recruitments/closed/create")
    public List<ClosedRecruitmentDto> createClosedRecruitments() {
        return devProfileTeamManagerApi.createClosedRecruitments();
    }
    @PostMapping("/program/create")
    public EducationProgramDto createProgram() {
        return devProfileEducationApi.createTestProgram();
    }

}
