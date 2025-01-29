package ua.knu.knudev.education.domain.bridge;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.program.ProgramSection;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "program_section_mapping",
        schema = "education",
        uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "section_id"})
)
@Builder
public class ProgramSectionMapping {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    private EducationProgram educationProgram;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private ProgramSection section;

    @Column(nullable = false)
    private int orderIndex;
}
