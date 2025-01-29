package ua.knu.knudev.educationapi.api;

import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

public interface EducationProgramApi {
    EducationProgramDto save(EducationProgramCreationRequest programCreationRequest);
    String getTest();
}
