package ua.knu.knudev.educationapi.session;

import lombok.Data;
import ua.knu.knudev.educationapi.dto.SprintAttemptDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SprintSubmissionDto {
    private UUID submissionId;         // Unique identifier for this submission record.
    private UUID participantId;        // The student's account ID.
    private LocalDateTime lastSubmissionTime; // Time of the most recent submission attempt.

    // A history (or “chat”) of submission attempts.
    // Each attempt would be represented by a SprintAttemptDto (containing attempt number, file reference, feedback, score, etc.)
    private List<SprintAttemptDto> attempts;
}
