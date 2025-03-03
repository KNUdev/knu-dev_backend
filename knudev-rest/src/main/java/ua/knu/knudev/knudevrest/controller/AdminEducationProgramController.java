package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.dto.ProgramSectionDto;
import ua.knu.knudev.educationapi.dto.ProgramSummaryDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/education")
@RequiredArgsConstructor
public class AdminEducationProgramController {

    private final EducationProgramApi educationProgramApi;

    @PostMapping(value = "/program/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EducationProgramDto saveProgram(@ModelAttribute EducationProgramCreationRequest creationRequest) {
        return educationProgramApi.save(creationRequest);
    }

    @PatchMapping(value = "/topic/{topicId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateTopic(@PathVariable UUID topicId, @ModelAttribute TopicCreationRequest topicCreationRequest) {
        educationProgramApi.updateTopicMeta(topicId, topicCreationRequest);
    }

    @PatchMapping(value = "/module/{moduleId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateModule(@PathVariable UUID moduleId, @ModelAttribute ModuleCreationRequest moduleCreationRequest) {
        educationProgramApi.updateModuleMeta(moduleId, moduleCreationRequest);
    }

    @PatchMapping(value = "/section/{sectionId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProgramSectionDto updateSection(@PathVariable UUID sectionId,
                                           @ModelAttribute SectionCreationRequest sectionCreationRequest) {
        return educationProgramApi.updateSectionMeta(sectionId, sectionCreationRequest);
    }

    @PatchMapping(value = "/program/{programId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProgram(@PathVariable UUID programId,
                              @ModelAttribute EducationProgramCreationRequest programCreationRequest) {
        educationProgramApi.updateProgramMeta(programId, programCreationRequest);
    }

    @GetMapping("/program")
    public EducationProgramDto getById(@RequestParam("id") UUID id) {
        return educationProgramApi.getById(id);
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

    @PatchMapping("/publish")
    public void publish(@RequestBody UUID programId) {
        //todo
//        educationProgramApi.publish(programId);
    }

}
