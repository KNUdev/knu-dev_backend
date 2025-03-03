package ua.knu.knudev.educationapi.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ua.knu.knudev.educationapi.dto.*;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;

import java.util.List;
import java.util.UUID;

@Validated
public interface EducationProgramApi {
    EducationProgramDto save(@Valid EducationProgramCreationRequest programCreationRequest);

    EducationProgramDto getById(UUID programId);

    EducationProgramDto updateProgramMeta(UUID programId, EducationProgramCreationRequest programCreationRequest);

    ProgramSectionDto updateSectionMeta(UUID sectionId, SectionCreationRequest sectionCreationRequest);

    ProgramModuleDto updateModuleMeta(UUID moduleId, ModuleCreationRequest moduleCreationRequest);

    ModuleTopicDto updateTopicMeta(UUID topicId, TopicCreationRequest moduleCreationRequest);

    List<ProgramSummaryDto> getAll();

    void deleteProgramById(UUID programId);

    void removeProgramSectionMapping(UUID programId, UUID sectionId);

    void removeSectionModuleMapping(UUID programId, UUID sectionId, UUID moduleId);

    void removeModuleTopicMapping(UUID programId, UUID sectionId, UUID moduleId, UUID topicId);
}
