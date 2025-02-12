package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.teammanager.domain.embeddable.ProjectAccountId;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(schema = "team_management", name = "project_account")
public class ProjectAccount {

    @EmbeddedId
    private ProjectAccountId id;

    @ManyToOne
    @MapsId("subprojectId")
    @JoinColumn(name = "subproject_id", referencedColumnName = "id", nullable = false)
    private Subproject project;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Column(nullable = false)
    private LocalDate dateJoined;

    @Column
    private LocalTime dateLeft;

    //todo total commits
    //todo total lines of code
    //todo add more metadata
}