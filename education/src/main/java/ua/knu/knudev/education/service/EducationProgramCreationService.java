package ua.knu.knudev.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.api.EducationTaskApi;
import ua.knu.knudev.assessmentmanagerapi.dto.EducationTaskDto;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.EducationTaskProxy;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.mapper.MultiLanguageFieldMapper;
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
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
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

//todo check the number of done transactions
//todo test with great amount of topics
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
    private final MultiLanguageFieldMapper multiLangFieldMapper;

    @Transactional
    public EducationProgramDto save(EducationProgramCreationRequest programCreationReq) {
        inputReqCoherenceValidator.validateProgramOrderSequence(programCreationReq);

        Map<LearningUnit, Map<Integer, MultipartFile>> tasksToUpload = buildEducationProgramAllTasksMap(programCreationReq);
        Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap = educationTaskApi.uploadAll(tasksToUpload);

        EducationProgram program = buildAndSaveProgram(programCreationReq, filenamesMap);

        programCreationReq.getSections().forEach(sectionRequest -> {
            ProgramSection section = buildAndSaveSection(sectionRequest, filenamesMap, program);

            sectionRequest.getModules().forEach(moduleRequest -> {
                ProgramModule module = buildAndSaveModule(moduleRequest, filenamesMap, section);

                moduleRequest.getTopics().forEach(topicRequest -> buildAndSaveTopic(topicRequest, filenamesMap, module));
            });
        });

        // TODO: Return a proper DTO
        return null;
    }

    private Map<LearningUnit, Map<Integer, MultipartFile>> buildEducationProgramAllTasksMap(
            EducationProgramCreationRequest request
    ) {
        Map<LearningUnit, Map<Integer, MultipartFile>> tasksMap = new HashMap<>();

        boolean programNotExists = ObjectUtils.isEmpty(request.getExistingProgramId());
        boolean programFinalTaskIsPresent = ObjectUtils.isNotEmpty(request.getFinalTask());
        if (programNotExists && programFinalTaskIsPresent) {
            tasksMap
                    .computeIfAbsent(LearningUnit.PROGRAM, k -> new HashMap<>())
                    .put(1, request.getFinalTask());
        }

        request.getSections().stream()
                .filter(s -> ObjectUtils.isEmpty(s.getExistingSectionId()))
                .filter(s -> ObjectUtils.isNotEmpty(s.getFinalTask()))
                .forEach(s ->
                        tasksMap
                                .computeIfAbsent(LearningUnit.SECTION, k -> new HashMap<>())
                                .put(s.getOrderIndex(), s.getFinalTask())
                );

        request.getSections().forEach(s ->
                s.getModules().stream()
                        .filter(m -> ObjectUtils.isEmpty(m.getExistingModuleId()))
                        .filter(m -> ObjectUtils.isNotEmpty(m.getFinalTask()))
                        .forEach(m ->
                                tasksMap
                                        .computeIfAbsent(LearningUnit.MODULE, k -> new HashMap<>())
                                        .put(m.getOrderIndex(), m.getFinalTask())
                        )
        );

        request.getSections().forEach(s ->
                s.getModules().forEach(m ->
                        m.getTopics().stream()
                                .filter(topic -> ObjectUtils.isEmpty(topic.getExistingTopicId()))
                                .filter(topic -> ObjectUtils.isNotEmpty(topic.getTask()))
                                .forEach(topic ->
                                        tasksMap
                                                .computeIfAbsent(LearningUnit.TOPIC, k -> new HashMap<>())
                                                .put(topic.getOrderIndex(), topic.getTask())
                                )
                )
        );

        return tasksMap;
    }

    ProgramSection getSectionById(UUID id) {
        return sectionRepository.findById(id).orElse(null);
    }

    private EducationTaskDto getFilenameForOrderIndex(
            LearningUnit learningUnit,
            int orderIndex,
            Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap
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

    private EducationProgram buildAndSaveProgram(EducationProgramCreationRequest programCreationReq,
                                                 Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap) {
        boolean programExists = ObjectUtils.isEmpty(programCreationReq.getExistingProgramId());
        EducationProgram program = programExists ? (
                EducationProgram.builder()
                        .name(multiLangFieldMapper.toDomain(programCreationReq.getName()))
                        .description(multiLangFieldMapper.toDomain(programCreationReq.getDescription()))
                        .expertise(programCreationReq.getExpertise())
                        .finalTask(
                                buildProxy(
                                        LearningUnit.PROGRAM,
                                        getFilenameForOrderIndex(
                                                LearningUnit.PROGRAM,
                                                1,
                                                filenamesMap
                                        )
                                )
                        )
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        ) : getProgramById(programCreationReq.getExistingProgramId());

        return educationProgramRepository.save(program);
    }

    private ProgramSection buildAndSaveSection(SectionCreationRequest sectionRequest,
                                               Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
                                               EducationProgram program) {
        ProgramSection section = ObjectUtils.isEmpty(sectionRequest.getExistingSectionId())
                ? ProgramSection.builder()
                .name(multiLangFieldMapper.toDomain(sectionRequest.getName()))
                .description(multiLangFieldMapper.toDomain(sectionRequest.getDescription()))
                .sectionFinalTask(
                        buildProxy(
                                LearningUnit.SECTION,
                                getFilenameForOrderIndex(
                                        LearningUnit.SECTION,
                                        sectionRequest.getOrderIndex(),
                                        filenamesMap
                                )
                        )
                )
                .lastModifiedDate(LocalDateTime.now())
                .build()
                : getSectionById(sectionRequest.getExistingSectionId());

        ProgramSection savedSection = sectionRepository.save(section);

        programSectionMappingRepository.save(
                ProgramSectionMapping.builder()
                        .educationProgram(program)
                        .section(section)
                        .orderIndex(sectionRequest.getOrderIndex())
                        .build()
        );
        return savedSection;
    }

    private ProgramModule buildAndSaveModule(ModuleCreationRequest moduleRequest,
                                             Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
                                             ProgramSection section) {
        ProgramModule module = ObjectUtils.isEmpty(moduleRequest.getExistingModuleId())
                ? ProgramModule.builder()
                .name(multiLangFieldMapper.toDomain(moduleRequest.getName()))
                .description(multiLangFieldMapper.toDomain(moduleRequest.getDescription()))
                .moduleFinalTask(
                        buildProxy(
                                LearningUnit.MODULE,
                                getFilenameForOrderIndex(
                                        LearningUnit.MODULE,
                                        moduleRequest.getOrderIndex(),
                                        filenamesMap
                                )
                        )
                )
                .lastModifiedDate(LocalDateTime.now())
                .build()
                : getModuleById(moduleRequest.getExistingModuleId());

        ProgramModule savedModule = moduleRepository.save(module);

        sectionModuleMappingRepository.save(
                SectionModuleMapping.builder()
                        .section(section)
                        .module(module)
                        .orderIndex(moduleRequest.getOrderIndex())
                        .build()
        );
        return savedModule;
    }

    private void buildAndSaveTopic(TopicCreationRequest topicRequest,
                                           Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
                                           ProgramModule module) {
        ProgramTopic topic = ObjectUtils.isEmpty(topicRequest.getExistingTopicId())
                ? ProgramTopic.builder()
                .name(multiLangFieldMapper.toDomain(topicRequest.getName()))
                .description(multiLangFieldMapper.toDomain(topicRequest.getDescription()))
                .task(
                        buildProxy(
                                LearningUnit.TOPIC,
                                getFilenameForOrderIndex(
                                        LearningUnit.TOPIC,
                                        topicRequest.getOrderIndex(),
                                        filenamesMap
                                )
                        )
                )
                .lastModifiedDate(LocalDateTime.now())
                .build()
                : getTopicById(topicRequest.getExistingTopicId());

        topicRepository.save(topic);

        moduleTopicMappingRepository.save(
                ModuleTopicMapping.builder()
                        .module(module)
                        .topic(topic)
                        .orderIndex(topicRequest.getOrderIndex())
                        .build()
        );

    }

    private EducationTaskProxy buildProxy(LearningUnit learningUnit, EducationTaskDto taskDto) {
        return EducationTaskProxy.builder()
                .id(taskDto.getId())
                .taskFilename(taskDto.getFilename())
                .learningUnit(learningUnit)
                .build();
    }

}
