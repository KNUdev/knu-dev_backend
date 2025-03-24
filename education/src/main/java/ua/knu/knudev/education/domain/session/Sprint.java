package ua.knu.knudev.education.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.EducationProgram;
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
@BatchSize(size = 100)
public class Sprint {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private EducationSession session;

    @Column(nullable = false)
    private int orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintType type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private int durationInDays;

    @Transient
    public LocalDateTime getDeadline() {
        return startDate.plusDays(durationInDays);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ProgramTopic programTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private ProgramModule programModule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private ProgramSection programSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private EducationProgram program;

    // Optionally, if the sprint includes a test, store its proxy.
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "test_id")
//    private EducationTestProxy educationTest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SprintStatus status;
}
