package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.QuestionAnswerVariant;

import java.util.UUID;

public interface QuestionAnswerVariantRepository extends JpaRepository<QuestionAnswerVariant, UUID> {
}
