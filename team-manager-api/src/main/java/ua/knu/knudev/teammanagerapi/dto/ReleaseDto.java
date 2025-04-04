package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a release")
public class ReleaseDto {

    @Schema(description = "Unique identifier of the release", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Timestamp when the release process was initialized", example = "2024-02-21T10:15:30")
    private LocalDateTime initializedAt;

    @Schema(description = "Expected or actual finish date of the release", example = "2024-03-01T18:00:00")
    private LocalDateTime releaseFinishDate;

    @Schema(description = "Version of the release", example = "1.2.0")
    private String version;

    @Schema(description = "Changelog in English describing the changes made in this release",
            example = "Fixed critical bug in payment processing")
    private String changesLogEn;

    @Schema(description = "Aggregated count of GitHub commits included in this release", example = "42")
    private Integer aggregatedGithubCommitCount;

    @Schema(description = "Developers associated with this release")
    private List<ReleaseParticipationDto> releaseDevelopers;
}
