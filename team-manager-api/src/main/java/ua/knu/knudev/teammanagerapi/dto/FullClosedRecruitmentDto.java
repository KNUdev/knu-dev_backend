package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDate;

@Builder
public record FullClosedRecruitmentDto(
        String name,
        LocalDate startDate,
        LocalDate finishDate,
        Expertise expertise,
        Integer maxPeopleAmount,
        Integer joinedPeopleAmount
) {
}
