package ua.knu.knudev.educationapi.dto.session;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SessionSprintPlanDto {
    private int totalDurationInDays;
    private LocalDateTime startDate;
    private LocalDateTime estimatedEndDate;
    private List<SprintSummaryDto> sprints;
}
