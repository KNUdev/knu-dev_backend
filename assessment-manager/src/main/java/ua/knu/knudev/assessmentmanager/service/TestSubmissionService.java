package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanagerapi.api.TestSubmissionApi;
import ua.knu.knudev.assessmentmanagerapi.dto.TestSubmissionResultsDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestSubmissionRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestSubmissionService implements TestSubmissionApi {
    @Override
    public TestSubmissionResultsDto submit(TestSubmissionRequest submissionRequest) {
        /*
            1. Check if user has not submitted the test. If true - throw exc
            2. Check if status is not failed -
            3. Map request to TestSubmissionAnswer
            4. Calculate result
            5. Save everything
            6. Return response
         */
        return null;
    }

    @Override
    public TestSubmissionResultsDto getSubmissionResults(UUID submissionId, UUID submitterAccountId) {
        /*
            1. See if user in Submission has completed the test (щоб повертати тільки результати по тестам саме юзера, який його пройшшов)
         */
        return null;
    }


}
