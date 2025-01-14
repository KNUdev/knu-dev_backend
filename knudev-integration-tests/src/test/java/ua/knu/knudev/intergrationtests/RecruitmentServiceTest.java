package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
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

    @Test
    void should_SuccessfullyJoin_When_EnoughSpace() {
        AccountProfile user = createAndSaveAccount("success@example.com");
        ActiveRecruitment recruitment = createAndSaveRecruitment(5);

        recruitmentService.joinActiveRecruitment(
                new RecruitmentJoinRequest(user.getId(), recruitment.getId())
        );

        int recruitedCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
        assertEquals(1, recruitedCount);
    }

    @Test
    void should_ThrowException_When_UserAlreadyJoined() {
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
    void should_ThrowException_When_RecruitmentNotFound() {
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
    void should_ThrowException_When_UserNotFound() {
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

    @Test
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
    void should_AllowOnlyOneJoin_When_OneSlotInConcurrentScenario() throws InterruptedException {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(1);
        AccountProfile user1 = createAndSaveAccount("conc1@example.com");
        AccountProfile user2 = createAndSaveAccount("conc2@example.com");

        RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
        RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

        // Act
        Thread t1 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req1);
            } catch (RecruitmentException ignored) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req2);
            } catch (RecruitmentException ignored) {
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Assert
        int recruitedCount = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
        assertEquals(1, recruitedCount);
    }

    @Test
    void should_AllowBothUsersJoin_When_BothUsersJoinRecruitmentSimultaneously() throws InterruptedException {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(2);
        AccountProfile user1 = createAndSaveAccount("concUser1@example.com");
        AccountProfile user2 = createAndSaveAccount("concUser2@example.com");

        RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
        RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

        // Act
        Thread t1 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req1);
            } catch (RecruitmentException ignored) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req2);
            } catch (RecruitmentException ignored) {
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Assert
        int recruitedCount = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
        assertEquals(2, recruitedCount);
    }

    @Test
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
    }
}
