package ua.knu.knudev.assessmentmanagerapi.api;

import ua.knu.knudev.assessmentmanagerapi.response.TaskAssignmentResponse;

public interface TaskAssignmentApi {
    TaskAssignmentResponse assignTaskToAccount(String accountEmail);
}
