package ua.knu.knudev.taskmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.taskmanager.domain.Task;
import ua.knu.knudev.taskmanager.domain.TaskAssignment;
import ua.knu.knudev.taskmanager.domain.TaskAssignmentStatus;
import ua.knu.knudev.taskmanager.repository.TaskAssignmentRepository;
import ua.knu.knudev.taskmanager.repository.TaskRepository;
import ua.knu.knudev.taskmanagerapi.exception.TaskException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    @Value("${application.assignments.activation-expiry-in-days}")
    private Integer assignmentActivationExpiryInDays;

    @Transactional
    public TaskAssignment assignTaskToStudent(UUID accountId) {
        Task availableTask = taskRepository.findRandomNotAssignedTask()
                .orElseThrow(() -> new TaskException("No available tasks at the moment ", HttpStatus.NO_CONTENT));
        LocalDateTime currentDate = LocalDateTime.now();

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignedAccountId(accountId);
        assignment.setTask(availableTask);

        assignment.setVerificationCode(generateUniqueCode());
        assignment.setCreationDate(currentDate);
        assignment.setActivationExpiryDate(currentDate.plusDays(assignmentActivationExpiryInDays));
        assignment.setStatus(TaskAssignmentStatus.PENDING);

        return taskAssignmentRepository.save(assignment);
    }

    private String generateUniqueCode() {
        return "abc123";
    }
}
