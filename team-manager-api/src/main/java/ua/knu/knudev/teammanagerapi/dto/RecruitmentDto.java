package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;

@Builder
public record RecruitmentDto(
        Integer recruitmentNumber,
        String recruitmentName,
        Expertise expertise,
        LocalDateTime recruitmentStartDateTime,
        LocalDateTime recruitmentEndDateTime,
        Integer recruitedPeopleNumber
) {
}
