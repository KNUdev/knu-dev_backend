package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.domain.embeddable.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.RecruitmentService;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentAutoCloseConditionsDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecruitmentServiceTest {

    @Autowired
    private RecruitmentService recruitmentService;
    @Autowired
    private ActiveRecruitmentRepository activeRecruitmentRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AccountProfileRepository accountProfileRepository;
    @Autowired
    private ClosedRecruitmentRepository closedRecruitmentRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Department testDepartment;
    private Specialty testSpecialty;

    @BeforeAll
    void globalSetup() {
        // Runs once before all tests in this class (if needed).
    }

    @AfterAll
    void globalTeardown() {
        // Runs once after all tests in this class (if needed).
    }

    @BeforeEach
    void setup() {
        testDepartment = createTestDepartmentWithSpecialties();
        testSpecialty = testDepartment.getSpecialties().iterator().next();
    }

    @AfterEach
    void tearDown() {
        closedRecruitmentRepository.deleteAll();
        activeRecruitmentRepository.deleteAll();
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
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

    private AccountProfile createAndSaveAccount(String email) {
        AccountProfile account = AccountProfile.builder()
                .id(UUID.randomUUID())
                .email(email)
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
                .build();
        return accountProfileRepository.save(account);
    }

    private ActiveRecruitment createAndSaveRecruitment(int maxCandidates) {
        LocalDateTime deadlineDate = LocalDateTime.now().plusDays(1);
        RecruitmentAutoCloseConditions conditions = new RecruitmentAutoCloseConditions(deadlineDate, maxCandidates);

        ActiveRecruitment recruitment = ActiveRecruitment.builder()
                .id(UUID.randomUUID())
                .name("Test Active Recruitment")
                .expertise(Expertise.BACKEND)
                .unit(KNUdevUnit.PRECAMPUS)
                .startedAt(LocalDateTime.now())
                .recruitmentAutoCloseConditions(conditions)
                .currentRecruited(Collections.emptySet())
                .build();

        return activeRecruitmentRepository.save(recruitment);
    }

    // ------------------------------------------------------------------------------------------
    // NESTED CLASS: OPENING RECRUITMENT
    // ------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Opening Recruitment Scenarios")
    class OpeningRecruitmentScenarios {

        @Test
        @DisplayName("Should successfully open a recruitment when given a valid request")
        void should_SuccessfullyOpenRecruitment_When_GivenValidRequest() {
            RecruitmentOpenRequest recruitmentOpenRequest = RecruitmentOpenRequest.builder()
                    .recruitmentName("Open Request")
                    .expertise(Expertise.BACKEND)
                    .unit(KNUdevUnit.PRECAMPUS)
                    .autoCloseConditions(new RecruitmentAutoCloseConditionsDto(
                            LocalDateTime.now().plusDays(1),
                            5
                    ))
                    .build();

            ActiveRecruitmentDto activeRecruitment = recruitmentService.openRecruitment(recruitmentOpenRequest);

            assertTrue(activeRecruitmentRepository.existsById(activeRecruitment.id()));
        }

        @Test
        @DisplayName("Should throw an exception when opening a duplicate recruitment (same expertise & unit)")
        void should_ThrowException_When_OpeningDuplicateRecruitmentInSameExpertiseAndUnit() {
            // We already have a recruitment with BACKEND + PRECAMPUS
            createAndSaveRecruitment(5);

            RecruitmentOpenRequest duplicateRequest = RecruitmentOpenRequest.builder()
                    .recruitmentName("Duplicate Recruitment")
                    .expertise(Expertise.BACKEND)
                    .unit(KNUdevUnit.PRECAMPUS)
                    .autoCloseConditions(new RecruitmentAutoCloseConditionsDto(
                            LocalDateTime.now().plusDays(1),
                            5
                    ))
                    .build();

            assertThrows(
                    RecruitmentException.class,
                    () -> recruitmentService.openRecruitment(duplicateRequest)
            );
        }
    }

    // ------------------------------------------------------------------------------------------
    // NESTED CLASS: JOINING RECRUITMENT
    // ------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Joining Recruitment Scenarios")
    class JoiningRecruitmentScenarios {

        @Test
        @DisplayName("Should successfully join a user when enough space in the recruitment")
        void should_SuccessfullyJoinUser_When_EnoughSpaceOnRecruitment() {
            AccountProfile user = createAndSaveAccount("success@example.com");
            ActiveRecruitment recruitment = createAndSaveRecruitment(5);

            recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user.getId(), recruitment.getId())
            );

            int recruitedCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
            assertEquals(1, recruitedCount);
        }

        @Test
        @DisplayName("Should throw exception when the same user joins an already-joined recruitment")
        void should_ThrowException_When_UserAlreadyJoinedRecruitment() {
            // Arrange
            AccountProfile user = createAndSaveAccount("joined@example.com");
            ActiveRecruitment recruitment = createAndSaveRecruitment(5);

            recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user.getId(), recruitment.getId())
            );

            // Act & Assert
            RecruitmentException exception = assertThrows(
                    RecruitmentException.class,
                    () -> recruitmentService.joinActiveRecruitment(
                            new RecruitmentJoinRequest(user.getId(), recruitment.getId())
                    )
            );
            assertTrue(exception.getMessage().contains("User is already in this recruitment"));

            int recruitedCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
            assertEquals(1, recruitedCount);
        }

        @Test
        @DisplayName("Should throw RecruitmentException when user joins a non-existent recruitment")
        void should_ThrowRecruitmentException_When_UserJoinsNonExistentRecruitment() {
            // Arrange
            AccountProfile user = createAndSaveAccount("notfound@example.com");
            UUID fakeRecruitmentId = UUID.randomUUID();

            // Act & Assert
            RecruitmentException exception = assertThrows(
                    RecruitmentException.class,
                    () -> recruitmentService.joinActiveRecruitment(
                            new RecruitmentJoinRequest(user.getId(), fakeRecruitmentId)
                    )
            );
            assertTrue(exception.getMessage().contains("There is no active recruitment with ID"));
        }

        @Test
        @DisplayName("Should throw AccountException when a non-existent user tries to join recruitment")
        void should_ThrowRecruitmentException_When_NonExistentUserJoinsRecruitment() {
            ActiveRecruitment recruitment = createAndSaveRecruitment(5);
            UUID invalidAccountId = UUID.randomUUID();

            AccountException ex = assertThrows(
                    AccountException.class,
                    () -> recruitmentService.joinActiveRecruitment(
                            new RecruitmentJoinRequest(invalidAccountId, recruitment.getId())
                    )
            );
            assertTrue(ex.getMessage().contains("does not exist"));
        }
    }

    // ------------------------------------------------------------------------------------------
    // NESTED CLASS: CLOSING RECRUITMENT
    // ------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Closing Recruitment Scenarios")
    class ClosingRecruitmentScenarios {

        @Test
        @DisplayName("Should close recruitment when the last slot is filled")
        void should_CloseRecruitment_When_LastSlotFilled() {
            // Arrange
            ActiveRecruitment recruitment = createAndSaveRecruitment(2);
            AccountProfile user1 = createAndSaveAccount("auto1@example.com");
            AccountProfile user2 = createAndSaveAccount("auto2@example.com");

            recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user1.getId(), recruitment.getId())
            );
            int beforeCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
            assertEquals(1, beforeCount);

            // Act
            recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user2.getId(), recruitment.getId())
            );

            // Assert
            boolean isStillActive = activeRecruitmentRepository.existsById(recruitment.getId());
            assertFalse(isStillActive);

            ClosedRecruitment closedRecruitment = closedRecruitmentRepository
                    .findById(recruitment.getId())
                    .orElse(null);
            assertNotNull(closedRecruitment);
            assertEquals(recruitment.getName(), closedRecruitment.getName());
        }

        @Test
        @DisplayName("Should throw exception when a second user tries to join a just-closed recruitment")
        void should_ThrowException_When_UserJoinsClosedRecruitment() {
            // Arrange
            ActiveRecruitment recruitment = createAndSaveRecruitment(1);
            AccountProfile user1 = createAndSaveAccount("cap1@example.com");

            recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user1.getId(), recruitment.getId())
            );
            AccountProfile user2 = createAndSaveAccount("cap2@example.com");

            // Act & Assert
            assertThrows(
                    RecruitmentException.class,
                    () -> recruitmentService.joinActiveRecruitment(
                            new RecruitmentJoinRequest(user2.getId(), recruitment.getId())
                    )
            );

            int recruitedCount = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
            assertEquals(1, recruitedCount);
        }

        @Test
        @DisplayName("Should throw exception if attempting to close a recruitment twice")
        void should_ThrowException_When_ClosingRecruitmentTwice() {
            ActiveRecruitment recruitment = createAndSaveRecruitment(2);

            recruitmentService.closeRecruitment(
                    new RecruitmentCloseRequest(recruitment.getId(), RecruitmentCloseCause.MANUAL_CLOSE)
            );

            assertThrows(
                    RecruitmentException.class,
                    () -> recruitmentService.closeRecruitment(
                            new RecruitmentCloseRequest(recruitment.getId(), RecruitmentCloseCause.MANUAL_CLOSE)
                    )
            );
        }

        @Test
        @DisplayName("Should successfully close an active recruitment when given a valid request")
        void should_SuccessfullyCloseRecruitment_When_GivenValidRequest() {
            RecruitmentOpenRequest recruitmentOpenRequest = RecruitmentOpenRequest.builder()
                    .recruitmentName("Open Request")
                    .expertise(Expertise.BACKEND)
                    .unit(KNUdevUnit.PRECAMPUS)
                    .autoCloseConditions(new RecruitmentAutoCloseConditionsDto(
                            LocalDateTime.now().plusDays(1),
                            5
                    ))
                    .build();
            ActiveRecruitmentDto activeRecruitment = recruitmentService.openRecruitment(recruitmentOpenRequest);

            RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(
                    activeRecruitment.id(),
                    RecruitmentCloseCause.MANUAL_CLOSE
            );
            recruitmentService.closeRecruitment(closeRequest);

            assertTrue(closedRecruitmentRepository.existsById(activeRecruitment.id()));
            assertFalse(activeRecruitmentRepository.existsById(activeRecruitment.id()));
        }
    }

    // ------------------------------------------------------------------------------------------
    // NESTED CLASS: AUTO-CLOSE & CONCURRENCY
    // ------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Auto-Close & Concurrency Scenarios")
    class AutoCloseAndConcurrencyScenarios {

        @Test
        @DisplayName("Should allow only one user to join concurrently when there is only one slot")
        void should_AllowOnlyOneJoin_When_OneSlotInConcurrentScenario() throws InterruptedException {
            // Arrange
            ActiveRecruitment recruitment = createAndSaveRecruitment(1);
            AccountProfile user1 = createAndSaveAccount("conc1@example.com");
            AccountProfile user2 = createAndSaveAccount("conc2@example.com");

            RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
            RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

            // Act
            Thread t1 = new Thread(() -> recruitmentService.joinActiveRecruitment(req1));
            Thread t2 = new Thread(() -> recruitmentService.joinActiveRecruitment(req2));

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // Assert
            int recruitedCount = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
            assertTrue(closedRecruitmentRepository.existsById(recruitment.getId()));
            assertEquals(1, recruitedCount);
        }

        @Test
        @DisplayName("Should allow both users to join concurrently when there are two slots")
        void should_AllowBothUsersJoin_When_BothUsersJoinRecruitmentSimultaneously() throws InterruptedException {
            // Arrange
            ActiveRecruitment recruitment = createAndSaveRecruitment(2);
            AccountProfile user1 = createAndSaveAccount("concUser1@example.com");
            AccountProfile user2 = createAndSaveAccount("concUser2@example.com");

            RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
            RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

            // Act
            Thread t1 = new Thread(() -> recruitmentService.joinActiveRecruitment(req1));
            Thread t2 = new Thread(() -> recruitmentService.joinActiveRecruitment(req2));

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // Assert
            int recruitedCount = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
            assertEquals(2, recruitedCount);
        }

        @Test
        @DisplayName("Should close recruitment when a close and join happen at the same time")
        void should_CloseRecruitment_When_ConcurrentCloseAndJoin() throws InterruptedException {
            // Arrange
            ActiveRecruitment recruitment = createAndSaveRecruitment(2);
            AccountProfile user = createAndSaveAccount("closeVsJoin@example.com");

            // Act
            Thread closer = new Thread(() -> recruitmentService.closeRecruitment(
                    new RecruitmentCloseRequest(recruitment.getId(), RecruitmentCloseCause.MANUAL_CLOSE)
            ));
            Thread joiner = new Thread(() -> recruitmentService.joinActiveRecruitment(
                    new RecruitmentJoinRequest(user.getId(), recruitment.getId())
            ));

            closer.start();
            joiner.start();
            closer.join();
            joiner.join();

            // Assert
            boolean recruitmentStillExists = activeRecruitmentRepository.existsById(recruitment.getId());
            if (!recruitmentStillExists) {
                int recruitedCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
                assertEquals(0, recruitedCount);
            }
            assertTrue(closedRecruitmentRepository.existsById(recruitment.getId()));
        }

        @Test
        @DisplayName("Should auto-close the recruitment when the deadline date is reached")
        void should_AutoClose_When_OnDeadlineDate() throws InterruptedException {
            RecruitmentOpenRequest recruitmentOpenRequest = RecruitmentOpenRequest.builder()
                    .recruitmentName("Should be AutoClosed")
                    .expertise(Expertise.BACKEND)
                    .unit(KNUdevUnit.PRECAMPUS)
                    .autoCloseConditions(new RecruitmentAutoCloseConditionsDto(
                            LocalDateTime.now().plusNanos(10),
                            5
                    ))
                    .build();

            ActiveRecruitmentDto recruitment = recruitmentService.openRecruitment(recruitmentOpenRequest);
            Thread.sleep(10);

            assertThrows(RecruitmentException.class, () -> recruitmentService.getById(recruitment.id()));
            assertFalse(activeRecruitmentRepository.existsById(recruitment.id()));
            assertTrue(closedRecruitmentRepository.existsById(recruitment.id()));
        }
    }
}
