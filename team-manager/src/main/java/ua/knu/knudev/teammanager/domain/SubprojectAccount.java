package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(schema = "team_management", name = "project_account")
public class SubprojectAccount {

    @EmbeddedId
    private SubprojectAccountId id;

    @ManyToOne
    @MapsId("subprojectId")
    @JoinColumn(name = "subproject_id", referencedColumnName = "id", nullable = false)
    private Subproject subproject;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Column(nullable = false)
    private LocalDate dateJoined;

    @Column
    private LocalDate dateLeft;

    private LocalDate lastCommitDate;
    private Integer totalCommits;
    private Integer totalLinesOfCodeWritten;
}