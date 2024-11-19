package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.TaskAssignment;

import java.util.Optional;
import java.util.UUID;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {
    boolean existsByVerificationCode(String verificationCode);
    boolean existsByAssignedAccountEmail(String assignedAccountEmail);
    Optional<TaskAssignment> findByAssignedAccountEmail(String assignedAccountEmail);
    int countByAssignedAccountEmail(String assignedAccountEmail);
}
