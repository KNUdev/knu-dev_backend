package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanager.domain.embeddable.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanager.mapper.RecruitmentAutoCloseConditionsMapper;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanager.repository.ClosedRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService implements RecruitmentApi {

    private final TransactionTemplate transactionTemplate;
    private final ActiveRecruitmentRepository activeRecruitmentRepository;
    private final ClosedRecruitmentRepository closedRecruitmentRepository;
    private final RecruitmentAutoCloseConditionsMapper recruitmentAutoCloseConditionsMapper;
    private final AccountProfileService accountProfileService;

    @Override
    public void openRecruitment(RecruitmentOpenRequest openRequest) {
        assertActiveRecruitmentNotExists(openRequest);

        RecruitmentAutoCloseConditions autoCloseConditions = recruitmentAutoCloseConditionsMapper
                .toDomain(openRequest.autoCloseConditions());

        ActiveRecruitment activeRecruitment = ActiveRecruitment.builder()
                .name(openRequest.recruitmentName())
                .expertise(openRequest.expertise())
                .recruitmentAutoCloseConditions(autoCloseConditions)
                .currentRecruited(Collections.emptySet())
                .unit(openRequest.unit())
                .startedAt(LocalDateTime.now())
                .build();
        activeRecruitmentRepository.save(activeRecruitment);

        log.info("Recruitment {} with expertise: {} was opened at: {}, auto-close date: {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now(),
                activeRecruitment.getRecruitmentAutoCloseConditions().getDeadlineDate()
        );
    }

    @Override
    @Transactional
    public void closeRecruitment(RecruitmentCloseRequest closeRequest) {
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(closeRequest.activeRecruitmentId());

        ClosedRecruitment closedRecruitment = buildClosedRecruitment(activeRecruitment);
        closedRecruitment.setCloseCause(closeRequest.closeCause());
        activeRecruitmentRepository.delete(activeRecruitment);
        closedRecruitmentRepository.save(closedRecruitment);

        log.info("Recruitment {} with expertise: {} was manually closed at {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now()
        );
    }

    @Override
    public void joinActiveRecruitment(RecruitmentJoinRequest joinRequest) {
        final int maxJoinRetries = 5;

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
            RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(activeRecruitmentId,
                    RecruitmentCloseCause.ON_RECRUITS_EXCEEDING);
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

    private ClosedRecruitment buildClosedRecruitment(ActiveRecruitment activeRecruitment) {
        ClosedRecruitment closedRecruitment = ClosedRecruitment.builder()
                .id(activeRecruitment.getId())
                .name(activeRecruitment.getName())
                .unit(activeRecruitment.getUnit())
                .expertise(activeRecruitment.getExpertise())
                .recruitmentAutoCloseConditions(activeRecruitment.getRecruitmentAutoCloseConditions())
                .closedAt(LocalDateTime.now())
                .startedAt(activeRecruitment.getStartedAt())
                .build();

        RecruitmentAnalytics recruitmentAnalytics = RecruitmentAnalytics.builder()
                .joinedUsers(activeRecruitment.getCurrentRecruited())
                .closedRecruitment(closedRecruitment)
                .build();
        closedRecruitment.setRecruitmentAnalytics(recruitmentAnalytics);
        return closedRecruitment;
    }

}