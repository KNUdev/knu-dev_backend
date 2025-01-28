package ua.knu.knudev.taskmanagerapi.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record FullTestDto(
        UUID id,
        String enName,
        LocalDate createdAt,
        Set<TestQuestionDto> testQuestionDtos
) {
}
