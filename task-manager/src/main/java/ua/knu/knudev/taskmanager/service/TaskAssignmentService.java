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
import ua.knu.knudev.taskmanagerapi.api.TaskAssignmentApi;
import ua.knu.knudev.taskmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.taskmanagerapi.exception.TaskException;
import ua.knu.knudev.taskmanagerapi.response.TaskAssignmentResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService implements TaskAssignmentApi {
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final AccountProfileApi accountProfileApi;
    @Value("${application.assignments.activation-expiry-in-days}")
    private Integer assignmentActivationExpiryInDays;

    @Override
    @Transactional
    public TaskAssignmentResponse assignTaskToAccount(String accountEmail) {
        assertAssignmentExists(accountEmail);

        LocalDateTime currentDate = LocalDateTime.now();
        AccountProfileDto account = accountProfileApi.getByEmail(accountEmail);

        Task availableTask = taskRepository.findRandomNotAssignedTaskByTechnicalRole(account.technicalRole())
                .orElseThrow(() -> new TaskException("No available tasks at the moment", HttpStatus.NOT_FOUND));

        TaskAssignment assignment = new TaskAssignment();
        String verificationCode = VerificationCodeGenerator.generateUniqueCode(taskAssignmentRepository);
        assignment.setAssignedAccountEmail(accountEmail);
        assignment.setTask(availableTask);
        assignment.setVerificationCode(verificationCode);
        assignment.setCreationDate(currentDate);
        assignment.setActivationExpiryDate(currentDate.plusDays(assignmentActivationExpiryInDays));
        assignment.setStatus(TaskAssignmentStatus.PENDING);

        TaskAssignment savedTaskAssignment = taskAssignmentRepository.save(assignment);
        return new TaskAssignmentResponse(savedTaskAssignment.getVerificationCode());
    }

    private void assertAssignmentExists(String accountEmail) {
        boolean assignmentExists = taskAssignmentRepository.existsByAssignedAccountEmail(accountEmail);
        if (assignmentExists) {
            throw new TaskAssignmentException("Task for this account is already assigned", HttpStatus.BAD_REQUEST);
        }
    }

    private static class VerificationCodeGenerator {
        public static String generateUniqueCode(TaskAssignmentRepository taskAssignmentRepository) {
            String code;
            do {
                code = generateRandomCode();
            } while (taskAssignmentRepository.existsByVerificationCode(code));
            return code;
        }

        private static String generateRandomCode() {
            int codeLength = 6;
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            SecureRandom random = new SecureRandom();
            StringBuilder codeBuilder = new StringBuilder(codeLength);
            for (int i = 0; i < codeLength; i++) {
                int index = random.nextInt(characters.length());
                codeBuilder.append(characters.charAt(index));
            }
            return codeBuilder.toString();
        }

    }
}
