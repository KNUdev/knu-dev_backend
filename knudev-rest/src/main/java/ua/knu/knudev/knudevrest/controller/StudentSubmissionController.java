package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.session.SprintSubmissionDto;
import ua.knu.knudev.knudevrest.config.TaskSubmissionRequestDto;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentSubmissionController {

//    private final StudentSubmissionService submissionService;

    /**
     * Student submits a task for a sprint.
     * POST /api/sessions/{sessionId}/sprints/{sprintId}/submissions?participantId={participantId}
     */
    @PostMapping("/sessions/{sessionId}/sprints/{sprintId}/submissions")
    public SprintSubmissionDto submitTask(@PathVariable UUID sessionId,
                                          @PathVariable UUID sprintId,
                                          @RequestParam UUID participantId,
                                          @RequestBody TaskSubmissionRequestDto request) {
//        return submissionService.submitTask(sessionId, sprintId, participantId, request);
        return null;
    }
}
