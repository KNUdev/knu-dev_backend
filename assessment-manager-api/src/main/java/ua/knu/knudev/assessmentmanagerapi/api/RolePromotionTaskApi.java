package ua.knu.knudev.assessmentmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.dto.RolePromotionTaskDto;

import java.util.Set;

public interface RolePromotionTaskApi extends BaseTaskApi<String> {
    String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file, String accountEmail);

    Set<RolePromotionTaskDto> getAllTasksByAccountEmail(String accountEmail);
}
