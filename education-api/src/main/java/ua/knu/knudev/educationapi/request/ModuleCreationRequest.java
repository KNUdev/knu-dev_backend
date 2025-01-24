package ua.knu.knudev.educationapi.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
public record ModuleCreationRequest(
        UUID existingModuleId,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<TopicCreationRequest> topics,
        MultipartFile finalTask,
        int orderIndex,
        UUID testId
) {}
