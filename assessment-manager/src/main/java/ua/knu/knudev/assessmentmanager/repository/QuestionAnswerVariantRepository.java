package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.QuestionAnswerVariant;

import java.util.UUID;

public interface QuestionAnswerVariantRepository extends JpaRepository<QuestionAnswerVariant, UUID> {
    boolean existsQuestionAnswerVariantByTestQuestion_Id(UUID testQuestionId);

    boolean existsQuestionAnswerVariantByEnVariantBody(String enVariantBody);
}
