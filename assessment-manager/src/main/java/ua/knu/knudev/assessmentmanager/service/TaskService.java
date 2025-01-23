package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.assessmentmanager.domain.TaskDomain;
import ua.knu.knudev.assessmentmanager.repository.TaskRepository;
import ua.knu.knudev.assessmentmanagerapi.api.TaskAPI;
import ua.knu.knudev.assessmentmanagerapi.dto.TaskDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskAPI {
    private final TaskRepository taskRepository;

    public String create(String filename, AccountTechnicalRole targetTechnicalRole) {
//        if(ObjectUtils.isEmpty(targetRole)) {
//            throw new
//        }
        checkTaskExistenceByFilename(filename);

        //todo checks

        TaskDomain task = new TaskDomain();
        task.setFilename(filename);
        task.setTargetTechnicalRole(targetTechnicalRole);
        task.setAdditionDate(LocalDateTime.now());

        TaskDomain savedTask = taskRepository.save(task);

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
                    String.format("TaskDomain %s already exists", filename), HttpStatus.BAD_REQUEST
            );
        }

    }

}
