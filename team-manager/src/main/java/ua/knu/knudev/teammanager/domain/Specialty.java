package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "en_name")),
            @AttributeOverride(name = "uk", column = @Column(name = "uk_name"))
    })
    private MultiLanguageField name;

    @ManyToMany(mappedBy = "specialties", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Department> departments = new HashSet<>();

    public Specialty(Double codeName, String nameInEnglish, String nameInUkrainian) {
        this.codeName = codeName;
        this.name = new MultiLanguageField(nameInEnglish, nameInUkrainian);
    }

}
