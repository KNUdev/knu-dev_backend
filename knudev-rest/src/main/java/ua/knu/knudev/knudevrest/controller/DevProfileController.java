package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanagerapi.devprofile.DevProfileTeamManagerApi;
import ua.knu.knudev.teammanagerapi.dto.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
@Profile("dev")
public class DevProfileController {
    private final DevProfileTeamManagerApi devProfileTeamManagerApi;

    @PostMapping("/departments/create")
    public List<DepartmentWithSpecialtiesDto> createTestDepartments() {
        return devProfileTeamManagerApi.createTestDepartments();
    }

    @PostMapping("/accounts/create")
    public List<ShortAccountProfileDto> createTestAccounts(@RequestBody Integer amount) {
        return devProfileTeamManagerApi.createTestAccounts(amount);
    }

    @PostMapping("/recruitments/create/active")
    public List<ActiveRecruitmentDto> createActiveRecruitments(@RequestBody Integer amount) {
        return devProfileTeamManagerApi.createActiveRecruitments(amount);
    }

    @PostMapping("/recruitment/join")
    public void joinActiveRecruitment() {
        devProfileTeamManagerApi.joinActiveRecruitment();
    }

    @PostMapping("/recruitments/create/closed")
    public List<ClosedRecruitmentDto> createClosedRecruitments() {
        return devProfileTeamManagerApi.createClosedRecruitments();
    }

    @PostMapping("/recruitments/get/active")
    public List<FullActiveRecruitmentDto> getActiveRecruitments() {
        return devProfileTeamManagerApi.getFullActiveRecruitments();
    }

    @PostMapping("/recruitments/get/closed")
    public List<FullClosedRecruitmentDto> getClosedRecruitments(@RequestParam String title,
                                                                @RequestParam Expertise expertise) {
        return devProfileTeamManagerApi.getFullClosedRecruitments(title, expertise);
    }

}
