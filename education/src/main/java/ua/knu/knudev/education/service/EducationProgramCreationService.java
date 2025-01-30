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
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.mapper.*;
import ua.knu.knudev.education.repository.EducationProgramRepository;
import ua.knu.knudev.education.repository.ModuleRepository;
import ua.knu.knudev.education.repository.SectionRepository;
import ua.knu.knudev.education.repository.TopicRepository;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.dto.ProgramModuleDto;
import ua.knu.knudev.educationapi.dto.ProgramSectionDto;
import ua.knu.knudev.educationapi.dto.ProgramTopicDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
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
    private final EducationMultiLanguageFieldMapper multiLangFieldMapper;

    private final ProgramMapper programMapper;
    private final SectionMapper sectionMapper;
    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;

    @Transactional
    public EducationProgramDto save(EducationProgramCreationRequest programCreationReq) {
        inputReqCoherenceValidator.validateProgramOrderSequence(programCreationReq);

        Map<LearningUnit, Map<Integer, MultipartFile>> tasksToUpload =
                buildEducationProgramAllTasksMap(programCreationReq);
        Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap =
                educationTaskApi.uploadAll(tasksToUpload);

        EducationProgram program = buildAndSaveProgram(programCreationReq, filenamesMap);

        EducationProgramDto programDto = programMapper.toDto(program);
        programDto.setFinalTaskUrl(program.getFinalTask().getFilename());

        Optional.ofNullable(programCreationReq.getSections())
                .orElse(Collections.emptyList())
                .forEach(sectionRequest -> {
                    ProgramSection section = buildAndSaveSection(sectionRequest, filenamesMap, program);
                    ProgramSectionDto sectionDto = sectionMapper.toDto(section);
                    programDto.getSections().add(sectionDto);
                    sectionDto.setFinalTaskUrl(section.getSectionFinalTask().getFilename());

                    Optional.ofNullable(sectionRequest.getModules())
                            .orElse(Collections.emptyList())
                            .forEach(moduleRequest -> {
                                ProgramModule module = buildAndSaveModule(moduleRequest, filenamesMap, section);
                                ProgramModuleDto moduleDto = moduleMapper.toDto(module);
                                moduleDto.setFinalTaskUrl(module.getModuleFinalTask().getFilename());
                                sectionDto.getModules().add(moduleDto);

                                Optional.ofNullable(moduleRequest.getTopics())
                                        .orElse(Collections.emptyList())
                                        .forEach(topicRequest -> {
                                            ProgramTopic topic = buildAndSaveTopic(topicRequest, filenamesMap, module);
                                            ProgramTopicDto topicDto = topicMapper.toDto(topic);
                                            topicDto.setTaskUrl(topic.getTask().getFilename());
                                            topicDto.setLearningResources(topic.getLearningResources());

                                            moduleDto.getTopics().add(topicDto);
                                        });
                            });
                });

        return programDto;
    }

    private Map<LearningUnit, Map<Integer, MultipartFile>> buildEducationProgramAllTasksMap(
            EducationProgramCreationRequest request
    ) {
        Map<LearningUnit, Map<Integer, MultipartFile>> tasksMap = new HashMap<>();

        if (ObjectUtils.isEmpty(request.getExistingProgramId())
                && ObjectUtils.isNotEmpty(request.getFinalTask())) {
            tasksMap.computeIfAbsent(LearningUnit.PROGRAM, k -> new HashMap<>())
                    .put(1, request.getFinalTask());
        }

        Optional.ofNullable(request.getSections()).orElse(Collections.emptyList())
                .stream()
                .peek(section -> {
                    if (ObjectUtils.isEmpty(section.getExistingSectionId())
                            && ObjectUtils.isNotEmpty(section.getFinalTask())) {
                        tasksMap.computeIfAbsent(LearningUnit.SECTION, k -> new HashMap<>())
                                .put(section.getOrderIndex(), section.getFinalTask());
                    }
                })
                .flatMap(section -> Optional.ofNullable(section.getModules())
                        .orElse(Collections.emptyList())
                        .stream()
                        .peek(module -> {
                            if (ObjectUtils.isEmpty(module.getExistingModuleId())
                                    && ObjectUtils.isNotEmpty(module.getFinalTask())) {
                                tasksMap.computeIfAbsent(LearningUnit.MODULE, k -> new HashMap<>())
                                        .put(module.getOrderIndex(), module.getFinalTask());
                            }
                        })
                        .flatMap(module -> Optional.ofNullable(module.getTopics())
                                .orElse(Collections.emptyList())
                                .stream()
                                .filter(topic -> ObjectUtils.isEmpty(topic.getExistingTopicId()))
                                .filter(topic -> ObjectUtils.isNotEmpty(topic.getTask()))
                                .peek(topic -> tasksMap
                                        .computeIfAbsent(LearningUnit.TOPIC, k -> new HashMap<>())
                                        .put(topic.getOrderIndex(), topic.getTask()))
                        )
                )
                .forEach(x -> { });

        return tasksMap;
    }

    private EducationProgram buildAndSaveProgram(
            EducationProgramCreationRequest programCreationReq,
            Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap
    ) {
        boolean programExists = ObjectUtils.isNotEmpty(programCreationReq.getExistingProgramId());
        EducationProgram program;
        if (programExists) {
            program = getProgramById(programCreationReq.getExistingProgramId());
        } else {
            EducationTaskProxy finalTaskProxy = null;
            if (ObjectUtils.isNotEmpty(programCreationReq.getFinalTask())) {
                EducationTaskDto taskDto = getFilenameForOrderIndex(
                        LearningUnit.PROGRAM,
                        1,
                        filenamesMap
                );
                finalTaskProxy = buildTaskProxy(LearningUnit.PROGRAM, taskDto);
            }

            program = EducationProgram.builder()
                    .name(multiLangFieldMapper.toDomain(programCreationReq.getName()))
                    .description(multiLangFieldMapper.toDomain(programCreationReq.getDescription()))
                    .expertise(programCreationReq.getExpertise())
                    .finalTask(finalTaskProxy)
                    .version(1)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
        }
        return educationProgramRepository.save(program);
    }

    private ProgramSection buildAndSaveSection(
            SectionCreationRequest sectionRequest,
            Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
            EducationProgram program
    ) {
        boolean sectionExists = ObjectUtils.isNotEmpty(sectionRequest.getExistingSectionId());
        ProgramSection section;

        if (sectionExists) {
            section = getSectionById(sectionRequest.getExistingSectionId());
        } else {
            EducationTaskProxy sectionFinalTask = null;
            if (ObjectUtils.isNotEmpty(sectionRequest.getFinalTask())) {
                EducationTaskDto dto = getFilenameForOrderIndex(
                        LearningUnit.SECTION,
                        sectionRequest.getOrderIndex(),
                        filenamesMap
                );
                sectionFinalTask = buildTaskProxy(LearningUnit.SECTION, dto);
            }

            section = ProgramSection.builder()
                    .name(multiLangFieldMapper.toDomain(sectionRequest.getName()))
                    .description(multiLangFieldMapper.toDomain(sectionRequest.getDescription()))
                    .sectionFinalTask(sectionFinalTask)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
        }

        ProgramSection savedSection = sectionRepository.save(section);

        programSectionMappingRepository.save(
                ProgramSectionMapping.builder()
                        .educationProgram(program)
                        .section(savedSection)
                        .orderIndex(sectionRequest.getOrderIndex())
                        .build()
        );
        return savedSection;
    }

    private ProgramModule buildAndSaveModule(
            ModuleCreationRequest moduleRequest,
            Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
            ProgramSection section
    ) {
        boolean moduleExists = ObjectUtils.isNotEmpty(moduleRequest.getExistingModuleId());
        ProgramModule module;

        if (moduleExists) {
            module = getModuleById(moduleRequest.getExistingModuleId());
        } else {
            EducationTaskProxy moduleFinalTask = null;
            if (ObjectUtils.isNotEmpty(moduleRequest.getFinalTask())) {
                EducationTaskDto dto = getFilenameForOrderIndex(
                        LearningUnit.MODULE,
                        moduleRequest.getOrderIndex(),
                        filenamesMap
                );
                moduleFinalTask = buildTaskProxy(LearningUnit.MODULE, dto);
            }

            module = ProgramModule.builder()
                    .name(multiLangFieldMapper.toDomain(moduleRequest.getName()))
                    .description(multiLangFieldMapper.toDomain(moduleRequest.getDescription()))
                    .moduleFinalTask(moduleFinalTask)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
        }

        ProgramModule savedModule = moduleRepository.save(module);

        sectionModuleMappingRepository.save(
                SectionModuleMapping.builder()
                        .section(section)
                        .module(savedModule)
                        .orderIndex(moduleRequest.getOrderIndex())
                        .build()
        );
        return savedModule;
    }

    private ProgramTopic buildAndSaveTopic(
            TopicCreationRequest topicRequest,
            Map<LearningUnit, Map<Integer, EducationTaskDto>> filenamesMap,
            ProgramModule module
    ) {
        boolean topicExists = ObjectUtils.isNotEmpty(topicRequest.getExistingTopicId());
        ProgramTopic topic;

        if (topicExists) {
            topic = getTopicById(topicRequest.getExistingTopicId());
        } else {
            EducationTaskProxy topicTaskProxy = null;
            if (ObjectUtils.isNotEmpty(topicRequest.getTask())) {
                EducationTaskDto dto = getFilenameForOrderIndex(
                        LearningUnit.TOPIC,
                        topicRequest.getOrderIndex(),
                        filenamesMap
                );
                topicTaskProxy = buildTaskProxy(LearningUnit.TOPIC, dto);
            }

            topic = ProgramTopic.builder()
                    .name(multiLangFieldMapper.toDomain(topicRequest.getName()))
                    .description(multiLangFieldMapper.toDomain(topicRequest.getDescription()))
                    .task(topicTaskProxy)
                    .learningResources(topicRequest.getLearningMaterials())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
        }

        ProgramTopic savedTopic = topicRepository.save(topic);

        moduleTopicMappingRepository.save(
                ModuleTopicMapping.builder()
                        .module(module)
                        .topic(savedTopic)
                        .orderIndex(topicRequest.getOrderIndex())
                        .build()
        );
        return savedTopic;
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

    private EducationTaskProxy buildTaskProxy(LearningUnit learningUnit, EducationTaskDto taskDto) {
        return EducationTaskProxy.builder()
                .id(taskDto.getId())
                .taskFilename(taskDto.getFilename())
                .learningUnit(learningUnit)
                .build();
    }

    private EducationProgram getProgramById(UUID id) {
        return educationProgramRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found for ID: " + id));
    }

    private ProgramSection getSectionById(UUID id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found for ID: " + id));
    }

    private ProgramModule getModuleById(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found for ID: " + id));
    }

    private ProgramTopic getTopicById(UUID id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found for ID: " + id));
    }

    @Override
    public String getTest() {
        return "";
    }
}

