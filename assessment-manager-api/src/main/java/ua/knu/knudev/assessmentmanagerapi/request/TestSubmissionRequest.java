package ua.knu.knudev.assessmentmanagerapi.request;

import lombok.Builder;
import lombok.Data;
import ua.knu.knudev.assessmentmanagerapi.constant.TestSubmissionStatus;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TestSubmissionRequest {
    private List<SubmittedAnswerDto> answers;
    private UUID submitterAccountId;
    private UUID submittedTestId;
    private TestSubmissionStatus status;
    private long timeTakenInSeconds;
}
