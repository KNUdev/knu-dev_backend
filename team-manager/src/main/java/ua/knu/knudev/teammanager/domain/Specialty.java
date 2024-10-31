package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "specialty")
@Builder
public class Specialty {

    @Id
    @Column(name = "code_name", nullable = false, updatable = false)
    private Double codeName;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "specialties", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Department> departments = new HashSet<>();

}
