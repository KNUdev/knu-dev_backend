package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.assessmentmanager.domain.*;
import ua.knu.knudev.assessmentmanager.domain.embeddable.DurationConfig;
import ua.knu.knudev.assessmentmanager.repository.TestRepository;
import ua.knu.knudev.assessmentmanager.repository.TestSubmissionRepository;
import ua.knu.knudev.assessmentmanager.service.TestSubmissionService;
import ua.knu.knudev.assessmentmanagerapi.constant.TestSubmissionStatus;
import ua.knu.knudev.assessmentmanagerapi.dto.TestSubmissionResultsDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TestException;
import ua.knu.knudev.assessmentmanagerapi.request.SubmittedAnswerDto;
import ua.knu.knudev.assessmentmanagerapi.request.TestSubmissionRequest;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.DepartmentRepository;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class TestSubmissionServiceIntegrationTest {

    private static final UUID TEST_SUBMISSION_ID = UUID.randomUUID();
    private static final UUID FIRST_TEST_SUBMISSION_ANSWER_ID = UUID.randomUUID();
    private static final UUID SECOND_TEST_SUBMISSION_ANSWER_ID = UUID.randomUUID();
    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_EN_NAME = "Test Name";
    private static final UUID FIRST_TEST_QUESTION_ID = UUID.randomUUID();
    private static final UUID SECOND_TEST_QUESTION_ID = UUID.randomUUID();
    private static final String FIRST_QUESTION_BODY = "First question body";
    private static final String SECOND_QUESTION_BODY = "Second question body";
    private static final UUID FIRST_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final UUID SECOND_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final UUID THIRD_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final UUID FOURTH_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final UUID FIFTH_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final UUID SIXTH_QUESTION_ANSWER_VARIANT_ID = UUID.randomUUID();
    private static final String FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY = "First answer variant";
    private static final String SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY = "Second answer variant";

    @Autowired
    private TestSubmissionService testSubmissionService;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private TestSubmissionRepository testSubmissionRepository;

    private TestSubmission testSubmission;
    private TestDomain testDomain;
    private Department testDepartment;
    private Specialty testSpecialty;
    private AccountProfile testAccountProfile;

    @BeforeEach
    public void setUp() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
        testAccountProfile = createAndSaveAccount();
        testDomain = createTestAndSave();
        testSubmission = createTestSubmissionAndSave();
    }

    @AfterEach
    public void tearDown() {
        testSubmissionRepository.deleteAll();
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
        testRepository.deleteAll();
    }

    private TestSubmission createTestSubmissionAndSave() {
        TestSubmission testSubmission = TestSubmission.builder()
                .id(TEST_SUBMISSION_ID)
                .submitterAccountId(testAccountProfile.getId())
                .testDomain(testDomain)
                .timeTakenInSeconds(TimeUnit.SECONDS.toSeconds(1000))
                .submittedAt(LocalDateTime.now().minusHours(2))
                .submissionStatus(TestSubmissionStatus.MANUAL)
                .rawScore(7.8)
                .percentageScore(78.0)
                .answers(new HashSet<>())
                .build();

        List<TestQuestion> testQuestions = testDomain.getTestQuestions().stream().toList();

        Set<TestSubmissionAnswer> answers = Set.of(
                createTestSubmissionAnswer(FIRST_TEST_SUBMISSION_ANSWER_ID, testQuestions.get(0), testSubmission),
                createTestSubmissionAnswer(SECOND_TEST_SUBMISSION_ANSWER_ID, testQuestions.get(1), testSubmission)
        );

        testSubmission.addAnswers(answers);
        return testSubmissionRepository.save(testSubmission);
    }

    private TestSubmissionAnswer createTestSubmissionAnswer(UUID answerId, TestQuestion testQuestion, TestSubmission testSubmission) {
        return TestSubmissionAnswer.builder()
                .id(answerId)
                .testSubmission(testSubmission)
                .testQuestion(testQuestion)
                .chosenVariants(testQuestion.getAnswerVariants())
                .build();
    }

    private Department createTestDepartmentWithSpecialties() {
        Department department = new Department();
        department.setName(new MultiLanguageField("Test Department", "Тестовий"));

        Specialty s1 = new Specialty(122.0, "Computer Science", "Науки");
        Specialty s2 = new Specialty(123.0, "Computer Engineering", "Інженерія");
        Specialty s3 = new Specialty(125.0, "Cybernetics", "Кібернетика");

        department.addSpecialty(s1);
        department.addSpecialty(s2);
        department.addSpecialty(s3);

        return departmentRepository.save(department);
    }

    private AccountProfile createAndSaveAccount() {
        AccountProfile account = AccountProfile.builder()
                .id(UUID.randomUUID())
                .email("test@knu.ua")
                .firstName("First")
                .lastName("Last")
                .middleName("Middle")
                .department(testDepartment)
                .specialty(testSpecialty)
                .expertise(Expertise.BACKEND)
                .unit(KNUdevUnit.CAMPUS)
                .technicalRole(AccountTechnicalRole.INTERN)
                .yearOfStudyOnRegistration(2)
                .registrationDate(LocalDateTime.now())
                .githubAccountUsername("qwe")
                .build();
        return accountProfileRepository.save(account);
    }

    private TestDomain createTestAndSave() {
        TestDomain createdTestDomain = TestDomain.builder()
                .id(TEST_ID)
                .createdAt(LocalDate.now())
                .enName(TEST_EN_NAME)
                .maxRawScore(100)
                .durationConfig(DurationConfig.builder()
                        .timeUnitPerTextCharacter(100)
                        .extraTimePerCorrectAnswer(100)
                        .build())
                .testDurationInMinutes(1000)
                .creatorId(UUID.randomUUID())
                .label("Test Label")
                .build();

        Set<QuestionAnswerVariant> firstQuestionAnswerVariants = Set.of(
                createQuestionAnswerVariant(FIRST_QUESTION_ANSWER_VARIANT_ID, FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "first", false),
                createQuestionAnswerVariant(SECOND_QUESTION_ANSWER_VARIANT_ID, FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "second", true),
                createQuestionAnswerVariant(THIRD_QUESTION_ANSWER_VARIANT_ID, FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "third", false)
        );

        Set<QuestionAnswerVariant> secondQuestionAnswerVariants = Set.of(
                createQuestionAnswerVariant(FOURTH_QUESTION_ANSWER_VARIANT_ID, SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "first", true),
                createQuestionAnswerVariant(FIFTH_QUESTION_ANSWER_VARIANT_ID, SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "second", false),
                createQuestionAnswerVariant(SIXTH_QUESTION_ANSWER_VARIANT_ID, SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "third", false)
        );

        Set<TestQuestion> testQuestions = Set.of(
                createTestQuestion(FIRST_TEST_QUESTION_ID, FIRST_QUESTION_BODY, firstQuestionAnswerVariants, createdTestDomain),
                createTestQuestion(SECOND_TEST_QUESTION_ID, SECOND_QUESTION_BODY, secondQuestionAnswerVariants, createdTestDomain)
        );

        createdTestDomain.setTestQuestions(testQuestions);

        return testRepository.save(createdTestDomain);
    }

    private TestQuestion createTestQuestion(UUID id, String enQuestionBody, Set<QuestionAnswerVariant> variants, TestDomain testDomain) {
        TestQuestion question = TestQuestion.builder()
                .id(id)
                .enQuestionBody(enQuestionBody)
                .answerVariants(variants)
                .testDomain(testDomain)
                .build();

        variants.forEach(variant -> variant.setTestQuestion(question));
        return question;
    }

    private QuestionAnswerVariant createQuestionAnswerVariant(UUID id, String enVariantBody, Boolean isCorrect) {
        return QuestionAnswerVariant.builder()
                .id(id)
                .enVariantBody(enVariantBody)
                .isCorrectAnswer(isCorrect)
                .build();
    }

    private SubmittedAnswerDto createSubmittedAnswerDto(UUID questionId, List<UUID> variantIds) {
        return SubmittedAnswerDto.builder()
                .questionId(questionId)
                .chosenVariantIds(variantIds)
                .build();
    }

    private TestSubmissionRequest createTestSubmissionRequest(TestSubmissionStatus status) {

        Set<UUID> selectedAnswerVariants = Set.of(getAnswerVariantId(0), getAnswerVariantId(1));

        List<SubmittedAnswerDto> answers = testDomain.getTestQuestions().stream()
                .map(testQuestion -> {
                    List<UUID> variantIds = testQuestion.getAnswerVariants().stream()
                            .map(QuestionAnswerVariant::getId)
                            .filter(selectedAnswerVariants::contains)
                            .toList();

                    return createSubmittedAnswerDto(testQuestion.getId(), variantIds);
                })
                .toList();

        return TestSubmissionRequest.builder()
                .answers(answers)
                .submitterAccountId(testAccountProfile.getId())
                .submittedTestId(testDomain.getId())
                .status(status)
                .timeTakenInSeconds(1000)
                .build();
    }

    private UUID getAnswerVariantId(Integer questionIndex) {
        return testDomain.getTestQuestions().stream()
                .toList()
                .get(questionIndex)
                .getAnswerVariants()
                .stream()
                .toList()
                .get(1)
                .getId();
    }

    @Nested
    @DisplayName("Test submission functionality tests")
    class SubmitTestSubmissionTests {

        @Test
        @DisplayName("Should throw exception when provided invalid test id")
        void should_ThrowException_When_ProvidedInvalidTestId() {
            TestSubmissionRequest request = createTestSubmissionRequest(TestSubmissionStatus.MANUAL);
            request.setSubmittedTestId(UUID.randomUUID());
            assertThrows(TestException.class, () -> testSubmissionService.submit(request));
        }

        @Test
        @DisplayName("Should build canceled test when provided CANCELED test status")
        void should_BuildCanceledTest_When_ProvidedCanceledTestStatus() {
            TestSubmissionRequest request = createTestSubmissionRequest(TestSubmissionStatus.CANCELED);

            TestSubmissionResultsDto response = testSubmissionService.submit(request);

            assertNotNull(response);
            assertNull(response.getQuestions());
            assertEquals(0.0, response.getScore().getRawScore());
            assertEquals(0.0, response.getScore().getPercentageScore());
            assertEquals(testAccountProfile.getId(), response.getUserId());
            assertEquals(testDomain.getEnName(), response.getTestName());
            assertTrue(testSubmissionRepository.existsById(response.getSubmissionId()));
        }

        @Test
        @DisplayName("Should successfully create test when provided valid data")
        void should_SuccessfullySubmitTest_When_ProvidedValidData() {
            TestSubmissionRequest request = createTestSubmissionRequest(TestSubmissionStatus.MANUAL);

            TestSubmissionResultsDto response = testSubmissionService.submit(request);

            assertNotNull(response);
            assertNotNull(response.getQuestions());
            assertNotEquals(0.0, response.getScore().getRawScore());
            assertNotEquals(0.0, response.getScore().getPercentageScore());
            assertEquals(testAccountProfile.getId(), response.getUserId());
            assertEquals(testDomain.getEnName(), response.getTestName());
            assertTrue(testSubmissionRepository.existsById(response.getSubmissionId()));
        }
    }

    @Nested
    @DisplayName("Get submission results tests")
    class GetSubmissionResultsTests {

        @Test
        @DisplayName("Should throw exception when provided not valid test submission id")
        void should_ThrowException_When_ProvidedNotValidTestSubmissionId() {
            assertThrows(TestException.class,
                    () -> testSubmissionService.getSubmissionResults(UUID.randomUUID(), testSubmission.getSubmitterAccountId()));
        }

        @Test
        @DisplayName("Should throw exception when provided not valid account id")
        void should_ThrowException_When_ProvidedNotValidAccountId() {
            assertThrows(TestException.class,
                    () -> testSubmissionService.getSubmissionResults(testSubmission.getId(), UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should successfully return submission result when provided valid data")
        void should_SuccessfullyReturnSubmissionResult_When_ProvidedValidData() {
            TestSubmissionResultsDto response = testSubmissionService.getSubmissionResults(testSubmission.getId(),
                    testSubmission.getSubmitterAccountId());

            assertNotNull(response);
            assertNotNull(response.getQuestions());
            assertNotEquals(0.0, response.getScore().getRawScore());
            assertNotEquals(0.0, response.getScore().getPercentageScore());
            assertEquals(testAccountProfile.getId(), response.getUserId());
            assertEquals(testDomain.getEnName(), response.getTestName());
            assertTrue(testSubmissionRepository.existsById(response.getSubmissionId()));
        }
    }
}
