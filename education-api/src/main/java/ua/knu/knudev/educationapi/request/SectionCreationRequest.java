package ua.knu.knudev.educationapi.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.validation.ValidCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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