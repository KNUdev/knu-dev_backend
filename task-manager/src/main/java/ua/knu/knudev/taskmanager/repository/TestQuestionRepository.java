package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.TestQuestion;

import java.util.List;
import java.util.UUID;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, UUID> {
    boolean existsByTestDomain_Id(UUID testDomainId);

    List<TestQuestion> findAllByTestDomain_Id(UUID testDomainId);

    boolean existsTestQuestionByEnQuestionBody(String enQuestionBody);

    List<TestQuestion> findAllByEnQuestionBody(String enQuestionBody);
}
