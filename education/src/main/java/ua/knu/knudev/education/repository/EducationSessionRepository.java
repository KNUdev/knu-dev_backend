package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.session.EducationSession;

import java.util.UUID;

public interface EducationSessionRepository extends JpaRepository<EducationSession, UUID> {
}
