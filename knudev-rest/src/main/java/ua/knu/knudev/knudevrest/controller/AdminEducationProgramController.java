package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

@RestController
@RequestMapping("/admin/education")
@RequiredArgsConstructor
public class AdminEducationProgramController {

    private final EducationProgramApi educationProgramApi;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createProgram(@ModelAttribute EducationProgramCreationRequest creationRequest) {
//        System.out.println(creationRequest);
        educationProgramApi.save(creationRequest);
    }
}
