package ua.knu.knudev.knudevrest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.*;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/education")
@RequiredArgsConstructor
public class AdminEducationProgramController {

    private final EducationProgramApi educationProgramApi;

    @PostMapping(value = "/program/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EducationProgramDto saveProgram(@Valid @ModelAttribute ProgramSaveRequest creationRequest) {
        return educationProgramApi.save(creationRequest);
    }

    @PatchMapping(value = "/topic/{topicId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProgramTopicDto updateTopic(@PathVariable UUID topicId, @ModelAttribute TopicSaveRequest topicSaveRequest) {
        return educationProgramApi.updateTopicMeta(topicId, topicSaveRequest);
    }

    @PatchMapping(value = "/module/{moduleId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProgramModuleDto updateModule(@PathVariable UUID moduleId, @ModelAttribute ModuleSaveRequest moduleSaveRequest) {
        return educationProgramApi.updateModuleMeta(moduleId, moduleSaveRequest);
    }

    @PatchMapping(value = "/section/{sectionId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProgramSectionDto updateSection(@PathVariable UUID sectionId,
                                           @ModelAttribute SectionSaveRequest sectionSaveRequest) {
        return educationProgramApi.updateSectionMeta(sectionId, sectionSaveRequest);
    }

    @PatchMapping(value = "/program/{programId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EducationProgramDto updateProgram(@PathVariable UUID programId,
                              @ModelAttribute ProgramSaveRequest programCreationRequest) {
        return educationProgramApi.updateProgramMeta(programId, programCreationRequest);
    }

    @GetMapping("/program")
    public EducationProgramDto getById(@RequestParam("id") UUID id) {
        return educationProgramApi.getById(id);
    }

    @GetMapping("/sections")
    public List<ProgramSectionDto> getAllSections() {
        return educationProgramApi.getSections();
    }

    @GetMapping("/modules")
    public List<ProgramModuleDto> getAllModules() {
        return educationProgramApi.getModules();
    }

    @GetMapping("/topics")
    public List<ProgramTopicDto> getAllTopics() {
        return educationProgramApi.getTopics();
    }

    @GetMapping("/programs")
    public List<ProgramSummaryDto> getAll() {
        return educationProgramApi.getAll();
    }

    @DeleteMapping("/mapping/program/{programId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgramWithUnitRelations(@PathVariable UUID programId) {
        educationProgramApi.deleteProgramById(programId);
    }

    @DeleteMapping("/mapping/program/{programId}/section/{sectionId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProgramSectionMapping(@PathVariable UUID programId,
                                            @PathVariable UUID sectionId) {
        educationProgramApi.removeProgramSectionMapping(programId, sectionId);
    }

    @DeleteMapping("/mapping/program/{programId}/section/{sectionId}/module/{moduleId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSectionModuleMapping(@PathVariable UUID programId,
                                           @PathVariable UUID sectionId,
                                           @PathVariable UUID moduleId) {
        educationProgramApi.removeSectionModuleMapping(programId, sectionId, moduleId);
    }

    @DeleteMapping("/mapping/program/{programId}/section/{sectionId}/module/{moduleId}/topic/{topicId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeModuleTopicMapping(@PathVariable UUID programId,
                                         @PathVariable UUID sectionId,
                                         @PathVariable UUID moduleId,
                                         @PathVariable UUID topicId) {
        educationProgramApi.removeModuleTopicMapping(programId, sectionId, moduleId, topicId);
    }

    @PatchMapping("/program/{programId}/publish")
    public EducationProgramDto publish(@PathVariable UUID programId) {
        return educationProgramApi.publish(programId);
    }

}
