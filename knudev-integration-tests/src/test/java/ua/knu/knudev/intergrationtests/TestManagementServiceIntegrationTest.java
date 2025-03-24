package ua.knu.knudev.intergrationtests;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.assessmentmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.assessmentmanager.domain.TestDomain;
import ua.knu.knudev.assessmentmanager.domain.TestQuestion;
import ua.knu.knudev.assessmentmanager.domain.embeddable.DurationConfig;
import ua.knu.knudev.assessmentmanager.repository.QuestionAnswerVariantRepository;
import ua.knu.knudev.assessmentmanager.repository.TestQuestionRepository;
import ua.knu.knudev.assessmentmanager.repository.TestRepository;
import ua.knu.knudev.assessmentmanager.service.TestService;
import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.assessmentmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TestException;
import ua.knu.knudev.assessmentmanagerapi.request.TestCreationRequest;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class TestManagementServiceIntegrationTest {

    private static final UUID TEST_ID = UUID.randomUUID();
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
    private static final String TEST_EN_NAME = "Test Name";

    @Autowired
    private TestService testManagementService;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private QuestionAnswerVariantRepository questionAnswerVariantRepository;

    private TestDomain testDomain;

    @BeforeEach
    public void setUp() {
        testDomain = createTestAndSave();
    }

    @AfterEach
    public void tearDown() {
        testRepository.deleteAll();
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
                createTestQuestions(FIRST_TEST_QUESTION_ID, FIRST_QUESTION_BODY, firstQuestionAnswerVariants, createdTestDomain),
                createTestQuestions(SECOND_TEST_QUESTION_ID, SECOND_QUESTION_BODY, secondQuestionAnswerVariants, createdTestDomain)
        );

        createdTestDomain.setTestQuestions(testQuestions);

        return testRepository.save(createdTestDomain);
    }

    private TestQuestion createTestQuestions(UUID id, String enQuestionBody, Set<QuestionAnswerVariant> variants, TestDomain testDomain) {
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

    private TestCreationRequest getTestCreationRequest(String enName) {
        Set<QuestionAnswerVariantDto> firstQuestionAnswers = Set.of(
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "first", true),
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "second", false),
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + "third", false)
        );

        Set<QuestionAnswerVariantDto> secondQuestionAnswers = Set.of(
                getQuestionAnswerVariantDto(SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "first", false),
                getQuestionAnswerVariantDto(SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "second", true),
                getQuestionAnswerVariantDto(SECOND_BASE_QUESTION_ANSWER_VARIANT_BODY + "third", false)
        );

        List<TestQuestionDto> questionDtos = List.of(
                getTestQuestionDto(FIRST_QUESTION_BODY, firstQuestionAnswers),
                getTestQuestionDto(SECOND_QUESTION_BODY, secondQuestionAnswers)
        );

        return TestCreationRequest.builder()
                .enName(enName)
                .questions(questionDtos)
                .timeUnitPerTextCharacter(100)
                .extraTimePerCorrectAnswer(100)
                .build();
    }

    private TestQuestionDto getTestQuestionDto(String questionBody, Set<QuestionAnswerVariantDto> variants) {
        return TestQuestionDto.builder()
                .enQuestionBody(questionBody)
                .answerVariantDtos(variants)
                .build();
    }

    private QuestionAnswerVariantDto getQuestionAnswerVariantDto(String enVariantBody, Boolean isCorrect) {
        return QuestionAnswerVariantDto.builder()
                .enVariantBody(enVariantBody)
                .isCorrectAnswer(isCorrect)
                .build();
    }

    private TestQuestionDto getCompliteTestQuestionDto(String questionBody) {
        Set<QuestionAnswerVariantDto> answerVariants = Set.of(getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + " FIRST VARIANT", true),
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + " SECOND VARIANT", false),
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + " THIRD VARIANT", false),
                getQuestionAnswerVariantDto(FIRST_BASE_QUESTION_ANSWER_VARIANT_BODY + " FOURTH VARIANT", true)
        );

        return getTestQuestionDto(questionBody, answerVariants);
    }

    private UUID getQuestionIdFromTest() {
        return testDomain.getTestQuestions().stream()
                .findAny()
                .orElseThrow()
                .getId();
    }

    private UUID getQuestionAnswerVariantIdFromTest() {
        return testDomain.getTestQuestions().stream()
                .findAny()
                .orElseThrow()
                .getAnswerVariants()
                .stream()
                .filter(answer -> answer.getIsCorrectAnswer().equals(false))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    @Nested
    @DisplayName("Create academic test tests")
    class CreateAcademicTestDomainTests {

        @Test
        @DisplayName("Should create test successfully when provided valid creation request")
        public void should_CreateTestSuccessfully_When_ProvidedValidCreationRequest() {
            //Arrange
            TestCreationRequest request = getTestCreationRequest(TEST_EN_NAME + " " + TEST_EN_NAME);

            //Act
            FullTestDto response = testManagementService.create(request);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertNotNull(response.createdAt(), "Created at should not be null");

            AtomicInteger answersAmountPerAllQuestions = new AtomicInteger();
            response.testQuestionDtos().forEach(testQuestionDto -> {
                answersAmountPerAllQuestions.addAndGet(testQuestionDto.answerVariantDtos().size());
            });

            UUID questionId = testQuestionRepository.findAllByTestDomain_Id(response.id()).get(0).getId();

            assertEquals(2, response.testQuestionDtos().size());
            assertEquals(TEST_EN_NAME + " " + TEST_EN_NAME, response.enName());
            assertEquals(6, answersAmountPerAllQuestions.get());
            assertTrue(testQuestionRepository.existsByTestDomain_Id(response.id()));
            assertTrue(questionAnswerVariantRepository.existsQuestionAnswerVariantByTestQuestion_Id(questionId));
        }

        @Test
        @DisplayName("Should throw exception when provided duplicated test name")
        public void should_ThrowTestException_When_ProvidedDuplicatedTestName() {
            TestCreationRequest request = getTestCreationRequest(TEST_EN_NAME);
            assertThrows(TestException.class, () -> testManagementService.create(request));
        }

    }

    @Nested
    @DisplayName("Get test by id tests")
    class GetTestDomainByIdTests {

        @Test
        @DisplayName("Should successfully return test when provided valid id")
        public void should_GetTestByIdSuccessfully_When_ProvidedValidId() {
            FullTestDto response = testManagementService.getById(testDomain.getId());

            assertNotNull(response, "Response should not be null");
            assertNotNull(response.createdAt(), "Created at should not be null");
            assertEquals(2, response.testQuestionDtos().size());
            assertEquals(TEST_EN_NAME, response.enName());
        }

        @Test
        @DisplayName("Should not return test by id when id not valid")
        public void should_ThrowTestException_When_IdNotValid() {
            assertThrows(TestException.class,
                    () -> testManagementService.getById(UUID.randomUUID()));
        }

    }

    @Nested
    @DisplayName("Change test name tests")
    class ChangeTestDomainNameTests {

        @Test
        @DisplayName("Should successfully change test name if provided valid data")
        public void should_ChangeTestNameSuccessfully_When_ProvidedValidData() {
            FullTestDto response = testManagementService.changeTestEnName(testDomain.getId(), "New name");

            assertNotNull(response, "Response should not be null");
            assertEquals("New name", response.enName());
            assertTrue(testRepository.existsTestDomainByEnName("New name"));
        }

        @Test
        @DisplayName("Should not change test name when id not valid")
        public void should_ThrowTestException_When_IdNotValid() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeTestEnName(UUID.randomUUID(), "New name"));
        }

        @Test
        @DisplayName("Should not change test name if new name is blank")
        public void should_ThrowTestException_When_NewNameIsBlank() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeTestEnName(testDomain.getId(), "  "));
        }
    }

    @Nested
    @DisplayName("Add test question tests")
    class AddTestDomainQuestionTests {

        @Test
        @DisplayName("Should successfully add test question when provided valid data")
        @Transactional
        public void should_AddTestQuestionSuccessfully_When_ProvidedValidData() {
            //Arrange
            TestQuestionDto request = getCompliteTestQuestionDto("New " + FIRST_QUESTION_BODY);

            //Act
            FullTestDto response = testManagementService.addTestQuestion(testDomain.getId(), request);

            //Assert
            AtomicInteger allAnswersQuantity = new AtomicInteger();

            response.testQuestionDtos().forEach(testQuestionDto -> {
                allAnswersQuantity.addAndGet(testQuestionDto.answerVariantDtos().size());
            });
            assertNotNull(response, "Response should not be null");
            assertEquals(3, response.testQuestionDtos().size());
            assertEquals(10, allAnswersQuantity.get());
            assertEquals(3, testQuestionRepository.findAllByTestDomain_Id(response.id()).size());
            assertEquals(10, questionAnswerVariantRepository.findAll().size());

            allAnswersQuantity.set(0);
            testRepository.findById(response.id()).ifPresent(test -> {
                test.getTestQuestions().forEach(question -> {
                    allAnswersQuantity.addAndGet(question.getAnswerVariants().size());
                });
            });
            assertEquals(10, allAnswersQuantity.get());
        }

        @Test
        @DisplayName("Shouldn`t add question if such already exist")
        public void should_NotAddQuestion_When_SuchQuestionAlreadyExist() {
            TestQuestionDto request = getCompliteTestQuestionDto(FIRST_QUESTION_BODY);

            FullTestDto response = testManagementService.addTestQuestion(testDomain.getId(), request);

            assertNotNull(response, "Response should not be null");
            assertEquals(2, response.testQuestionDtos().size());
            assertEquals(2, testQuestionRepository.findAllByTestDomain_Id(response.id()).size());
        }

        @Test
        @DisplayName("Should not add test question when test id not valid")
        public void should_ThrowTestException_When_IdNotValid() {
            assertThrows(TestException.class,
                    () -> testManagementService.addTestQuestion(UUID.randomUUID(),
                            getCompliteTestQuestionDto("New " + FIRST_QUESTION_BODY)));
        }

    }

    @Nested
    @DisplayName("Delete question tests")
    class QuestionDeleteTests {

        @Test
        @DisplayName("Should successfully delete question when provided valid data")
        @Transactional
        public void should_DeleteQuestionSuccessfully_When_ProvidedValidData() {
            //Arrange
            UUID questionId = testDomain.getTestQuestions().stream()
                    .map(TestQuestion::getId)
                    .findFirst()
                    .orElseThrow();

            //Act
            FullTestDto response = testManagementService.deleteTestQuestion(testDomain.getId(), questionId);

            //Assert
            assertNotNull(response, "Response should not be null");
            int testQuestionsAmount = testRepository.findById(testDomain.getId()).orElseThrow()
                    .getTestQuestions()
                    .size();

            assertEquals(1, response.testQuestionDtos().size());
            assertEquals(1, testQuestionsAmount);
            assertEquals(1, testQuestionRepository.findAllByTestDomain_Id(response.id()).size());
            assertEquals(3, questionAnswerVariantRepository.findAll().size());
        }

        @Test
        @DisplayName("Shouldn`t delete question when it only one in test")
        @Transactional
        public void should_ThrowTestException_When_OnlyOneQuestionInTest() {
            List<UUID> questionsIds = testDomain.getTestQuestions().stream()
                    .map(TestQuestion::getId)
                    .toList();
            testManagementService.deleteTestQuestion(testDomain.getId(), questionsIds.get(0));

            assertThrows(TestException.class,
                    () -> testManagementService.deleteTestQuestion(testDomain.getId(), questionsIds.get(1))
            );
        }

        @Test
        @DisplayName("Should not delete test question when test id not valid")
        public void should_ThrowTestException_When_IdNotValid() {
            assertThrows(TestException.class,
                    () -> testManagementService.deleteTestQuestion(UUID.randomUUID(), FIRST_TEST_QUESTION_ID)
            );
        }

    }

    @Nested
    @DisplayName("Change question body tests")
    class ChangeQuestionBodyTests {

        @Test
        @DisplayName("Should successfully change question body when provided valid data")
        public void should_ChangeQuestionBodySuccessfully_When_ProvidedValidData() {
            UUID questionId = getQuestionIdFromTest();
            TestQuestionDto response = testManagementService.changeTestQuestionEnBody(questionId, "Changed test body");

            assertNotNull(response, "Response should not be null");
            assertEquals("Changed test body", response.enQuestionBody());
            assertTrue(testQuestionRepository.existsTestQuestionByEnQuestionBody("Changed test body"));
            assertEquals(3, response.answerVariantDtos().size());
        }

        @Test
        @DisplayName("Shouldn`t change test question body when question with such body already provided")
        public void should_NotChangeQuestionBodySuccessfully_When_QuestionWithSuchBodyAlreadyProvided() {
            //Arrange
            UUID questionId = testDomain.getTestQuestions().stream()
                    .filter(question ->
                            question.getEnQuestionBody().equals(SECOND_QUESTION_BODY))
                    .findFirst()
                    .orElseThrow()
                    .getId();

            //Act
            TestQuestionDto response = testManagementService.changeTestQuestionEnBody(questionId, SECOND_QUESTION_BODY);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertNotEquals(2, testQuestionRepository.findAllByEnQuestionBody(SECOND_QUESTION_BODY).size());
            assertEquals(1, testQuestionRepository.findAllByEnQuestionBody(SECOND_QUESTION_BODY).size());
        }

        @Test
        @DisplayName("Shouldn`t change test body when provided not valid question id")
        public void should_ThrowTestException_When_NotValidQuestionId() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeTestQuestionEnBody(UUID.randomUUID(), FIRST_QUESTION_BODY)
            );
        }
    }

    @Nested
    @DisplayName("Add question answer variant tests")
    class AddQuestionAnswerVariantTests {

        @Test
        @DisplayName("Should successfully add answer variant to test when provided valid data")
        public void should_AddAnswerVariantSuccessfully_When_ProvidedValidData() {
            UUID questionId = getQuestionIdFromTest();
            QuestionAnswerVariantDto requests = getQuestionAnswerVariantDto("New created answer variant", false);

            TestQuestionDto response = testManagementService.addQuestionAnswerVariant(questionId, requests);

            assertNotNull(response, "Response should not be null");
            assertEquals(4, response.answerVariantDtos().size());
            assertEquals(7, questionAnswerVariantRepository.findAll().size());
            assertTrue(questionAnswerVariantRepository.existsQuestionAnswerVariantByEnVariantBody("New created answer variant"));
        }

        @Test
        @DisplayName("Shouldn`t add question answer variant when provided not valid question id")
        public void should_ThrowTestException_When_NotValidQuestionId() {
            assertThrows(TestException.class,
                    () -> testManagementService.addQuestionAnswerVariant(UUID.randomUUID(),
                            getQuestionAnswerVariantDto("New created answer variant", false))
            );
        }

        @Test
        @DisplayName("Shouldn`t add question answer when provided already existed variant")
        public void should_NotAddAnswerVariantSuccessfully_When_AlreadyExistedVariant() {
            //Arrange
            UUID questionId = getQuestionIdFromTest();
            String enVariantBody = testDomain.getTestQuestions().stream()
                    .filter(question -> question.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow()
                    .getAnswerVariants()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .getEnVariantBody();

            //Act
            TestQuestionDto response = testManagementService.addQuestionAnswerVariant(questionId,
                    getQuestionAnswerVariantDto(enVariantBody, false));

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(3, response.answerVariantDtos().size());
            assertEquals(6, questionAnswerVariantRepository.findAll().size());
        }

    }

    @Nested
    @DisplayName("Delete question answer variant tests")
    class DeleteQuestionAnswerVariantTests {

        @Test
        @DisplayName("Should successfully delete question answer variant when provided valid data")
        @Transactional
        public void should_DeleteAnswerVariantSuccessfully_When_ProvidedValidData() {
            //Arrange
            UUID questionId = getQuestionIdFromTest();
            UUID answerVariantId = testDomain.getTestQuestions().stream()
                    .filter(question -> question.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow()
                    .getAnswerVariants()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .getId();

            //Act
            TestQuestionDto response = testManagementService.deleteQuestionAnswerVariant(questionId, answerVariantId);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(2, response.answerVariantDtos().size());
            assertEquals(5, questionAnswerVariantRepository.findAll().size());
            assertTrue(testQuestionRepository.existsById(questionId));
            assertEquals(2, testQuestionRepository.findById(questionId).get().getAnswerVariants().size());
        }

        @Test
        @DisplayName("Shouldn`t delete question answer variant when question has less than 2 answers")
        @Transactional
        public void should_NotDeleteAnswerVariantSuccessfully_When_QuestionHasLessThanTwoAnswers() {
            //Arrange
            UUID questionId = getQuestionIdFromTest();
            List<QuestionAnswerVariant> answerVariantsByQuestion = testDomain.getTestQuestions().stream()
                    .filter(question -> question.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow()
                    .getAnswerVariants()
                    .stream().toList();
            testManagementService.deleteQuestionAnswerVariant(questionId, answerVariantsByQuestion.get(0).getId());
            testManagementService.deleteQuestionAnswerVariant(questionId, answerVariantsByQuestion.get(1).getId());

            //Act
            TestQuestionDto response = testManagementService.deleteQuestionAnswerVariant(questionId, answerVariantsByQuestion.get(2).getId());

            //Assert
            assertNotNull(response, "Response should not be null");
            assertEquals(1, response.answerVariantDtos().size());
            assertEquals(4, questionAnswerVariantRepository.findAll().size());
            assertEquals(1, testQuestionRepository.findById(questionId).orElseThrow().getAnswerVariants().size());
            assertTrue(testQuestionRepository.existsById(questionId));
        }

        @Test
        @DisplayName("Shouldn`t delete question answer variant when there are no question answer variants id in question")
        public void should_ThrowTestException_When_QuestionHasNoQuestionAnswerVariantId() {
            assertThrows(TestException.class,
                    () -> testManagementService.deleteQuestionAnswerVariant(getQuestionIdFromTest(), UUID.randomUUID())
            );
        }

        @Test
        @DisplayName("Shouldn`t add question answer variant when provided not valid question id")
        public void should_ThrowTestException_When_NotValidQuestionId() {
            assertThrows(TestException.class,
                    () -> testManagementService.deleteQuestionAnswerVariant(UUID.randomUUID(), UUID.randomUUID())
            );
        }

    }

    @Nested
    @DisplayName("Change question answer variant body tests")
    class ChangeQuestionAnswerVariantBodyTests {

        @Test
        @DisplayName("Should successfully change question answer variant body")
        public void should_ChangeAnswerVariantBodySuccessfully_When_ProvidedValidData() {
            //Arrange
            UUID questionAnswerVariantId = getQuestionAnswerVariantIdFromTest();

            //Act
            QuestionAnswerVariantDto response = testManagementService.changeQuestionAnswerVariantEnBody(
                    questionAnswerVariantId, "New changed body!!!");

            //Assert
            assertNotNull(response, "Response should not be null");
            assertTrue(questionAnswerVariantRepository.existsById(questionAnswerVariantId));
            assertEquals("New changed body!!!", questionAnswerVariantRepository.findById(
                    questionAnswerVariantId).orElseThrow().getEnVariantBody());
            assertEquals("New changed body!!!", response.enVariantBody());
        }

        @Test
        @DisplayName("Shouldn`t change question answer variant body when provided not valid id")
        public void should_ThrowTestException_When_NotValidId() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeQuestionAnswerVariantEnBody(UUID.randomUUID(), "New changed body!!!")
            );
        }

        @Test
        @DisplayName("Should throw test exception when provided null name value")
        public void should_ThrowTestException_When_NullNameValue() {
            UUID questionAnswerVariantId = getQuestionAnswerVariantIdFromTest();
            assertThrows(TestException.class,
                    () -> testManagementService.changeQuestionAnswerVariantEnBody(questionAnswerVariantId, null));
        }

    }

    @Nested
    @DisplayName("Change question answer variant correctness tests")
    class ChangeQuestionAnswerVariantCorrectnessTests {

        @Test
        @DisplayName("Should successfully change question answer variant correctness when provided valid data")
        public void should_ChangeAnswerVariantCorrectnessSuccessfully_When_ProvidedValidData() {
            //Arrange
            UUID questionAnswerVariantId = getQuestionAnswerVariantIdFromTest();

            //Act
            QuestionAnswerVariantDto response = testManagementService.changeQuestionAnswerVariantCorrectness(questionAnswerVariantId, true);

            //Assert
            assertNotNull(response, "Response should not be null");
            assertTrue(questionAnswerVariantRepository.existsById(questionAnswerVariantId));
            assertEquals(true, questionAnswerVariantRepository.findById(questionAnswerVariantId).orElseThrow().getIsCorrectAnswer());
            assertTrue(response.isCorrectAnswer());
        }

        @Test
        @DisplayName("Shouldn`t change question answer variant correctness when new correctness is null")
        public void should_ThrowTestException_When_NewCorrectnessIsNull() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeQuestionAnswerVariantCorrectness(getQuestionAnswerVariantIdFromTest(), null)
            );
        }

        @Test
        @DisplayName("Shouldn`t change question answer variant correctness when provided not valid variant id")
        public void should_ThrowTestException_When_NotValidVariantId() {
            assertThrows(TestException.class,
                    () -> testManagementService.changeQuestionAnswerVariantCorrectness(UUID.randomUUID(), true)
            );
        }
    }

}
