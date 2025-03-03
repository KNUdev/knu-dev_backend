package ua.knu.knudev.educationapi.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ValidCreationRequest
public class EducationProgramCreationRequest extends BaseLearningUnitSaveRequest{
    private UUID existingProgramId;
    private List<SectionCreationRequest> sections;
    private Expertise expertise;

    @Builder(toBuilder = true)
    public EducationProgramCreationRequest(MultiLanguageFieldDto name, MultiLanguageFieldDto description, MultipartFile finalTask, UUID existingProgramId, List<SectionCreationRequest> sections, Expertise expertise) {
        super(name, description, finalTask);
        this.existingProgramId = existingProgramId;
        this.sections = sections;
        this.expertise = expertise;
    }
}
