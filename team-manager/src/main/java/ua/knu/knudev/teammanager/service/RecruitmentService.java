package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanager.domain.embeddable.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanager.mapper.RecruitmentMapper;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentAutoCloseConditionsDto;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class RecruitmentService implements RecruitmentApi {

    private final TransactionTemplate transactionTemplate;
    private final ActiveRecruitmentRepository activeRecruitmentRepository;
    private final AccountProfileService accountProfileService;
    private final TaskScheduler taskScheduler;
    private final RecruitmentCloseService recruitmentCloseService;
    private final RecruitmentMapper recruitmentMapper;

    public RecruitmentService(TransactionTemplate transactionTemplate,
                              ActiveRecruitmentRepository activeRecruitmentRepository,
                              AccountProfileService accountProfileService,
                              @Qualifier(value = "closeRecruitmentTaskScheduler") TaskScheduler taskScheduler,
                              RecruitmentCloseService recruitmentCloseService,
                              RecruitmentMapper recruitmentMapper) {
        this.transactionTemplate = transactionTemplate;
        this.activeRecruitmentRepository = activeRecruitmentRepository;
        this.accountProfileService = accountProfileService;
        this.taskScheduler = taskScheduler;
        this.recruitmentCloseService = recruitmentCloseService;
        this.recruitmentMapper = recruitmentMapper;
    }

    @Override
    public ActiveRecruitmentDto openRecruitment(RecruitmentOpenRequest openRequest) {
        assertActiveRecruitmentNotExists(openRequest);

        RecruitmentAutoCloseConditionsDto requestConditions = openRequest.autoCloseConditions();
        RecruitmentAutoCloseConditions autoCloseConditions = new RecruitmentAutoCloseConditions(
                requestConditions.deadlineDate(),
                requestConditions.maxCandidates()
        );

        ActiveRecruitment activeRecruitment = ActiveRecruitment.builder()
                .name(openRequest.recruitmentName())
                .expertise(openRequest.expertise())
                .recruitmentAutoCloseConditions(autoCloseConditions)
                .currentRecruited(Collections.emptySet())
                .unit(openRequest.unit())
                .startedAt(LocalDateTime.now())
                .build();
        ActiveRecruitment savedRecruitment = activeRecruitmentRepository.save(activeRecruitment);

        scheduleRecruitmentAutoCloseOnDeadlineDate(savedRecruitment.getId(), autoCloseConditions.getDeadlineDate());

        log.info("Recruitment {} with expertise: {} was opened at: {}, auto-close date: {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now(),
                activeRecruitment.getRecruitmentAutoCloseConditions().getDeadlineDate()
        );

        return recruitmentMapper.toDto(savedRecruitment);
    }

    @Override
    public ClosedRecruitmentDto closeRecruitment(RecruitmentCloseRequest closeRequest) {
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(closeRequest.activeRecruitmentId());

        ClosedRecruitmentDto closedRecruitment = recruitmentCloseService.closeRecruitment(closeRequest, activeRecruitment);

        log.info("Recruitment {} with expertise: {} was closed by cause {} at {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                closeRequest.closeCause(),
                LocalDateTime.now()
        );
        return closedRecruitment;
    }

    @Override
    public void joinActiveRecruitment(RecruitmentJoinRequest joinRequest) {
        final int maxJoinRetries = 10;

        for (int attempt = 1; attempt <= maxJoinRetries; attempt++) {
            try {
                transactionTemplate.executeWithoutResult(status -> doJoinActiveRecruitment(joinRequest));
                return;
            } catch (ObjectOptimisticLockingFailureException ex) {
                log.warn("Optimistic lock failed on attempt {}/{}. Message={}",
                        attempt, maxJoinRetries, ex.getMessage());
                if (attempt == maxJoinRetries) {
                    throw new RecruitmentException("Concurrent conflict, refresh and try again");
                }
            }
        }
    }

    public ActiveRecruitmentDto getById(UUID id) {
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(id);
        return recruitmentMapper.toDto(activeRecruitment);
    }

    private void doJoinActiveRecruitment(RecruitmentJoinRequest joinRequest) {
        UUID accountId = joinRequest.accountId();
        UUID activeRecruitmentId = joinRequest.activeRecruitmentId();

        if (activeRecruitmentRepository.hasUserJoined(activeRecruitmentId, accountId)) {
            throw new RecruitmentException("User is already in this recruitment");
        }

        AccountProfile accountProfileDomain = accountProfileService.getDomainById(accountId);
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(activeRecruitmentId);

        int currentRecruitedCount = activeRecruitmentRepository.countRecruited(activeRecruitmentId);
        int maxRecruitedLimit = activeRecruitment.getRecruitmentAutoCloseConditions().getMaxCandidates();

        if (currentRecruitedCount < maxRecruitedLimit) {
            activeRecruitment.joinUserToRecruitment(accountProfileDomain);
        }
        activeRecruitmentRepository.saveAndFlush(activeRecruitment);

        int freshCount = activeRecruitmentRepository.countRecruited(activeRecruitmentId);
        if (freshCount == maxRecruitedLimit) {
            RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(
                    activeRecruitmentId,
                    RecruitmentCloseCause.ON_RECRUITS_EXCEEDING
            );
            closeRecruitment(closeRequest);
        }
    }

    private ActiveRecruitment getActiveRecruitmentDomainById(UUID activeRecruitmentId) {
        return activeRecruitmentRepository.findById(activeRecruitmentId).orElseThrow(
                () -> new RecruitmentException("There is no active recruitment with ID: " + activeRecruitmentId)
        );
    }

    private void assertActiveRecruitmentNotExists(RecruitmentOpenRequest openRequest) {
        Expertise expertise = openRequest.expertise();
        KNUdevUnit unit = openRequest.unit();

        boolean existsActiveByExpertise = activeRecruitmentRepository.existsByExpertiseAndUnit(
                expertise, unit
        );
        if (existsActiveByExpertise) {
            throw new RecruitmentException(
                    String.format("Recruitment with expertise: %s in unit %S already exists", expertise, unit)
            );
        }
    }

    private void scheduleRecruitmentAutoCloseOnDeadlineDate(UUID activeRecruitmentId, LocalDateTime deadlineDate) {
        Runnable recruitmentAutoCloseTask = () -> {
            RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(
                    activeRecruitmentId,
                    RecruitmentCloseCause.ON_TIME_LIMIT
            );
            closeRecruitment(closeRequest);
        };
        Instant instant = deadlineDate
                .atZone(ZoneId.of("Europe/Kiev"))
                .toInstant();

        Runnable safeTask = () -> {
            try {
                recruitmentAutoCloseTask.run();
            } catch (Exception e) {
                log.error("Error on autoclose recruitment", e);
            }
        };

        taskScheduler.schedule(safeTask, instant);
        log.info("Scheduled auto close task for recruitment with id: {} at deadline: {}",
                activeRecruitmentId, deadlineDate);
    }

}