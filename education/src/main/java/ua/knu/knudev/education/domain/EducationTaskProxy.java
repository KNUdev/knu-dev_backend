package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.assessmentmanagerapi.dto.Task;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.util.UUID;

@Entity
@Table(schema = "assessment_management", name = "education_program_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationTaskProxy implements Task {
    @Id
    @UuidGenerator
    private UUID id;
    private String taskFilename;
    @Column(name = "learning_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private LearningUnit learningUnit;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getFilename() {
        return taskFilename;
    }

}
