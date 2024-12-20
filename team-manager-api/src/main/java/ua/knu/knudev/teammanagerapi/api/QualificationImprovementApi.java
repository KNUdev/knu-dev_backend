package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.RecruitmentCloseRequestDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentOpenRequestDto;

public interface QualificationImprovementApi {

    void initializeRecruitment(RecruitmentOpenRequestDto creationRequestDto);

    void closeRecruitment(RecruitmentCloseRequestDto closeRequestDto);
}
