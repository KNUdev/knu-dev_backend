package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionSprintDto {
    private UUID sectionId;
    private String sectionName;
    // The list of modules (with their topic sprints and module final sprint)
    private List<ModuleSprintDto> modules;
    // The final sprint for the section.
    private SprintDto sectionFinalSprint;
}
