package ua.knu.knudev.educationapi.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ValidCreationRequest
public class TopicCreationRequest {
   private  UUID existingTopicId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private MultipartFile task;
    private Set<String> learningMaterials;
    private Integer orderIndex;
    private Set<UUID> testIds;
}