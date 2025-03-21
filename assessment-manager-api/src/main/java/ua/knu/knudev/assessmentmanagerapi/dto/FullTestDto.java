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
        Set<TestQuestionDto> testQuestionDtos,
        Integer executionTimeInMinutes,
        Integer timeUnitPerTextCharacter,
        Integer extraTimePerCorrectAnswer
) {
}
