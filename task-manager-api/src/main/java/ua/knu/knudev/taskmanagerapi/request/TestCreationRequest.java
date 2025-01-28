package ua.knu.knudev.taskmanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import ua.knu.knudev.taskmanagerapi.dto.TestQuestionDto;

import java.util.List;

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
        List<@Valid TestQuestionDto> questions
) {
}
