package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.session.SprintDto;
import ua.knu.knudev.knudevrest.config.ExtendSprintRequestDto;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SprintController {

//    private final SprintService sprintService;

    /**
     * Retrieve detailed information for a specific sprint.
     * GET /api/sessions/{sessionId}/sprints/{sprintId}
     */
    @GetMapping("/sessions/{sessionId}/sprints/{sprintId}")
    //todo perhaps just the sprint. Or create Active sprint which extends from sprint
    public ActiveSprintDto getSprintDetails(@PathVariable UUID sessionId,
                                            @PathVariable UUID sprintId) {
        return sprintService.getActiveSprintDto(sessionId, sprintId);
    }

    /**
     * Extend the duration (deadline) of a sprint.
     * PUT /api/sprints/{sprintId}/extend
     */
    @PutMapping("/sprints/{sprintId}/extend")
    public SprintDto extendSprint(@PathVariable UUID sprintId,
                                  @RequestBody ExtendSprintRequestDto request) {
//        return sprintService.extendSprintDuration(sprintId, request.getAdditionalDays());
        return null;
    }
}
