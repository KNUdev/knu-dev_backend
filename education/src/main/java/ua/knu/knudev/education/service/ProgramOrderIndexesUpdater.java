package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProgramOrderIndexesUpdater {

    private final TransactionTemplate transactionTemplate;
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;

    public void updateOrderIndexes(ProgramSaveRequest programSaveReq) {
        if(isSectionsIndexUpdate(programSaveReq)) {
            updateSectionsOrderIndexes(programSaveReq);
        }
        if(isModulesIndexUpdate(programSaveReq)) {
            updateModulesOrderIndexes(programSaveReq);
        }
        if(isTopicsIndexUpdate(programSaveReq)) {
            updateTopicOrderIndexes(programSaveReq);

        }
    }

    private void updateSectionsOrderIndexes(ProgramSaveRequest programSaveReq) {
        List<ProgramSectionMapping> programSectionMappings = programSectionMappingRepository.findByEducationProgramId(
                programSaveReq.getExistingProgramId()
        );

        List<UUID> PsMappingIds = programSectionMappings.stream().map(ProgramSectionMapping::getId).toList();
        transactionTemplate.executeWithoutResult(status -> {
            programSectionMappingRepository.deleteAllById(PsMappingIds);
            status.flush();
        });

        List<ProgramSectionMapping> PSMsToSave = programSectionMappings.stream()
                .peek(PSMapping -> {
                    SectionSaveRequest section = programSaveReq.getSections().stream()
                            .filter(s -> s.getExistingSectionId().equals(PSMapping.getSection().getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Section does not exist"));
                    PSMapping.setOrderIndex(section.getOrderIndex());
                })
                .peek(PSMapping -> PSMapping.setId(null))
                .toList();
        programSectionMappingRepository.saveAll(new ArrayList<>(PSMsToSave));
    }

    private void updateModulesOrderIndexes(ProgramSaveRequest programSaveReq) {
        programSaveReq.getSections()
                .forEach(section -> {
                    if (CollectionUtils.isNotEmpty(section.getModules())) {
                        section.getModules().forEach(module -> {
                            List<SectionModuleMapping> SMM = sectionModuleMappingRepository
                                    .findByEducationProgram_IdAndSection_IdAndModule_Id(
                                            programSaveReq.getExistingProgramId(),
                                            section.getExistingSectionId(),
                                            module.getExistingModuleId()
                                    );

                            List<UUID> SmMappingIds = SMM.stream().map(SectionModuleMapping::getId).toList();
                            transactionTemplate.executeWithoutResult(status -> {
                                sectionModuleMappingRepository.deleteAllById(SmMappingIds);
                                status.flush();
                            });

                            List<SectionModuleMapping> SMMsToSave = SMM.stream()
                                    .peek(secModMapping -> {
                                        ModuleSaveRequest existingModule = programSaveReq.getSections().stream()
                                                .flatMap(s -> {
                                                    List<ModuleSaveRequest> modules = s.getModules();
                                                    if (CollectionUtils.isEmpty(modules)) {
                                                        return Stream.empty();
                                                    }
                                                    return modules.stream();
                                                })
                                                .filter(moduleSaveReq -> moduleSaveReq.getExistingModuleId()
                                                        .equals(secModMapping.getModule().getId()))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("Module does not exist"));
                                        secModMapping.setOrderIndex(existingModule.getOrderIndex());
                                    })
                                    .peek(secModMapping -> secModMapping.setId(null))
                                    .toList();

                            sectionModuleMappingRepository.saveAll(new ArrayList<>(SMMsToSave));
                        });
                    }
                });
    }

    private void updateTopicOrderIndexes(ProgramSaveRequest programSaveReq) {
        programSaveReq.getSections().forEach(section -> {
            if (CollectionUtils.isNotEmpty(section.getModules())) {
                section.getModules().forEach(module -> {
                    if (CollectionUtils.isNotEmpty(module.getTopics())) {
                        module.getTopics().forEach(topic -> {
                            List<ModuleTopicMapping> MTM = moduleTopicMappingRepository
                                    .findByProgram_IdAndSection_IdAndModule_IdAndTopic_Id(
                                            programSaveReq.getExistingProgramId(),
                                            section.getExistingSectionId(),
                                            module.getExistingModuleId(),
                                            topic.getExistingTopicId()
                                    );
                            List<UUID> MTMIds = MTM.stream().map(ModuleTopicMapping::getId).toList();
                            transactionTemplate.executeWithoutResult(status -> {
                                moduleTopicMappingRepository.deleteAllById(MTMIds);
                                status.flush();
                            });

                            List<ModuleTopicMapping> MTMsToSave = MTM.stream()
                                    .peek(moduleTopicMapping -> {
                                        TopicSaveRequest existingTopic = programSaveReq.getSections().stream()
                                                .flatMap(s -> CollectionUtils.isEmpty(s.getModules())
                                                        ? Stream.empty()
                                                        : s.getModules().stream())
                                                .flatMap(m -> CollectionUtils.isEmpty(m.getTopics())
                                                        ? Stream.empty()
                                                        : m.getTopics().stream())
                                                .filter(topicSaveReq -> topicSaveReq.getExistingTopicId()
                                                        .equals(moduleTopicMapping.getModule().getId()))
                                                .findFirst()
                                                .orElseThrow(() -> new IllegalStateException("Module does not exist"));
                                        moduleTopicMapping.setOrderIndex(existingTopic.getOrderIndex());

                                    })
                                    .peek(moduleTopicMapping -> moduleTopicMapping.setId(null))
                                    .toList();

                            moduleTopicMappingRepository.saveAll(new ArrayList<>(MTMsToSave));
                        });
                    }
                });
            }
        });
    }

    public boolean isOrderIndexRequest(ProgramSaveRequest req) {
        if (CollectionUtils.isNotEmpty(req.getSections())) {
            return isSectionsIndexUpdate(req) || isModulesIndexUpdate(req) || isTopicsIndexUpdate(req);
        }
        return false;
    }

    private boolean isSectionsIndexUpdate(ProgramSaveRequest req) {
        boolean sectionsIndexChange = req.getSections().stream()
                .allMatch(section -> isIndexValid(section.getExistingSectionId(), section.getOrderIndex()));
        boolean sectionsIdsAreValid = req.getSections().stream()
                .anyMatch(section -> section.getExistingSectionId() != null);
        return sectionsIndexChange && sectionsIdsAreValid;
    }

    private boolean isModulesIndexUpdate(ProgramSaveRequest req) {
        boolean modulesIdsAreValid = req.getSections().stream()
                .flatMap(section -> section.getModules() == null ? Stream.empty() : section.getModules().stream())
                .anyMatch(module -> module.getExistingModuleId() != null);
        boolean modulesIndexChange = req.getSections().stream()
                .flatMap(section -> section.getModules() == null ? Stream.empty() : section.getModules().stream())
                .allMatch(module -> isIndexValid(module.getExistingModuleId(), module.getOrderIndex()));
        return modulesIndexChange && modulesIdsAreValid;
    }

    private boolean isTopicsIndexUpdate(ProgramSaveRequest req) {
        boolean topicsIndexChange = req.getSections().stream()
                .flatMap(section -> section.getModules() == null ? Stream.empty() : section.getModules().stream())
                .flatMap(module -> module.getTopics() == null ? Stream.empty() : module.getTopics().stream())
                .allMatch(topic -> isIndexValid(topic.getExistingTopicId(), topic.getOrderIndex()));
        boolean topicsIdsAreValid = req.getSections().stream()
                .flatMap(section -> section.getModules() == null ? Stream.empty() : section.getModules().stream())
                .flatMap(module -> module.getTopics() == null ? Stream.empty() : module.getTopics().stream())
                .anyMatch(topic -> topic.getExistingTopicId() != null);
        return topicsIndexChange && topicsIdsAreValid;
    }

    private boolean isIndexValid(Object existingId, Object orderIndex) {
        return (existingId == null && orderIndex == null) || (existingId != null && orderIndex != null);
    }
}

