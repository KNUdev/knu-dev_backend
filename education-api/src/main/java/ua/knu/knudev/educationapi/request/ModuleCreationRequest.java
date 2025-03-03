package ua.knu.knudev.educationapi.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ValidCreationRequest
public class ModuleCreationRequest extends BaseLearningUnitSaveRequest{
    private UUID existingModuleId;
    private List<TopicCreationRequest> topics;
    private Integer orderIndex;

    @Builder(toBuilder = true)
    public ModuleCreationRequest(MultiLanguageFieldDto name, MultiLanguageFieldDto description, MultipartFile finalTask, UUID existingModuleId, List<TopicCreationRequest> topics, Integer orderIndex) {
        super(name, description, finalTask);
        this.existingModuleId = existingModuleId;
        this.topics = topics;
        this.orderIndex = orderIndex;
    }
}
