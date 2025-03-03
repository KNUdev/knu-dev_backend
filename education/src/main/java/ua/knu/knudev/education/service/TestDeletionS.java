package ua.knu.knudev.education.service;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.repository.bridge.ModuleTopicMappingRepository;
import ua.knu.knudev.education.repository.bridge.ProgramSectionMappingRepository;
import ua.knu.knudev.education.repository.bridge.SectionModuleMappingRepository;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestDeletionS {

    private final TransactionTemplate transactionTemplate;
    private final ProgramSectionMappingRepository programSectionMappingRepository;
    private final SectionModuleMappingRepository sectionModuleMappingRepository;
    private final ModuleTopicMappingRepository moduleTopicMappingRepository;

    public void updateOrderIndexes(EducationProgramCreationRequest programSaveReq) {
        List<ProgramSectionMapping> programSectionMappings = programSectionMappingRepository.findByEducationProgramId(programSaveReq.getExistingProgramId());

        List<UUID> PsMappingIds = programSectionMappings.stream().map(ProgramSectionMapping::getId).toList();
        transactionTemplate.executeWithoutResult(status -> {
            programSectionMappingRepository.deleteAllById(PsMappingIds);
            status.flush();
        });

        List<ProgramSectionMapping> PSMappingsToSave = programSectionMappings.stream()
                .peek(PSMapping -> {
                    SectionCreationRequest section = programSaveReq.getSections().stream()
                            .filter(s -> s.getExistingSectionId().equals(PSMapping.getSection().getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Section does not exist"));
                    PSMapping.setOrderIndex(section.getOrderIndex());
                })
                .peek(PSMapping -> PSMapping.setId(null))
                .toList();
        programSectionMappingRepository.saveAll(new ArrayList<>(PSMappingsToSave));


        List<UUID> SMMappingIds = programSaveReq.getSections().stream()
                .flatMap(s -> s.getModules().stream())
                .map(ModuleCreationRequest::getExistingModuleId)
                .toList();
        transactionTemplate.executeWithoutResult(status -> {
            programSectionMappingRepository.deleteAllById(PsMappingIds);
            status.flush();
        });

//        sectionModuleMappingRepository.findBySectionAndModule()
        //todo do for modules and topics
    }
}
