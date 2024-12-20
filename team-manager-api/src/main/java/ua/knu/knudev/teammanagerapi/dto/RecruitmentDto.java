package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.RecruitmentStatus;
import ua.knu.knudev.knudevcommon.constant.RecruitmentType;

import java.time.LocalDateTime;

@Builder
public record RecruitmentDto(
        Integer recruitmentNumber,
        String recruitmentName,
        RecruitmentType recruitmentType,
        LocalDateTime recruitmentStartDateTime,
        LocalDateTime recruitmentEndDateTime,
        RecruitmentStatus recruitmentStatus,
        Integer recruitedPeopleNumber
) {
}
