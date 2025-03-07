package ua.knu.knudev.educationapi.request;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ValidCreationRequest
public class ModuleSaveRequest extends BaseLearningUnitSaveRequest {
    private UUID existingModuleId;
    private List<@Valid TopicSaveRequest> topics;
    private Integer orderIndex;

    @Builder(toBuilder = true)
    public ModuleSaveRequest(MultiLanguageFieldDto name,
                             MultiLanguageFieldDto description,
                             MultipartFile finalTask,
                             UUID existingModuleId,
                             List<TopicSaveRequest> topics,
                             Integer orderIndex) {
        super(name, description, finalTask);
        this.existingModuleId = existingModuleId;
        this.topics = topics;
        this.orderIndex = orderIndex;
    }
}
