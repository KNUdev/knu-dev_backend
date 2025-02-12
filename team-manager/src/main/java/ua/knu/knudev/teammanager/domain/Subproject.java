package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubprojectType type;

    //todo status

    @Column(name = "resource_url")
    private String resourceUrl;

    //todo name better to represent active developers
    //todo maybe put to release
    @OneToMany(mappedBy = "subproject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubprojectAccount> activeDevelopers = new HashSet<>();

    @OneToMany(mappedBy = "subproject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Release> releases = new HashSet<>();
}
