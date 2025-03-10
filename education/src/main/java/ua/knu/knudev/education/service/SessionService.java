package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.repository.EducationSessionRepository;
import ua.knu.knudev.education.repository.SprintRepository;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.educationapi.dto.session.SessionFullDto;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.dto.session.SprintDto;
import ua.knu.knudev.educationapi.enums.SessionStatus;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService implements SessionApi {

    private final ProgramService programService;
    private final SprintService sprintService; // has generateSessionSprintPlan(UUID programId)
    private final EducationSessionRepository sessionRepository;
    private final SprintRepository sprintRepository;
//    private final SessionMapper sessionMapper; // for mapping to SessionFullDto

    @Override
    public SessionFullDto createSession(SessionCreationRequest request) {
        EducationProgram program = programService.getProgramById(request.programId());

        // 2. Generate the sprint plan (preview) based on the program.
        List<Sprint> sprints = sprintService.generateSessionSprintPlan(program);

        // 3. Map sprint adjustments from request into a lookup map.
//        Map<UUID, Integer> adjustments = request.getSprintAdjustments().stream()
//                .collect(Collectors.toMap(SprintAdjustmentDto::getSprintId, SprintAdjustmentDto::getDurationDays));

//        // 4. Apply adjustments to the preview plan.
//        applySprintAdjustments(planDto, adjustments);
//
//        // 5. Convert the plan DTO into a flat list of Sprint domain objects.
//        List<Sprint> sprintDomains = flattenPlanDtoToDomain(planDto);

        // 6. Compute start dates for sprints.
        LocalDateTime sessionStart = LocalDateTime.now();


        LocalDateTime sessionEstimateEndDate = getSessionEstimateEndDate(sprints);
        EducationSession session = EducationSession.builder()
                .educationProgram(program)
//                .mentorIds(request.getMentorIds())
                .estimatedEndDate(sessionEstimateEndDate)
                .startDate(sessionStart)
                .status(SessionStatus.CREATED)
                .build();
        setSprintStartDates(sprints, session);

        // 9. Persist the session and sprints.
        sessionRepository.save(session);
        sprintRepository.saveAll(sprints);

        // 10. Return the full session DTO (including sprint details).
//        return sessionMapper.toSessionFullDto(session);
        return null;
    }

    private LocalDateTime getSessionEstimateEndDate(List<Sprint> sprints) {
        Sprint lastSprint = sprints.get(sprints.size() - 1);
        //todo think
        return LocalDateTime.now();
//        return lastSprint.getS
    }

    private LocalDateTime setSprintStartDates(List<Sprint> sprints, EducationSession session) {
        LocalDateTime sessionStart = LocalDateTime.now();
        sprints.sort(Comparator.comparingInt(Sprint::getOrderIndex));
        LocalDateTime currentStart = sessionStart;
        for (Sprint sprint : sprints) {
            session.addSprint(sprint);
//            sprint.setStartDate(currentStart);
            currentStart = currentStart.plusDays(sprint.getDurationDays());
        }
        return currentStart;
    }

    /**
     * Recursively applies the adjusted durations to all sprints in the plan DTO.
     */
    private void applySprintAdjustments(SessionSprintPlanDto planDto, Map<UUID, Integer> adjustments) {
        // Update each section
        if (planDto.getSections() != null) {
            planDto.getSections().forEach(section -> {
                // Update modules within the section.
                if (section.getModules() != null) {
                    section.getModules().forEach(module -> {
                        // Update each topic sprint.
                        if (module.getTopicSprints() != null) {
                            module.getTopicSprints().forEach(sprint -> {
                                if (adjustments.containsKey(sprint.getSprintId())) {
                                    sprint.setDurationDays(adjustments.get(sprint.getSprintId()));
                                }
                            });
                        }
                        // Update module final sprint.
                        if (module.getModuleFinalSprint() != null && adjustments.containsKey(module.getModuleFinalSprint().getSprintId())) {
                            module.getModuleFinalSprint().setDurationDays(adjustments.get(module.getModuleFinalSprint().getSprintId()));
                        }
                    });
                }
                // Update section final sprint.
                if (section.getSectionFinalSprint() != null && adjustments.containsKey(section.getSectionFinalSprint().getSprintId())) {
                    section.getSectionFinalSprint().setDurationDays(adjustments.get(section.getSectionFinalSprint().getSprintId()));
                }
            });
        }
        // Update program final sprint.
        if (planDto.getProgramFinalSprint() != null && adjustments.containsKey(planDto.getProgramFinalSprint().getSprintId())) {
            planDto.getProgramFinalSprint().setDurationDays(adjustments.get(planDto.getProgramFinalSprint().getSprintId()));
        }
    }

    /**
     * Flattens the grouped SessionSprintPlanDto into a single list of Sprint domain objects.
     */
    private List<Sprint> flattenPlanDtoToDomain(SessionSprintPlanDto planDto) {
        List<Sprint> list = new ArrayList<>();
        // Process each section.
        if (planDto.getSections() != null) {
            planDto.getSections().forEach(sectionDto -> {
                // Process each module.
                if (sectionDto.getModules() != null) {
                    sectionDto.getModules().forEach(moduleDto -> {
                        // Add topic sprints.
                        if (moduleDto.getTopicSprints() != null) {
                            moduleDto.getTopicSprints().forEach(sprintDto -> {
                                Sprint sprint = convertDtoToDomain(sprintDto);
                                list.add(sprint);
                            });
                        }
                        // Add module final sprint.
                        if (moduleDto.getModuleFinalSprint() != null) {
                            Sprint sprint = convertDtoToDomain(moduleDto.getModuleFinalSprint());
                            list.add(sprint);
                        }
                    });
                }
                // Add section final sprint.
                if (sectionDto.getSectionFinalSprint() != null) {
                    Sprint sprint = convertDtoToDomain(sectionDto.getSectionFinalSprint());
                    list.add(sprint);
                }
            });
        }
        // Add program final sprint.
        if (planDto.getProgramFinalSprint() != null) {
            Sprint sprint = convertDtoToDomain(planDto.getProgramFinalSprint());
            list.add(sprint);
        }
        return list;
    }

    /**
     * Converts a SprintDto from the plan into a Sprint domain object.
     */
    private Sprint convertDtoToDomain(SprintDto dto) {
        Sprint sprint = Sprint.builder()
                .orderIndex(dto.getOrderIndex())
                .sprintType(dto.getSprintType())
                .durationDays(dto.getDurationDays())
                // Additional properties (like title/description) can be stored in the domain if needed.
                .build();
        // Optionally, set properties based on sprint type.
        // For example, if sprintType == "TOPIC", you might later associate a ProgramTopic entity.
        // For now, the DTO carries relatedId and relatedName only for display.
        return sprint;
    }
}
