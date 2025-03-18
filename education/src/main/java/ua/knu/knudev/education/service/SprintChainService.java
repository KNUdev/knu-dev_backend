package ua.knu.knudev.education.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.repository.SprintRepository;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.exception.SprintException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class SprintChainService {

    private final TaskScheduler taskScheduler;
    private final SprintRepository sprintRepository;
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public SprintChainService(@Qualifier("educationTaskScheduler") TaskScheduler taskScheduler,
                              SprintRepository sprintRepository) {
        this.taskScheduler = taskScheduler;
        this.sprintRepository = sprintRepository;
    }

    public void launchSprintChain(UUID sessionId) {
        Sprint firstSprint = sprintRepository.findBySession_IdAndOrderIndex(sessionId, 1);
        if (firstSprint != null) {
            scheduleSprint(firstSprint);
        }
    }

    public void extendCurrentSprintDuration(UUID sprintId, int additionalDays) {
        Sprint currentSprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        boolean statusIsValid = currentSprint.getStatus() == SprintStatus.FUTURE
                || currentSprint.getStatus() == SprintStatus.ACTIVE;
        if (statusIsValid) {
            int newDuration = currentSprint.getDurationInDays() + additionalDays;
            currentSprint.setDurationInDays(newDuration);
            sprintRepository.save(currentSprint);

            UUID sessionId = currentSprint.getSession().getId();
            int nextOrderIndex = currentSprint.getOrderIndex() + 1;
            Sprint nextSprint = sprintRepository.findBySession_IdAndOrderIndex(sessionId, nextOrderIndex);

            if (nextSprint != null) {
                LocalDateTime newNextStartDate = currentSprint.getStartDate().plusDays(newDuration);
                nextSprint.setStartDate(newNextStartDate);
                sprintRepository.save(nextSprint);

                ScheduledFuture<?> existingTask = scheduledTasks.get(nextSprint.getId());
                if (existingTask != null && !existingTask.isDone()) {
                    existingTask.cancel(false);
                }
                scheduleSprint(nextSprint);
            }
            sprintRepository.updateSprintStartDates(
                    currentSprint.getSession().getId(),
                    currentSprint.getOrderIndex(),
                    additionalDays
            );
        } else {
            throw new SprintException(
                    "Spring with id: " + currentSprint.getId() + "has status " + currentSprint.getStatus()
            );
        }
    }

    private void scheduleSprint(Sprint sprint) {
        Instant startInstant = sprint.getStartDate()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Runnable safeTask = () -> {
            try {
                startSprint(sprint);
            } catch (Exception e) {
                throw new SprintException(e.getMessage());
            }
        };

        ScheduledFuture<?> future = taskScheduler.schedule(safeTask, startInstant);
        scheduledTasks.put(sprint.getId(), future);

        log.info("Successfully scheduled sprint with id {}, index {}", sprint.getId().toString(), sprint.getOrderIndex());
    }

    private void startSprint(Sprint sprint) {
        Sprint currentSprint = sprintRepository.findById(sprint.getId())
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        currentSprint.setStatus(SprintStatus.ACTIVE);
        sprintRepository.save(currentSprint);

        completePreviousSprint(currentSprint);

        // Todo logic to start sprint
        // runSprintBusinessLogic(currentSprint);

        Sprint nextSprint = sprintRepository.findBySession_IdAndOrderIndex(
                currentSprint.getSession().getId(), currentSprint.getOrderIndex() + 1);
        if (nextSprint != null) {
            LocalDateTime newStartDate = currentSprint.getStartDate()
                    .plusDays(currentSprint.getDurationInDays());
            nextSprint.setStartDate(newStartDate);
            sprintRepository.save(nextSprint);
            scheduleSprint(nextSprint);
        }
    }

    private void completePreviousSprint(Sprint currentSprint) {
        int previousOrderIndex = currentSprint.getOrderIndex() - 1;
        if (previousOrderIndex >= 1) {
            Sprint previousSprint = sprintRepository.findBySession_IdAndOrderIndex(
                    currentSprint.getSession().getId(), previousOrderIndex
            );
            if (previousSprint != null && previousSprint.getStatus() != SprintStatus.COMPLETED) {
                previousSprint.setStatus(SprintStatus.COMPLETED);
                sprintRepository.save(previousSprint);
            }
        }
    }

}

