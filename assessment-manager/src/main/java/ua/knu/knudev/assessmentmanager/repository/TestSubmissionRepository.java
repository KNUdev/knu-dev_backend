package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.TestSubmission;

import java.util.UUID;

public interface TestSubmissionRepository extends JpaRepository<TestSubmission, UUID> {

}
