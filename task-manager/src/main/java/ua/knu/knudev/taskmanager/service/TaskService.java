package ua.knu.knudev.taskmanager.service;

import org.springframework.stereotype.Service;
import ua.knu.knudev.taskmanagerapi.api.TaskAPI;
import ua.knu.knudev.taskmanagerapi.dto.TaskDto;

@Service
public class TaskService implements TaskAPI {

    @Override
    public TaskDto getById(String taskId) {
        return null;
    }

}
