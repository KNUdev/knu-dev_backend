package ua.knu.knudev.educationapi.api;

import org.springframework.context.annotation.Profile;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;

@Profile("dev")
public interface DevProfileEducationApi {
    EducationProgramDto createTestProgram();
}
