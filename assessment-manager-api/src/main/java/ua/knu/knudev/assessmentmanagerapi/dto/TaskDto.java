package ua.knu.knudev.assessmentmanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO representing a task with body and name")
@Data
public class TaskDto {

    @Schema(description = "The content/body of the task", example = "Complete astronauts moon mission",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String body;

    @Schema(description = "The name or title of the task", example = "Astronauts mission",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
