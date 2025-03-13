package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(schema = "assessment_management", name = "test")
public class TestDomain {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String enName;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private Integer maxRowScore;

    @OneToMany(mappedBy = "testDomain", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestion> testQuestions = new HashSet<>();

    public void associateTestWithQuestionsAndVariants() {
        Set<TestQuestion> questions = this.getTestQuestions();
        questions.forEach(question -> question.setTestDomain(this));

        questions.forEach(testQuestion -> testQuestion
                .getAnswerVariants()
                .forEach(variant -> variant.setTestQuestion(testQuestion))
        );
    }

    public void updateMaxRowScore() {
        this.maxRowScore = calculateMaxRowScore();
    }

    private int calculateMaxRowScore() {
        return Math.toIntExact(
                this.testQuestions.stream()
                        .map(TestQuestion::getAnswerVariants)
                        .flatMap(Set::stream)
                        .filter(QuestionAnswerVariant::getIsCorrectAnswer)
                        .count()
        );
    }

}
