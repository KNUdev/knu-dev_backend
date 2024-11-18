package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
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
    private String nameInEnglish;

    @Column(nullable = false, updatable = false, unique = true)
    private String nameInUkrainian;

    @ManyToMany(mappedBy = "specialties", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Department> departments = new HashSet<>();

    public Specialty(Double codeName, String nameInEnglish, String nameInUkrainian) {
        this.codeName = codeName;
        this.nameInEnglish = nameInEnglish;
        this.nameInUkrainian = nameInUkrainian;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialty that)) return false;
        return Objects.equals(codeName, that.codeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeName);
    }
}
