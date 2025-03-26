package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;

@Schema(description = "Request DTO for receiving closed recruitment details")
@Builder
public record ClosedRecruitmentReceivingRequest(
        @Schema(description = "Name of the recruitment", example = "Backend Developer Hiring")
        String name,

        @Schema(description = "Required expertise level for the recruitment", example = "FULLSTACK",
                implementation = Expertise.class)
        Expertise expertise,

        @Schema(description = "Required recruitment start date", example = "2021-10-10T10:00:00")
        LocalDateTime startedAt,

        @Schema(description = "Required recruitment end date", example = "2022-10-10T10:00:00")
        LocalDateTime closedAt,

        @Schema(description = "Page number", example = "1", implementation = Integer.class)
        Integer pageNumber,

        @Schema(description = "The number of elements per page", example = "9", implementation = Integer.class)
        Integer pageSize
) {
}
