package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.RecruitmentStatus;

import java.time.LocalDateTime;

@Builder
public record RecruitmentDto(
        Integer recruitmentNumber,
        String recruitmentName,
        Expertise expertise,
        LocalDateTime recruitmentStartDateTime,
        LocalDateTime recruitmentEndDateTime,
        RecruitmentStatus recruitmentStatus,
        Integer recruitedPeopleNumber
) {
}
