package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;

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
                .toDomain(creationRequestDto.autoCloseConditions());

        ActiveRecruitment activeRecruitment = ActiveRecruitment.builder()
                .name(creationRequestDto.recruitmentName())
                .expertise(creationRequestDto.expertise())
                .recruitmentAutoCloseConditions(autoCloseConditions)
                .currentRecruitedCount(0)
                .build();
        activeRecruitmentRepository.save(activeRecruitment);
        log.info("Recruitment: {} with expertise: {} was opened at: {}, auto-close date: {}", activeRecruitment.getName(),
                activeRecruitment.getExpertise(), LocalDateTime.now(), activeRecruitment.getRecruitmentAutoCloseConditions().deadlineDate());
    }

    @Override
    @Transactional
    public void manuallyCloseRecruitment(Expertise expertise) {
        ActiveRecruitment activeRecruitment = activeRecruitmentRepository.findById(expertise.name()).orElseThrow(
                () -> new RecruitmentException("There is no active recruitment with "+ expertise + " expertise"));
        ClosedRecruitment closedRecruitment = buildClosedRecruitment(activeRecruitment);
        activeRecruitmentRepository.delete(activeRecruitment);
        closedRecruitmentRepository.save(closedRecruitment);
        log.info("Recruitment {} with expertise {}, with total people of {}, was manually closed at {}", activeRecruitment.getName(),
                activeRecruitment.getExpertise(), activeRecruitment.getRecruitmentAutoCloseConditions().maxCandidates(),
                LocalDateTime.now());
    }

//    TODO WE NEED TO REDO THIS METHOD LITTLE
    @Transactional
    protected void autoCloseRecruitment() {
        int numberOfRecruitedPeople = 0;

        List<ActiveRecruitment> activeRecruitmentsToClose = new ArrayList<>();
        List<ClosedRecruitment> closedRecruitments = activeRecruitmentRepository.findAll().stream()
                .filter(activeRecruitment -> autoCloseRecruitmentFilter(activeRecruitment, numberOfRecruitedPeople))
                .map(activeRecruitment -> {
                    activeRecruitmentsToClose.add(activeRecruitment);
                    return buildClosedRecruitment(activeRecruitment);
                })
                .collect(Collectors.toList());

        if (!closedRecruitments.isEmpty()) {
            closedRecruitmentRepository.saveAll(closedRecruitments);
            activeRecruitmentRepository.deleteAll(activeRecruitmentsToClose);
            log.info("Auto closed recruitments: {}", closedRecruitments);
        }
    }

//    TODO REDO THAT ALSO
    private static boolean autoCloseRecruitmentFilter(ActiveRecruitment activeRecruitment, Integer numberOfRecruitedPeople) {
        RecruitmentAutoCloseConditions autoCloseConditions = activeRecruitment.getRecruitmentAutoCloseConditions();
        return autoCloseConditions.maxCandidates() <= numberOfRecruitedPeople
                || autoCloseConditions.deadlineDate().isBefore(LocalDateTime.now());

    }

    private ClosedRecruitment buildClosedRecruitment(ActiveRecruitment activeRecruitment) {
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
        checkForActiveRecruitments(recruitmentOpenRequest.expertise());
    }


    private void validateOpenRequestData(RecruitmentOpenRequest recruitmentOpenRequest) {
        if (ObjectUtils.isEmpty(recruitmentOpenRequest.autoCloseConditions())) {
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
