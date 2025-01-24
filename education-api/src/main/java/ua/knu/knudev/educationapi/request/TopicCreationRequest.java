package ua.knu.knudev.educationapi.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
public record TopicCreationRequest(
        UUID existingTopicId,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        MultipartFile task,
        Set<String> learningMaterials,
        int orderIndex,
        Set<UUID> testIds
) {
}