package ua.knu.knudev.assessmentmanagerapi.api;

import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestCreationRequest;

import java.util.UUID;

public interface TestManagementApi {
    FullTestDto create(TestCreationRequest testCreationRequest);

    FullTestDto getById(UUID testId);

    void deleteTestById(UUID testId);

    FullTestDto changeTestEnName(UUID testId, String newEnName);

    FullTestDto addTestQuestion(UUID testId, TestQuestionDto testQuestionDto);

    FullTestDto deleteTestQuestion(UUID testId, UUID questionId);

    TestQuestionDto changeTestQuestionEnBody(UUID questionId, String newEnBody);

    TestQuestionDto addQuestionAnswerVariant(UUID questionId, QuestionAnswerVariantDto questionAnswerVariantDto);

    TestQuestionDto deleteQuestionAnswerVariant(UUID questionId, UUID questionAnswerVariantId);

    QuestionAnswerVariantDto changeQuestionAnswerVariantEnBody(UUID questionAnswerVariantId, String newEnBody);

    QuestionAnswerVariantDto changeQuestionAnswerVariantCorrectness(UUID questionAnswerVariantId, Boolean newCorrectnessValue);

}
