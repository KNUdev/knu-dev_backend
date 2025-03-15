package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ua.knu.knudev.knudevcommon.constant.Expertise;

@Schema(description = "Request DTO for receiving closed recruitment details")
public record ClosedRecruitmentReceivingRequest(
        @Schema(description = "Name of the recruitment", example = "Backend Developer Hiring")
        String name,

        @Schema(description = "Required expertise level for the recruitment", example = "FULLSTACK",
                implementation = Expertise.class)
        Expertise expertise
) {
}
