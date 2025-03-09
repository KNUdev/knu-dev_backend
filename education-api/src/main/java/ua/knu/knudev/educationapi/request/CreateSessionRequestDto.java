package ua.knu.knudev.educationapi.request;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateSessionRequestDto {
    private UUID programId;
    private Set<UUID> mentorIds;
    /**
     * List of sprint adjustments.
     * Each adjustment maps a preview sprint’s temporary ID to its adjusted duration.
     */
    private List<SprintAdjustmentDto> sprintAdjustments;
}
