package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.taskmanagerapi.api.TaskAssignmentApi;
import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/assign")
public class TaskAssignmentController {

    private final TaskAssignmentApi taskAssignmentApi;

    //todo somehow download the assignment
    @PostMapping("/to/{email}")
    public TaskAssignmentResponse assignTask(@PathVariable(name = "email") String accountEmail) {
        return taskAssignmentApi.assignTaskToAccount(accountEmail);
    }
}
