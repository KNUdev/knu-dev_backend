package ua.knu.knudev.educationapi.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ua.knu.knudev.educationapi.dto.*;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;

import java.util.List;
import java.util.UUID;

@Validated
public interface EducationProgramApi {
    EducationProgramDto save(@Valid ProgramSaveRequest programCreationRequest);

    EducationProgramDto getById(UUID programId);

    EducationProgramDto publish(UUID programId);

    EducationProgramDto updateProgramMeta(UUID programId, ProgramSaveRequest programCreationRequest);

    ProgramSectionDto updateSectionMeta(UUID sectionId, SectionSaveRequest sectionSaveRequest);

    ProgramModuleDto updateModuleMeta(UUID moduleId, ModuleSaveRequest moduleSaveRequest);

    ProgramTopicDto updateTopicMeta(UUID topicId, TopicSaveRequest moduleCreationRequest);

    List<ProgramSummaryDto> getAll();

    void deleteProgramById(UUID programId);

    void removeProgramSectionMapping(UUID programId, UUID sectionId);

    void removeSectionModuleMapping(UUID programId, UUID sectionId, UUID moduleId);

    void removeModuleTopicMapping(UUID programId, UUID sectionId, UUID moduleId, UUID topicId);
}
