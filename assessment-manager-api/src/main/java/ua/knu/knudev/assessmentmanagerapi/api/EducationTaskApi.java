package ua.knu.knudev.assessmentmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanagerapi.dto.EducationTaskDto;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.util.Map;

public interface EducationTaskApi extends BaseTaskApi<String> {

    Map<LearningUnit, Map<Integer, EducationTaskDto>> uploadAll(
            Map<LearningUnit, Map<Integer, MultipartFile>> educationProgramTasks
    );

}
