package ua.knu.knudev.educationapi.request;

import jakarta.validation.constraints.NotNull;
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
public class SectionCreationRequest {
    private UUID existingSectionId;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private List<ModuleCreationRequest> modules;
    private MultipartFile finalTask;
    @NotNull(message = "Order index must be present even when the section already exists")
    private int orderIndex;
    private UUID finalTestId;
}