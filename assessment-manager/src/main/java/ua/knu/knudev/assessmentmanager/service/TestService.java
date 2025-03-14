package ua.knu.knudev.assessmentmanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.assessmentmanager.domain.TestDomain;
import ua.knu.knudev.assessmentmanager.domain.TestQuestion;
import ua.knu.knudev.assessmentmanager.domain.embeddable.DurationConfig;
import ua.knu.knudev.assessmentmanager.mapper.QuestionAnswerVariantMapper;
import ua.knu.knudev.assessmentmanager.mapper.TestMapper;
import ua.knu.knudev.assessmentmanager.mapper.TestQuestionMapper;
import ua.knu.knudev.assessmentmanager.repository.QuestionAnswerVariantRepository;
import ua.knu.knudev.assessmentmanager.repository.TestQuestionRepository;
import ua.knu.knudev.assessmentmanager.repository.TestRepository;
import ua.knu.knudev.assessmentmanagerapi.api.TestApi;
import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.assessmentmanagerapi.dto.ShortTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TestException;
import ua.knu.knudev.assessmentmanagerapi.request.TestCreationRequest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService implements TestApi {

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

        int extraTimePerCorrectAnswer = testCreationRequest.extraTimePerCorrectAnswer();
        int timeUnitPerTextCharacter = testCreationRequest.timeUnitPerTextCharacter();
        Integer testDurationInMinutes = calculateTestDurationTime(timeUnitPerTextCharacter, extraTimePerCorrectAnswer, testQuestions);

        TestDomain testDomain = TestDomain.builder()
                .createdAt(LocalDate.now())
                .testQuestions(testQuestions)
                .enName(testCreationRequest.enName())
                .durationConfig(buildDurationConfig(timeUnitPerTextCharacter, extraTimePerCorrectAnswer))
                .testDurationInMinutes(testDurationInMinutes)
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
    public boolean existsById(UUID testId) {
        return testRepository.existsById(testId);
    }

    @Override
    public List<ShortTestDto> getAll() {
        return testRepository.findAll().stream()
                .map(test -> ShortTestDto.builder()
                        .id(test.getId())
                        .enName(test.getEnName())
                        .build())
                .toList();
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

        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();
        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();

        List<String> questionsNames = testQuestions.stream()
                .map(TestQuestion::getEnQuestionBody)
                .toList();

        if (questionsNames.contains(testQuestion.getEnQuestionBody())) {
            log.warn("TestQuestion with such name already exists");
            return testMapper.toDto(testDomain);
        }

        testQuestions.add(testQuestion);
        testDomain.associateTestWithQuestionsAndVariants();
        Integer testDurationInMinutes = calculateTestDurationTime(timeUnitPerTextCharacter, extraTimePerCorrectAnswer, testQuestions);
        testDomain.setTestDurationInMinutes(testDurationInMinutes);

        TestDomain savedTestDomain = testRepository.save(testDomain);
        log.info("Added test question: {}", testQuestion);

        return testMapper.toDto(savedTestDomain);
    }

    @Override
    public FullTestDto deleteTestQuestion(UUID testId, UUID questionId) {
        TestDomain testDomain = getTestById(testId);

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

        if (testQuestions.size() < 2) {
            throw new TestException("Test must contain at least 1 question");
        }

        TestQuestion questionToDelete = testQuestions.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new TestException("Question with ID " + questionId + " does not exist in test " + testId));

        log.info("Removing question with id: {}", questionId);
        testQuestions.remove(questionToDelete);
        testQuestionRepository.delete(questionToDelete);

        Integer testDurationInMinutes = calculateTestDurationTime(timeUnitPerTextCharacter, extraTimePerCorrectAnswer, testQuestions);
        testDomain.setTestDurationInMinutes(testDurationInMinutes);

        TestDomain savedTestDomain = testRepository.save(testDomain);
        return testMapper.toDto(savedTestDomain);
    }



    @Override
    @Transactional
    public TestQuestionDto changeTestQuestionEnBody(UUID questionId, String newEnBody) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        TestDomain testDomain = testQuestion.getTestDomain();

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

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

        Integer testDurationInMinutes = calculateTestDurationTime(timeUnitPerTextCharacter, extraTimePerCorrectAnswer, testQuestions);
        testDomain.setTestDurationInMinutes(testDurationInMinutes);
        testRepository.save(testDomain);

        return testQuestionMapper.toDto(savedTestQuestion);
    }

    @Override
    @Transactional
    public TestQuestionDto addQuestionAnswerVariant(UUID questionId, QuestionAnswerVariantDto questionAnswerVariantDto) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        TestDomain testDomain = testQuestion.getTestDomain();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
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
        updateAndSaveTestDurationTime(testDomain, testQuestions, timeUnitPerTextCharacter, extraTimePerCorrectAnswer);

        log.info("Added question answer variant with id: {}", questionAnswerVariant.getId());
        return testQuestionMapper.toDto(savedTestQuestion);
    }

    @Override
    @Transactional
    public TestQuestionDto deleteQuestionAnswerVariant(UUID questionId, UUID questionAnswerVariantId) {
        TestQuestion testQuestion = getTestQuestionById(questionId);
        TestDomain testDomain = testQuestion.getTestDomain();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
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
        updateAndSaveTestDurationTime(testDomain, testQuestions, timeUnitPerTextCharacter, extraTimePerCorrectAnswer);

        return testQuestionMapper.toDto(testQuestion);
    }

    @Override
    public QuestionAnswerVariantDto changeQuestionAnswerVariantEnBody(UUID questionAnswerVariantId, String newEnBody) {
        QuestionAnswerVariant questionAnswerVariant = getQuestionAnswerVariantById(questionAnswerVariantId);
        TestDomain testDomain = questionAnswerVariant.getTestQuestion().getTestDomain();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
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
        updateAndSaveTestDurationTime(testDomain, testQuestions, timeUnitPerTextCharacter, extraTimePerCorrectAnswer);

        log.info("EnVariantBody: {}, was changed on: {}", oldEnVariantBody, newEnBody);
        return questionAnswerVariantMapper.toDto(savedQuestionAnswerVariant);
    }

    @Override
    public QuestionAnswerVariantDto changeQuestionAnswerVariantCorrectness(UUID questionAnswerVariantId, Boolean newCorrectnessValue) {
        QuestionAnswerVariant questionAnswerVariant = getQuestionAnswerVariantById(questionAnswerVariantId);
        TestDomain testDomain = questionAnswerVariant.getTestQuestion().getTestDomain();
        Set<TestQuestion> testQuestions = testDomain.getTestQuestions();

        Integer timeUnitPerTextCharacter = testDomain.getDurationConfig().getTimeUnitPerTextCharacter();
        Integer extraTimePerCorrectAnswer = testDomain.getDurationConfig().getExtraTimePerCorrectAnswer();
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
        updateAndSaveTestDurationTime(testDomain, testQuestions, timeUnitPerTextCharacter, extraTimePerCorrectAnswer);

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

    private DurationConfig buildDurationConfig(Integer timeUnitPerTextCharacter, Integer extraTimePerCorrectAnswer) {
        return DurationConfig.builder()
                .timeUnitPerTextCharacter(timeUnitPerTextCharacter)
                .extraTimePerCorrectAnswer(extraTimePerCorrectAnswer)
                .build();
    }

    private void updateAndSaveTestDurationTime(TestDomain testDomain, Set<TestQuestion> testQuestions, Integer timeUnitPerTextCharacter,
                                               Integer extraTimePerCorrectAnswer) {
        Integer testDurationInMinutes = calculateTestDurationTime(timeUnitPerTextCharacter, extraTimePerCorrectAnswer, testQuestions);
        testDomain.setTestDurationInMinutes(testDurationInMinutes);
        testRepository.save(testDomain);
    }

    private Integer calculateTestDurationTime(Integer timeUnitPerTextCharacter, Integer extraTimePerCorrectAnswer,
                                              Set<TestQuestion> testQuestions) {
        int testExecutionTime = 0;

        for (TestQuestion question : testQuestions) {
            Set<QuestionAnswerVariant> answerVariants = question.getAnswerVariants();

            int questionBodyLength = question.getEnQuestionBody() != null ?
                    question.getEnQuestionBody().length() : 0;

            int variantsBodyLength = answerVariants.stream()
                    .mapToInt(variant -> variant.getEnVariantBody() != null ?
                            variant.getEnVariantBody().length() : 0)
                    .sum();

            int correctAnswersAmount = (int) answerVariants.stream()
                    .filter(QuestionAnswerVariant::getIsCorrectAnswer)
                    .count();

            int timeForOneQuestion = timeUnitPerTextCharacter * (questionBodyLength + variantsBodyLength) +
                    (extraTimePerCorrectAnswer * correctAnswersAmount);

            testExecutionTime += timeForOneQuestion;
        }

        return testExecutionTime;
    }


}
