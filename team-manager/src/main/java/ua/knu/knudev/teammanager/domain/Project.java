package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import java.time.LocalDate;
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
public class Project {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "name_en")),
            @AttributeOverride(name = "uk", column = @Column(name = "name_uk"))
    })
    private MultiLanguageField name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "description_en")),
            @AttributeOverride(name = "uk", column = @Column(name = "description_uk"))
    })
    private MultiLanguageField description;

    @Column
    private String avatarFilename;

    @Column(nullable = false, updatable = false)
    private LocalDate startedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @ElementCollection(targetClass = ProjectTag.class)
    @CollectionTable(schema = "team_management", name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tags", nullable = false)
    private Set<ProjectTag> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(schema = "team_management", name = "github_repo_links", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "github_repo_link", nullable = false)
    private Set<String> githubRepoLinks = new HashSet<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectReleaseInfo releaseInfo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectAccount> projectAccounts = new HashSet<>();

}
