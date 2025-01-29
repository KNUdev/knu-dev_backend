package ua.knu.knudev.educationapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ValidCreationRequest
public class EducationProgramCreationRequest {
    private UUID existingProgramId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private List<SectionCreationRequest> sections;
    private Expertise expertise;
    private MultipartFile finalTask;
}
