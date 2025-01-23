package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.TaskAssignment;

import java.util.Optional;
import java.util.UUID;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {
    boolean existsByVerificationCode(String verificationCode);
    boolean existsByAssignedAccountEmail(String assignedAccountEmail);
    Optional<TaskAssignment> findByAssignedAccountEmail(String assignedAccountEmail);
    int countByAssignedAccountEmail(String assignedAccountEmail);
}
