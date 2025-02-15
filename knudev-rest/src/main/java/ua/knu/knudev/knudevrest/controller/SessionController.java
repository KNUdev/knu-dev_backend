package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.dto.SessionFullDto;
import ua.knu.knudev.educationapi.request.CreateSessionRequestDto;
import ua.knu.knudev.educationapi.session.SessionSprintPlanDto;
import ua.knu.knudev.knudevrest.config.MentorUpdateRequestDto;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {

//    private final SprintService sprintService;
//    private final SessionService sessionService;

    /**
     * Preview the sprint plan for a selected program.
     * GET /api/sprints/preview?programId={programId}
     */
    @GetMapping("/sprints/preview")
    public SessionSprintPlanDto previewSprintPlan(@RequestParam UUID programId) {
//        return sprintService.generateSessionSprintPlan(programId);
    }

    /**
     * Create a new session using the adjusted sprint plan and selected mentors.
     * POST /api/sessions
     */
    @PostMapping("/sessions")
    public SessionFullDto createSession(@RequestBody CreateSessionRequestDto request) {
        return null;
//        return sessionService.createSession(request);
    }

    /**
     * Update mentors for an existing session.
     * PUT /api/sessions/{sessionId}/mentors
     */
    @PutMapping("/sessions/{sessionId}/mentors")
    public SessionFullDto updateSessionMentors(@PathVariable UUID sessionId,
                                               @RequestBody MentorUpdateRequestDto request) {
//        return sessionService.updateMentors(sessionId, request.getMentorIds());
        return null;
    }

    /**
     * Retrieve full details of an active session.
     * GET /api/sessions/{sessionId}
     */
    @GetMapping("/sessions/{sessionId}")
    public SessionFullDto getSession(@PathVariable UUID sessionId) {
//        return sessionService.getSessionFullDto(sessionId);
        return null;
    }
}
