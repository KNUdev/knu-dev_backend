package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecruitmentAutoCloseConditionsDto {

    private LocalDateTime deadlineDate;
    private Integer maxCandidates;
}
