package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer> {
}
