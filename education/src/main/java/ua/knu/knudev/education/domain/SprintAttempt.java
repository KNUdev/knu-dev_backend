package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.educationapi.dto.AttemptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "education", name = "sprint_attempt")
public class SprintAttempt {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sprint_submission_id", referencedColumnName = "id")
    private SprintSubmission sprintSubmission;

    // Sequential attempt number (first, second, etc.)
    @Column(nullable = false)
    private int attemptNumber;

    // Timestamp of submission.
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // Reference to the submitted file (e.g., filename or URL).
    @Column(name = "submission_file", nullable = false)
    private String submissionFile;

    // Mentor feedback text.
    @Column
    private String mentorFeedback;

    // Score given by the mentor (if applicable).
    @Column
    private Integer score;

    // Attempt status: e.g., SUBMITTED, PENDING_REVIEW, PASSED, NEEDS_REDO.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus attemptStatus;

    // --- Concurrency control fields ---

    // When a mentor picks this attempt for review, this field is set to their UUID.
    @Column(name = "locked_by", updatable = false)
    private UUID lockedBy;

    // Timestamp when the lock was acquired.
    @Column(name = "lock_time", updatable = false)
    private LocalDateTime lockTime;

    // Optimistic locking version field.
    @Version
    private Long version;
}
