package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.SubprojectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(schema = "team_management", name = "subproject")
public class Subproject {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubprojectType type;

    @Column(name = "resource_url")
    private String resourceUrl;

    @OneToMany(mappedBy = "subproject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Release> releases = new HashSet<>();

    @OneToMany(mappedBy = "subproject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubprojectAccount> allDevelopers = new HashSet<>();

    public void associateDevelopersWithSubproject(Set<SubprojectAccount> subprojectAccounts) {
        for (SubprojectAccount subprojectAccount : subprojectAccounts) {
            subprojectAccount.setSubproject(this);
        }
    }

    public void associateReleasesWithSubproject(Set<Release> releases) {
        for (Release release : releases) {
            release.setSubproject(this);
        }
    }

    public void addDevelopers(Set<SubprojectAccount> subprojectAccounts) {
        for (SubprojectAccount subprojectAccount : subprojectAccounts) {
            this.allDevelopers.add(subprojectAccount);
            subprojectAccount.setSubproject(this);
        }
    }
}
