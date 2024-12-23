package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanager.domain.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanager.mapper.RecruitmentAutoCloseConditionsMapper;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanager.repository.ClosedRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentOpenRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService implements RecruitmentApi {

    private final ActiveRecruitmentRepository activeRecruitmentRepository;
    private final ClosedRecruitmentRepository closedRecruitmentRepository;
    private final RecruitmentAutoCloseConditionsMapper recruitmentAutoCloseConditionsMapper;

    @Override
    public void openRecruitment(RecruitmentOpenRequest creationRequestDto) {
        openRequestValidator(creationRequestDto);

        RecruitmentAutoCloseConditions autoCloseConditions = recruitmentAutoCloseConditionsMapper
                .toDomain(creationRequestDto.getAutoCloseConditions());

        ActiveRecruitment activeRecruitment = ActiveRecruitment.builder()
                .name(creationRequestDto.getRecruitmentName())
                .expertise(creationRequestDto.getExpertise())
                .recruitmentAutoCloseConditions(autoCloseConditions)
                .build();
        activeRecruitmentRepository.save(activeRecruitment);
        log.info("Recruitment {} opened", creationRequestDto.getRecruitmentName());
    }

    @Override
    @Transactional
    public void closeRecruitment(Expertise expertise) {
        closeRequestValidator(expertise);
        ActiveRecruitment activeRecruitment = activeRecruitmentRepository.findById(expertise.name()).orElseThrow();
        ClosedRecruitment closedRecruitment = closedRecruitmentBuilder(activeRecruitment);
        activeRecruitmentRepository.delete(activeRecruitment);
        closedRecruitmentRepository.save(closedRecruitment);
        log.info("Closed recruitment {}", closedRecruitment);
    }

    @Scheduled(fixedRate = 600000)
    @Transactional
    protected void autoCloseRecruitment() {
        // TODO: треба додати це поле в ActiveRecruitment або звідкись його брати, щоб я міг його дістати бо воно також треба і для статистики
        int numberOfRecruitedPeople = 0;

        List<ActiveRecruitment> activeRecruitmentsToClose = new ArrayList<>();
        List<ClosedRecruitment> closedRecruitments = activeRecruitmentRepository.findAll().stream()
                .filter(activeRecruitment -> autoCloseRecruitmentFilter(activeRecruitment, numberOfRecruitedPeople))
                .map(activeRecruitment -> {
                    activeRecruitmentsToClose.add(activeRecruitment);
                    return closedRecruitmentBuilder(activeRecruitment);
                })
                .collect(Collectors.toList());

        if (!closedRecruitments.isEmpty()) {
            closedRecruitmentRepository.saveAll(closedRecruitments);
            activeRecruitmentRepository.deleteAll(activeRecruitmentsToClose);
            log.info("Auto closed recruitments: {}", closedRecruitments);
        }
    }

    private static boolean autoCloseRecruitmentFilter(ActiveRecruitment activeRecruitment, Integer numberOfRecruitedPeople) {
        RecruitmentAutoCloseConditions autoCloseConditions = activeRecruitment.getRecruitmentAutoCloseConditions();
        return autoCloseConditions.getMaxCandidates() <= numberOfRecruitedPeople
                || autoCloseConditions.getDeadlineDate().isBefore(LocalDateTime.now());

    }

    private static ClosedRecruitment closedRecruitmentBuilder(ActiveRecruitment activeRecruitment) {
        return ClosedRecruitment.builder()
                .name(activeRecruitment.getName())
                .expertise(activeRecruitment.getExpertise())
                .recruitmentAutoCloseConditions(activeRecruitment.getRecruitmentAutoCloseConditions())
                .closedAt(LocalDateTime.now())
                .startedAt(activeRecruitment.getStartedAt())
                .build();
    }

    private void openRequestValidator(RecruitmentOpenRequest recruitmentOpenRequest) {
        validateOpenRequestData(recruitmentOpenRequest);
        checkForActiveRecruitments(recruitmentOpenRequest.getExpertise());
    }

    private void closeRequestValidator(Expertise expertise) {
        if (ObjectUtils.isEmpty(expertise)) {
            throw new IllegalArgumentException("Expertise cannot be empty");
        }
    }

    private void validateOpenRequestData(RecruitmentOpenRequest recruitmentOpenRequest) {
        if (ObjectUtils.isEmpty(recruitmentOpenRequest.getAutoCloseConditions())
                || StringUtils.isEmpty(recruitmentOpenRequest.getRecruitmentName())
                || ObjectUtils.isEmpty(recruitmentOpenRequest.getExpertise())) {
            throw new IllegalArgumentException("Recruitment open request is not valid, please check all data");
        }
    }

    private void checkForActiveRecruitments(Expertise expertise) {
        int activeRecruitmentsSize = activeRecruitmentRepository.findActiveRecruitmentByExpertise(expertise).size();
        if (activeRecruitmentsSize > 0) {
            throw new IllegalStateException("Such recruitment already active");
        }
    }

}
