package ua.knu.knudev.taskmanagerapi.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the verification code for task assignment.")
public record TaskAssignmentResponse(
        @Schema(description = "The verification code for the assigned task", example = "AbC123!")
        String verificationCode
) {
}
