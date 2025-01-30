package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

import java.util.UUID;

@RestController
@RequestMapping("/admin/education/program")
@RequiredArgsConstructor
public class AdminEducationProgramController {

    private final EducationProgramApi educationProgramApi;


    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EducationProgramDto createProgram(@ModelAttribute EducationProgramCreationRequest creationRequest) {
        return educationProgramApi.save(creationRequest);
    }

    @PatchMapping("/publish")
    public void publish(@RequestBody UUID programId) {
        //todo
//        educationProgramApi.publish(programId);
    }

//    @PatchMapping("/topic/{topicId}/update")
//    public void updateTopic(@RequestBody UUID programId, @PathVariable UUID topicId) {
//
//    }
//
//    @PatchMapping("/module/{moduleId}/update")
//    public void updateModule(@RequestBody UUID programId, @PathVariable UUID moduleId) {
//
//    }
//
//    @PatchMapping("/section/{sectionId}/update")
//    public void updateSection(@RequestBody UUID programId, @PathVariable UUID sectionId) {
//
//    }
//
//    @PatchMapping("/program/{programId}/update")
//    public void updateProgramNode(@RequestBody UUID programId, @PathVariable UUID programId) {
//
//    }


}
