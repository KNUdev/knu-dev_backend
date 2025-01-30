package ua.knu.knudev.educationapi.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

@Validated
public interface EducationProgramApi {
    EducationProgramDto save(@Valid EducationProgramCreationRequest programCreationRequest);
    String getTest();
}
