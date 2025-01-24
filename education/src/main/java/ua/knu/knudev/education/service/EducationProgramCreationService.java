package ua.knu.knudev.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.api.EducationTaskApi;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.education.domain.EducationTaskProxy;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.repository.EducationProgramRepository;
import ua.knu.knudev.education.repository.ModuleRepository;
import ua.knu.knudev.education.repository.SectionRepository;
import ua.knu.knudev.education.repository.TopicRepository;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
//todo refactor and greatly test
//todo create save and publish method. Save allows empty sections, publish does not allow empty program, section or module (with no sub-units)
public class EducationProgramCreationService implements EducationProgramApi {
    private final EducationProgramRepository educationProgramRepository;
    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;
    private final TopicRepository topicRepository;

    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;

    private final EducationProgramRequestCoherenceValidator inputReqCoherenceValidator;
    private final EducationTaskApi educationTaskApi;

    @Transactional
    public EducationProgramDto save(EducationProgramCreationRequest programCreationReq) {
        inputReqCoherenceValidator.validateProgramCreationRequest(programCreationReq);

        Map<LearningUnit, Map<Integer, MultipartFile>> tasksToUpload = buildEducationProgramAllTasksMap(programCreationReq);
        Map<LearningUnit, Map<Integer, String>> filenamesMap = educationTaskApi.uploadAll(tasksToUpload);

        boolean programExists = ObjectUtils.isEmpty(programCreationReq.existingProgramId());
        EducationProgram program = programExists ? (
                EducationProgram.builder()
                        .name(buildField(programCreationReq.name()))
                        .description(buildField(programCreationReq.description()))
                        .expertise(programCreationReq.expertise())
                        .finalTask(
                                EducationTaskProxy.builder()
                                        .taskFilename(
                                                getFilenameForOrderIndex(LearningUnit.PROGRAM, 1, filenamesMap)
                                        )
                                        .build()
                        )
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        ) : getProgramById(programCreationReq.existingProgramId());

        educationProgramRepository.save(program);

        programCreationReq.sections().forEach(sectionRequest -> {
            ProgramSection section = ObjectUtils.isEmpty(sectionRequest.existingSectionId())
                    ? ProgramSection.builder()
                    .name(buildField(sectionRequest.name()))
                    .description(buildField(sectionRequest.description()))
                    .sectionFinalTask(
                            EducationTaskProxy.builder()
                                    .taskFilename(
                                            // Now just do direct map lookup by (LEARNING_UNIT, orderIndex)
                                            getFilenameForOrderIndex(LearningUnit.SECTION,
                                                    sectionRequest.orderIndex(),
                                                    filenamesMap)
                                    )
                                    .build()
                    )
                    .lastModifiedDate(LocalDateTime.now())
                    .build()
                    : getSectionById(sectionRequest.existingSectionId());

            sectionRepository.save(section);

            programSectionMappingRepository.save(
                    ProgramSectionMapping.builder()
                            .educationProgram(program)
                            .section(section)
                            .orderIndex(sectionRequest.orderIndex())
                            .build()
            );

            sectionRequest.modules().forEach(moduleRequest -> {
                ProgramModule module = ObjectUtils.isEmpty(moduleRequest.existingModuleId())
                        ? ProgramModule.builder()
                        .name(buildField(moduleRequest.name()))
                        .description(buildField(moduleRequest.description()))
                        .moduleFinalTask(
                                EducationTaskProxy.builder()
                                        .taskFilename(
                                                getFilenameForOrderIndex(LearningUnit.MODULE,
                                                        moduleRequest.orderIndex(),
                                                        filenamesMap)
                                        )
                                        .build()
                        )
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
                        : getModuleById(moduleRequest.existingModuleId());

                moduleRepository.save(module);

                sectionModuleMappingRepository.save(
                        SectionModuleMapping.builder()
                                .section(section)
                                .module(module)
                                .orderIndex(moduleRequest.orderIndex())
                                .build()
                );

                moduleRequest.topics().forEach(topicRequest -> {
                    ProgramTopic topic = ObjectUtils.isEmpty(topicRequest.existingTopicId())
                            ? ProgramTopic.builder()
                            .name(buildField(topicRequest.name()))
                            .description(buildField(topicRequest.description()))
                            .task(
                                    EducationTaskProxy.builder()
                                            .taskFilename(
                                                    getFilenameForOrderIndex(LearningUnit.TOPIC,
                                                            topicRequest.orderIndex(),
                                                            filenamesMap)
                                            )
                                            .build()
                            )
                            .lastModifiedDate(LocalDateTime.now())
                            .build()
                            : getTopicById(topicRequest.existingTopicId());

                    topicRepository.save(topic);


                    moduleTopicMappingRepository.save(
                            ModuleTopicMapping.builder()
                                    .module(module)
                                    .topic(topic)
                                    .orderIndex(topicRequest.orderIndex())
                                    .build()
                    );
                });
            });
        });

        // TODO: Return a proper DTO
        return null;
    }


    private Map<LearningUnit, Map<Integer, MultipartFile>> buildEducationProgramAllTasksMap(
            EducationProgramCreationRequest request
    ) {
        Map<LearningUnit, Map<Integer, MultipartFile>> tasksMap = new HashMap<>();

        boolean programNotExists = ObjectUtils.isEmpty(request.existingProgramId());
        boolean programFinalTaskIsPresent = ObjectUtils.isNotEmpty(request.finalTask());
        if (programNotExists && programFinalTaskIsPresent) {
            tasksMap
                    .computeIfAbsent(LearningUnit.PROGRAM, k -> new HashMap<>())
                    .put(1, request.finalTask());
        }

        request.sections().stream()
                .filter(s -> ObjectUtils.isEmpty(s.existingSectionId()))
                .filter(s -> ObjectUtils.isNotEmpty(s.finalTask()))
                .forEach(s ->
                        tasksMap
                                .computeIfAbsent(LearningUnit.SECTION, k -> new HashMap<>())
                                .put(s.orderIndex(), s.finalTask())
                );

        request.sections().forEach(s ->
                s.modules().stream()
                        .filter(m -> ObjectUtils.isEmpty(m.existingModuleId()))
                        .filter(m -> ObjectUtils.isNotEmpty(m.finalTask()))
                        .forEach(m ->
                                tasksMap
                                        .computeIfAbsent(LearningUnit.MODULE, k -> new HashMap<>())
                                        .put(m.orderIndex(), m.finalTask())
                        )
        );

        request.sections().forEach(s ->
                s.modules().forEach(m ->
                        m.topics().stream()
                                .filter(t -> ObjectUtils.isEmpty(t.existingTopicId()))
                                .filter(t -> ObjectUtils.isNotEmpty(t.task()))
                                .forEach(t ->
                                        tasksMap
                                                .computeIfAbsent(LearningUnit.TOPIC, k -> new HashMap<>())
                                                .put(t.orderIndex(), t.task())
                                )
                )
        );

        return tasksMap;
    }

    //todo maybe mapper
    private MultiLanguageField buildField(MultiLanguageFieldDto multiLanguageFieldDto) {
        return MultiLanguageField.builder()
                .en(multiLanguageFieldDto.getEn())
                .uk(multiLanguageFieldDto.getUk())
                .build();
    }

    ProgramSection getSectionById(UUID id) {
        return sectionRepository.findById(id).orElse(null);
    }

    private String getFilenameForOrderIndex(
            LearningUnit learningUnit,
            int orderIndex,
            Map<LearningUnit, Map<Integer, String>> filenamesMap
    ) {
        return Optional.ofNullable(filenamesMap.get(learningUnit))
                .map(innerMap -> innerMap.get(orderIndex))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No file found for " + learningUnit + " with order index " + orderIndex
                ));
    }

    private ProgramModule getModuleById(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found for ID: " + id));
    }

    private ProgramTopic getTopicById(UUID id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found for ID: " + id));
    }

    private EducationProgram getProgramById(UUID id) {
        return educationProgramRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found for ID: " + id));
    }

}
