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

    //todo maybe desctiption
    //todo add version. major.minor.bf

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Project project;

    //todo logs
    private String changesEn;

    private Integer aggregatedGithubCommitCount;

    //todo maybe add more interesting metadata

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReleaseParticipation> releaseDevelopers = new HashSet<>();

}
