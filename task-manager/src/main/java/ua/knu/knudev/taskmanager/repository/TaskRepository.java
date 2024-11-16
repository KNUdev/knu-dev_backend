package ua.knu.knudev.taskmanager.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.taskmanager.domain.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t WHERE t.targetTechnicalRole = :role AND t.id NOT IN (" +
            "SELECT ta.task.id FROM TaskAssignment ta) " +
            "ORDER BY function('random')")
    Optional<Task> findRandomNotAssignedTaskByTechnicalRole(@Param("role") AccountTechnicalRole role);

    boolean existsByFilename(String filename);

}
