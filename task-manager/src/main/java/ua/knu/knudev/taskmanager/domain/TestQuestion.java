package ua.knu.knudev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.knu.knudev.taskmanager.domain.embeddable.QuestionAnswerVariant;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "task_management", name = "test_question")
public class TestQuestion {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(nullable = false)
    private String question;

    @ElementCollection
    @CollectionTable(
            name = "question_answer_variant",
            schema = "task_management",
            joinColumns = @JoinColumn(name = "test_question_id")
    )
    private Set<QuestionAnswerVariant> answerVariants = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id", nullable = false)
    private Test test;

}
