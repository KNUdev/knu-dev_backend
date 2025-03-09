package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.dto.session.ModuleSprintDto;
import ua.knu.knudev.educationapi.dto.session.SectionSprintDto;
import ua.knu.knudev.educationapi.dto.session.SessionSprintPlanDto;
import ua.knu.knudev.educationapi.dto.session.SprintDto;
import ua.knu.knudev.educationapi.enums.SprintStatus;
import ua.knu.knudev.educationapi.enums.SprintType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl {

    private final ProgramService programService;
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;

    /**
     * Generates a session sprint plan for a given program.
     * The plan is grouped by Section and Module, and each sprint is assigned a default duration.
     * This DTO is used for time adjustment on the UI.
     */
//    @Override
    public SessionSprintPlanDto generateSessionSprintPlan(UUID programId) {
        // Load the full program.
        EducationProgram program = programService.getProgramById(programId);

        // Load all section mappings for the program in one DB call.
        List<ProgramSectionMapping> sectionMappings = programSectionMappingRepository.findByEducationProgramId(program.getId());
        sectionMappings.sort(Comparator.comparingInt(ProgramSectionMapping::getOrderIndex));

        // Collect section IDs.
        Set<UUID> sectionIds = sectionMappings.stream()
                .map(mapping -> mapping.getSection().getId())
                .collect(Collectors.toSet());

        // Load all module mappings for these sections.
        List<SectionModuleMapping> moduleMappings = sectionModuleMappingRepository.findBySectionIdIn(sectionIds);

        // Collect module IDs.
        Set<UUID> moduleIds = moduleMappings.stream()
                .map(mapping -> mapping.getModule().getId())
                .collect(Collectors.toSet());

        // Load all topic mappings for these modules.
        List<ModuleTopicMapping> topicMappings = moduleTopicMappingRepository.findByModuleIdIn(moduleIds);

        // Use an atomic counter to assign a global order index.
        AtomicInteger orderCounter = new AtomicInteger(1);

        // Build SectionSprintDto list.
        List<SectionSprintDto> sectionDtos = sectionMappings.stream().map(sectionMapping -> {
            SectionSprintDto sectionDto = new SectionSprintDto();
            var section = sectionMapping.getSection();
            sectionDto.setSectionId(section.getId());
            // Assuming the Section entity has a MultiLanguageField "name" with getEn()
            sectionDto.setSectionName(section.getName().getEn());

            // Find all modules for this section, sorted by their orderIndex.
            List<SectionModuleMapping> modulesForSection = moduleMappings.stream()
                    .filter(m -> m.getSection().getId().equals(section.getId()))
                    .sorted(Comparator.comparingInt(SectionModuleMapping::getOrderIndex))
                    .collect(Collectors.toList());

            // Build ModuleSprintDto for each module.
            List<ModuleSprintDto> moduleDtos = modulesForSection.stream().map(moduleMapping -> {
                ModuleSprintDto moduleDto = new ModuleSprintDto();
                var module = moduleMapping.getModule();
                moduleDto.setModuleId(module.getId());
                moduleDto.setModuleName(module.getName().getEn());

                // For this module, get all topic mappings sorted by orderIndex.
                List<ModuleTopicMapping> topicsForModule = topicMappings.stream()
                        .filter(t -> t.getModule().getId().equals(module.getId()))
                        .sorted(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex))
                        .collect(Collectors.toList());

                // Create TOPIC sprints for each topic.
                List<SprintDto> topicSprints = topicsForModule.stream().map(topicMapping -> {
                    SprintDto sprintDto = new SprintDto();
                    sprintDto.setSprintId(UUID.randomUUID());
                    sprintDto.setOrderIndex(orderCounter.getAndIncrement());
                    sprintDto.setSprintType(SprintType.TOPIC);
                    sprintDto.setDurationDays(7); // default duration for a topic sprint
                    sprintDto.setTitle(topicMapping.getTopic().getName().getEn());
                    sprintDto.setDescription(topicMapping.getTopic().getDescription().getEn());
                    sprintDto.setSprintStatus(SprintStatus.FUTURE);
                    return sprintDto;
                }).collect(Collectors.toList());
                moduleDto.setTopicSprints(topicSprints);

                // Create a MODULE_FINAL sprint.
                SprintDto moduleFinal = new SprintDto();
                moduleFinal.setSprintId(UUID.randomUUID());
                moduleFinal.setOrderIndex(orderCounter.getAndIncrement());
                moduleFinal.setSprintType(SprintType.MODULE_FINAL);
                moduleFinal.setDurationDays(2); // default duration for module final
                moduleFinal.setTitle("Module Final: " + module.getName().getEn());
                moduleFinal.setDescription("Final sprint for module " + module.getName().getEn());
                moduleFinal.setSprintStatus(SprintStatus.FUTURE);
                moduleDto.setModuleFinalSprint(moduleFinal);

                return moduleDto;
            }).collect(Collectors.toList());
            sectionDto.setModules(moduleDtos);

            // Create a SECTION_FINAL sprint.
            SprintDto sectionFinal = new SprintDto();
            sectionFinal.setSprintId(UUID.randomUUID());
            sectionFinal.setOrderIndex(orderCounter.getAndIncrement());
            sectionFinal.setSprintType(SprintType.SECTION_FINAL);
            sectionFinal.setDurationDays(3); // default duration for section final
            sectionFinal.setTitle("Section Final: " + section.getName().getEn());
            sectionFinal.setDescription("Final sprint for section " + section.getName().getEn());
            sectionFinal.setSprintStatus(SprintStatus.FUTURE);
            sectionDto.setSectionFinalSprint(sectionFinal);

            return sectionDto;
        }).collect(Collectors.toList());

        // Finally, create a PROGRAM_FINAL sprint.
        SprintDto programFinal = new SprintDto();
        programFinal.setSprintId(UUID.randomUUID());
        programFinal.setOrderIndex(orderCounter.getAndIncrement());
        programFinal.setSprintType(SprintType.PROGRAM_FINAL);
        programFinal.setDurationDays(5); // default duration for program final
        programFinal.setTitle("Program Final");
        programFinal.setDescription("Final sprint for the entire program");
        programFinal.setSprintStatus(SprintStatus.FUTURE);

        // Build the overall SessionSprintPlanDto.
        SessionSprintPlanDto planDto = new SessionSprintPlanDto();
        planDto.setSections(sectionDtos);
        planDto.setProgramFinalSprint(programFinal);

        return planDto;
    }
}


