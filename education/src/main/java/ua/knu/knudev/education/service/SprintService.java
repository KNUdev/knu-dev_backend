package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.repository.SprintRepository;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;
import ua.knu.knudev.educationapi.exception.SprintException;
import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintService {
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;
    private final SprintRepository sprintRepository;

    @Value("${application.session.sprints.defaultDurationInDays}")
    private Integer defaultSprintDuration;

    @Value("${application.session.sprints.moduleEndStateTimeInDays}")
    private Integer moduleEndStaleTimeInDays;

    @Value("${application.session.sprints.sectionEndStaleTimeInDays}")
    private Integer sectionEndStaleTimeInDays;

    public List<Sprint> generateSessionSprintPlan(EducationProgram program, LocalDateTime sessionStart) {
        List<ProgramSectionMapping> sectionMappings =
                programSectionMappingRepository.findByEducationProgramId(program.getId());
        sectionMappings.sort(Comparator.comparingInt(ProgramSectionMapping::getOrderIndex));

        Set<UUID> sectionIds = sectionMappings.stream()
                .map(mapping -> mapping.getSection().getId())
                .collect(Collectors.toSet());

        List<SectionModuleMapping> moduleMappings = sectionModuleMappingRepository.findBySectionIdIn(sectionIds);
        moduleMappings.sort(Comparator.comparingInt(SectionModuleMapping::getOrderIndex));

        Set<UUID> moduleIds = moduleMappings.stream()
                .map(mapping -> mapping.getModule().getId())
                .collect(Collectors.toSet());

        List<ModuleTopicMapping> topicMappings = moduleTopicMappingRepository.findByModuleIdIn(moduleIds);
        topicMappings.sort(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex));

        AtomicInteger orderCounter = new AtomicInteger(1);
        List<Sprint> sprintsToSave = new ArrayList<>();

        for (ProgramSectionMapping sectionMapping : sectionMappings) {
            ProgramSection section = sectionMapping.getSection();

            List<SectionModuleMapping> modulesForSection = moduleMappings.stream()
                    .filter(m -> m.getSection().getId().equals(section.getId()))
                    .sorted(Comparator.comparingInt(SectionModuleMapping::getOrderIndex))
                    .toList();

            for (SectionModuleMapping moduleMapping : modulesForSection) {
                ProgramModule module = moduleMapping.getModule();

                List<ModuleTopicMapping> topicsForModule = topicMappings.stream()
                        .filter(t -> t.getModule().getId().equals(module.getId()))
                        .sorted(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex))
                        .toList();

                for (ModuleTopicMapping topicMapping : topicsForModule) {
                    ProgramTopic topic = topicMapping.getTopic();
                    Sprint topicSprint = Sprint.builder()
                            .durationInDays(defaultSprintDuration)
                            .type(SprintType.TOPIC)
                            .status(SprintStatus.FUTURE)
                            .orderIndex(orderCounter.getAndIncrement())
                            .program(program)
                            .programSection(section)
                            .programModule(module)
                            .programTopic(topic)
                            .build();

                    sprintsToSave.add(topicSprint);
                }

                Sprint moduleFinalSprint = Sprint.builder()
                        .durationInDays(defaultSprintDuration)
                        .type(SprintType.MODULE_FINAL)
                        .status(SprintStatus.FUTURE)
                        .orderIndex(orderCounter.getAndIncrement())
                        .program(program)
                        .programSection(section)
                        .programModule(module)
                        .build();

                sprintsToSave.add(moduleFinalSprint);
            }

            Sprint sectionFinalSprint = Sprint.builder()
                    .durationInDays(defaultSprintDuration)
                    .type(SprintType.SECTION_FINAL)
                    .status(SprintStatus.FUTURE)
                    .orderIndex(orderCounter.getAndIncrement())
                    .program(program)
                    .programSection(section)
                    .build();

            sprintsToSave.add(sectionFinalSprint);
        }

        Sprint programSprint = Sprint.builder()
                .durationInDays(defaultSprintDuration)
                .type(SprintType.PROGRAM_FINAL)
                .status(SprintStatus.FUTURE)
                .orderIndex(orderCounter.getAndIncrement())
                .program(program)
                .build();
        sprintsToSave.add(programSprint);

        updateSprintsStartDates(sprintsToSave, sessionStart);
        return sprintsToSave;
    }

    public List<Sprint> adjustSprintsDurations(List<SprintAdjustmentRequest> adjustments, EducationSession session) {
        List<Sprint> foundSprints = sprintRepository.findAllBySession_Id(session.getId());
        List<Sprint> adjustedSprintsDurationInDays = foundSprints.stream()
                .peek(sprint -> {
                    SprintAdjustmentRequest adjustment = adjustments.stream()
                            .filter(adj -> adj.getNewDurationInDays() != null)
                            .filter(adj -> adj.getSprintId() != null)
                            .filter(adj -> sprint.getId().equals(adj.getSprintId()))
                            .findFirst()
                            .orElseThrow(() -> new SprintException(
                                    "Sprint not found according to adjustment request"
                            ));
                    if (ObjectUtils.isNotEmpty(adjustment)) {
                        sprint.setDurationInDays(adjustment.getNewDurationInDays());
                    }
                })
                .collect(Collectors.toList());

        List<Sprint> sprintsToSave = updateSprintsStartDates(adjustedSprintsDurationInDays, session.getStartDate());
        sortSprintByOrderIndex(sprintsToSave);

        return sprintRepository.saveAll(sprintsToSave);
    }

    private List<Sprint> updateSprintsStartDates(List<Sprint> sprints, LocalDateTime sessionStart) {
        sortSprintByOrderIndex(sprints);

        LocalDateTime currentStart = sessionStart;
        for (Sprint sprint : sprints) {
            sprint.setStartDate(currentStart);
            currentStart = currentStart.plusDays(sprint.getDurationInDays());
            currentStart = switch (sprint.getType()) {
                case MODULE_FINAL -> currentStart.plusDays(moduleEndStaleTimeInDays);
                case SECTION_FINAL -> currentStart.plusDays(sectionEndStaleTimeInDays);
                case TOPIC -> currentStart;
                case PROGRAM_FINAL -> currentStart;
            };
        }
        return sprints;
    }

    private void sortSprintByOrderIndex(List<Sprint> sprints) {
        sprints.sort(Comparator.comparingInt(Sprint::getOrderIndex));
    }

}


