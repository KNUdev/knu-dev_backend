package ua.knu.knudev.educationapi.dto.session;

import lombok.Builder;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.UUID;

@Builder
public class SprintSummaryDto {
    private UUID sprintId;
    private int orderIndex;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private SprintType sprintType;
    private SprintStatus sprintStatus;
    private int durationInDays;
}
