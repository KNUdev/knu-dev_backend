package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.mapper.RecruitmentAutoCloseConditionsMapper;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanager.repository.ClosedRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService implements RecruitmentApi {

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
                .build();
        activeRecruitmentRepository.save(activeRecruitment);

        log.info("Recruitment {} with expertise: {} was opened at: {}, auto-close date: {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now(),
                activeRecruitment.getRecruitmentAutoCloseConditions().deadlineDate()
        );
    }

    @Override
    @Transactional
    public void closeRecruitment(UUID activeRecruitmentId) {
        //todo perhaps app some field why active recruitment was closed (enum with 3 values)
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(activeRecruitmentId);

        ClosedRecruitment closedRecruitment = buildClosedRecruitment(activeRecruitment);
        activeRecruitmentRepository.delete(activeRecruitment);
        closedRecruitmentRepository.save(closedRecruitment);

        log.info("Recruitment {} with expertise: {} was manually closed at {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    public void joinActiveRecruitment(RecruitmentJoinRequest joinRequest) {
        UUID accountId = joinRequest.accountId();
        UUID activeRecruitmentId = joinRequest.activeRecruitmentId();
        if (activeRecruitmentRepository.hasUserJoined(activeRecruitmentId, accountId)) {
            throw new RecruitmentException("User is already in this recruitment");
        }

        AccountProfile accountProfileDomain = accountProfileService.getDomainById(accountId);
        ActiveRecruitment activeRecruitment = getActiveRecruitmentDomainById(activeRecruitmentId);

        int currentRecruitedCount = activeRecruitmentRepository.countRecruited(activeRecruitmentId);
        int maxRecruitedLimit = activeRecruitment.getRecruitmentAutoCloseConditions().maxCandidates();
        try {
            if (currentRecruitedCount < maxRecruitedLimit) {
                activeRecruitment.joinUserToRecruitment(accountProfileDomain);
            }
            activeRecruitmentRepository.saveAndFlush(activeRecruitment);

            closeRecruitmentIfMaxCandidatesExceed(currentRecruitedCount, maxRecruitedLimit, activeRecruitmentId);
        } catch (ObjectOptimisticLockingFailureException ex) {
            closeRecruitmentIfMaxCandidatesExceed(currentRecruitedCount, maxRecruitedLimit, activeRecruitmentId);
            throw new RecruitmentException("Something went wrong. Please refresh and try again");
        }
    }

    private void closeRecruitmentIfMaxCandidatesExceed(int currentRecruitedCount,
                                                       int maxRecruited,
                                                       UUID activeRecruitmentId) {
        if (currentRecruitedCount + 1 == maxRecruited) {
            closeRecruitment(activeRecruitmentId);
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