package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.UUID;

@Data
public class SprintDto {
    private UUID sprintId;
    private int orderIndex;
    private SprintType sprintType;
    private int durationInDays;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private SprintStatus sprintStatus;
    // Optional: nested detailed info for active/past sprints
    // private DetailedSprintInfoDto detailedInfo;
}