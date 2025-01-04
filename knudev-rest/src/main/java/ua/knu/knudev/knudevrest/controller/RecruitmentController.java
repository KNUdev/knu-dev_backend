package ua.knu.knudev.knudevrest.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {
    private final RecruitmentApi recruitmentApi;

    @PostMapping("/join")
    public void joinActiveRecruitment(@RequestBody RecruitmentJoinRequest joinRequest) {
        recruitmentApi.joinActiveRecruitment(joinRequest);
    }

    //todo close, open recruitment. On close manually to service pass MANUAL_CLOSE


}
