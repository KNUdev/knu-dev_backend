package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.util.UUID;

public interface RecruitmentApi {

    void openRecruitment(RecruitmentOpenRequest openRequest);

    void closeRecruitment(UUID activeRecruitmentId, RecruitmentCloseCause closeCause);

    void joinActiveRecruitment(RecruitmentJoinRequest joinRequest);
}
