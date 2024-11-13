package ua.knu.knudev.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.taskmanager.domain.Task;
import ua.knu.knudev.taskmanager.repository.TaskRepository;
import ua.knu.knudev.taskmanagerapi.api.TaskAPI;
import ua.knu.knudev.taskmanagerapi.dto.TaskDto;
import ua.knu.knudev.taskmanagerapi.exception.TaskException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskAPI {
    private final TaskRepository taskRepository;

    public String create(String filename, AccountRole targetRole) {
//        if(ObjectUtils.isEmpty(targetRole)) {
//            throw new
//        }
        checkTaskExistenceByFilename(filename);

        //todo checks

        Task task = new Task();
        task.setFilename(filename);
        task.setTargetRole(targetRole);
        task.setAdditionDate(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        return savedTask.getFilename();
    }

    @Override
    public TaskDto getById(String taskId) {
        return null;
    }

    public boolean existsByFilename(String filename) {
        return taskRepository.existsByFilename(filename);
    }

    private void checkTaskExistenceByFilename(String filename) {
        boolean taskExists = existsByFilename(filename);
        if (taskExists) {
            throw new TaskException(
                    String.format("Task %s already exists", filename), HttpStatus.BAD_REQUEST
            );
        }

    }

}
