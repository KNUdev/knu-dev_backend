package ua.knu.knudev.education.domain.bridge;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "module_topic_mapping",
        schema = "education",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "topic_id"})
)
@Builder
public class ModuleTopicMapping {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    private EducationProgram program;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private ProgramSection section;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private ProgramModule module;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private ProgramTopic topic;

    @Column(nullable = false)
    private int orderIndex;

}
