package ua.knu.knudev.assessmentmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

public interface TaskUploadAPI {
    String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file);
    String uploadTaskForEducationProgram(LearningUnit learningUnit, MultipartFile file);
}
