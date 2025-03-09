package ua.knu.knudev.educationapi.session;

import lombok.Data;
import java.util.List;

@Data
public class SessionSprintPlanDto {
    // Grouped by section.
    private List<SectionSprintDto> sections;
    // The final sprint for the entire program.
    private SprintDto programFinalSprint;
}
