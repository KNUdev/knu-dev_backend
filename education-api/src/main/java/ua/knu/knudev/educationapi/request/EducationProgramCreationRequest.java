package ua.knu.knudev.educationapi.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

//todo validate. Either UUID and nothing all. Or all and no UUID
@Builder
public record EducationProgramCreationRequest(
        UUID existingProgramId,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<SectionCreationRequest> sections,
        Expertise expertise,
        MultipartFile finalTask
) {

}
