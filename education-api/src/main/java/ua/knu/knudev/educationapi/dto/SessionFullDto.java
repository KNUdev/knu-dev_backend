package ua.knu.knudev.educationapi.dto;

import lombok.Data;
import ua.knu.knudev.educationapi.session.SprintDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SessionFullDto {
    private UUID sessionId;
    private EducationProgramDto program; // your existing full program DTO (or a summary)
    private LocalDateTime sessionStartDate;
    private LocalDateTime sessionEndDate;
    private String status; // e.g., CREATED, ONGOING, COMPLETED
    private List<SprintDto> sprints;
}
