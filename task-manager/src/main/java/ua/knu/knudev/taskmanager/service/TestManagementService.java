package ua.knu.knudev.taskmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.taskmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.taskmanager.domain.TestDomain;
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
        if (testRepository.existsTestDomainByEnName(testCreationRequest.enName())) {
            throw new TestException("Test domain with name " + testCreationRequest.enName() + " already exists");
        }
        Set<TestQuestion> testQuestions = new HashSet<>(testQuestionMapper.toDomains(testCreationRequest.questions()));

        TestDomain testDomain = TestDomain.builder()
                .createdAt(LocalDate.now())
                .testQuestions(testQuestions)
                .enName(testCreationRequest.enName())
                .build();

        testDomain.associateTestWithQuestionsAndVariants();
        TestDomain savedTestDomain = testRepository.save(testDomain);
        log.info("Saved test: {}", savedTestDomain);
        return testMapper.toDto(savedTestDomain);
    }

    @Override
    @Transactional
    public FullTestDto getById(UUID testId) {
        TestDomain testDomain = getTestById(testId);
        return testMapper.toDto(testDomain);
    }

    @Override
    @Transactional
    public void deleteTestById(UUID testId) {
        log.info("Deleting test: {}", testId);
        testRepository.deleteById(testId);
    }

    @Override
    public FullTestDto changeTestEnName(UUID testId, String newEnName) {
        TestDomain testDomain = getTestById(testId);

        if (StringUtils.isBlank(newEnName)) {
            throw new TestException("New enName is blank");
        }
        if (testDomain.getEnName().equals(newEnName)) {
            log.warn("New enName the same with old enName: {}, so nothing was changed", testDomain.getEnName());
            return testMapper.toDto(testDomain);
        }

        log.info("Changing test enName for test ID {}: '{}' -> '{}'",
                testId, testDomain.getEnName(), newEnName);

        testDomain.setEnName(newEnName);
        TestDomain savedTestDomain = testRepository.save(testDomain);
        return testMapper.toDto(savedTestDomain);
    }

    @Override
    @Transactional
    public FullTestDto addTestQuestion(UUID testId, TestQuestionDto testQuestionDto) {
        TestDomain testDomain = getTestById(testId);
        TestQuestion testQuestion = testQuestionMapper.toDomain(testQuestionDto);

        List<String> questionsNames = testDomain.getTestQuestions().stream()
                .map(TestQuestion::getEnQuestionBody)
                .toList();

        if (questionsNames.contains(testQuestion.getEnQuestionBody())) {
            log.warn("TestQuestion with such name already exists");
            return testMapper.toDto(testDomain);
        }

        testDomain.getTestQuestions().add(testQuestion);
        testDomain.associateTestWithQuestionsAndVariants();
        TestDomain savedTestDomain = testRepository.save(testDomain);
        log.info("Added test question: {}", testQuestion);
        return testMapper.toDto(savedTestDomain);
    }

    @Override
    public FullTestDto deleteTestQuestion(UUID testId, UUID questionId) {
        TestDomain testDomain = getTestById(testId);

        if (testDomain.getTestQuestions().size() < 2) {
            throw new TestException("Test must contain at least 1 question");
        }

        TestQuestion questionToDelete = testDomain.getTestQuestions()
                .stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new TestException("Question with ID " + questionId + " does not exist in test " + testId));

        log.info("Removing question with id: {}", questionId);
        testDomain.getTestQuestions().remove(questionToDelete);
        testQuestionRepository.delete(questionToDelete);
        return testMapper.toDto(testDomain);
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
        if (StringUtils.isBlank(newEnBody)) {
            throw new TestException("New enVariantBody is empty");
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

    private TestDomain getTestById(UUID testId) {
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

}
