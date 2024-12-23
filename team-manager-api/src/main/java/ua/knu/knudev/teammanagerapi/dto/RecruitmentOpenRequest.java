package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import lombok.Data;
import ua.knu.knudev.knudevcommon.constant.Expertise;

@Data
@Builder
public class RecruitmentOpenRequest {

    private String recruitmentName;
    private Expertise expertise;
    private RecruitmentAutoCloseConditionsDto autoCloseConditions;
}
