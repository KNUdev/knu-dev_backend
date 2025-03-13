package ua.knu.knudev.educationapi.api;

import ua.knu.knudev.educationapi.dto.session.SessionFullDto;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;
import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.util.List;
import java.util.UUID;

public interface SessionApi {
    //    SessionDto create(SessionCreationRequest sessionCreationRequest);
//    List<SprintDto> generateSprintsForProgram(UUID programId);
    SessionSprintPlanDto createSession(SessionCreationRequest request);
    void adjustSprintsDurations(List<SprintAdjustmentRequest> adjustments, UUID sessionId);
    void extendCurrentSprintDuration(UUID sprintId, Integer extensionDays);
}