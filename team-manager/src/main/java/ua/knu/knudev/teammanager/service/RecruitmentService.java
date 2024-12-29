package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanager.domain.embeddable.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanager.mapper.RecruitmentAutoCloseConditionsMapper;
import ua.knu.knudev.teammanager.repository.ActiveRecruitmentRepository;
import ua.knu.knudev.teammanager.repository.ClosedRecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;
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
        ActiveRecruitment activeRecruitment = activeRecruitmentRepository.findById(activeRecruitmentId).orElseThrow(
                () -> new RecruitmentException("There is no active recruitment with ID: " + activeRecruitmentId)
        );

        ClosedRecruitment closedRecruitment = buildClosedRecruitment(activeRecruitment);
        activeRecruitmentRepository.delete(activeRecruitment);
        closedRecruitmentRepository.save(closedRecruitment);

        log.info("Recruitment {} with expertise {}, was manually closed at {}",
                activeRecruitment.getName(),
                activeRecruitment.getExpertise(),
                LocalDateTime.now()
        );
    }

//    TODO WE NEED TO REDO THIS METHOD LITTLE
//    @Transactional
//    protected void autoCloseRecruitment() {
//        int numberOfRecruitedPeople = 0;
//
//        List<ActiveRecruitment> activeRecruitmentsToClose = new ArrayList<>();
//        List<ClosedRecruitment> closedRecruitments = activeRecruitmentRepository.findAll().stream()
//                .filter(activeRecruitment -> autoCloseRecruitmentFilter(activeRecruitment, numberOfRecruitedPeople))
//                .map(activeRecruitment -> {
//                    activeRecruitmentsToClose.add(activeRecruitment);
//                    return buildClosedRecruitment(activeRecruitment);
//                })
//                .collect(Collectors.toList());
//
//        if (!closedRecruitments.isEmpty()) {
//            closedRecruitmentRepository.saveAll(closedRecruitments);
//            activeRecruitmentRepository.deleteAll(activeRecruitmentsToClose);
//            log.info("Auto closed recruitments: {}", closedRecruitments);
//        }
//    }
//
////    TODO REDO THAT ALSO
//    private static boolean autoCloseRecruitmentFilter(ActiveRecruitment activeRecruitment, Integer numberOfRecruitedPeople) {
//        RecruitmentAutoCloseConditions autoCloseConditions = activeRecruitment.getRecruitmentAutoCloseConditions();
//        return autoCloseConditions.maxCandidates() <= numberOfRecruitedPeople
//                || autoCloseConditions.deadlineDate().isBefore(LocalDateTime.now());
//
//    }

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
        return ClosedRecruitment.builder()
                .id(activeRecruitment.getId())
                .name(activeRecruitment.getName())
                .expertise(activeRecruitment.getExpertise())
                .recruitmentAutoCloseConditions(activeRecruitment.getRecruitmentAutoCloseConditions())
                .closedAt(LocalDateTime.now())
                .startedAt(activeRecruitment.getStartedAt())
                .build();
    }
}
