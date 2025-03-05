package ua.knu.knudev.educationapi.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ValidCreationRequest
public class SectionSaveRequest extends BaseLearningUnitSaveRequest{
    private UUID existingSectionId;
    private List<ModuleSaveRequest> modules;
    private Integer orderIndex;

    @Builder(toBuilder = true)
    public SectionSaveRequest(MultiLanguageFieldDto name, MultiLanguageFieldDto description, MultipartFile finalTask, UUID existingSectionId, List<ModuleSaveRequest> modules, Integer orderIndex) {
        super(name, description, finalTask);
        this.existingSectionId = existingSectionId;
        this.modules = modules;
        this.orderIndex = orderIndex;
    }
}