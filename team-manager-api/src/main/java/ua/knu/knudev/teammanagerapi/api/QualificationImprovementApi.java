package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.RecruitmentCloseRequestDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentCreationRequestDto;

public interface QualificationImprovementApi {

    void initializeRecruitment(RecruitmentCreationRequestDto creationRequestDto);

    void closeRecruitment(RecruitmentCloseRequestDto closeRequestDto);
}
