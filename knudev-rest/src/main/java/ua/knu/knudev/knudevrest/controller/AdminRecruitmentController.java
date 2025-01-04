package ua.knu.knudev.knudevrest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitment")
public class AdminRecruitmentController {
    private final RecruitmentApi recruitmentApi;

    @PostMapping("/open")
    public void open(@RequestBody @Valid RecruitmentOpenRequest openRequest) {
        recruitmentApi.openRecruitment(openRequest);
    }

    @PostMapping("/close")
    public void close(@RequestBody UUID activeRecruitmentId) {
        RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(
                activeRecruitmentId, RecruitmentCloseCause.MANUAL_CLOSE);
        recruitmentApi.closeRecruitment(closeRequest);
    }

}
