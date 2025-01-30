package ua.knu.knudev.assessmentmanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Schema(description = "Data Transfer Object for a single answer variant of a test question")
@Builder
public record QuestionAnswerVariantDto(

        @NotEmpty(message = "Field 'enVariantBody' cannot be blank or empty.")
        @Pattern(
                regexp = "^[A-Za-z\\s-]+$",
                message = "Field 'enVariantBody' can contain only English alphabet letters"
        )
        @Schema(
                description = "The body of the answer variant in English.",
                example = "Programming language",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String enVariantBody,

        @NotEmpty(message = "Field 'isCorrectAnswer' cannot be blank or empty.")
        @Schema(
                description = "Indicates whether this answer variant is correct.",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean isCorrectAnswer
) {
}
