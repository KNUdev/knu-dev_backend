package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ModuleSprintDto {
    private UUID moduleId;
    private String moduleName;
    // A list of TOPIC sprints for this module.
    private List<SprintDto> topicSprints;
    // The final sprint for the module.
    private SprintDto moduleFinalSprint;
}