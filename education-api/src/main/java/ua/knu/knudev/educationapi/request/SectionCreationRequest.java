package ua.knu.knudev.educationapi.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
public record SectionCreationRequest(
        UUID existingSectionId,
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<ModuleCreationRequest> modules,
        MultipartFile finalTask,
        @NotNull(message = "Order index must be present even when the section already exists")
        int orderIndex,
        UUID finalTestId
) {}