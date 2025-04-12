package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.educationapi.api.SprintApi;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;
import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/session")
public class AdminEducationSessionController {
    private final SessionApi sessionApi;

    @PostMapping
    public void create(@RequestBody SessionCreationRequest sessionCreationRequest) {
        sessionApi.createSession(sessionCreationRequest);
    }

    @GetMapping
    public void get() {

    }

    @PostMapping("/mentors/update")
    public void updateMentors() {
        //todo
    }

    @PatchMapping("{sessionId}/sprints/adjust")
    public void adjustSprintsDeadlines(@PathVariable UUID sessionId,
                                       @RequestBody List<SprintAdjustmentRequest> sprintsAdjustments) {
        sessionApi.adjustSprintsDurations(sprintsAdjustments, sessionId);
    }

    @PatchMapping("/sprints/{sprintId}/extend")
    public void extendSprintDeadline(@PathVariable UUID sprintId,
                                     @RequestBody Integer extensionDays) {
        sessionApi.extendSprintDuration(sprintId, extensionDays);
    }




}
