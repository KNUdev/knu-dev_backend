package ua.knu.knudev.educationapi.api;

import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SprintApi {
    void adjustSprintsDurations(List<SprintAdjustmentRequest> adjustments, UUID sessionId);
}
