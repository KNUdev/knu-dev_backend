package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

public interface RecruitmentApi {

    void openRecruitment(RecruitmentOpenRequest openRequest);

    void closeRecruitment(RecruitmentCloseRequest closeRequest);

    void joinActiveRecruitment(RecruitmentJoinRequest joinRequest);
}
