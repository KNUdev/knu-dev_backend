package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ua.knu.knudev.education.domain.EducationSession;

import java.util.UUID;

public interface EducationSessionRepository extends JpaRepository<EducationSession, UUID> {
}
