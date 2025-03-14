package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.FullClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.util.List;

public interface RecruitmentApi {

    ActiveRecruitmentDto openRecruitment(RecruitmentOpenRequest openRequest);

    ClosedRecruitmentDto closeRecruitment(RecruitmentCloseRequest closeRequest);

    void joinActiveRecruitment(RecruitmentJoinRequest joinRequest);

    List<FullClosedRecruitmentDto> getClosedRecruitments(RecruitmentCloseRequest closeRequest);
}
