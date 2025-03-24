package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "assessment_management", name = "test_submission_answer")
public class TestSubmissionAnswer {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_submission_id", referencedColumnName = "id", nullable = false)
    private TestSubmission testSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_question_id", referencedColumnName = "id", nullable = false)
    private TestQuestion testQuestion;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            schema = "assessment_management",
            name = "test_submission_answer_variant",
            joinColumns = @JoinColumn(name = "submission_answer_id"),
            inverseJoinColumns = @JoinColumn(name = "variant_id")
    )
    private Set<QuestionAnswerVariant> chosenVariants = new HashSet<>();

    public void addChosenVariant(QuestionAnswerVariant variant) {
        this.chosenVariants.add(variant);
    }
}
