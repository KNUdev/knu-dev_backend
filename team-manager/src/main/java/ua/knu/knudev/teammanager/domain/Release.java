package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column
    //todo Це перший комміт після минулого релізу або баг фіксу
    private LocalDate releaseStartDate;

    @Column
    private LocalDate releaseFinishDate;

    @Column(nullable = false)
    private String version;

    @ManyToOne
    @MapsId("subprojectId")
    @JoinColumn(name = "subproject_id", referencedColumnName = "id", nullable = false)
    private Subproject subproject;

    @Column(nullable = false)
    private String changesLogEn;

    @Column(nullable = false, updatable = false)
    private Integer aggregatedGithubCommitCount;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReleaseParticipation> releaseDevelopers = new HashSet<>();

}
