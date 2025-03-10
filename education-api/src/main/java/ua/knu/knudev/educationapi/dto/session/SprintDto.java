package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;

import java.util.UUID;

@Data
public class SprintDto {
    private UUID sprintId;           // Unique identifier (generated for preview)
    private int orderIndex;          // Global order index
    private SprintType sprintType;       // "TOPIC", "MODULE_FINAL", "SECTION_FINAL", "PROGRAM_FINAL"
    private int durationDays;        // Default duration (adjustable via UI)
    private String title;            // e.g., topic name or "Module Final: [Module Name]"
    private String description;      // Optional short info
    private SprintStatus sprintStatus; // FUTURE, ACTIVE, COMPLETED (default FUTURE)
    // Optional: nested detailed info for active/past sprints
    // private DetailedSprintInfoDto detailedInfo;
}