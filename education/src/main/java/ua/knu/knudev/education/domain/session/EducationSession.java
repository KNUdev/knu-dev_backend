package ua.knu.knudev.education.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.educationapi.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "education", name = "education_session")
public class EducationSession {

    @Id
    @UuidGenerator
    private UUID id;

    // Session is based on an existing education program.
    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    private EducationProgram educationProgram;

    // When the session starts (e.g., after recruitment ends)
    @Column(nullable = false, updatable = false)
    private LocalDateTime sessionStartDate;

    // Optionally, when the session ends
    private LocalDateTime sessionEndDate;

    // Status of the session (e.g., CREATED, ONGOING, COMPLETED, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    // Ordered chain of sprints (ordered by orderIndex)
    @OneToMany(mappedBy = "educationSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Sprint> sprints = new ArrayList<>();

    // Participants (stored as account UUIDs)
    @ElementCollection
    @CollectionTable(name = "education_session_participants", schema = "education", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "participant_id", nullable = false)
    private Set<UUID> participantIds = new HashSet<>();

    // Mentors (stored as account UUIDs)
    @ElementCollection
    @CollectionTable(name = "education_session_mentors", schema = "education", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "mentor_id", nullable = false)
    private Set<UUID> mentorIds = new HashSet<>();
}
