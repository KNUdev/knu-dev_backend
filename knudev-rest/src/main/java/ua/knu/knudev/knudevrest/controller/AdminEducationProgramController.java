package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

import java.util.UUID;

@RestController
@RequestMapping("/admin/education/program")
@RequiredArgsConstructor
public class AdminEducationProgramController {

    private final EducationProgramApi educationProgramApi;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createProgram(@ModelAttribute EducationProgramCreationRequest creationRequest) {
        educationProgramApi.save(creationRequest);
    }

    @PatchMapping("/publish")
    public void publish(@RequestBody UUID programId) {
        //todo
//        educationProgramApi.publish(programId);
    }

}
