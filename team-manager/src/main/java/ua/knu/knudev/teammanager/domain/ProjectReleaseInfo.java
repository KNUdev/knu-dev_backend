package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "team_management", name = "project_release_info")
public class ProjectReleaseInfo {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column
    private LocalDate releaseDate;

    @Column
    private String projectDomain;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Project project;

}
