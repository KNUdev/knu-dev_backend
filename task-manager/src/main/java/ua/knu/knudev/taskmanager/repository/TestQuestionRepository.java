package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.TestQuestion;

import java.util.UUID;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, UUID> {
}
