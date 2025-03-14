package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.assessmentmanager.domain.*;
import ua.knu.knudev.assessmentmanager.repository.QuestionAnswerVariantRepository;
import ua.knu.knudev.assessmentmanager.repository.TestQuestionRepository;
import ua.knu.knudev.assessmentmanager.repository.TestRepository;
import ua.knu.knudev.assessmentmanager.repository.TestSubmissionRepository;
import ua.knu.knudev.assessmentmanagerapi.api.TestSubmissionApi;
import ua.knu.knudev.assessmentmanagerapi.constant.TestSubmissionStatus;
import ua.knu.knudev.assessmentmanagerapi.dto.AnswerVariantResultDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionResultDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestScore;
import ua.knu.knudev.assessmentmanagerapi.dto.TestSubmissionResultsDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TestException;
import ua.knu.knudev.assessmentmanagerapi.request.SubmittedAnswerDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestSubmissionRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSubmissionService implements TestSubmissionApi {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final QuestionAnswerVariantRepository questionAnswerVariantRepository;
    private final TestSubmissionRepository testSubmissionRepository;

    @Override
    public TestSubmissionResultsDto submit(TestSubmissionRequest submissionRequest) {
        TestSubmissionStatus testSubmissionStatus = submissionRequest.getStatus();
        checkTestSubmissionStatusOnFailed(testSubmissionStatus);

        UUID submittedTestId = submissionRequest.getSubmittedTestId();
        List<SubmittedAnswerDto> answers = submissionRequest.getAnswers();
        TestDomain testDomain = getTestDomainById(submittedTestId);

        List<UUID> questionIds = answers.stream().map(SubmittedAnswerDto::getQuestionId).toList();

        List<UUID> chosenVariantIds = answers.stream().flatMap(
                answer -> answer.getChosenVariantIds().stream()).toList();

        List<TestQuestion> testQuestions = testQuestionRepository.findAllByIdIn(questionIds);
        List<QuestionAnswerVariant> chosenVariants = questionAnswerVariantRepository.findAllByIdIn(chosenVariantIds);

        Map<TestQuestion, List<QuestionAnswerVariant>> question2AnswerVariants = buildQuestion2AnswerVariantsMap(chosenVariants, testQuestions);

        Map<Double, Double> rawToPercentageScore = calculateTestScore(question2AnswerVariants, testDomain.getMaxRawScore());

        Map.Entry<Double, Double> rowToPercentageScoreEntry = rawToPercentageScore.entrySet().stream().findFirst()
                .orElseThrow(() -> new TestException("Row score not calculated!"));

        TestSubmission testSubmission = getTestSubmission(submissionRequest, testDomain, rowToPercentageScoreEntry.getKey(), rowToPercentageScoreEntry.getValue());
        Set<TestSubmissionAnswer> testSubmissionAnswers = getTestSubmissionAnswers(question2AnswerVariants, testSubmission);
        testSubmission.getAnswers().addAll(testSubmissionAnswers);

        return getTestSubmissionResultsDto(testSubmission, testQuestions, chosenVariantIds);
    }

    @Override
    public TestSubmissionResultsDto getSubmissionResults(UUID submissionId, UUID submitterAccountId) {
        TestSubmission testSubmission = testSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new TestException("Submission with ID:" + submissionId + " not found!"));

        if (!testSubmission.getSubmitterAccountId().equals(submitterAccountId)) {
            throw new TestException(("There are no submitter accounts associated with this id:" + submitterAccountId));
        }

        Set<TestSubmissionAnswer> testSubmissionAnswers = testSubmission.getAnswers();
        List<TestQuestion> testQuestions = testSubmissionAnswers.stream()
                .map(TestSubmissionAnswer::getTestQuestion)
                .toList();

        List<UUID> chosenAnswersIds = testSubmissionAnswers.stream()
                .map(TestSubmissionAnswer::getChosenVariants)
                .flatMap(Collection::stream)
                .map(QuestionAnswerVariant::getId)
                .toList();

        return getTestSubmissionResultsDto(testSubmission, testQuestions, chosenAnswersIds);
    }

    private HashMap<Double, Double> calculateTestScore(Map<TestQuestion, List<QuestionAnswerVariant>> questionsToAnswerVariants,
                                                       Integer maxRowScore) {
        HashMap<Double, Double> rawToPercentageScore = new HashMap<>();
        double percentageScore = 0.0;
        double rawScore;

        List<Double> percentageScoresToAllQuestions = calculatePercentageScoresPerAllQuestions(questionsToAnswerVariants);

        for (Double percentageScoresToAllQuestion : percentageScoresToAllQuestions) {
            percentageScore += percentageScoresToAllQuestion;
        }

        percentageScore = new BigDecimal(percentageScore)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        rawScore = BigDecimal.valueOf((maxRowScore * percentageScore) / 100)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        rawToPercentageScore.put(rawScore, percentageScore);

        return rawToPercentageScore;
    }

    private List<Double> calculatePercentageScoresPerAllQuestions(Map<TestQuestion, List<QuestionAnswerVariant>> questionsToAnswerVariants) {
        List<Double> percentageScoresToAllQuestions = new ArrayList<>();

        questionsToAnswerVariants.forEach((testQuestion, answerVariants) -> {
            double correctAnswersAmount = Math.toIntExact(testQuestion.getAnswerVariants().stream()
                    .filter(QuestionAnswerVariant::getIsCorrectAnswer)
                    .count());

            int receivedCorrectAnswersAmount = Math.toIntExact(answerVariants.stream()
                    .filter(QuestionAnswerVariant::getIsCorrectAnswer)
                    .count());

            double questionPercentageScore = (receivedCorrectAnswersAmount * 100) / correctAnswersAmount;
            percentageScoresToAllQuestions.add(questionPercentageScore);
        });

        return percentageScoresToAllQuestions;
    }

    private Set<TestSubmissionAnswer> getTestSubmissionAnswers(Map<TestQuestion, List<QuestionAnswerVariant>> questionsToAnswerVariants,
                                                               TestSubmission testSubmission) {
        return questionsToAnswerVariants.entrySet().stream()
                .map(entry -> TestSubmissionAnswer.builder()
                        .testSubmission(testSubmission)
                        .testQuestion(entry.getKey())
                        .chosenVariants(new HashSet<>(entry.getValue()))
                        .build())
                .collect(Collectors.toSet());
    }

    private void checkTestSubmissionStatusOnFailed(TestSubmissionStatus testSubmissionStatus) {
        if (testSubmissionStatus.equals(TestSubmissionStatus.FAILED)) {
            throw new TestException("Test submission failed");
        }
    }

    private TestDomain getTestDomainById(UUID id) {
        return testRepository.findById(id).orElseThrow(
                () -> new TestException("Test with ID:" + id + " not found"));
    }

    private Map<UUID, List<QuestionAnswerVariant>> buildQuestionId2AnswerVariantsMap(List<QuestionAnswerVariant> questionAnswerVariants) {
        return questionAnswerVariants.stream().collect(Collectors.groupingBy(
                variant -> variant.getTestQuestion().getId()));

    }

    private Map<TestQuestion, List<QuestionAnswerVariant>> buildQuestion2AnswerVariantsMap(List<QuestionAnswerVariant> questionAnswerVariants,
                                                                                           List<TestQuestion> testQuestions) {
        Map<UUID, List<QuestionAnswerVariant>> questionId2AnswerVariants = buildQuestionId2AnswerVariantsMap(questionAnswerVariants);

        return testQuestions.stream()
                .collect(Collectors.toMap(
                        question -> question,
                        question -> questionId2AnswerVariants.getOrDefault(question.getId(), Collections.emptyList())
                ));
    }

    private TestSubmission getTestSubmission(TestSubmissionRequest submissionRequest, TestDomain testDomain,
                                             Double rawScore, Double percentageScore) {
        return TestSubmission.builder()
                .submitterAccountId(submissionRequest.getSubmitterAccountId())
                .testDomain(testDomain)
                .timeTakenInSeconds(submissionRequest.getTimeTakenInSeconds())
                .submittedAt(LocalDateTime.now())
                .submissionStatus(submissionRequest.getStatus())
                .rawScore(rawScore)
                .percentageScore(percentageScore)
                .answers(new HashSet<>())
                .build();
    }

    private List<TestQuestionResultDto> getTestQuestionResultDto(List<TestQuestion> testQuestions, List<UUID> chosenVariantIds) {
        return testQuestions.stream()
                .map(question -> {
                    List<AnswerVariantResultDto> answerVariants = question.getAnswerVariants().stream()
                            .map(variant -> AnswerVariantResultDto.builder()
                                    .variantId(variant.getId())
                                    .variantBody(variant.getEnVariantBody())
                                    .selectedByUser(chosenVariantIds.contains(variant.getId()))
                                    .correct(variant.getIsCorrectAnswer())
                                    .build()).toList();

                    return TestQuestionResultDto.builder()
                            .questionId(question.getId())
                            .questionBody(question.getEnQuestionBody())
                            .variants(answerVariants)
                            .build();
                }).toList();
    }

    private TestScore getTestScore(TestSubmission submission) {
        return TestScore.builder()
                .rawScore(submission.getRawScore())
                .percentageScore(submission.getPercentageScore())
                .build();
    }

    private TestSubmissionResultsDto getTestSubmissionResultsDto(TestSubmission submission, List<TestQuestion> testQuestions,
                                                                 List<UUID> chosenVariantIds) {
        List<TestQuestionResultDto> questions = getTestQuestionResultDto(testQuestions, chosenVariantIds);

        return TestSubmissionResultsDto.builder()
                .submissionId(submission.getId())
                .userId(submission.getSubmitterAccountId())
                .testName(submission.getTestDomain().getEnName())
                .score(getTestScore(submission))
                .status(submission.getSubmissionStatus().name())
                .timeTakenInSeconds(submission.getTimeTakenInSeconds())
                .questions(questions)
                .build();
    }
}
