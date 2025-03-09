package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionApi {

    private final ProgramService educationProgramService;
    private final SprintServiceImpl sprintService; // has generateSessionSprintPlan(UUID programId)
    private final EducationSessionRepository sessionRepository;
    private final SprintRepository sprintRepository;
//    private final SessionMapper sessionMapper; // for mapping to SessionFullDto

    /**
     * Creates a new session based on the program and the adjusted sprint plan.
     */
    @Override
    public SessionFullDto createSession(CreateSessionRequestDto request) {
        // 1. Retrieve the program.
        EducationProgram program = educationProgramService.getProgramById(request.getProgramId());

        // 2. Generate the sprint plan (preview) based on the program.
        SessionSprintPlanDto planDto = sprintService.generateSessionSprintPlan(request.getProgramId());

        // 3. Map sprint adjustments from request into a lookup map.
        Map<UUID, Integer> adjustments = request.getSprintAdjustments().stream()
                .collect(Collectors.toMap(SprintAdjustmentDto::getSprintId, SprintAdjustmentDto::getDurationDays));

        // 4. Apply adjustments to the preview plan.
        applySprintAdjustments(planDto, adjustments);

        // 5. Convert the plan DTO into a flat list of Sprint domain objects.
        List<Sprint> sprintDomains = flattenPlanDtoToDomain(planDto);

        // 6. Compute start dates for sprints.
        LocalDateTime sessionStart = LocalDateTime.now(); // session start date
        sprintDomains.sort(Comparator.comparingInt(Sprint::getOrderIndex));
        LocalDateTime currentStart = sessionStart;
        for (Sprint sprint : sprintDomains) {
            sprint.setStartDate(currentStart);
            currentStart = currentStart.plusDays(sprint.getDurationDays());
        }
        LocalDateTime sessionEnd = currentStart;

        // 7. Build the EducationSession domain object.
        EducationSession session = EducationSession.builder()
                .educationProgram(program)
                .mentorIds(request.getMentorIds())
                .sessionStartDate(sessionStart)
                .sessionEndDate(sessionEnd)
                .status(SessionStatus.CREATED)
                .build();

        // 8. Associate sprints with the session.
        sprintDomains.forEach(sprint -> sprint.setEducationSession(session));

        // 9. Persist the session and sprints.
        sessionRepository.save(session);
        sprintRepository.saveAll(sprintDomains);

        // 10. Return the full session DTO (including sprint details).
//        return sessionMapper.toSessionFullDto(session);
        return null;
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
