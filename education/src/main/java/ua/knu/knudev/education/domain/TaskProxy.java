package ua.knu.knudev.education.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ua.knu.knudev.assessmentmanagerapi.dto.Task;

import java.util.UUID;

@Entity
@Table(schema = "task_management", name = "task")
public class TaskProxy implements Task {
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
