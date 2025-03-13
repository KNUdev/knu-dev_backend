package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.TestQuestion;

import java.util.List;
import java.util.UUID;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, UUID> {
    boolean existsByTestDomain_Id(UUID testDomainId);

    List<TestQuestion> findAllByTestDomain_Id(UUID testDomainId);

    boolean existsTestQuestionByEnQuestionBody(String enQuestionBody);

    List<TestQuestion> findAllByEnQuestionBody(String enQuestionBody);

    List<TestQuestion> findAllByIdIn(List<UUID> ids);
}
