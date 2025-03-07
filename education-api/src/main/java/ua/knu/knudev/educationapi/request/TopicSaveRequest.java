package ua.knu.knudev.educationapi.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class TopicSaveRequest extends BaseLearningUnitSaveRequest {
    private UUID existingTopicId;
    private List<String> learningResources;
    private Integer orderIndex;
    private UUID testId;

    @Min(1)
    @Max(10)
    private Integer difficulty;

    @Builder(toBuilder = true)
    public TopicSaveRequest(MultiLanguageFieldDto name,
                            MultiLanguageFieldDto description,
                            MultipartFile finalTask,
                            UUID existingTopicId,
                            List<String> learningResources,
                            Integer orderIndex,
                            UUID testId,
                            Integer difficulty) {
        super(name, description, finalTask);
        this.existingTopicId = existingTopicId;
        this.learningResources = learningResources;
        this.orderIndex = orderIndex;
        this.testId = testId;
        this.difficulty = difficulty;
    }
}