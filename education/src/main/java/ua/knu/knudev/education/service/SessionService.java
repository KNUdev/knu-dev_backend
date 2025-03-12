package ua.knu.knudev.education.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.mapper.SprintMapper;
import ua.knu.knudev.education.repository.EducationSessionRepository;
import ua.knu.knudev.education.repository.SprintRepository;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.dto.session.SprintSummaryDto;
import ua.knu.knudev.educationapi.enums.SessionStatus;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;
import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SessionService implements SessionApi {

    private final ProgramService programService;
    private final SprintService sprintService; // has generateSessionSprintPlan(UUID programId)
    private final EducationSessionRepository sessionRepository;
    private final SprintRepository sprintRepository;
    private final TaskScheduler taskScheduler;
    private final SprintChainService sprintChainService;
    private final SprintMapper sprintMapper;
//    private final SessionMapper sessionMapper; // for mapping to SessionFullDto


    public SessionService(ProgramService programService,
                          SprintService sprintService,
                          EducationSessionRepository sessionRepository,
                          SprintRepository sprintRepository,
                          @Qualifier(value = "educationTaskScheduler") TaskScheduler taskScheduler, SprintChainService sprintChainService, SprintMapper sprintMapper) {
        this.programService = programService;
        this.sprintService = sprintService;
        this.sessionRepository = sessionRepository;
        this.sprintRepository = sprintRepository;
        this.taskScheduler = taskScheduler;
        this.sprintChainService = sprintChainService;
        this.sprintMapper = sprintMapper;
    }

    @Override
    @Transactional
    public SessionSprintPlanDto createSession(SessionCreationRequest request) {
        EducationProgram program = programService.getProgramById(request.programId());
        List<Sprint> sprints = sprintService.generateSessionSprintPlan(program);
        LocalDateTime sessionStart = request.startDate();
        LocalDateTime sessionEstimateEndDate = getSessionEstimateEndDate(sprints);

        EducationSession session = EducationSession.builder()
                .educationProgram(program)
//                .mentorIds(request.getMentorIds())
                .estimatedEndDate(sessionEstimateEndDate)
                .startDate(sessionStart)
                .status(SessionStatus.SCHEDULED)
                .build();
        session.addSAllSprints(sprints);

        EducationSession savedSession = sessionRepository.save(session);
        scheduleSessionStart(session.getId(), sessionStart);

        int totalSessionDuration = savedSession.getEstimatedEndDate().getDayOfYear() - sessionStart.getDayOfYear();
        List<SprintSummaryDto> sprintDtos = sprintMapper.toDtos(sprints);
        return SessionSprintPlanDto.builder()
                .estimatedEndDate(sessionEstimateEndDate)
                .startDate(sessionStart)
                .totalDurationInDays(totalSessionDuration)
                .sprints(sprintDtos)
                .build();
    }

    @Override
    @Transactional
    public void adjustSprintsDurations(List<SprintAdjustmentRequest> adjustments, UUID sessionId) {
        boolean sessionExists = sessionRepository.existsById(sessionId);
        if (!sessionExists) {
            //todo session exception
            throw new RuntimeException("Session with id " + sessionId + " does not exist");
        }
        List<Sprint> sprints = sprintService.adjustSprintsDurations(adjustments, sessionId);
        LocalDateTime sessionEstimateEndDate = getSessionEstimateEndDate(sprints);
        sessionRepository.updateEstimatedEndDateById(sessionId, sessionEstimateEndDate);
    }

    private void startSession(UUID scheduledSessionId) {
        EducationSession session = getById(scheduledSessionId);
        session.setStatus(SessionStatus.ONGOING);
        sprintChainService.launchSprintChain(scheduledSessionId);
        sessionRepository.save(session);
    }

    private void scheduleSessionStart(UUID scheduledSessionId, LocalDateTime sessionStartDate) {
        Runnable startSessionTask = () -> startSession(scheduledSessionId);
        Instant instant = sessionStartDate
                .atZone(ZoneId.of("Europe/Kiev"))
                .toInstant();

        Runnable safeTask = () -> {
            try {
                startSessionTask.run();
            } catch (Exception e) {
                log.error("Error on autoclose recruitment", e);
            }
        };

        taskScheduler.schedule(safeTask, instant);
        log.info("Scheduled session({}), start task for {}", scheduledSessionId, sessionStartDate);
    }

    private LocalDateTime getSessionEstimateEndDate(List<Sprint> sprints) {
        Sprint lastSprint = sprints.get(sprints.size() - 1);
        return lastSprint.getStartDate().plusDays(lastSprint.getDurationInDays());
    }

    private EducationSession getById(UUID sessionId) {
        //todo session exc
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session with id " + sessionId + " does not exist"));
    }

    /**
     * Recursively applies the adjusted durations to all sprints in the plan DTO.
     */
//    private void applySprintAdjustments(SessionSprintPlanDto planDto, Map<UUID, Integer> adjustments) {
//        // Update each section
//        if (planDto.getSections() != null) {
//            planDto.getSections().forEach(section -> {
//                // Update modules within the section.
//                if (section.getModules() != null) {
//                    section.getModules().forEach(module -> {
//                        // Update each topic sprint.
//                        if (module.getTopicSprints() != null) {
//                            module.getTopicSprints().forEach(sprint -> {
//                                if (adjustments.containsKey(sprint.getSprintId())) {
//                                    sprint.setDurationDays(adjustments.get(sprint.getSprintId()));
//                                }
//                            });
//                        }
//                        // Update module final sprint.
//                        if (module.getModuleFinalSprint() != null && adjustments.containsKey(module.getModuleFinalSprint().getSprintId())) {
//                            module.getModuleFinalSprint().setDurationDays(adjustments.get(module.getModuleFinalSprint().getSprintId()));
//                        }
//                    });
//                }
//                // Update section final sprint.
//                if (section.getSectionFinalSprint() != null && adjustments.containsKey(section.getSectionFinalSprint().getSprintId())) {
//                    section.getSectionFinalSprint().setDurationDays(adjustments.get(section.getSectionFinalSprint().getSprintId()));
//                }
//            });
//        }
//        // Update program final sprint.
//        if (planDto.getProgramFinalSprint() != null && adjustments.containsKey(planDto.getProgramFinalSprint().getSprintId())) {
//            planDto.getProgramFinalSprint().setDurationDays(adjustments.get(planDto.getProgramFinalSprint().getSprintId()));
//        }
//    }
//
//    /**
//     * Flattens the grouped SessionSprintPlanDto into a single list of Sprint domain objects.
//     */
//    private List<Sprint> flattenPlanDtoToDomain(SessionSprintPlanDto planDto) {
//        List<Sprint> list = new ArrayList<>();
//        // Process each section.
//        if (planDto.getSections() != null) {
//            planDto.getSections().forEach(sectionDto -> {
//                // Process each module.
//                if (sectionDto.getModules() != null) {
//                    sectionDto.getModules().forEach(moduleDto -> {
//                        // Add topic sprints.
//                        if (moduleDto.getTopicSprints() != null) {
//                            moduleDto.getTopicSprints().forEach(sprintDto -> {
//                                Sprint sprint = convertDtoToDomain(sprintDto);
//                                list.add(sprint);
//                            });
//                        }
//                        // Add module final sprint.
//                        if (moduleDto.getModuleFinalSprint() != null) {
//                            Sprint sprint = convertDtoToDomain(moduleDto.getModuleFinalSprint());
//                            list.add(sprint);
//                        }
//                    });
//                }
//                // Add section final sprint.
//                if (sectionDto.getSectionFinalSprint() != null) {
//                    Sprint sprint = convertDtoToDomain(sectionDto.getSectionFinalSprint());
//                    list.add(sprint);
//                }
//            });
//        }
//        // Add program final sprint.
//        if (planDto.getProgramFinalSprint() != null) {
//            Sprint sprint = convertDtoToDomain(planDto.getProgramFinalSprint());
//            list.add(sprint);
//        }
//        return list;
//    }
//
//    /**
//     * Converts a SprintDto from the plan into a Sprint domain object.
//     */
//    private Sprint convertDtoToDomain(SprintDto dto) {
//        Sprint sprint = Sprint.builder()
//                .orderIndex(dto.getOrderIndex())
//                .sprintType(dto.getSprintType())
//                .durationDays(dto.getDurationDays())
//                // Additional properties (like title/description) can be stored in the domain if needed.
//                .build();
//        // Optionally, set properties based on sprint type.
//        // For example, if sprintType == "TOPIC", you might later associate a ProgramTopic entity.
//        // For now, the DTO carries relatedId and relatedName only for display.
//        return sprint;
//    }
}
