package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "team_management", name = "release")
public class Release {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column
    private LocalDateTime initializedAt;

    @Column
    private LocalDateTime releaseFinishDate;

    @Column(nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subproject_id", referencedColumnName = "id", nullable = false)
    private Subproject subproject;

    @Column(nullable = false)
    private String changesLogEn;

    @Column(nullable = false, updatable = false)
    private Integer aggregatedGithubCommitCount;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReleaseParticipation> releaseDevelopers = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void associateReleaseWithParticipations() {
        for (ReleaseParticipation participation : releaseDevelopers) {
            participation.setRelease(this);
        }
    }
}