package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.RolePromotionConditions;

import java.util.UUID;

public interface RolePromotionConditionRepository extends JpaRepository<RolePromotionConditions, UUID> {
}
