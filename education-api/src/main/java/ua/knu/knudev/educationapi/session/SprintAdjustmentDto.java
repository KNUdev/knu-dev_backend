package ua.knu.knudev.educationapi.session;

import lombok.Data;

import java.util.UUID;

@Data
public class SprintAdjustmentDto {
    private UUID sprintId;      // the temporary ID from the preview plan
    private int orderIndex;     // helpful for validation; must match preview order
    private int durationDays;   // the adjusted duration
}