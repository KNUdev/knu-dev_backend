package ua.knu.knudev.educationapi.request;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

public record SectionCreationRequest(
        MultiLanguageFieldDto name,
        MultiLanguageFieldDto description,
        Set<ModuleCreationRequest> modules,
        MultipartFile finalTask,
        int orderIndex,
        UUID finalTestId
) {}