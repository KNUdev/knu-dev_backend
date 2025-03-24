package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record FullTestDto(
        UUID id,
        String enName,
        LocalDate createdAt,
        Integer maxRawScore,
        Set<TestQuestionDto> testQuestionDtos,
        Integer durationInMinutes,
        Integer timeUnitPerTextCharacter,
        Integer extraTimePerCorrectAnswer
) {
}
