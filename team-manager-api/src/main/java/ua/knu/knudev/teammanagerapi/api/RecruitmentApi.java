package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.util.UUID;

public interface RecruitmentApi {

    void openRecruitment(RecruitmentOpenRequest openRequest);

    void closeRecruitment(UUID activeRecruitmentId);

    void addUserToRecruitment(UUID activeRecruitmentId, UUID accountProfileId);
}
