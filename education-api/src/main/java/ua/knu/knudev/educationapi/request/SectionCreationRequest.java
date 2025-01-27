package ua.knu.knudev.educationapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ValidCreationRequest
public class SectionCreationRequest {
    private UUID existingSectionId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private List<ModuleCreationRequest> modules;
    private MultipartFile finalTask;
    private Integer orderIndex;
    private UUID finalTestId;
}