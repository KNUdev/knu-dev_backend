//package ua.knu.knudev.education.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ua.knu.knudev.education.domain.EducationProgram;
//import ua.knu.knudev.education.domain.Sprint;
//import ua.knu.knudev.educationapi.dto.SprintType;
//import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
//import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
//import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
//import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
//import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
//import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
//import ua.knu.knudev.educationapi.api.SessionApi;
//import ua.knu.knudev.educationapi.dto.SessionDto;
//import ua.knu.knudev.educationapi.session.SprintDto;
//import ua.knu.knudev.educationapi.request.SessionCreationRequest;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class SessionService implements SessionApi {
//    private final EducationProgramService educationProgramService;
//
//    private final ProgramSectionMappingRepository programSectionMappingRepository;
//    private final SectionModuleMappingRepository sectionModuleMappingRepository;
//    private final ModuleTopicMappingRepository moduleTopicMappingRepository;
//
//    @Override
//    public SessionDto create(SessionCreationRequest sessionCreationRequest) {
//        return null;
//    }
//
//    @Override
//    public List<SprintDto> generateSprintsForProgram(UUID programId) {
//        return List.of();
//    }
//
//    //    @Override
//    public List<Sprint> generateSprintsDomainForProgram(UUID programId) {
//        EducationProgram program = educationProgramService.getProgramById(programId);
//
//        // Load all section mappings for the given program in one DB call.
//        List<ProgramSectionMapping> sectionMappings = programSectionMappingRepository.findByEducationProgramId(
//                program.getId()
//        );
//        // Sort section mappings by orderIndex.
//        sectionMappings.sort(Comparator.comparingInt(ProgramSectionMapping::getOrderIndex));
//
//        // Collect section IDs.
//        Set<UUID> sectionIds = sectionMappings.stream()
//                .map(mapping -> mapping.getSection().getId())
//                .collect(Collectors.toSet());
//
//        // Load all module mappings for these sections.
//        List<SectionModuleMapping> moduleMappings = sectionModuleMappingRepository.findBySectionIdIn(sectionIds);
//        // (Sorting of each group will be done later via streams.)
//
//        // Collect module IDs.
//        Set<UUID> moduleIds = moduleMappings.stream()
//                .map(mapping -> mapping.getModule().getId())
//                .collect(Collectors.toSet());
//
//        // Load all topic mappings for these modules.
//        List<ModuleTopicMapping> topicMappings = moduleTopicMappingRepository.findByModuleIdIn(moduleIds);
//        // (Again, sort by orderIndex when processing each module.)
//
//        List<Sprint> sprints = new ArrayList<>();
//        AtomicInteger orderCounter = new AtomicInteger(1);
//
//        // Process each section (in order).
//        sectionMappings.forEach(sectionMapping -> {
//            // Get the section.
//            var section = sectionMapping.getSection();
//
//            // For this section, find all modules (from the module mapping) sorted by orderIndex.
//            List<SectionModuleMapping> modulesForSection = moduleMappings.stream()
//                    .filter(m -> m.getSection().getId().equals(section.getId()))
//                    .sorted(Comparator.comparingInt(SectionModuleMapping::getOrderIndex))
//                    .toList();
//
//            // Process each module.
//            modulesForSection.forEach(moduleMapping -> {
//                var module = moduleMapping.getModule();
//                // For this module, find all topic mappings sorted by orderIndex.
//                List<ModuleTopicMapping> topicsForModule = topicMappings.stream()
//                        .filter(t -> t.getModule().getId().equals(module.getId()))
//                        .sorted(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex))
//                        .toList();
//
//                // For each topic, create a TOPIC sprint.
//                topicsForModule.forEach(topicMapping -> {
//                    Sprint topicSprint = Sprint.builder()
//                            .sprintType(SprintType.TOPIC)
//                            .programTopic(topicMapping.getTopic())
//                            .orderIndex(orderCounter.getAndIncrement())
//                            .build();
//                    sprints.add(topicSprint);
//                });
//
//                // After processing topics in a module, add a MODULE_FINAL sprint.
//                Sprint moduleFinalSprint = Sprint.builder()
//                        .sprintType(SprintType.MODULE_FINAL)
//                        .programModule(module)
//                        .orderIndex(orderCounter.getAndIncrement())
//                        .build();
//                sprints.add(moduleFinalSprint);
//            });
//
//            // After processing all modules in the section, add a SECTION_FINAL sprint.
//            Sprint sectionFinalSprint = Sprint.builder()
//                    .sprintType(SprintType.SECTION_FINAL)
//                    .programSection(section)
//                    .orderIndex(orderCounter.getAndIncrement())
//                    .build();
//            sprints.add(sectionFinalSprint);
//        });
//
//        // Finally, add a PROGRAM_FINAL sprint.
//        Sprint programFinalSprint = Sprint.builder()
//                .sprintType(SprintType.PROGRAM_FINAL)
//                .orderIndex(orderCounter.getAndIncrement())
//                .build();
//        sprints.add(programFinalSprint);
//
//        return sprints;
//    }
//}



