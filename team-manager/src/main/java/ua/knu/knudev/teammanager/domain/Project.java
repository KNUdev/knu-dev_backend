package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "project")
@Builder
@BatchSize(size = 10)
public class Project {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "description_en")),
            @AttributeOverride(name = "uk", column = @Column(name = "description_uk"))
    })
    private MultiLanguageField description;

    @Column
    private String banner;

    @Column
    private LocalDate startedAt;

    @Column
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @BatchSize(size = 40)
    @ElementCollection(targetClass = ProjectTag.class, fetch = FetchType.EAGER)
    @CollectionTable(schema = "team_management", name = "tag", joinColumns = @JoinColumn(name = "project_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tags", nullable = false)
    private Set<ProjectTag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "architect_account_id", referencedColumnName = "id", updatable = false)
    private AccountProfile architect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_account_id", referencedColumnName = "id")
    private AccountProfile supervisor;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subproject> subprojects = new HashSet<>();

    private void addSubproject(Subproject subproject) {
        subprojects.add(subproject);
        subproject.setProject(this);
    }

    public void addSubprojects(Set<Subproject> subprojects) {
        for (Subproject subproject : subprojects) {
            addSubproject(subproject);
        }
    }

}
