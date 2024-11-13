package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.TaskAssignment;

import java.util.UUID;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {
}
