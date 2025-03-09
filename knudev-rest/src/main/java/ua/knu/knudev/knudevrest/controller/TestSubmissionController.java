package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.assessmentmanagerapi.api.TestSubmissionApi;
import ua.knu.knudev.assessmentmanagerapi.dto.TestSubmissionResultsDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestSubmissionRequest;

import java.util.UUID;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestSubmissionController {
    private final TestSubmissionApi testSubmissionApi;

    @PostMapping("/submit")
    public TestSubmissionResultsDto submitTest(@RequestBody TestSubmissionRequest request) {
        return testSubmissionApi.submit(request);
    }

    @GetMapping("submission/{submissionId}/{submitterAccountId}/results")
    public TestSubmissionResultsDto getSubmissionResults(
            @PathVariable UUID submissionId, @PathVariable UUID submitterAccountId
    ) {
        return testSubmissionApi.getSubmissionResults(submissionId, submitterAccountId);
    }

}