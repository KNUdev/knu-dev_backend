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

}
