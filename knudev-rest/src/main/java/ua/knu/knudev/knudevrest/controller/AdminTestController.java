package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.taskmanagerapi.api.TestManagementApi;
import ua.knu.knudev.taskmanagerapi.dto.FullTestDto;
import ua.knu.knudev.taskmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.taskmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.taskmanagerapi.request.TestCreationRequest;

import java.util.UUID;

@RestController
@RequestMapping("/admin/test")
@RequiredArgsConstructor
public class AdminTestController {

    private final TestManagementApi testManagementApi;

    @PostMapping("/create")
    public void createTest(@RequestParam TestCreationRequest testCreationRequest) {
        testManagementApi.create(testCreationRequest);
    }

    @GetMapping("/{testId}")
    public FullTestDto getTest(@PathVariable UUID testId) {
        return testManagementApi.getById(testId);
    }

    @DeleteMapping("/{testId}/delete")
    public void deleteTest(@PathVariable UUID testId) {
        testManagementApi.deleteTestById(testId);
    }

    @PatchMapping("/{testId}/change/name")
    public void changeTestName(@PathVariable UUID testId, @RequestParam String newEnName) {
        testManagementApi.changeTestEnName(testId, newEnName);
    }

    @PatchMapping("/{testId}/add/question")
    public void addQuestion(@PathVariable UUID testId, @RequestParam TestQuestionDto question) {
        testManagementApi.addTestQuestion(testId, question);
    }

    @DeleteMapping("/{testId}/delete/question/{questionId}")
    public void deleteQuestion(@PathVariable UUID testId, @PathVariable UUID questionId) {
        testManagementApi.deleteTestQuestion(testId, questionId);
    }

    @PatchMapping("/question/{questionId}/change/body")
    public void changeQuestionBody(@PathVariable UUID questionId, @RequestParam String newEnBody) {
        testManagementApi.changeTestQuestionEnBody(questionId, newEnBody);
    }

    @PatchMapping("/question/{questionId}/add/answerVariant")
    public void addQuestionAnswer(@PathVariable UUID questionId, @RequestParam QuestionAnswerVariantDto answerVariant) {
        testManagementApi.addQuestionAnswerVariant(questionId, answerVariant);
    }

    @DeleteMapping("/question/{questionId}/delete/answerVariant/{answerVariantId}")
    public void deleteQuestionAnswer(@PathVariable UUID questionId, @PathVariable UUID answerVariantId) {
        testManagementApi.deleteQuestionAnswerVariant(questionId, answerVariantId);
    }

    @PatchMapping("/questionAnswerVariant/{questionAnswerVariantId}/change/body")
    public void changeQuestionAnswerBody(@PathVariable UUID questionAnswerVariantId, @RequestParam String newEnBody) {
        testManagementApi.changeQuestionAnswerVariantEnBody(questionAnswerVariantId, newEnBody);
    }

    @PatchMapping("/questionAnswerVariant/{questionAnswerVariantId}/change/correctness")
    public void changeQuestionAnswerCorrectness(@PathVariable UUID questionAnswerVariantId, @RequestParam Boolean correctness) {
        testManagementApi.changeQuestionAnswerVariantCorrectness(questionAnswerVariantId, correctness);
    }

}
