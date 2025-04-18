package ua.knu.knudev.education.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.mapper.SessionMapper;
import ua.knu.knudev.education.mapper.SprintMapper;
import ua.knu.knudev.education.repository.EducationSessionRepository;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.educationapi.dto.session.SessionFullDto;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.dto.session.SprintSummaryDto;
import ua.knu.knudev.educationapi.enums.SessionStatus;
import ua.knu.knudev.educationapi.exception.EducationSessionException;
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
    private final SprintService sprintService;
    private final EducationSessionRepository sessionRepository;
    private final TaskScheduler taskScheduler;
    private final SprintChainService sprintChainService;
    private final SprintMapper sprintMapper;
    private final SessionMapper sessionMapper;

    public SessionService(ProgramService programService,
                          SprintService sprintService,
                          EducationSessionRepository sessionRepository,
                          @Qualifier(value = "educationTaskScheduler") TaskScheduler taskScheduler,
                          SprintChainService sprintChainService,
                          SprintMapper sprintMapper,
                          SessionMapper sessionMapper) {
        this.programService = programService;
        this.sprintService = sprintService;
        this.sessionRepository = sessionRepository;
        this.taskScheduler = taskScheduler;
        this.sprintChainService = sprintChainService;
        this.sprintMapper = sprintMapper;
        this.sessionMapper = sessionMapper;
    }

    @Override
    @Transactional
    public SessionSprintPlanDto createSession(SessionCreationRequest request) {
        LocalDateTime sessionStart = request.startDate();

        EducationProgram program = programService.getProgramById(request.programId());
        List<Sprint> sprints = sprintService.generateSessionSprintPlan(program, sessionStart);
        LocalDateTime sessionEstimateEndDate = getSessionEstimateEndDate(sprints);

        EducationSession session = EducationSession.builder()
                .educationProgram(program)
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
        EducationSession session = getById(sessionId);
        if (session.getStatus() == SessionStatus.ONGOING) {
            throw new EducationSessionException(
                    String.format("Session with id %s has already started at %s", sessionId, session.getStartDate()),
                    HttpStatus.BAD_REQUEST
            );
        }

        List<Sprint> sprints = sprintService.adjustSprintsDurations(adjustments, session);
        LocalDateTime sessionEstimateEndDate = getSessionEstimateEndDate(sprints);
        sessionRepository.updateEstimatedEndDateById(sessionId, sessionEstimateEndDate);
    }

    @Transactional
    @Override
    public void extendSprintDuration(UUID sprintId, Integer extensionDays) {
        sprintChainService.extendCurrentSprintDuration(sprintId, extensionDays);
    }

    @Override
    public List<SessionFullDto> getAllSessionsByMentorId(UUID mentorId) {
        List<EducationSession> educationSessions = sessionRepository.findAllByMentorId(mentorId).orElseThrow(
                () -> new EducationSessionException(
                        "Sessions with mentor id " + mentorId + " does not exist",
                        HttpStatus.BAD_REQUEST
                )
        );
        return sessionMapper.toDtos(educationSessions);
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
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EducationSessionException(
                        "Session with id " + sessionId + " does not exist",
                        HttpStatus.BAD_REQUEST
                ));
    }

}
