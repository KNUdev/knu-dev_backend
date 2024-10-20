package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "specialty")
public class Specialty {

    @Id
    @Column(nullable = false)
    private Double codeName;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "specialties", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Set<Department> departments;

}
