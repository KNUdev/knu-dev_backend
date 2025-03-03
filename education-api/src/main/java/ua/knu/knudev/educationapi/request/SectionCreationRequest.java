package ua.knu.knudev.educationapi.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
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
public class SectionCreationRequest extends BaseLearningUnitSaveRequest{
    private UUID existingSectionId;
    private List<ModuleCreationRequest> modules;
    private Integer orderIndex;

    @Builder(toBuilder = true)
    public SectionCreationRequest(MultiLanguageFieldDto name, MultiLanguageFieldDto description, MultipartFile finalTask, UUID existingSectionId, List<ModuleCreationRequest> modules, Integer orderIndex) {
        super(name, description, finalTask);
        this.existingSectionId = existingSectionId;
        this.modules = modules;
        this.orderIndex = orderIndex;
    }
}