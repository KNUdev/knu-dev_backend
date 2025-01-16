package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanager.domain.RecruitmentAnalytics;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanager.repository.ClosedRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentCloseService {

    private final ActiveRecruitmentRepository activeRecruitmentRepository;
    private final ClosedRecruitmentRepository closedRecruitmentRepository;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public ClosedRecruitmentDto closeRecruitment(RecruitmentCloseRequest closeRequest, ActiveRecruitment activeRecruitment) {
        ClosedRecruitment closedRecruitment = buildClosedRecruitment(activeRecruitment);
        closedRecruitment.setCloseCause(closeRequest.closeCause());
        activeRecruitmentRepository.delete(activeRecruitment);
        ClosedRecruitment savedClosedRecruitment = closedRecruitmentRepository.save(closedRecruitment);
        return new ClosedRecruitmentDto(savedClosedRecruitment.getId(), savedClosedRecruitment.getName());
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
