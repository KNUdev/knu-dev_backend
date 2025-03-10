package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.education.repository.SprintRepository;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.dto.session.SprintDto;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SprintService {

    private static final int sprintDuration = 3;
    private final ProgramService programService;
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;
    private final SprintRepository sprintRepository;


    public List<Sprint> generateSessionSprintPlan(EducationProgram program) {
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
                            .durationDays(sprintDuration)
                            .sprintType(SprintType.TOPIC)
                            .sprintStatus(SprintStatus.FUTURE)
                            .orderIndex(orderCounter.getAndIncrement())
                            .program(program)
                            .programSection(section)
                            .programModule(module)
                            .programTopic(topic)
                            .build();

                    sprintsToSave.add(topicSprint);
                }

                Sprint moduleFinalSprint = Sprint.builder()
                        .durationDays(sprintDuration)
                        .sprintType(SprintType.MODULE_FINAL)
                        .sprintStatus(SprintStatus.FUTURE)
                        .orderIndex(orderCounter.getAndIncrement())
                        .program(program)
                        .programSection(section)
                        .programModule(module)
                        .build();

                sprintsToSave.add(moduleFinalSprint);
            }

            Sprint sectionFinalSprint = Sprint.builder()
                    .durationDays(sprintDuration)
                    .sprintType(SprintType.SECTION_FINAL)
                    .sprintStatus(SprintStatus.FUTURE)
                    .orderIndex(orderCounter.getAndIncrement())
                    .program(program)
                    .programSection(section)
                    .build();

            sprintsToSave.add(sectionFinalSprint);
        }

        Sprint programSprint = Sprint.builder()
                .durationDays(sprintDuration)
                .sprintType(SprintType.PROGRAM_FINAL)
                .sprintStatus(SprintStatus.FUTURE)
                .orderIndex(orderCounter.getAndIncrement())
                .program(program)
                .build();
        sprintsToSave.add(programSprint);

        return sprintsToSave;
    }


}


