package ua.knu.knudev.education.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.educationapi.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(schema = "education", name = "education_session")
public class EducationSession {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    private EducationProgram educationProgram;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    private LocalDateTime estimatedEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Sprint> sprints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "education_session_participants",
            schema = "education",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "participant_id")
    private Set<UUID> participantIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "education_session_mentors",
            schema = "education",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "mentor_id")
    private Set<UUID> mentorIds = new HashSet<>();

    public void addSprint(Sprint sprint) {
        if(CollectionUtils.isEmpty(sprints)) {
            sprints = new ArrayList<>();
        }
        sprints.add(sprint);
        sprint.setSession(this);
    }
}
