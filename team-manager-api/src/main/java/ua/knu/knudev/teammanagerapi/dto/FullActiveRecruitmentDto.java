package ua.knu.knudev.teammanagerapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO for active recruitment")
public record FullActiveRecruitmentDto(
        @Schema(description = "UUID recruitment id")
        UUID id,

        @Schema(description = "Title of the recruitment", example = "Frontend Developer Recruitment")
        String name,

        @Schema(description = "Start date of the recruitment")
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        LocalDateTime startedAt,

        @Schema(description = "Finish date of the recruitment")
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        LocalDateTime deadlineDate,

        @Schema(description = "Required expertise level for the recruitment")
        Expertise expertise,

        @Schema(description = "Maximum number of people that can join", example = "10")
        Integer maxCandidates,

        @Schema(description = "Current number of joined people", example = "5")
        Integer joinedPeopleAmount
) {
}
