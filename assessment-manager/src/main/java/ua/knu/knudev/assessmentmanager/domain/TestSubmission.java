package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.assessmentmanagerapi.constant.TestSubmissionStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "assessment_management", name = "test_submission")
public class TestSubmission {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "submitter_account_id", nullable = false)
    private UUID submitterAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id", nullable = false)
    private TestDomain testDomain;

    @Column(name = "time_taken_seconds")
    private Long timeTakenInSeconds;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", nullable = false)
    private TestSubmissionStatus submissionStatus;

    @Column(name = "raw_score")
    private Double rawScore;

    @Column(name = "percentage_score")
    private Double percentageScore;

    @OneToMany(mappedBy = "testSubmission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TestSubmissionAnswer> answers = new HashSet<>();

    public void addAnswer(TestSubmissionAnswer answer) {
        answer.setTestSubmission(this);
        this.answers.add(answer);
    }

    public void addAnswers(Collection<TestSubmissionAnswer> answers) {
        for (TestSubmissionAnswer answer : answers) {
            addAnswer(answer);
        }
    }

    @Transient
    public Duration getTimeTaken() {
        return (timeTakenInSeconds != null)
                ? Duration.ofSeconds(timeTakenInSeconds)
                : null;
    }
}
