package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.assessmentmanagerapi.api.TestApi;
import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.assessmentmanagerapi.dto.ShortTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestCreationRequest;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/test")
@RequiredArgsConstructor
public class AdminTestController {

    private final TestApi testApi;

    @Operation(
            summary = "Create a new test",
            description = "Creates a new test with the provided data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Test successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullTestDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/create")
    public FullTestDto createTest(@Valid @NotNull @RequestParam @Parameter(
            name = "Test creation request",
            description = "Test creation data",
            in = ParameterIn.HEADER,
            required = true,
            schema = @Schema(implementation = FullTestDto.class)
    ) TestCreationRequest testCreationRequest) {
        return testApi.create(testCreationRequest);
    }

    @Operation(
            summary = "Get test by ID",
            description = "Retrieves a test by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Test successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FullTestDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{testId}")
    public FullTestDto getTest(@PathVariable @Parameter(
            name = "Test id",
            description = "Test id",
            in = ParameterIn.HEADER,
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    ) UUID testId) {
        return testApi.getById(testId);
    }

    @GetMapping("/all")
    public List<ShortTestDto> getAllShort() {
        return testApi.getAll();
    }

    @Operation(
            summary = "Delete test by ID",
            description = "Deletes a test by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Test successfully deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{testId}/delete")
    public void deleteTest(@PathVariable @Parameter(
            name = "Test id",
            description = "Test id",
            in = ParameterIn.HEADER,
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    ) UUID testId) {
        testApi.deleteTestById(testId);
    }

    @Operation(
            summary = "Change test name",
            description = "Updates the name of a test identified by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Test name successfully updated",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Test id",
                    description = "Test id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "New en name",
                    description = "New test name in english",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "New test"
            )
    })
    @PatchMapping("/{testId}/change/name")
    public void changeTestName(@PathVariable UUID testId, @RequestParam String newEnName) {
        testApi.changeTestEnName(testId, newEnName);
    }

    @Operation(
            summary = "Add question to test",
            description = "Adds a new question to the specified test."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Question successfully added",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Test id",
                    description = "Test id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "Question",
                    description = "Question object to add",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = TestQuestionDto.class)
            )
    })
    @PatchMapping("/{testId}/question/add")
    public void addQuestion(@PathVariable UUID testId,
                            @Valid @NotNull @RequestParam TestQuestionDto question) {
        testApi.addTestQuestion(testId, question);
    }

    @Operation(
            summary = "Delete question from test",
            description = "Deletes a question from the specified test."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Question successfully deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test or question not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Test id",
                    description = "Test id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "Question id",
                    description = "Question to delete id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
    })
    @DeleteMapping("/{testId}/question/delete/{questionId}")
    public void deleteQuestion(@PathVariable UUID testId, @PathVariable UUID questionId) {
        testApi.deleteTestQuestion(testId, questionId);
    }

    @Operation(
            summary = "Change question body",
            description = "Updates the body of a question identified by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Question body successfully updated",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Question not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Test id",
                    description = "Test id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "New en body",
                    description = "New en body of question",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "Do you have a pet?"
            )
    })
    @PatchMapping("/question/{questionId}/change/body")
    public void changeQuestionBody(@PathVariable UUID questionId, @RequestParam String newEnBody) {
        testApi.changeTestQuestionEnBody(questionId, newEnBody);
    }

    @Operation(
            summary = "Add answer variant to question",
            description = "Adds a new answer variant to the specified question."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer variant successfully added",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Question not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Question id",
                    description = "Question id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "Question answer variant dto",
                    description = "Question answer variant object",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = QuestionAnswerVariantDto.class)
            )
    })
    @PatchMapping("/question/{questionId}/add/answer-variant")
    public void addQuestionAnswer(@PathVariable UUID questionId,
                                  @Valid @NotNull @RequestParam QuestionAnswerVariantDto answerVariant) {
        testApi.addQuestionAnswerVariant(questionId, answerVariant);
    }

    @Operation(
            summary = "Delete answer variant from question",
            description = "Deletes an answer variant from the specified question."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer variant successfully deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Question or answer variant not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Answer variant id",
                    description = "Answer variant id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "Question id",
                    description = "Question to delete id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
    })
    @DeleteMapping("/question/{questionId}/delete/answer-variant/{answerVariantId}")
    public void deleteQuestionAnswer(@PathVariable UUID questionId, @PathVariable UUID answerVariantId) {
        testApi.deleteQuestionAnswerVariant(questionId, answerVariantId);
    }

    @Operation(
            summary = "Change answer variant body",
            description = "Updates the body of an answer variant identified by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer variant body successfully updated",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Answer variant not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Question answer variant id",
                    description = "Question answer variant id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "New answer variant en body",
                    description = "New en body of answer variant",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "Yes"
            )
    })
    @PatchMapping("/question-answer-variant/{questionAnswerVariantId}/change/body")
    public void changeQuestionAnswerBody(@PathVariable UUID questionAnswerVariantId, @RequestParam String newEnBody) {
        testApi.changeQuestionAnswerVariantEnBody(questionAnswerVariantId, newEnBody);
    }

    @Operation(
            summary = "Change answer variant correctness",
            description = "Updates the correctness of an answer variant identified by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer variant correctness successfully updated",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Answer variant not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Parameters(value = {
            @Parameter(
                    name = "Question answer variant id",
                    description = "Question answer variant id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ),
            @Parameter(
                    name = "Correctness",
                    description = "If answer is correct",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "true"
            )
    })
    @PatchMapping("/question-answer-variant/{questionAnswerVariantId}/change/correctness")
    public void changeQuestionAnswerCorrectness(@PathVariable UUID questionAnswerVariantId, @RequestParam Boolean correctness) {
        testApi.changeQuestionAnswerVariantCorrectness(questionAnswerVariantId, correctness);
    }
}
