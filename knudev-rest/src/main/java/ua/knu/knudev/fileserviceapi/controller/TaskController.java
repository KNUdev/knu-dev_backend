package ua.knu.knudev.fileserviceapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.taskmanagerapi.api.TaskAPI;
import ua.knu.knudev.taskmanagerapi.dto.TaskDto;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskAPI taskAPI;

    @GetMapping
    public TaskDto getTaskById(@RequestParam String taskId) {
        return taskAPI.getById(taskId);
    }

}
