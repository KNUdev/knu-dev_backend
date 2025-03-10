package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.session.Sprint;

import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {
}
