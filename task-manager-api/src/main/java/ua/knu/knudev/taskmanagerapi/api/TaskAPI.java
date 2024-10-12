package ua.knu.knudev.taskmanagerapi.api;

import ua.knu.knudev.taskmanagerapi.dto.TaskDto;

public interface TaskAPI {

    TaskDto getById(String taskId);
}
