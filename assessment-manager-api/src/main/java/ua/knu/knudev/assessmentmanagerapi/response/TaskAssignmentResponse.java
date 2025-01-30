package ua.knu.knudev.assessmentmanagerapi.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the verification code for task assignment.")
public record TaskAssignmentResponse(
        @Schema(description = "The verification code for the assigned task", example = "ABC123D",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String verificationCode
) {
}
