package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "This object contains join recruitment data")
public record RecruitmentJoinRequest(
        @Schema(description = "Account id", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID accountId,

        @Schema(description = "Active recruitment id", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID activeRecruitmentId) {
}
