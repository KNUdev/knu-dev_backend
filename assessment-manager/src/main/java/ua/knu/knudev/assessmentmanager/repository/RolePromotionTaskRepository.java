package ua.knu.knudev.assessmentmanager.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.assessmentmanager.domain.RolePromotionTask;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RolePromotionTaskRepository extends JpaRepository<RolePromotionTask, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM RolePromotionTask t WHERE t.targetTechnicalRole = :role AND t.id NOT IN (" +
            "SELECT ta.task.id FROM TaskAssignment ta) " +
            "ORDER BY function('random') LIMIT 1")
    Optional<RolePromotionTask> findRandomNotAssignedTaskByTechnicalRole(@Param("role") AccountTechnicalRole role);

    boolean existsByTaskFilename(String taskFilename);

    Optional<Set<RolePromotionTask>> getAllByCreatorAccountEmail(String creatorAccountEmail);
}
