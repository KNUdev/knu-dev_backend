package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.EducationProgram;

import java.util.UUID;

public interface EducationProgramRepository extends JpaRepository<EducationProgram, UUID> {
}
