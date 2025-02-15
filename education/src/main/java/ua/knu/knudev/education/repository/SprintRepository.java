package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.Sprint;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
}
