package ua.knu.knudev.educationapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TopicCreationRequest {
   private  UUID existingTopicId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private MultipartFile task;
    private Set<String> learningMaterials;
    private int orderIndex;
    private Set<UUID> testIds;
}