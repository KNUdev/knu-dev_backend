package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SprintAttemptDto {
    private int attemptNumber;
    private LocalDateTime submittedAt;
    private String submissionFile;
    private String mentorFeedback;
    private Integer score;
    private String attemptStatus;
}
