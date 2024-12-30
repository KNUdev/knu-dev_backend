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
import ua.knu.knudev.teammanager.repository.*;
import ua.knu.knudev.teammanager.service.RecruitmentService;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void tearDown() {
        closedRecruitmentRepository.deleteAll();
        activeRecruitmentRepository.deleteAll();
        accountProfileRepository.deleteAll();
        departmentRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    private Department createTestDepartmentWithSpecialties() {
        Department department = new Department();
        department.setNameInEnglish("Test Department");
        department.setNameInUkrainian("Тестовий");

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
                .technicalRole(AccountTechnicalRole.INTERN)
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

    // -------------------------------------------------------------------
    // 1) User Successfully Joins (Happy Path)
    // -------------------------------------------------------------------
    @Test
    void testUserSuccessfullyJoins() {
        // Arrange
        AccountProfile user = createAndSaveAccount("success@example.com");
        ActiveRecruitment recruitment = createAndSaveRecruitment(5); // plenty of space

        // Act
        recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user.getId(), recruitment.getId()));

        // Assert
        int count = activeRecruitmentRepository.countRecruited(recruitment.getId());
        assertThat(count).isEqualTo(1);
    }

    // -------------------------------------------------------------------
    // 2) User Already Joined => RecruitmentException
    // -------------------------------------------------------------------
    @Test
    void testUserAlreadyJoined() {
        // Arrange
        AccountProfile user = createAndSaveAccount("joined@example.com");
        ActiveRecruitment recruitment = createAndSaveRecruitment(5);

        // First join
        recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user.getId(), recruitment.getId()));

        // Act & Assert (second join)
        assertThatThrownBy(() ->
                recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user.getId(), recruitment.getId()))
        ).isInstanceOf(RecruitmentException.class)
                .hasMessageContaining("User is already in this recruitment");

        int count = activeRecruitmentRepository.countRecruited(recruitment.getId());
        assertThat(count).isEqualTo(1); // still only 1
    }

    // -------------------------------------------------------------------
    // 3) Recruitment Does Not Exist => RecruitmentException
    // -------------------------------------------------------------------
    @Test
    void testRecruitmentDoesNotExist() {
        // Arrange
        AccountProfile user = createAndSaveAccount("notfound@example.com");
        UUID fakeRecruitmentId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user.getId(), fakeRecruitmentId))
        ).isInstanceOf(RecruitmentException.class)
                .hasMessageContaining("There is no active recruitment with ID");
    }

    // -------------------------------------------------------------------
    // 4) User Does Not Exist => AccountException
    // -------------------------------------------------------------------
    @Test
    void testUserDoesNotExist() {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(5);
        UUID fakeAccountId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(fakeAccountId, recruitment.getId()))
        ).isInstanceOf(ua.knu.knudev.teammanagerapi.exception.AccountException.class)
                .hasMessageContaining("does not exist");
    }

    // -------------------------------------------------------------------
    // 5) At Capacity => user not added
    // -------------------------------------------------------------------
    @Test
    void testRecruitmentIsAtCapacity() {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(1); // capacity = 1
        AccountProfile user1 = createAndSaveAccount("cap1@example.com");
        recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user1.getId(), recruitment.getId()));

        // Now it's at capacity
        AccountProfile user2 = createAndSaveAccount("cap2@example.com");

        assertThrows(
                RecruitmentException.class,
                () -> recruitmentService.joinActiveRecruitment(
                        new RecruitmentJoinRequest(user2.getId(), recruitment.getId())
                )
        );

        int count = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
        assertThat(count).isEqualTo(1); // user2 not added
    }

    // -------------------------------------------------------------------
    // 6) Closes Automatically After Last Slot
    //    e.g., capacity=2, already 1 joined => second join closes it
    // -------------------------------------------------------------------
    @Test
    void testAutoCloseAfterLastSlot() {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(2);
        AccountProfile user1 = createAndSaveAccount("auto1@example.com");
        AccountProfile user2 = createAndSaveAccount("auto2@example.com");

        // First user
        recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user1.getId(), recruitment.getId()));
        int beforeCount = activeRecruitmentRepository.countRecruited(recruitment.getId());
        assertThat(beforeCount).isEqualTo(1);

        // Second user => triggers capacity=2 => auto-close
        recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user2.getId(), recruitment.getId()));

        // Assert: The recruitment should be closed
        boolean isStillActive = activeRecruitmentRepository.existsById(recruitment.getId());
        assertThat(isStillActive).isFalse();

        // And check in closedRecruitment
        ClosedRecruitment closed = closedRecruitmentRepository.findById(recruitment.getId()).orElse(null);
        assertThat(closed).isNotNull();
        assertThat(closed.getName()).isEqualTo(recruitment.getName());
    }

    // -------------------------------------------------------------------
    // 7) Concurrency: Two Users, One Slot => second user fails
    // -------------------------------------------------------------------
    @Test
    void testConcurrency_TwoUsersOneSlot() throws InterruptedException {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(1); // capacity=1
        AccountProfile user1 = createAndSaveAccount("conc1@example.com");
        AccountProfile user2 = createAndSaveAccount("conc2@example.com");

        // Prepare requests
        RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
        RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

        // Act: run in parallel
        Thread t1 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req1);
            } catch (RecruitmentException e) {
                // Possibly log or store an outcome
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                recruitmentService.joinActiveRecruitment(req2);
            } catch (RecruitmentException e) {
                // Possibly log or store an outcome
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Assert:
        // Only one user ends up in recruitment. The other should see an optimistic lock or a fail
        int count = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
        assertThat(count).isEqualTo(1);
    }

    // -------------------------------------------------------------------
    // 8) Concurrency: Two Users, Enough Slots => both succeed
    // -------------------------------------------------------------------
    @Test
    //Todo this fails
    void testConcurrency_TwoUsersEnoughSlots() throws InterruptedException {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(2); // capacity=2
        AccountProfile user1 = createAndSaveAccount("concUser1@example.com");
        AccountProfile user2 = createAndSaveAccount("concUser2@example.com");

        // Prepare requests
        RecruitmentJoinRequest req1 = new RecruitmentJoinRequest(user1.getId(), recruitment.getId());
        RecruitmentJoinRequest req2 = new RecruitmentJoinRequest(user2.getId(), recruitment.getId());

        // Act
        Thread t1 = new Thread(() -> recruitmentService.joinActiveRecruitment(req1));
        Thread t2 = new Thread(() -> recruitmentService.joinActiveRecruitment(req2));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Assert: both joined if concurrency didn't break
        int count = closedRecruitmentRepository.countTotalRecruited(recruitment.getId());
        assertThat(count).isEqualTo(2);
    }

    // -------------------------------------------------------------------
    // 9) Concurrency: One Thread Closes, Another Thread Joins
    // -------------------------------------------------------------------
    @Test
    void testConcurrency_CloseVsJoin() throws InterruptedException {
        // Arrange
        ActiveRecruitment recruitment = createAndSaveRecruitment(2);
        AccountProfile user = createAndSaveAccount("closeVsJoin@example.com");

        // Act
        Thread closer = new Thread(() -> {
            recruitmentService.closeRecruitment(recruitment.getId());
        });
        Thread joiner = new Thread(() -> {
            recruitmentService.joinActiveRecruitment(new RecruitmentJoinRequest(user.getId(), recruitment.getId()));
        });

        closer.start();
        joiner.start();
        closer.join();
        joiner.join();

        // Assert:
        // If close happened first, the recruitment is gone -> user can't join
        // or if the join sneaks in, the version is bumped or the row is deleted
        boolean stillExists = activeRecruitmentRepository.existsById(recruitment.getId());
        if (!stillExists) {
            // Then the user definitely didn't join an already-closed recruitment
            int count = activeRecruitmentRepository.countRecruited(recruitment.getId());
            assertThat(count).isEqualTo(0);
        } else {
            // Edge case: The join might have happened before the close commit
            // Then the final close might throw an optimistic lock exception
            // or succeed, removing the recruitment.
            // Depending on your logic, you might expect different results.
        }
    }
}
