package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TestQuestionResultDto {
    private UUID questionId;
    private String questionBody;
    private List<AnswerVariantResultDto> variants;
}