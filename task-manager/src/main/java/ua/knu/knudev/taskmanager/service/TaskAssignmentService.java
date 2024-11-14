package ua.knu.knudev.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.taskmanager.domain.Task;
import ua.knu.knudev.taskmanager.domain.TaskAssignment;
import ua.knu.knudev.taskmanager.domain.TaskAssignmentStatus;
import ua.knu.knudev.taskmanager.repository.TaskAssignmentRepository;
import ua.knu.knudev.taskmanager.repository.TaskRepository;
import ua.knu.knudev.taskmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.taskmanagerapi.exception.TaskException;
import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final AccountProfileApi accountProfileApi;
    @Value("${application.assignments.activation-expiry-in-days}")
    private Integer assignmentActivationExpiryInDays;

    public TaskAssignmentResponse assignTaskToStudent(String accountEmail) {
        assertAssignmentExists(accountEmail);

        LocalDateTime currentDate = LocalDateTime.now();
        AccountProfileDto account = accountProfileApi.getByEmail(accountEmail);

        Task availableTask = taskRepository.findRandomNotAssignedTaskByTechnicalRole(account.technicalRole())
                .orElseThrow(() -> new TaskException("No available tasks at the moment ", HttpStatus.NO_CONTENT));

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignedAccountEmail(accountEmail);
        assignment.setTask(availableTask);
        assignment.setVerificationCode(generateUniqueCode());
        assignment.setCreationDate(currentDate);
        assignment.setActivationExpiryDate(currentDate.plusDays(assignmentActivationExpiryInDays));
        assignment.setStatus(TaskAssignmentStatus.PENDING);

        TaskAssignment savedTaskAssignment = taskAssignmentRepository.save(assignment);
        return new TaskAssignmentResponse(savedTaskAssignment.getVerificationCode());
    }

    private void assertAssignmentExists(String accountEmail) {
        boolean assignmentExists = taskAssignmentRepository.existsByAssignedAccountEmail(accountEmail);
        if(assignmentExists) {
            throw new TaskAssignmentException("Assignment already exists", HttpStatus.BAD_REQUEST);
        }

    }

    private String generateUniqueCode() {
        return "abc123";
    }
}
