package ua.knu.knudev.educationapi.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SprintAdjustmentRequest {
    private UUID sprintId;
    private Integer newDurationInDays;
}