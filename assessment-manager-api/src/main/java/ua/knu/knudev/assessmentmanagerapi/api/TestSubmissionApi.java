package ua.knu.knudev.assessmentmanagerapi.api;

import ua.knu.knudev.assessmentmanagerapi.dto.TestSubmissionResultsDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestSubmissionRequest;

import java.util.UUID;

public interface TestSubmissionApi {
    TestSubmissionResultsDto submit(TestSubmissionRequest submissionRequest);
    TestSubmissionResultsDto getSubmissionResults(UUID submissionId, UUID submitterAccountId);
}
