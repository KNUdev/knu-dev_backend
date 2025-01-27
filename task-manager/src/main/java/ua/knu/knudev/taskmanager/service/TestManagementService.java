package ua.knu.knudev.taskmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.taskmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.taskmanager.domain.Test;
import ua.knu.knudev.taskmanager.domain.TestQuestion;
import ua.knu.knudev.taskmanager.mapper.QuestionAnswerVariantMapper;
import ua.knu.knudev.taskmanager.mapper.TestMapper;
import ua.knu.knudev.taskmanager.mapper.TestQuestionMapper;
import ua.knu.knudev.taskmanager.repository.QuestionAnswerVariantRepository;
import ua.knu.knudev.taskmanager.repository.TestQuestionRepository;
import ua.knu.knudev.taskmanager.repository.TestRepository;
import ua.knu.knudev.taskmanagerapi.api.TestManagementApi;
import ua.knu.knudev.taskmanagerapi.dto.FullTestDto;
import ua.knu.knudev.taskmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.taskmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.taskmanagerapi.exception.TestException;
import ua.knu.knudev.taskmanagerapi.request.TestCreationRequest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestManagementService implements TestManagementApi {

    private final TestMapper testMapper;
    private final TestQuestionMapper testQuestionMapper;
    private final QuestionAnswerVariantMapper questionAnswerVariantMapper;
    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final QuestionAnswerVariantRepository questionAnswerVariantRepository;

    @Override
    @Transactional
    public FullTestDto create(TestCreationRequest testCreationRequest) {
        Set<TestQuestion> testQuestions = new HashSet<>(testQuestionMapper.toDomains(testCreationRequest.questions()));

        Test test = Test.builder()
                .createdAt(LocalDate.now())
                .testQuestions(testQuestions)
                .enName(testCreationRequest.enName())
                .build();

        associateTestWithQuestionsAndVariants(test, testQuestions);

        Test savedTest = testRepository.save(test);
        log.info("Saved test: {}", savedTest);
        return testMapper.toDto(savedTest);
    }

    @Override
    @Transactional
    public FullTestDto getById(UUID testId) {
        Test test = getTestById(testId);
        return testMapper.toDto(test);
    }

    @Override
    @Transactional
    public void deleteTestById(UUID testId) {
        log.info("Deleting test: {}", testId);
        testRepository.deleteById(testId);
    }

    @Override
    public FullTestDto changeTestEnName(UUID testId, String newEnName) {
        Test test = getTestById(testId);

        if (StringUtils.isBlank(newEnName)) {
            throw new TestException("New enName is blank");
        }
        if (test.getEnName().equals(newEnName)) {
            log.warn("New enName the same with old enName: {}, so nothing was changed", test.getEnName());
            return testMapper.toDto(test);
        }

        log.info("Changing test enName for test ID {}: '{}' -> '{}'",
                testId, test.getEnName(), newEnName);

        test.setEnName(newEnName);
        Test savedTest = testRepository.save(test);
        return testMapper.toDto(savedTest);
    }

    @Override
    @Transactional
    public FullTestDto addTestQuestion(UUID testId, TestQuestionDto testQuestionDto) {
        Test test = getTestById(testId);
        TestQuestion testQuestion = testQuestionMapper.toDomain(testQuestionDto);
        associateTestWithQuestionsAndVariants(test, Set.of(testQuestion));

        List<String> questionsNames = test.getTestQuestions().stream()
                .map(TestQuestion::getEnQuestionBody)
                .toList();

        if (questionsNames.contains(testQuestion.getEnQuestionBody())) {
            log.warn("TestQuestion with such name already exists");
            return testMapper.toDto(test);
        }

        test.getTestQuestions().add(testQuestion);
        Test savedTest = testRepository.save(test);
        log.info("Added test question: {}", testQuestion);
        return testMapper.toDto(savedTest);
    }

    @Override
    public FullTestDto deleteTestQuestion(UUID testId, UUID questionId) {
        Test test = getTestById(testId);

        if (test.getTestQuestions().size() < 2) {
            throw new TestException("Test must contain at least 1 question");
        }

        TestQuestion questionToDelete = test.getTestQuestions()
                .stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new TestException("Question with ID " + questionId + " does not exist in test " + testId));

        log.info("Removing question with id: {}", questionId);
        test.getTestQuestions().remove(questionToDelete);
        testQuestionRepository.delete(questionToDelete);
        return testMapper.toDto(test);
    }


    @Override
    @Transactional
    public TestQuestionDto changeTestQuestionEnBody(UUID questionId, String newEnBody) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        List<String> questionsBodies = testQuestionRepository.findAll().stream()
                .map(TestQuestion::getEnQuestionBody)
                .toList();

        if (questionsBodies.contains(newEnBody)) {
            log.warn("Question with such body already exists");
            return testQuestionMapper.toDto(testQuestion);
        }

        log.info("Changing test question body for question ID {}: '{}' -> '{}'",
                questionId, testQuestion.getEnQuestionBody(), newEnBody);

        testQuestion.setEnQuestionBody(newEnBody);
        TestQuestion savedTestQuestion = testQuestionRepository.save(testQuestion);
        return testQuestionMapper.toDto(savedTestQuestion);
    }

    @Override
    @Transactional
    public TestQuestionDto addQuestionAnswerVariant(UUID questionId, QuestionAnswerVariantDto questionAnswerVariantDto) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        QuestionAnswerVariant questionAnswerVariant = questionAnswerVariantMapper.toDomain(questionAnswerVariantDto);

        List<String> variantsBody = testQuestion.getAnswerVariants().stream()
                .map(QuestionAnswerVariant::getEnVariantBody)
                .toList();

        if (variantsBody.contains(questionAnswerVariant.getEnVariantBody())) {
            log.warn("Answer variant already exists so nothing will be added");
            return testQuestionMapper.toDto(testQuestion);
        }

        questionAnswerVariant.setTestQuestion(testQuestion);
        testQuestion.getAnswerVariants().add(questionAnswerVariant);
        TestQuestion savedTestQuestion = testQuestionRepository.save(testQuestion);

        log.info("Added question answer variant with id: {}", questionAnswerVariant.getId());
        return testQuestionMapper.toDto(savedTestQuestion);
    }

    @Override
    @Transactional
    public TestQuestionDto deleteQuestionAnswerVariant(UUID questionId, UUID questionAnswerVariantId) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        List<UUID> answersId = testQuestion.getAnswerVariants().stream()
                .map(QuestionAnswerVariant::getId)
                .toList();

        if (!answersId.contains(questionAnswerVariantId)) {
            throw new TestException("Answer variant with id " + questionAnswerVariantId
                    + " does not exist in test question with id " + testQuestion.getId());
        }
        if (testQuestion.getAnswerVariants().size() < 2) {
            log.warn("TestQuestion must contain at least 1 answer variant, so nothing will be deleted");
            return testQuestionMapper.toDto(testQuestion);
        }

        QuestionAnswerVariant answerVariantToDelete = testQuestion.getAnswerVariants()
                .stream()
                .filter(questionAnswerVariant -> questionAnswerVariant.getId().equals(questionAnswerVariantId))
                .findFirst()
                .orElseThrow(() -> new TestException("QuestionVariantAnswer with ID " + questionAnswerVariantId
                        + " does not exist in question " + questionId));

        log.info("Removing answerVariant with id: {}", questionAnswerVariantId);
        testQuestion.getAnswerVariants().remove(answerVariantToDelete);
        questionAnswerVariantRepository.delete(answerVariantToDelete);
        return testQuestionMapper.toDto(testQuestion);
    }

    @Override
    public QuestionAnswerVariantDto changeQuestionAnswerVariantEnBody(UUID questionAnswerVariantId, String newEnBody) {
        QuestionAnswerVariant questionAnswerVariant = getQuestionAnswerVariantById(questionAnswerVariantId);
        String oldEnVariantBody = questionAnswerVariant.getEnVariantBody();

        if (oldEnVariantBody.equals(newEnBody)) {
            log.warn("New enVariantBody: {}, the same with old enVariantBody, so nothing will be changed", newEnBody);
            return questionAnswerVariantMapper.toDto(questionAnswerVariant);
        }

        questionAnswerVariant.setEnVariantBody(newEnBody);
        QuestionAnswerVariant savedQuestionAnswerVariant = questionAnswerVariantRepository.save(questionAnswerVariant);
        log.info("EnVariantBody: {}, was changed on: {}", oldEnVariantBody, newEnBody);
        return questionAnswerVariantMapper.toDto(savedQuestionAnswerVariant);
    }

    @Override
    public QuestionAnswerVariantDto changeQuestionAnswerVariantCorrectness(UUID questionAnswerVariantId, Boolean newCorrectnessValue) {
        QuestionAnswerVariant questionAnswerVariant = getQuestionAnswerVariantById(questionAnswerVariantId);
        Boolean isCorrectAnswer = questionAnswerVariant.getIsCorrectAnswer();

        if (newCorrectnessValue == null) {
            throw new TestException("Correctness cannot be null");
        }
        if (isCorrectAnswer == newCorrectnessValue) {
            log.warn("New correctness value: {} == old correctness value: {}, so nothing will be changed", newCorrectnessValue, isCorrectAnswer);
            return questionAnswerVariantMapper.toDto(questionAnswerVariant);
        }

        questionAnswerVariant.setIsCorrectAnswer(newCorrectnessValue);
        QuestionAnswerVariant changedQuestionAnswerVariant = questionAnswerVariantRepository.save(questionAnswerVariant);
        log.info("Correctness value: {}, was changed on: {}", isCorrectAnswer, newCorrectnessValue);
        return questionAnswerVariantMapper.toDto(changedQuestionAnswerVariant);
    }

    private Test getTestById(UUID testId) {
        return testRepository.findById(testId).orElseThrow(
                () -> new TestException("Test with testId " + testId + " not found"));
    }

    private TestQuestion getTestQuestionById(UUID questionId) {
        return testQuestionRepository.findById(questionId).orElseThrow(
                () -> new TestException("Question with id " + questionId + " not found"));
    }

    private QuestionAnswerVariant getQuestionAnswerVariantById(UUID questionAnswerVariantId) {
        return questionAnswerVariantRepository.findById(questionAnswerVariantId).orElseThrow(
                () -> new TestException("QuestionAnswerVariant with ID " + questionAnswerVariantId + " does not exist"));
    }

    private void associateTestWithQuestionsAndVariants(Test test, Set<TestQuestion> testQuestions) {
        testQuestions.forEach(question -> question.setTest(test));

        testQuestions.forEach(testQuestion ->
                testQuestion.getAnswerVariants().forEach(variant -> variant.setTestQuestion(testQuestion))
        );
    }

}
