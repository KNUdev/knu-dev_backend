package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.teammanager.domain.embeddable.ProjectAccountId;

import java.time.LocalDate;

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
    @MapsId("projectId")
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Column(nullable = false)
    private LocalDate dateJoined;
}