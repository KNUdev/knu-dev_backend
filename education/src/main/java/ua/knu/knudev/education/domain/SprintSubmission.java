package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "education", name = "sprint_submission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sprint_id", "participant_id"}))
public class SprintSubmission {

    @Id
    @UuidGenerator
    private UUID id;

    // The sprint for which this submission is made.
    @ManyToOne(optional = false)
    @JoinColumn(name = "sprint_id", referencedColumnName = "id")
    private Sprint sprint;

    // The student's account ID.
    @Column(name = "participant_id", nullable = false)
    private UUID participantId;

    // History of submission attempts (ordered by attempt number).
    @OneToMany(mappedBy = "sprintSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("attemptNumber ASC")
    private List<SprintAttempt> attempts = new ArrayList<>();
}
