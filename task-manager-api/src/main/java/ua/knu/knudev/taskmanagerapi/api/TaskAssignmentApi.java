package ua.knu.knudev.taskmanagerapi.api;

import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;

public interface TaskAssignmentApi {
    TaskAssignmentResponse assignTaskToAccount(String accountEmail);
}
