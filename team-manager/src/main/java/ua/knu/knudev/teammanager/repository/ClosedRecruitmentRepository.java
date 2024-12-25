package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;

import java.util.UUID;

public interface ClosedRecruitmentRepository extends JpaRepository<ClosedRecruitment, UUID> {
}
