package ua.knu.knudev.education.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ua.knu.knudev.assessmentmanagerapi.dto.Task;

import java.util.UUID;

@Entity
@Table(schema = "assessment_management", name = "education_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationTaskProxy implements Task {
    @Id
    private UUID id;
    private String taskFilename;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getFilename() {
        return taskFilename;
    }

}
