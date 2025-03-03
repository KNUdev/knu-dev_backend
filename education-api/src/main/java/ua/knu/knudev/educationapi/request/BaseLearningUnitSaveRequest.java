package ua.knu.knudev.educationapi.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseLearningUnitSaveRequest {
    protected MultiLanguageFieldDto name;
    protected MultiLanguageFieldDto description;
    protected MultipartFile finalTask;
}
