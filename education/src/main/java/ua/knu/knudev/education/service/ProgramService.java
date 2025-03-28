package ua.knu.knudev.education.service;

import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.api.TestApi;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.*;
import ua.knu.knudev.education.domain.program.BaseLearningUnit;
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
import ua.knu.knudev.educationapi.dto.*;
import ua.knu.knudev.educationapi.exception.ProgramException;
import ua.knu.knudev.educationapi.request.*;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService implements EducationProgramApi {

    private final EducationProgramRepository programRepository;
    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;
    private final EducationProgramRequestCoherenceValidator inputReqCoherenceValidator;
    private final EducationMultiLanguageFieldMapper multiLangFieldMapper;
    private final PDFServiceApi pdfServiceApi;
    private final TestApi testApi;
    private final ProgramOrderIndexesUpdater programOrderIndexesUpdater;
    private final ProgramMapper programMapper;
    private final SectionMapper sectionMapper;
    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;
    private final ProgramMapperDtoHelper mapperDtoHelper;

    @Transactional
    public EducationProgramDto getById(UUID programId) {
        EducationProgram program = getProgramById(programId);
        EducationProgramDto programDto = mapperDtoHelper.toProgramDto(program);

        List<ProgramSectionMapping> sectionMappings = programSectionMappingRepository
                .findByEducationProgramId(program.getId());
        sectionMappings.sort(Comparator.comparingInt(ProgramSectionMapping::getOrderIndex));

        List<UUID> sectionIds = sectionMappings.stream()
                .map(mapping -> mapping.getSection().getId())
                .collect(Collectors.toList());
        List<SectionModuleMapping> moduleMappings = sectionModuleMappingRepository.findBySectionIdIn(sectionIds);
        Map<UUID, List<SectionModuleMapping>> modulesBySection = moduleMappings.stream()
                .sorted(Comparator.comparingInt(SectionModuleMapping::getOrderIndex))
                .collect(Collectors.groupingBy(mapping -> mapping.getSection().getId()));

        List<UUID> moduleIds = moduleMappings.stream()
                .map(mapping -> mapping.getModule().getId())
                .distinct()
                .collect(Collectors.toList());
        List<ModuleTopicMapping> topicMappings = moduleTopicMappingRepository.findByModuleIdIn(moduleIds);
        Map<UUID, List<ModuleTopicMapping>> topicsByModule = topicMappings.stream()
                .sorted(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex))
                .collect(Collectors.groupingBy(mapping -> mapping.getModule().getId()));

        List<ProgramSectionDto> sectionDtos = sectionMappings.stream()
                .map(sectionMapping -> {
                    ProgramSection section = sectionMapping.getSection();
                    ProgramSectionDto sectionDto = mapperDtoHelper.toSectionDto(section, sectionMapping);

                    List<SectionModuleMapping> modulesForSection = modulesBySection.getOrDefault(
                            section.getId(), Collections.emptyList()
                    );
                    List<ProgramModuleDto> moduleDtos = modulesForSection.stream()
                            .map(moduleMapping -> {
                                ProgramModule module = moduleMapping.getModule();
                                ProgramModuleDto moduleDto = mapperDtoHelper.toModuleDto(module, moduleMapping);

                                List<ModuleTopicMapping> topicsForModule = topicsByModule.getOrDefault(
                                        module.getId(), Collections.emptyList()
                                );
                                List<ProgramTopicDto> topicDtos = topicsForModule.stream()
                                        .map(topicMapping -> {
                                            ProgramTopic topic = topicMapping.getTopic();
                                            return mapperDtoHelper.toTopicDto(topic, topicMapping);
                                        })
                                        .collect(Collectors.toList());

                                moduleDto.setTopics(topicDtos);
                                return moduleDto;
                            })
                            .collect(Collectors.toList());

                    sectionDto.setModules(moduleDtos);
                    return sectionDto;
                })
                .collect(Collectors.toList());

        programDto.setSections(sectionDtos);
        return programDto;
    }

    @Override
    @Transactional
    public EducationProgramDto publish(UUID programId) {
        EducationProgram program = getProgramById(programId);
        if (program.isPublished()) {
            throw new ProgramException(
                    "Program with id: " + programId + " is already published",
                    HttpStatus.BAD_REQUEST
            );
        }
        program.setPublished(true);
        programRepository.save(program);
        return getById(programId);
    }

    @Override
    public EducationProgramDto updateProgramMeta(UUID programId, ProgramSaveRequest programReq) {
        //todo block
        EducationProgram program = getProgramById(programId);

        updateGenericFields(program, programReq, PdfSubfolder.EDUCATION_PROGRAM_PROGRAM_TASKS);
        if (ObjectUtils.isNotEmpty(program.getExpertise())) {
            program.setExpertise(programReq.getExpertise());
        }

        EducationProgram savedSection = programRepository.save(program);
        return programMapper.toDto(savedSection);
    }

    @Override
    public ProgramSectionDto updateSectionMeta(UUID sectionId, SectionSaveRequest sectionReq) {
        //todo block update if this section is in ANY of active SPRINTS
        ProgramSection section = getSectionById(sectionId);

        updateGenericFields(section, sectionReq, PdfSubfolder.EDUCATION_PROGRAM_SECTION_TASKS);

        ProgramSection savedSection = sectionRepository.save(section);
        return sectionMapper.toDto(savedSection);
    }

    @Override
    public ProgramModuleDto updateModuleMeta(UUID moduleId, ModuleSaveRequest moduleReq) {
        //todo block
        ProgramModule module = getModuleById(moduleId);

        updateGenericFields(module, moduleReq, PdfSubfolder.EDUCATION_PROGRAM_MODULE_TASKS);

        ProgramModule savedSection = moduleRepository.save(module);
        return moduleMapper.toDto(savedSection);
    }

    @Override
    public ProgramTopicDto updateTopicMeta(UUID topicId, TopicSaveRequest topicReq) {
        ProgramTopic topic = getTopicById(topicId);
        updateGenericFields(topic, topicReq, PdfSubfolder.EDUCATION_PROGRAM_TOPIC_TASKS);

        if (ObjectUtils.isNotEmpty(topicReq.getTestId())) {
            boolean testExists = testApi.existsById(topicReq.getTestId());
            if (testExists) {
                topic.setTestId(topicReq.getTestId());
            }
        }

        if (ObjectUtils.isNotEmpty(topicReq.getDifficulty())) {
            topic.setDifficulty(topicReq.getDifficulty());
        }

        if (CollectionUtils.isNotEmpty(topicReq.getLearningResources())) {
            Set<String> newLearningResources = topicReq.getLearningResources().stream()
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            topic.setLearningResources(newLearningResources);
        }

        ProgramTopic savedTopic = topicRepository.save(topic);
        return topicMapper.toDto(savedTopic);
    }

    public List<ProgramSummaryDto> getAll() {
        QProgramSectionMapping psm = QProgramSectionMapping.programSectionMapping;
        QSectionModuleMapping smm = QSectionModuleMapping.sectionModuleMapping;
        QModuleTopicMapping mtm = QModuleTopicMapping.moduleTopicMapping;

        List<EducationProgram> programs = programRepository.findAll();
        Map<UUID, Tuple> countMap = programRepository.fetchProgramSummariesIdCountMap();

        return programs.stream()
                .map(program -> {
                    Tuple t = countMap.get(program.getId());
                    long sectionsCount = (t == null) ? 0L : t.get(psm.section.id.countDistinct());
                    long modulesCount = (t == null) ? 0L : t.get(smm.module.id.countDistinct());
                    long topicsCount = (t == null) ? 0L : t.get(mtm.topic.id.countDistinct());

                    return ProgramSummaryDto.builder()
                            .id(program.getId())
                            .name(multiLangFieldMapper.toDto(program.getName()))
                            .createdAt(program.getCreatedDate())
                            .lastUpdatedAt(program.getLastModifiedDate())
                            .totalSections((int) sectionsCount)
                            .totalModules((int) modulesCount)
                            .totalTopics((int) topicsCount)
                            .expertise(program.getExpertise())
                            .isPublished(program.isPublished())
                            //todo 0 for now. In future change
                            .totalActiveSessions(0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProgramSectionDto> getSections() {
        List<ProgramSection> sections = sectionRepository.findAll();
        return sectionMapper.toDtos(sections);
    }

    @Override
    public List<ProgramModuleDto> getModules() {
        List<ProgramModule> modules = moduleRepository.findAll();
        return moduleMapper.toDtos(modules);
    }

    @Override
    public List<ProgramTopicDto> getTopics() {
        List<ProgramTopic> topics = topicRepository.findAll();
        return topicMapper.toDtos(topics);
    }

    @Transactional
    public void deleteProgramById(UUID programId) {
        //todo block deletion

        moduleTopicMappingRepository.removeAllByProgramId(programId);
        sectionModuleMappingRepository.removeAllByProgramId(programId);
        programSectionMappingRepository.removeAllByProgramId(programId);

        programSectionMappingRepository.flush();
        sectionModuleMappingRepository.flush();
        moduleTopicMappingRepository.flush();

        programRepository.deleteById(programId);
    }

    @Transactional
    public void removeProgramSectionMapping(UUID programId, UUID sectionId) {
        //todo block deletion

        programSectionMappingRepository.removeProgramSectionMapping(programId, sectionId);
        sectionModuleMappingRepository.removeSectionModuleMappingsBySectionId(programId, sectionId);
        moduleTopicMappingRepository.removeModuleTopicMappingsBySectionId(programId, sectionId);

        programSectionMappingRepository.flush();
        programSectionMappingRepository.adjustOrderIndexes(programId);
    }

    @Transactional
    public void removeSectionModuleMapping(UUID programId, UUID sectionId, UUID moduleId) {
        //todo block deletion

        sectionModuleMappingRepository.removeSectionModuleMapping(programId, sectionId, moduleId);
        moduleTopicMappingRepository.removeModuleTopicMappingsByModuleId(programId, sectionId, moduleId);

        sectionModuleMappingRepository.flush();
        sectionModuleMappingRepository.adjustOrderIndexes(programId, sectionId);
    }

    @Transactional
    public void removeModuleTopicMapping(UUID programId, UUID sectionId, UUID moduleId, UUID topicId) {
        //todo block deletion
        moduleTopicMappingRepository.removeModuleTopicMapping(programId, sectionId, moduleId, topicId);

        moduleTopicMappingRepository.flush();
        moduleTopicMappingRepository.adjustOrderIndexes(programId, sectionId, moduleId);
    }

    private <T extends BaseLearningUnit, V extends BaseLearningUnitSaveRequest> T updateGenericFields(
            T learningUnit,
            V saveReq,
            PdfSubfolder pdfSubfolder
    ) {
        learningUnit.setName(
                ObjectUtils.isNotEmpty(saveReq.getName())
                        ? multiLangFieldMapper.toDomain(saveReq.getName())
                        : learningUnit.getName()
        );

        learningUnit.setDescription(
                ObjectUtils.isNotEmpty(saveReq.getDescription())
                        ? multiLangFieldMapper.toDomain(saveReq.getDescription())
                        : learningUnit.getDescription()
        );

        if (ObjectUtils.isNotEmpty(saveReq.getFinalTask())) {
            String newTaskFilename = pdfServiceApi.updateByFilename(
                    learningUnit.getFinalTaskFilename(),
                    saveReq.getFinalTask(),
                    pdfSubfolder
            );
            learningUnit.setFinalTaskFilename(newTaskFilename);
        }
        return learningUnit;
    }

    @Transactional
    public EducationProgramDto save(ProgramSaveRequest programSaveReq) {
        inputReqCoherenceValidator.validateProgramOrderSequence(programSaveReq);

        Map<LearningUnit, Map<Integer, MultipartFile>> tasksToUpload = buildEducationProgramAllTasksMap(programSaveReq);
        Map<LearningUnit, Map<Integer, String>> filenamesMap = uploadAllProgramTasks(tasksToUpload);

        EducationProgram program = buildAndSaveProgram(programSaveReq, filenamesMap);

        EducationProgramDto programDto = programMapper.toDto(program);
        programDto.setFinalTaskUrl(program.getFinalTaskFilename());

        boolean isOrderIndexesUpdateReq = programOrderIndexesUpdater.isOrderIndexRequest(programSaveReq);
        if (isOrderIndexesUpdateReq) {
            programOrderIndexesUpdater.updateOrderIndexes(programSaveReq);
            return getById(programSaveReq.getExistingProgramId());
        } else {
            Optional.ofNullable(programSaveReq.getSections())
                    .orElse(Collections.emptyList())
                    .forEach(sectionRequest -> {
                        ProgramSection section = buildAndSaveSection(
                                programSaveReq, sectionRequest, filenamesMap, program
                        );
                        ProgramSectionDto sectionDto = mapperDtoHelper.toSectionDto(section);

                        programDto.getSections().add(sectionDto);

                        Optional.ofNullable(sectionRequest.getModules())
                                .orElse(Collections.emptyList())
                                .forEach(moduleRequest -> {
                                    ProgramModule module = buildAndSaveModule(
                                            sectionRequest, moduleRequest, filenamesMap, section, program
                                    );
                                    ProgramModuleDto moduleDto = mapperDtoHelper.toModuleDto(module);
                                    sectionDto.getModules().add(moduleDto);

                                    Optional.ofNullable(moduleRequest.getTopics())
                                            .orElse(Collections.emptyList())
                                            .forEach(topicRequest -> {
                                                ProgramTopic topic = buildAndSaveTopic(
                                                        moduleRequest, topicRequest, filenamesMap, program, section, module
                                                );
                                                ProgramTopicDto topicDto = mapperDtoHelper.toTopicDto(topic);

                                                moduleDto.getTopics().add(topicDto);
                                            });
                                });
                    });
        }

        return programDto;
    }

    private Map<LearningUnit, Map<Integer, MultipartFile>> buildEducationProgramAllTasksMap(
            ProgramSaveRequest request
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
                                .filter(topic -> ObjectUtils.isNotEmpty(topic.getFinalTask()))
                                .peek(topic -> tasksMap
                                        .computeIfAbsent(LearningUnit.TOPIC, k -> new HashMap<>())
                                        .put(topic.getOrderIndex(), topic.getFinalTask()))
                        )
                )
                .forEach(x -> {
                });

        return tasksMap;
    }

    private EducationProgram buildAndSaveProgram(
            ProgramSaveRequest programCreationReq,
            Map<LearningUnit, Map<Integer, String>> filenamesMap
    ) {
        boolean programExists = ObjectUtils.isNotEmpty(programCreationReq.getExistingProgramId());
        EducationProgram program;
        if (programExists) {
            program = getProgramById(programCreationReq.getExistingProgramId());
        } else {
            String taskFilename = getFilenameForOrderIndex(
                    LearningUnit.PROGRAM,
                    1,
                    filenamesMap
            );

            program = EducationProgram.builder()
                    .name(multiLangFieldMapper.toDomain(programCreationReq.getName()))
                    .description(multiLangFieldMapper.toDomain(programCreationReq.getDescription()))
                    .expertise(programCreationReq.getExpertise())
                    .finalTaskFilename(taskFilename)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
        }
        return programRepository.save(program);
    }

    private ProgramSection buildAndSaveSection(
            ProgramSaveRequest programSaveReq,
            SectionSaveRequest sectionReq,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            EducationProgram program
    ) {
        boolean sectionExists = ObjectUtils.isNotEmpty(sectionReq.getExistingSectionId());
        ProgramSection section;

        if (sectionExists) {
            section = getSectionById(sectionReq.getExistingSectionId());
            boolean mappingExists = programSectionMappingRepository.existsByEducationProgram_IdAndSection_Id(
                    program.getId(), section.getId()
            );
            if (!mappingExists) {
                int orderIndex = programSaveReq.getSections().indexOf(sectionReq) + 1;

                programSectionMappingRepository.save(
                        ProgramSectionMapping.builder()
                                .educationProgram(program)
                                .section(section)
                                .orderIndex(orderIndex)
                                .build()
                );
            }
        } else {
            String taskFilename = getFilenameForOrderIndex(
                    LearningUnit.SECTION,
                    sectionReq.getOrderIndex(),
                    filenamesMap
            );

            ProgramSection sectionToSave = ProgramSection.builder()
                    .name(multiLangFieldMapper.toDomain(sectionReq.getName()))
                    .description(multiLangFieldMapper.toDomain(sectionReq.getDescription()))
                    .finalTaskFilename(taskFilename)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
            section = sectionRepository.save(sectionToSave);

            programSectionMappingRepository.save(
                    ProgramSectionMapping.builder()
                            .educationProgram(program)
                            .section(section)
                            .orderIndex(sectionReq.getOrderIndex())
                            .build()
            );
        }

        return section;
    }

    private ProgramModule buildAndSaveModule(
            SectionSaveRequest sectionSaveReq,
            ModuleSaveRequest moduleRequest,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            ProgramSection section,
            EducationProgram program
    ) {
        boolean moduleExists = ObjectUtils.isNotEmpty(moduleRequest.getExistingModuleId());
        ProgramModule module;

        if (moduleExists) {
            module = getModuleById(moduleRequest.getExistingModuleId());
            boolean mappingExists = sectionModuleMappingRepository.existsByEducationProgram_IdAndSection_IdAndModule_Id(
                    program.getId(), section.getId(), module.getId()
            );
            if (!mappingExists) {
                int orderIndex = sectionSaveReq.getModules().indexOf(moduleRequest) + 1;

                sectionModuleMappingRepository.save(
                        SectionModuleMapping.builder()
                                .educationProgram(program)
                                .section(section)
                                .module(module)
                                .orderIndex(orderIndex)
                                .build()
                );
            }
        } else {
            String taskFilename = getFilenameForOrderIndex(
                    LearningUnit.MODULE,
                    moduleRequest.getOrderIndex(),
                    filenamesMap
            );

            ProgramModule moduleToSave = ProgramModule.builder()
                    .name(multiLangFieldMapper.toDomain(moduleRequest.getName()))
                    .description(multiLangFieldMapper.toDomain(moduleRequest.getDescription()))
                    .finalTaskFilename(taskFilename)
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
            module = moduleRepository.save(moduleToSave);

            sectionModuleMappingRepository.save(
                    SectionModuleMapping.builder()
                            .educationProgram(program)
                            .section(section)
                            .module(module)
                            .orderIndex(moduleRequest.getOrderIndex())
                            .build()
            );
        }

        return module;
    }

    private ProgramTopic buildAndSaveTopic(
            ModuleSaveRequest moduleSaveRequest,
            TopicSaveRequest topicRequest,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            EducationProgram program,
            ProgramSection section,
            ProgramModule module
    ) {
        boolean topicExists = ObjectUtils.isNotEmpty(topicRequest.getExistingTopicId());
        ProgramTopic topic;

        if (topicExists) {
            topic = getTopicById(topicRequest.getExistingTopicId());

            boolean mappingExists = moduleTopicMappingRepository.existsByProgram_IdAndSection_IdAndModule_IdAndTopic_Id(
                    program.getId(), section.getId(), module.getId(), topic.getId()
            );
            if (!mappingExists) {
                int orderIndex = moduleSaveRequest.getTopics().indexOf(topicRequest) + 1;

                moduleTopicMappingRepository.save(
                        ModuleTopicMapping.builder()
                                .program(program)
                                .section(section)
                                .module(module)
                                .topic(topic)
                                .orderIndex(orderIndex)
                                .build()
                );
            }
        } else {
            String taskFilename = getFilenameForOrderIndex(
                    LearningUnit.TOPIC,
                    topicRequest.getOrderIndex(),
                    filenamesMap
            );

            ProgramTopic topicToSave = ProgramTopic.builder()
                    .name(multiLangFieldMapper.toDomain(topicRequest.getName()))
                    .description(multiLangFieldMapper.toDomain(topicRequest.getDescription()))
                    .finalTaskFilename(taskFilename)
                    .learningResources(
                            CollectionUtils.isNotEmpty(topicRequest.getLearningResources()) ?
                                    new HashSet<>(topicRequest.getLearningResources())
                                    : Collections.emptySet()
                    )
                    .lastModifiedDate(LocalDateTime.now())
                    .difficulty(topicRequest.getDifficulty())
                    .build();
            if (ObjectUtils.isNotEmpty(topicRequest.getTestId())) {
                boolean testExists = testApi.existsById(topicRequest.getTestId());
                if (testExists) {
                    topicToSave.setTestId(topicRequest.getTestId());
                }
            }

            topic = topicRepository.save(topicToSave);

            moduleTopicMappingRepository.save(
                    ModuleTopicMapping.builder()
                            .program(program)
                            .section(section)
                            .module(module)
                            .topic(topic)
                            .orderIndex(topicRequest.getOrderIndex())
                            .build()
            );
        }

        return topic;
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

    private Map<LearningUnit, Map<Integer, String>> uploadAllProgramTasks(
            Map<LearningUnit, Map<Integer, MultipartFile>> educationProgramTasks
    ) {
        return educationProgramTasks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        innerEntry -> {
                                            MultipartFile fileToUpload = innerEntry.getValue();
                                            //todo custom filename
                                            return pdfServiceApi.uploadFile(
                                                    fileToUpload,
                                                    PdfSubfolder.getFromLearningUnit(entry.getKey())
                                            );
                                        }

                                ))
                ));
    }

    public EducationProgram getProgramById(UUID id) {
        return programRepository.findById(id)
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

}
