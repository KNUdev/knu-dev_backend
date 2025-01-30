package ua.knu.knudev.assessmentmanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Set;

@Builder
@Schema(description = "Data Transfer Object for a test question with answer variants")
public record TestQuestionDto(

        @NotEmpty(message = "Field 'enQuestionBody' cannot be blank or empty.")
        @Pattern(
                regexp = "^[A-Za-z\\s-]+$",
                message = "Field 'enQuestionBody' can contain only English alphabet letters"
        )
        @Schema(
                description = "The question body in English.",
                example = "What is Java?",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String enQuestionBody,

        @NotEmpty(message = "AnswerVariantDtos set cannot be empty.")
        @Schema(
                description = "A set of possible answer variants for the question.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = QuestionAnswerVariantDto.class
        )
        Set<@Valid QuestionAnswerVariantDto> answerVariantDtos
) {
}
