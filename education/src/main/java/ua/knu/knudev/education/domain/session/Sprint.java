package ua.knu.knudev.education.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "education", name = "sprint")
public class Sprint {

    @Id
    @UuidGenerator
    private UUID id;

    // Back reference to the owning session.
    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private EducationSession educationSession;

    // Order in the session.
    @Column(nullable = false)
    private int orderIndex;

    // Sprint type determines which reference is applicable.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintType sprintType;

    // When the sprint starts (set upon activation).
    @Column(nullable = false)
    private LocalDateTime startDate;

    // Duration (in days) for completing this sprint.
    @Column(nullable = false)
    private int durationDays;

    // Computed deadline; not stored persistently.
    @Transient
    public LocalDateTime getDeadline() {
        return startDate.plusDays(durationDays);
    }

    // For a TOPIC sprint.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ProgramTopic programTopic;

    // For a MODULE_FINAL sprint.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private ProgramModule programModule;

    // For a SECTION_FINAL sprint.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private ProgramSection programSection;

    // Optionally, if the sprint includes a test, store its proxy.
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "test_id")
//    private EducationTestProxy educationTest;

    // Sprint status: FUTURE (not started), ACTIVE (in progress), COMPLETED (ended)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintStatus sprintStatus;
}
