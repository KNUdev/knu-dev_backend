package ua.knu.knudev.education.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.api.TestApi;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.QEducationProgram;
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
import ua.knu.knudev.educationapi.request.*;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

@Service
@RequiredArgsConstructor
//todo fix update on order indexes
public class EducationProgramCreationService implements EducationProgramApi {

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

    private final ProgramMapper programMapper;
    private final SectionMapper sectionMapper;
    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;

    private final QEducationProgram qProgram = QEducationProgram.educationProgram;
    private final QProgramSectionMapping qPSM = QProgramSectionMapping.programSectionMapping;
    private final QSectionModuleMapping qSMM = QSectionModuleMapping.sectionModuleMapping;
    private final QModuleTopicMapping qMTM = QModuleTopicMapping.moduleTopicMapping;

    private final TestDeletionS testDeletionS;

    @Transactional
    public EducationProgramDto getById(UUID programId) {
        // 1. Load the program entity.
        EducationProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found for ID: " + programId));

        // 2. Build the EducationProgramDto manually.
        EducationProgramDto programDto = new EducationProgramDto();
        programDto.setId(program.getId());
        programDto.setName(new MultiLanguageFieldDto(program.getName().getEn(), program.getName().getUk()));
        programDto.setDescription(new MultiLanguageFieldDto(program.getDescription().getEn(), program.getDescription().getUk()));
        programDto.setExpertise(program.getExpertise()); // or use proper conversion if enum
        programDto.setPublished(program.isPublished());

        programDto.setFinalTaskFilename(program.getFinalTaskFilename());
        programDto.setFinalTaskUrl(pdfServiceApi.getPathByFilename(
                program.getFinalTaskFilename(), PdfSubfolder.EDUCATION_PROGRAM_PROGRAM_TASKS
        ));
        // 3. Load all section mappings for the given program.
        List<ProgramSectionMapping> sectionMappings = programSectionMappingRepository.findByEducationProgramId(program.getId());
        sectionMappings.sort(Comparator.comparingInt(ProgramSectionMapping::getOrderIndex));

        // 4. For each section mapping, build a ProgramSectionDto.
        List<ProgramSectionDto> sectionDtos = new ArrayList<>();
        for (ProgramSectionMapping sectionMapping : sectionMappings) {
            ProgramSection section = sectionMapping.getSection();
            ProgramSectionDto sectionDto = new ProgramSectionDto();
            sectionDto.setId(section.getId());
            sectionDto.setName(new MultiLanguageFieldDto(section.getName().getEn(), section.getName().getUk()));
            sectionDto.setDescription(new MultiLanguageFieldDto(section.getDescription().getEn(), section.getDescription().getUk()));
//            sectionDto.setFinalTaskUrl(section.getSectionFinalTask() != null ? section.getSectionFinalTask().getFilename() : null);
            sectionDto.setFinalTaskFilename(section.getFinalTaskFilename());
            sectionDto.setFinalTaskUrl(pdfServiceApi.getPathByFilename(
                    section.getFinalTaskFilename(), PdfSubfolder.EDUCATION_PROGRAM_SECTION_TASKS
            ));

            // 5. Load modules for this section using SectionModuleMappingRepository.
            List<SectionModuleMapping> moduleMappings = sectionModuleMappingRepository.findBySectionId(section.getId());
            moduleMappings.sort(Comparator.comparingInt(SectionModuleMapping::getOrderIndex));
            List<ProgramModuleDto> moduleDtos = new ArrayList<>();
            for (SectionModuleMapping moduleMapping : moduleMappings) {
                ProgramModule module = moduleMapping.getModule();
                ProgramModuleDto moduleDto = new ProgramModuleDto();
                moduleDto.setId(module.getId());
                moduleDto.setName(new MultiLanguageFieldDto(module.getName().getEn(), module.getName().getUk()));
                moduleDto.setDescription(new MultiLanguageFieldDto(module.getDescription().getEn(), module.getDescription().getUk()));
                moduleDto.setFinalTaskFilename(module.getFinalTaskFilename());
                moduleDto.setFinalTaskUrl(pdfServiceApi.getPathByFilename(
                        module.getFinalTaskFilename(), PdfSubfolder.EDUCATION_PROGRAM_MODULE_TASKS
                ));

                // 6. Load topics for this module using ModuleTopicMappingRepository.
                List<ModuleTopicMapping> topicMappings = moduleTopicMappingRepository.findByModuleId(module.getId());
                topicMappings.sort(Comparator.comparingInt(ModuleTopicMapping::getOrderIndex));
                List<ModuleTopicDto> topicDtos = new ArrayList<>();
                for (ModuleTopicMapping topicMapping : topicMappings) {
                    ProgramTopic topic = topicMapping.getTopic();
                    ModuleTopicDto topicDto = new ModuleTopicDto();
                    topicDto.setId(topic.getId());
                    topicDto.setName(new MultiLanguageFieldDto(topic.getName().getEn(), topic.getName().getUk()));
                    topicDto.setDescription(new MultiLanguageFieldDto(topic.getDescription().getEn(), topic.getDescription().getUk()));
                    topicDto.setDifficulty(topic.getDifficulty());

                    topicDto.setTestId(topic.getTestId());
                    topicDto.setFinalTaskFilename(topic.getFinalTaskFilename());
                    topicDto.setFinalTaskUrl(pdfServiceApi.getPathByFilename(
                            topic.getFinalTaskFilename(), PdfSubfolder.EDUCATION_PROGRAM_TOPIC_TASKS
                    ));
//                    topicDto.setLearningResources(topic.getLearningResources());
                    topicDtos.add(topicDto);
                }
                moduleDto.setTopics(topicDtos);
                moduleDtos.add(moduleDto);
            }
            sectionDto.setModules(moduleDtos);
            sectionDtos.add(sectionDto);
        }
        programDto.setSections(sectionDtos);

        return programDto;
    }

    @Override
    public EducationProgramDto updateProgramMeta(UUID programId, EducationProgramCreationRequest programReq) {
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
    public ProgramSectionDto updateSectionMeta(UUID sectionId, SectionCreationRequest sectionReq) {
        //todo block update if this section is in ANY of active SPRINTS
        ProgramSection section = getSectionById(sectionId);

        updateGenericFields(section, sectionReq, PdfSubfolder.EDUCATION_PROGRAM_SECTION_TASKS);

        ProgramSection savedSection = sectionRepository.save(section);
        return sectionMapper.toDto(savedSection);
    }

    @Override
    public ProgramModuleDto updateModuleMeta(UUID moduleId, ModuleCreationRequest moduleReq) {
        //todo block
        ProgramModule module = getModuleById(moduleId);

        updateGenericFields(module, moduleReq, PdfSubfolder.EDUCATION_PROGRAM_MODULE_TASKS);

        ProgramModule savedSection = moduleRepository.save(module);
        return moduleMapper.toDto(savedSection);
    }

    @Override
    public ModuleTopicDto updateTopicMeta(UUID topicId, TopicCreationRequest topicReq) {
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

        ProgramTopic savedTopic = topicRepository.save(topic);
        return topicMapper.toDto(savedTopic);
    }

    public List<ProgramSummaryDto> getAll() {
        // Q-Types for QueryDSL:
        QEducationProgram ep = QEducationProgram.educationProgram;
        QProgramSectionMapping psm = QProgramSectionMapping.programSectionMapping;
        QSectionModuleMapping smm = QSectionModuleMapping.sectionModuleMapping;
        QModuleTopicMapping mtm = QModuleTopicMapping.moduleTopicMapping;

        // 1) Fetch the domain objects (no grouping).
        List<EducationProgram> programs = getQueryFactory()
                .selectFrom(ep)
                .fetch();

        // 2) In a separate query, do a group-by to get distinct counts.
        //    This returns a list of Tuples: (programId, totalSections, totalModules, totalTopics).
        List<Tuple> aggregator = getQueryFactory()
                .select(
                        ep.id,                             // program ID
                        psm.section.id.countDistinct(),    // distinct sections
                        smm.module.id.countDistinct(),     // distinct modules
                        mtm.topic.id.countDistinct()       // distinct topics
                )
                .from(ep)
                // left joins on bridging tables
                .leftJoin(psm).on(psm.educationProgram.id.eq(ep.id))
                .leftJoin(smm).on(smm.section.id.eq(psm.section.id))
                .leftJoin(mtm).on(mtm.module.id.eq(smm.module.id))
                .groupBy(ep.id) // group by program
                .fetch();

        Map<UUID, Tuple> countMap = aggregator.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(ep.id),
                        tuple -> tuple
                ));

        return programs.stream()
                .map(program -> {
                    Tuple t = countMap.get(program.getId());
                    long sectionsCount = (t == null) ? 0L : t.get(psm.section.id.countDistinct());
                    long modulesCount = (t == null) ? 0L : t.get(smm.module.id.countDistinct());
                    long topicsCount = (t == null) ? 0L : t.get(mtm.topic.id.countDistinct());

                    return new ProgramSummaryDto(
                            program.getId(),
                            new MultiLanguageFieldDto(
                                    program.getName().getEn(),
                                    program.getName().getUk()
                            ),
                            (int) sectionsCount,
                            (int) modulesCount,
                            (int) topicsCount,
                            program.getExpertise(),            // from domain
                            program.getCreatedDate(),           // from BaseLearningUnit
                            program.getLastModifiedDate(),      // from BaseLearningUnit
                            program.isPublished(),              // from domain
                            0  // totalActiveSessions is temp => set 0 for now
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProgramById(UUID programId) {
        getQueryFactory().delete(qPSM)
                .where(qPSM.educationProgram.id.eq(programId))
                .execute();

        getQueryFactory().delete(qProgram)
                .where(qProgram.id.eq(programId))
                .execute();
    }

    @Transactional
    public void removeProgramSectionMapping(UUID programId, UUID sectionId) {
        getQueryFactory().delete(qPSM)
                .where(
                        qPSM.educationProgram.id.eq(programId),
                        qPSM.section.id.eq(sectionId)
                )
                .execute();
    }

    @Transactional
    public void removeSectionModuleMapping(UUID programId, UUID sectionId, UUID moduleId) {
        getQueryFactory().delete(qSMM)
                .where(
                        qSMM.section.id.eq(sectionId),
                        qSMM.module.id.eq(moduleId),
                        qSMM.section.id.in(
                                JPAExpressions.select(qPSM.section.id)
                                        .from(qPSM)
                                        .where(qPSM.educationProgram.id.eq(programId))
                        )
                )
                .execute();
    }

    @Transactional
    public void removeModuleTopicMapping(UUID programId, UUID sectionId, UUID moduleId, UUID topicId) {
        JPAQueryFactory queryFactory = getQueryFactory();
        queryFactory.delete(qMTM)
                .where(
                        qMTM.module.id.eq(moduleId),
                        qMTM.topic.id.eq(topicId),
                        qMTM.module.id.in(
                                JPAExpressions.select(qSMM.module.id)
                                        .from(qSMM)
                                        .join(qPSM).on(qPSM.section.id.eq(qSMM.section.id))
                                        .where(
                                                qSMM.section.id.eq(sectionId),
                                                qPSM.educationProgram.id.eq(programId)
                                        )
                        )
                )
                .execute();
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
    public EducationProgramDto save(EducationProgramCreationRequest programSaveReq) {
        inputReqCoherenceValidator.validateProgramOrderSequence(programSaveReq);

        Map<LearningUnit, Map<Integer, MultipartFile>> tasksToUpload = buildEducationProgramAllTasksMap(programSaveReq);
        Map<LearningUnit, Map<Integer, String>> filenamesMap = uploadAllProgramTasks(tasksToUpload);

        EducationProgram program = buildAndSaveProgram(programSaveReq, filenamesMap);

        EducationProgramDto programDto = programMapper.toDto(program);
        programDto.setFinalTaskUrl(program.getFinalTaskFilename());

        boolean isOrderIndexesUpdateReq = isOrderIndex(programSaveReq);
        if(isOrderIndexesUpdateReq) {
            testDeletionS.updateOrderIndexes(programSaveReq);
        } else {
            Optional.ofNullable(programSaveReq.getSections())
                    .orElse(Collections.emptyList())
                    .forEach(sectionRequest -> {
                        ProgramSection section = buildAndSaveSection(sectionRequest, filenamesMap, program);
                        ProgramSectionDto sectionDto = sectionMapper.toDto(section);
                        programDto.getSections().add(sectionDto);
                        //todo put separate url and file for this and for rest LU`s.
                        sectionDto.setFinalTaskUrl(section.getFinalTaskFilename());

                        Optional.ofNullable(sectionRequest.getModules())
                                .orElse(Collections.emptyList())
                                .forEach(moduleRequest -> {
                                    ProgramModule module = buildAndSaveModule(moduleRequest, filenamesMap, section);
                                    ProgramModuleDto moduleDto = moduleMapper.toDto(module);
                                    moduleDto.setFinalTaskUrl(module.getFinalTaskFilename());
                                    sectionDto.getModules().add(moduleDto);

                                    Optional.ofNullable(moduleRequest.getTopics())
                                            .orElse(Collections.emptyList())
                                            .forEach(topicRequest -> {
                                                ProgramTopic topic = buildAndSaveTopic(topicRequest, filenamesMap, module);
                                                ModuleTopicDto topicDto = topicMapper.toDto(topic);
                                                topicDto.setFinalTaskUrl(topic.getFinalTaskFilename());
                                                topicDto.setLearningResources(topic.getLearningResources());

                                                moduleDto.getTopics().add(topicDto);
                                            });
                                });
                    });
        }

        return programDto;
    }

    private boolean isOrderIndex(EducationProgramCreationRequest programSaveReq) {
//        boolean b = !programSaveReq.getSections().stream().map(SectionCreationRequest::getOrderIndex).toList().isEmpty();
//
//        ObjectUtils.allNotNull(
//                programSaveReq.getExistingProgramId(),
//        );
        return false;
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
            EducationProgramCreationRequest programCreationReq,
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
            SectionCreationRequest sectionReq,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            EducationProgram program
    ) {
        boolean sectionExists = ObjectUtils.isNotEmpty(sectionReq.getExistingSectionId());
        ProgramSection section;

        if (sectionExists) {
            section = getSectionById(sectionReq.getExistingSectionId());
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
            ModuleCreationRequest moduleRequest,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            ProgramSection section
    ) {
        boolean moduleExists = ObjectUtils.isNotEmpty(moduleRequest.getExistingModuleId());
        ProgramModule module;

        if (moduleExists) {
            module = getModuleById(moduleRequest.getExistingModuleId());
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
                            .section(section)
                            .module(module)
                            .orderIndex(moduleRequest.getOrderIndex())
                            .build()
            );
        }

        return module;
    }

    private ProgramTopic buildAndSaveTopic(
            TopicCreationRequest topicRequest,
            Map<LearningUnit, Map<Integer, String>> filenamesMap,
            ProgramModule module
    ) {
        boolean topicExists = ObjectUtils.isNotEmpty(topicRequest.getExistingTopicId());
        ProgramTopic topic;

        if (topicExists) {
            topic = getTopicById(topicRequest.getExistingTopicId());
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
                    .learningResources(topicRequest.getLearningMaterials())
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
                                            //todo maybe custom filename
                                            return pdfServiceApi.uploadFile(
                                                    fileToUpload,
                                                    PdfSubfolder.getFromLearningUnit(entry.getKey())
                                            );
                                        }

                                ))
                ));
    }

    private EducationProgram getProgramById(UUID id) {
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
