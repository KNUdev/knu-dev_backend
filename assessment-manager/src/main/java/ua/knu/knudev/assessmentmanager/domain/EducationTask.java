package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

@Getter
@Setter
@Entity
@Table(
        schema = "assessment_management",
        name = "education_program_task",
        indexes = {
                @Index(columnList = "learning_unit", name = "idx_learning_unit"),
                @Index(columnList = "task_filename", name = "idx_filename"),
        }
)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EducationTask extends BaseTask {
    @Enumerated(EnumType.STRING)
    @Column(name = "learning_unit", nullable = false)
    private LearningUnit learningUnit;
}
