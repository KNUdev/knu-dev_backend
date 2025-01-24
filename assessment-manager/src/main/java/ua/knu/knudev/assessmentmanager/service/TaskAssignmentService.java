package ua.knu.knudev.assessmentmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanager.domain.RolePromotionTask;
import ua.knu.knudev.assessmentmanager.domain.TaskAssignment;
import ua.knu.knudev.assessmentmanager.domain.TaskAssignmentStatus;
import ua.knu.knudev.assessmentmanager.repository.RolePromotionTaskRepository;
import ua.knu.knudev.assessmentmanager.repository.TaskAssignmentRepository;
import ua.knu.knudev.assessmentmanagerapi.api.TaskAssignmentApi;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;
import ua.knu.knudev.assessmentmanagerapi.response.TaskAssignmentResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAssignmentService implements TaskAssignmentApi {
    private final RolePromotionTaskRepository rolePromotionTaskRepository;
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

        RolePromotionTask availableTask = rolePromotionTaskRepository
                .findRandomNotAssignedTaskByTechnicalRole(account.technicalRole())
                .orElseThrow(() -> {
                    String errorMessage = String.format("No available tasks at the moment for role %S. " +
                            "Please contact support.", account.technicalRole());
                    log.error("No available tasks at the moment for account with email: {}, role: {}",
                            account.email(), account.technicalRole());
                    return new TaskException(errorMessage, HttpStatus.NOT_FOUND);
                });

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
            throw new TaskAssignmentException("TaskDomain for this account is already assigned", HttpStatus.BAD_REQUEST);
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
            //todo put to config
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
