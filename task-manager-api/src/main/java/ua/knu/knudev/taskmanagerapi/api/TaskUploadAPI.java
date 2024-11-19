package ua.knu.knudev.taskmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;

public interface TaskUploadAPI {
    String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file);
}
