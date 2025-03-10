package ua.knu.knudev.educationapi.dto.session;

import lombok.Data;
import ua.knu.knudev.educationapi.dto.TestMetadataDto;
import ua.knu.knudev.educationapi.dto.session.SprintAttemptDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class DetailedSprintInfoDto {
    // The chain history of submission attempts (feedback, score, etc.)
    private List<SprintAttemptDto> submissionHistory;
    // Overall sprint score, if computed
    private Integer sprintScore;
    // The date when the sprint was passed (if applicable)
    private LocalDateTime sprintPassingDate;
    // Full task details: URL, learning resources, etc.
    private String taskUrl;
    private Set<String> learningResources;
    // If the sprint includes a test, include its metadata:
    private TestMetadataDto testMetadata;
}