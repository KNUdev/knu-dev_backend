package ua.knu.knudev.education.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.educationapi.enums.AttemptStatus;

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_submission_id", referencedColumnName = "id")
    private SprintSubmission sprintSubmission;

    @Column(nullable = false)
    private int attemptNumber;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "submission_file", nullable = false)
    private String submissionFilename;

    @Column
    private String mentorFeedbackEn;

    @Column
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus attemptStatus;

    // --- Concurrency control fields ---
    // When a mentor picks this attempt for review, this field is set to their UUID.
//    @Column(name = "locked_by", updatable = false)
//    private UUID lockedBy;
//
//    // Timestamp when the lock was acquired.
//    @Column(name = "lock_time", updatable = false)
//    private LocalDateTime lockTime;
//
//    // Optimistic locking version field.
//    @Version
//    private Long version;
}
