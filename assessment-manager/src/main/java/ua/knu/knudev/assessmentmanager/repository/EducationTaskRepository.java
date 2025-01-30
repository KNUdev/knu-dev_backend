package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.EducationTask;

import java.util.UUID;

public interface EducationTaskRepository extends JpaRepository<EducationTask, UUID> {
}
