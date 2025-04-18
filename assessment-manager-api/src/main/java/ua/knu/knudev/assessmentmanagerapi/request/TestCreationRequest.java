package ua.knu.knudev.assessmentmanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Request object for creating a test with name and questions")
public record TestCreationRequest(

        @NotEmpty(message = "Field 'enName' cannot be blank or empty.")
        @Pattern(
                regexp = "^[A-Za-z\\s-]+$",
                message = "Field 'enName' can contain only English alphabet letters"
        )
        @Schema(
                description = "The name of the test in English.",
                example = "Sample Test Name",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String enName,

        @NotEmpty(message = "TestQuestionDtos set cannot be empty.")
        @Schema(
                description = "A set of questions for the test.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = TestQuestionDto.class
        )
        List<@Valid TestQuestionDto> questions,

        @NotEmpty(message = "TimeUnitPerTextCharacter can not be 0")
        @Schema(
                description = "Constant. Time unit for text(question and answer) in seconds",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer timeUnitPerTextCharacter,

        @NotEmpty(message = "ExtraTimePerCorrectAnswer can not be 0")
        @Schema(
                description = "Constant. Extra time added for each correct answer in seconds",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Min(0)
        Integer extraTimePerCorrectAnswer,

        @NotEmpty(message = "Label can not be empty")
        @Schema(
                description = "Label for the test",
                example = "Topic",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String label,

        @NotNull(message = "Creator ID can not be empty")
        @Schema(
                description = "Creator ID",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID creatorId

) {
}
