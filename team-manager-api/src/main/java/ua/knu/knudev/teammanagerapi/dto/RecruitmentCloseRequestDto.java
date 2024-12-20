package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitmentCloseRequestDto {

    private Integer recruitmentNumber;
    private Integer recruitedPeopleNumber;
}
