package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import lombok.Data;
import ua.knu.knudev.knudevcommon.constant.RecruitmentType;

@Data
@Builder
public class RecruitmentCreationRequestDto {

    private String recruitmentName;
    private RecruitmentType recruitmentType;
}
