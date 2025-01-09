package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.teammanager.domain.Project;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "team_manager", name = "project_release_info")
public class ProjectReleaseInfo {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column
    private LocalDate releaseDate;

    @Column
    private String projectDomain;

    @OneToOne(mappedBy = "projectReleaseInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Project project;

}
