package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(schema = "assessment_management", name = "question_answer_variant")
public class QuestionAnswerVariant {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String enVariantBody;

    @Column(nullable = false)
    private Boolean isCorrectAnswer;

    @ManyToOne
    @JoinColumn(name = "test_question_id", referencedColumnName = "id", nullable = false)
    private TestQuestion testQuestion;
}
