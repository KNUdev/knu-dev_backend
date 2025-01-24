package ua.knu.knudev.assessmentmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;

public interface RolePromotionTaskApi extends BaseTaskApi<String> {
    String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file);
}
