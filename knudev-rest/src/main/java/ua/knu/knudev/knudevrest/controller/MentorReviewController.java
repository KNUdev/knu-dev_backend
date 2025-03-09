package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.dto.SprintAttemptDto;
import ua.knu.knudev.knudevrest.config.ReviewRequestDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MentorReviewController {

//    private final MentorReviewService reviewService;

    /**
     * Retrieve pending submissions for a sprint review.
     * GET /api/sprints/{sprintId}/submissions?reviewStatus=pending
     */
    @GetMapping("/sprints/{sprintId}/submissions")
    public List<SprintAttemptDto> getPendingSubmissions(@PathVariable UUID sprintId,
                                                        @RequestParam(required = false) String reviewStatus) {
        return null;
//        return reviewService.getPendingSubmissions(sprintId, reviewStatus);
    }

    /**
     * Mentor reviews a specific submission attempt.
     * POST /api/sprints/submissions/{attemptId}/review?mentorId={mentorId}
     */
    @PostMapping("/sprints/submissions/{attemptId}/review")
    public SprintAttemptDto reviewSubmission(@PathVariable UUID attemptId,
                                             @RequestParam UUID mentorId,
                                             @RequestBody ReviewRequestDto reviewRequest) {
//        return reviewService.reviewSubmission(attemptId, mentorId, reviewRequest);
        return null;
    }
}