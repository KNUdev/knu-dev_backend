package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;

public interface ClosedRecruitmentRepository extends JpaRepository<ClosedRecruitment, Integer> {
}
