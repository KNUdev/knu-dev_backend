package ua.knu.knudev.taskmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;

public interface TaskUploadAPI {
    String uploadTaskForRole(AccountRole accountRole, MultipartFile file);
}
