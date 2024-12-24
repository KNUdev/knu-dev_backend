package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentOpenRequest;

public interface RecruitmentApi {

    void openRecruitment(RecruitmentOpenRequest creationRequestDto);

    void manuallyCloseRecruitment(Expertise expertise);
}
