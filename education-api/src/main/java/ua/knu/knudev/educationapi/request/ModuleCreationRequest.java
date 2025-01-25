package ua.knu.knudev.educationapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModuleCreationRequest {
    private UUID existingModuleId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private List<TopicCreationRequest> topics;
    private MultipartFile finalTask;
    private int orderIndex;
    private UUID testId;
}
