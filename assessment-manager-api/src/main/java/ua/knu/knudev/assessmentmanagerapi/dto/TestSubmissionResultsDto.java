package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TestSubmissionResultsDto {
    private UUID submissionId;
    private UUID userId;
    private String testName;
    private TestScore score;
    private String status;
    private long timeTakenInSeconds;
    private List<TestQuestionResultDto> questions;
}
