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
@Entity
@Builder
@Table(schema = "assessment_management", name = "test_question")
public class TestQuestion {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String enQuestionBody;

    @OneToMany(mappedBy = "testQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionAnswerVariant> answerVariants = new HashSet<>();

    //todo maybe remove @JoinColumn
    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id", nullable = false)
    private TestDomain testDomain;

}
