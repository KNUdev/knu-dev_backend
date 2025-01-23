package ua.knu.knudev.educationapi.request;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;

public record EducationProgramCreationRequest(
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<SectionCreationRequest> sections,
        Expertise expertise,
        MultipartFile finalTask
) {

}
