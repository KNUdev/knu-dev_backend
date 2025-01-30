package ua.knu.knudev.assessmentmanagerapi.api;

import ua.knu.knudev.assessmentmanagerapi.dto.TaskDto;

public interface TaskAPI {
    TaskDto getById(String taskId);
}
